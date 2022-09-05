package com.example.mumentbackend.web;

import com.example.mumentbackend.domain.Account;
import com.example.mumentbackend.service.AuthService;
import com.example.mumentbackend.service.SecurityService;
import com.example.mumentbackend.web.dto.account.LoginResponseDto;
import com.example.mumentbackend.web.dto.account.RefreshResponseDto;
import com.example.mumentbackend.web.dto.account.SignupResponseDto;
import com.example.mumentbackend.web.dto.account.SignupRequestDto;
import com.example.mumentbackend.web.dto.account.kakao.KakaoAccountDto;
import com.example.mumentbackend.web.dto.account.kakao.KakaoTokenDto;
import com.example.mumentbackend.web.dto.token.TokenDto;
import com.example.mumentbackend.web.dto.token.TokenRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/*

@sierrah
OAuth Kakao 인증 관련 요청을 처리하는 API 입니다.


*/
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final SecurityService securityService;

    /*
    @sierrah
    인가코드로 카카오 서버에 액세스 토큰을 요청하고,
    해당 토큰으로 사용자 정보를 받아와 DB에 저장하는 API 입니다.
    GET 방식으로 param 에 들어오는 인가코드를 추출하여 처리 로직을 수행합니다.

    🤔 생각해볼 것 :
        - 유저 정보를 불러오는 과정은 다른 컨트롤러 단으로 분리? (=> 해결 완료!)
        - 로그인 요청이 들어오면, JWT 를 매번 발급? (=> 해야지 임뫄..)
        - refresh token 만료시?
    */

    @GetMapping("/login/kakao")
    public ResponseEntity<LoginResponseDto> kakaoLogin(HttpServletRequest request) {

        String code = request.getParameter("code");
        System.out.println(code);
        KakaoTokenDto kakaoTokenDto = authService.getKakaoAccessToken(code);
        System.out.println("kakaoTokenDto: " + kakaoTokenDto);
        String kakaoAccessToken = kakaoTokenDto.getAccess_token();
        System.out.println("kakaoAccessToken: " + kakaoAccessToken);

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setKakaoAccessToken(kakaoAccessToken);
        //  account 받아와야할듯??
        KakaoAccountDto kakaoAccountDto = authService.getKakaoInfo(kakaoAccessToken);
        Account account = authService.mapKakaoInfo(kakaoAccountDto);
        System.out.println("매핑된 정보:" + account);
        loginResponseDto.setAccount(account);
        loginResponseDto.setKakaoAccessToken(kakaoAccessToken);

        TokenDto tokenDto = authService.kakaoLogin(kakaoAccessToken);
        HttpHeaders headers;
        if (tokenDto != null) {
            loginResponseDto.setLoginSuccess(true);
            headers = authService.setTokenHeaders(tokenDto);
        } else {
            headers = new HttpHeaders();
            loginResponseDto.setLoginSuccess(false);
        }

        return ResponseEntity.ok().headers(headers).body(loginResponseDto);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> kakaoSignup(@RequestBody SignupRequestDto requestDto) {

        // requestDto 로 데이터 받아와서 accountId 반환
        Long accountId = authService.kakaoSignUp(requestDto);
        Account account = authService.accountFindById(accountId);

        // 최초 가입자에게는 RefreshToken, AccessToken 모두 발급
        TokenDto tokenDto = securityService.signup(accountId);

        // AccessToken 은 header 에 세팅하고, refreshToken 은 httpOnly 쿠키로 세팅
        HttpHeaders headers = authService.setTokenHeaders(tokenDto);

        // 응답 작성
        SignupResponseDto signUpResponseDto = new SignupResponseDto();
        signUpResponseDto.setEmail(account.getEmail());
        signUpResponseDto.setAccountName(account.getAccountName());
        signUpResponseDto.setPicture(account.getPicture());

        return ResponseEntity.ok().headers(headers).body(signUpResponseDto);
    }

    @GetMapping("/reissue")
    public ResponseEntity reissue(HttpServletRequest request,
                                  @CookieValue(name = "RefreshToken") Cookie cookie) {
        String accessToken = request.getHeader("Authorization");
        System.out.println("뽑아낸 access token: " + accessToken); //확인용
        String refreshToken = cookie.getValue();
        System.out.println("뽑아낸 refresh token: " + refreshToken); //확인용

        TokenRequestDto tokenRequestDto = new TokenRequestDto(accessToken, refreshToken);
        TokenDto newTokenDto = securityService.reissue(tokenRequestDto);

        HttpHeaders headers = authService.setTokenHeaders(newTokenDto);

        return ResponseEntity.ok().headers(headers).body("토큰 재발행이 완료되었습니당");
    }

    @GetMapping("/refresh")
    public ResponseEntity refresh(@CookieValue(name = "RefreshToken") Cookie cookie) {

        String refreshToken = cookie.getValue(); //쿠키속 refreshToken 가져오기
        RefreshResponseDto responseDto = securityService.refresh(refreshToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", responseDto.getAccessToken());
        return ResponseEntity.ok().headers(headers).body(responseDto.getAccount());
    }
}
