package com.example.mumentbackend.web;

import com.example.mumentbackend.config.auth.jwt.JwtProvider;
import com.example.mumentbackend.domain.Account;
import com.example.mumentbackend.service.AuthService;
import com.example.mumentbackend.service.SecurityService;
import com.example.mumentbackend.web.dto.account.LoginResponseDto;
import com.example.mumentbackend.web.dto.account.SignupResponseDto;
import com.example.mumentbackend.web.dto.account.SignupRequestDto;
import com.example.mumentbackend.web.dto.account.kakao.KakaoTokenDto;
import com.example.mumentbackend.web.dto.token.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        // authService.kakaologin 상에서 다 처리해야함
        LoginResponseDto loginResponseDto = authService.kakaoLogin(kakaoAccessToken);

        return ResponseEntity.ok(loginResponseDto);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> kakaoSignup(@RequestBody SignupRequestDto requestDto) {

        // requestDto 로 데이터 받아와서 accountId 반환
        Long accountId = authService.kakaoSignUp(requestDto);

        // 최초 가입자에게는 RefreshToken, AccessToken 모두 발급
        TokenDto tokenDto = securityService.signup(accountId);

        // AccessToken 은 header 에 세팅하고, refreshToken 은 httpOnly 쿠키로 세팅
        SignupResponseDto signUpResponseDto = new SignupResponseDto();
        HttpHeaders headers = new HttpHeaders();
        ResponseCookie cookie = ResponseCookie.from("RefreshToken", tokenDto.getRefreshToken())
                        .maxAge(60*60*24*7) // 쿠키 유효기간 7일로 설정했음
                        .path("/")
                        .secure(true)
                        .sameSite("None")
                        .httpOnly(true)
                        .build();
        headers.add("Set-Cookie", cookie.toString());
        headers.add("Authorization", tokenDto.getAccessToken());

        signUpResponseDto.setResult("가입이 완료되었습니다.");
        return ResponseEntity.ok().headers(headers).body(signUpResponseDto);
    }

}
