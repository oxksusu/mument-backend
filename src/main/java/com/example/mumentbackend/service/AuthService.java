package com.example.mumentbackend.service;

import com.example.mumentbackend.config.auth.jwt.JwtProvider;
import com.example.mumentbackend.domain.*;
import com.example.mumentbackend.domain.repository.AccountRepository;
import com.example.mumentbackend.domain.KakaoToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/*

@sierrah
카카오 로그인 로직을 처리합니다.

< getAccessToken(String code) >
프론트에서 인가코드 (code) 를 받아와
카카오 API 서버로부터 Access Token 을 받아옵니다.

< saveAccountInfo(String access_token) >
카카오 서버로부터 발급받은 Access Token 으로 사용자 정보를 받아오고, 이를 DB에 저장합니다.

 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final JwtProvider jwtProvider;

    /* 환경변수 가져오기 */
    @Value("${kakao.key}")
    String KAKAO_REST_API_KEY;

    @Value("${kakao.redirect}")
    String KAKAO_REDIRECT_URI;

    /* 1. 프론트에서 1회성 코드 받아와 AccessToken 따오는 메소드 */
    public KakaoToken getAccessToken(String code) {

        System.out.println(code); //확인용

        RestTemplate rt = new RestTemplate(); //통신용
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpBody 객체 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code"); //카카오 공식문서 기준 authorization_code 로 고정
        params.add("client_id", KAKAO_REST_API_KEY); //카카오 앱 REST API 키
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code); //인가 코드 요청시 받은 인가 코드값, 프론트에서 받아오는 그 코드

        // 헤더와 바디 합치기 위해 HttpEntity 객체 생성
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
        System.out.println(kakaoTokenRequest);

        // 카카오로부터 Access token 받아옴 -> 카카오가 보내는 토큰은 JSON 형식이라 파싱해야함
        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // JSON 데이터를 KakaoToken 객체에 담음
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoToken oAuthToken = null;
        try {
            oAuthToken = objectMapper.readValue(accessTokenResponse.getBody(), KakaoToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return oAuthToken;
    }


    /* 2. Access Token 으로 카카오 API 서버에서 회원 정보를 받아와 저장하고, jwt 를 발행해주는 메소드 */
    public String getLogin(String accessToken) {

        // 1) access token 으로 카카오 서버 데이터(JSON)를 kakaoAccountInfo 에 받아오기
        KakaoAccountInfo kakaoAccountInfo = getAccountInfo(accessToken);

        // 2) 이메일 꺼내오기
        String kakaoEmail = kakaoAccountInfo.getKakao_account().getEmail();
        String nickname = kakaoAccountInfo.getKakao_account().getProfile().getNickname();

        Account account = accountRepository.findByEmail(kakaoEmail);

        // 3) DB에 계정이 존재하지 않으면, 계정 정보를 DB에 저장함
        // 4) refresh token 도 발급해서 넣어줌
        if (!(accountRepository.existsByEmail(kakaoEmail))) {
            account = Account.builder()
                    .socialLoginType(SocialLoginType.KAKAO)
                    .nickname(nickname)
                    .email(kakaoEmail)
                    .authority(Authority.USER)
                    .refreshToken(jwtProvider.createRefreshToken())
                    .build();

            accountRepository.save(account);
        }

        String jwtToken = jwtProvider.createJwtToken(account);

        return jwtToken;
    }

    /* 3. access token 으로 JSON 응답 받아오는 메소드 */

    public KakaoAccountInfo getAccountInfo(String accessToken) {

        RestTemplate rt = new RestTemplate(); //통신용

        /* 요청 헤더 설정하기
           카카오 서버에 정보 요청하려면 헤더에 access token 담아서 보내야 함
           줴발 ... 오타 안 나게 조심하시오 괜히 아까처럼 또 고생하지 말고 */
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> accountInfoRequest = new HttpEntity<>(headers);

        /* POST 방식으로 API 서버에 요청 보내고, response 객체에 응답 받아오기 */
        ResponseEntity<String> accountInfoResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                accountInfoRequest,
                String.class
        );

        /* JSON 데이터 파싱하기 */
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoAccountInfo kakaoAccountInfo = null;
        try {
            kakaoAccountInfo = objectMapper.readValue(accountInfoResponse.getBody(), KakaoAccountInfo.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return kakaoAccountInfo;
    }

    /* DB에서 리프레시 토큰 찾는 메소드 */
    public String findRefreshToken(String kakaoToken) {
        KakaoAccountInfo kakaoAccountInfo = getAccountInfo(kakaoToken);
        String kakaoEmail = kakaoAccountInfo.getKakao_account().getEmail();
        String refreshToken = accountRepository.findByEmail(kakaoEmail).getRefreshToken();

        return refreshToken;
    }
}
