package com.example.mumentbackend.web;

import com.example.mumentbackend.config.auth.jwt.JwtProperties;
import com.example.mumentbackend.config.auth.jwt.JwtProvider;
import com.example.mumentbackend.service.AuthService;
import com.example.mumentbackend.domain.KakaoToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
    private final JwtProvider jwtProvider;

    /*
    @sierrah
    인가코드로 카카오 서버에 액세스 토큰을 요청하고,
    해당 토큰으로 사용자 정보를 받아와 DB에 저장하는 API 입니다.
    GET 방식으로 param 에 들어오는 인가코드를 추출하여 처리 로직을 수행합니다.

    🤔 생각해볼 것 :
        - 유저 정보를 불러오는 과정은 다른 컨트롤러 단으로 분리? (=> 해결 완료!)
        - 로그인 요청이 들어오면, JWT 를 매번 발급? (=> 해야지 임뫄..)
    */
    @GetMapping("/oauth/kakao")
    public ResponseEntity kakaoSignUp(@RequestParam("code") String code) {

        HttpHeaders headers = new HttpHeaders();

        /* 카카오 API 서버에서 Access Token = oAuth token 발급 */
        KakaoToken kakaoToken = authService.getAccessToken(code);
        String oAuthToken = kakaoToken.getAccess_token();

        /* 로그인 시도시 JWT 발급 */
        String accessToken = authService.getLogin(oAuthToken); // access_token 이 반환됨

        /* 리프레시 토큰이 DB에 존재하지 않으면 (회원 정보가 없으면) 발급함 */
        String refreshToken = jwtProvider.createRefreshToken();
        /* 리프레시 토큰은 http-only 쿠키로 설정해줘야 함 */
        ResponseCookie cookie = ResponseCookie.from("RefreshToken", refreshToken)
                .maxAge(60*60*24*7) // 쿠키 유효기간 설정
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();

        /* 응답 Header 에 refresh token 추가 */
        headers.add("Set-Cookie", cookie.toString());


        /* access token 발급 */
        headers.add(JwtProperties.HEADER_STRING_ACCESS, JwtProperties.TOKEN_PREFIX + accessToken);
        return ResponseEntity.ok()
                .headers(headers)
                .body("회원 정보 저장과 jwt 발행이 완료되었습니다.");
    }

    @GetMapping("/oauth/refresh")
    public void setRefreshToken(@CookieValue(value="RefreshToken") Cookie cookie) {

        String cookieValue = cookie.getValue();
        System.out.println("쿠키 값을 읽어와봐용 : " + cookieValue); //오오오오오오
    }

}
