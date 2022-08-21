package com.example.mumentbackend.web;

import com.example.mumentbackend.domain.Account;
import com.example.mumentbackend.service.KakaoService;
import com.example.mumentbackend.domain.OAuthToken;
import lombok.RequiredArgsConstructor;
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

    생각해볼 것 :
    */
    @GetMapping("/oauth/kakao")
    public Account getLoginCode(@RequestParam("code") String code) {

        /* 1. Access Token 을 받아오는 메서드 getAccessToken */
        OAuthToken oAuthToken = kakaoService.getAccessToken(code);

        /* 2. 발급받은 Access Token 으로 카카오 서버에서 회원정보를 받아와 DB에 저장하는 메서드 saveAccountInfo */
        Account account = kakaoService.saveAccountInfo(oAuthToken.getAccess_token()); // access_token 이 반환됨

        return account;

    }

    /*
    @sierrah
    받아온 액세스 토큰으로 카카오 서버에 사용자 정보를 요청하고,
    받아온 사용자 정보를 DB에 저장하는 API 입니다.
    to-do : 이 과정에서 로그인 수단까지 함께 저장할 것인지?
     */

}
