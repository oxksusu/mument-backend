package com.example.mumentbackend.web;

import com.example.mumentbackend.config.auth.jwt.JwtProperties;
import com.example.mumentbackend.service.KakaoService;
import com.example.mumentbackend.domain.OAuthToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*

@sierrah
OAuth Kakao 인증 관련 요청을 처리하는 API 입니다.


*/
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class KakaoController {

    private final KakaoService kakaoService;

    /*
    @sierrah
    인가코드로 카카오 서버에 액세스 토큰을 요청하고,
    해당 토큰으로 사용자 정보를 받아와 DB에 저장하는 API입니다.
    GET 방식으로 param 에 들어오는 인가코드를 추출하여 처리 로직을 수행합니다.

    🤔 생각해볼 것 :
        - 유저 정보를 불러오는 과정은 다른 컨트롤러 단으로 분리? (=> 해결 완료!)
        - 로그인 요청이 들어오면 인증된 사용자에게
    */
    @GetMapping("/oauth/kakao")
    public ResponseEntity kakaoLogin(@RequestParam("code") String code) {

        /* 1. Access Token 을 받아오는 메서드 */
        OAuthToken oAuthToken = kakaoService.getAccessToken(code);

        String accessToken = oAuthToken.getAccess_token();

        /* 2. Access Token 으로 카카오 서버에서 회원정보를 받아와 DB에 저장하는 메서드*/
        String jwtToken = kakaoService.saveAccountInfoAndGetJwt(accessToken); // access_token 이 반환됨

        /* 3. 응답 헤더에 jwt 달아주기 */
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);

//        System.out.println(headers);
//        System.out.println("JWT : " + jwtToken);

        return ResponseEntity.ok().headers(headers).body("회원 정보 저장과 jwt 발행이 완료되었습니다.");
    }

}
