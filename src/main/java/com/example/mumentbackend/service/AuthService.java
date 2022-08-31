package com.example.mumentbackend.service;

import com.example.mumentbackend.config.auth.jwt.JwtProvider;
import com.example.mumentbackend.config.exception.excase.CUserNotFoundException;
import com.example.mumentbackend.domain.Account;
import com.example.mumentbackend.domain.Role;
import com.example.mumentbackend.domain.repository.AccountRepository;
import com.example.mumentbackend.web.dto.account.SignupRequestDto;
import com.example.mumentbackend.web.dto.account.kakao.KakaoTokenDto;
import com.example.mumentbackend.web.dto.account.LoginResponseDto;
import com.example.mumentbackend.web.dto.account.kakao.KakaoAccountDto;
import com.example.mumentbackend.web.dto.token.TokenDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/*

@sierrah
카카오 로그인 로직을 처리합니다.

 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountRepository accountRepository;
    private final SecurityService securityService;

    /* 환경변수 가져오기 */
    @Value("${kakao.key}")
    String KAKAO_CLIENT_ID;

    @Value("${kakao.redirect}")
    String KAKAO_REDIRECT_URI;

    /* 인가코드로 kakaoAccessToken 따오는 메소드 */
    public KakaoTokenDto getKakaoAccessToken(String code) {

        RestTemplate rt = new RestTemplate(); //통신용
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpBody 객체 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code"); //카카오 공식문서 기준 authorization_code 로 고정
        params.add("client_id", KAKAO_CLIENT_ID); //카카오 앱 REST API 키
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code); //인가 코드 요청시 받은 인가 코드값, 프론트에서 받아오는 그 코드

        // 헤더와 바디 합치기 위해 HttpEntity 객체 생성
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
        System.out.println(kakaoTokenRequest);

        // 카카오로부터 Access token 수신
        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // JSON Parsing (-> KakaoTokenDto)
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoTokenDto kakaoTokenDto = null;
        try {
            kakaoTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return kakaoTokenDto;
    }

    /* kakaoAccessToken 으로 카카오 서버에 정보 요청 */
    public KakaoAccountDto getKakaoInfo(String kakaoAccessToken) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> accountInfoRequest = new HttpEntity<>(headers);

        // POST 방식으로 API 서버에 요청 보내고, response 받아옴
        ResponseEntity<String> accountInfoResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                accountInfoRequest,
                String.class
        );

        System.out.println("카카오 서버에서 정상적으로 데이터를 수신했습니다.");
        // JSON Parsing (-> kakaoAccountDto)
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoAccountDto kakaoAccountDto = null;
        try {
            kakaoAccountDto = objectMapper.readValue(accountInfoResponse.getBody(), KakaoAccountDto.class);
        } catch (JsonProcessingException e) { e.printStackTrace(); }

        return kakaoAccountDto;

    }

    /* kakaoAccountDto -> Account 객체로 매핑 */
    public Account mapKakaoInfo(KakaoAccountDto accountDto) {

        // kakaoAccountDto 에서 필요한 정보 꺼내기
        Long kakaoId = accountDto.getId();
        String email = accountDto.getKakao_account().getEmail();
        String nickname = accountDto.getKakao_account().getProfile().getNickname();

        System.out.println("매핑한 정보 : " + email + ", " + nickname);
        // Account 객체에 매핑
        return Account.builder()
                .socialId(kakaoId)
                .loginType("KAKAO")
                .email(email)
                .nickname(nickname)
                .role("USER")
                .build();
    }


    /* login 요청 보내는 회원가입 유무 판단해 분기 처리 */
    public TokenDto kakaoLogin(String kakaoAccessToken) {

        // kakaoAccessToken 으로 카카오 회원정보 받아옴
        KakaoAccountDto kakaoAccountDto = getKakaoInfo(kakaoAccessToken);
        String kakaoEmail = kakaoAccountDto.getKakao_account().getEmail();

        // kakaoAccountDto 를 Account 로 매핑
        Account selectedAccount = mapKakaoInfo(kakaoAccountDto);
        System.out.println("수신된 account 정보 : " + selectedAccount);

        // 매핑만 하고 DB에 저장하질 않았으니까 Autogenerated 인 id가 null 로 나왔던거네 아... 오케오케 굿

        Account account = accountRepository.findByEmail(kakaoEmail);

        // 가입되어 있으면 해당하는 Account 객체를 응답
        // 존재하면 true + access token 을, 존재하지 않으면 False 리턴
        if (account != null) {

            // 토큰 발급
            TokenDto tokenDto = securityService.login(account.getId());
            System.out.println("로그인이 확인됐고, 토큰을 발급했습니다.");
            return tokenDto;
        } else return null;
    }

    /* 회원가입 */
    public Long kakaoSignUp(SignupRequestDto requestDto) {

        KakaoAccountDto kakaoAccountDto = getKakaoInfo(requestDto.getKakaoAccessToken());
        Account account = mapKakaoInfo(kakaoAccountDto);

        // 닉네임, 프로필사진 set
        String accountName = requestDto.getAccountName();
        String accountPicture = requestDto.getPicture();
        account.setAccountName(accountName);
        account.setPicture(accountPicture);

        // save
        accountRepository.save(account);

        // 회원가입 결과로 회원가입한 accountId 리턴
        return account.getId();
    }

    public HttpHeaders setTokenHeaders(TokenDto tokenDto) {
        HttpHeaders headers = new HttpHeaders();
        ResponseCookie cookie = ResponseCookie.from("RefreshToken", tokenDto.getRefreshToken())
                .path("/")
                .maxAge(60*60*24*7) // 쿠키 유효기간 7일로 설정했음
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        headers.add("Set-cookie", cookie.toString());
        headers.add("Authorization", tokenDto.getAccessToken());

        return headers;
    }

    public Account accountFindById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(CUserNotFoundException::new);
    }
}
