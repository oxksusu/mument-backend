package com.example.mumentbackend.service;

import com.example.mumentbackend.config.auth.jwt.JwtProvider;
import com.example.mumentbackend.config.exception.excase.CEmailLoginFailedException;
import com.example.mumentbackend.config.exception.excase.CRefreshTokenException;
import com.example.mumentbackend.config.exception.excase.CUserNotFoundException;
import com.example.mumentbackend.domain.Account;
import com.example.mumentbackend.domain.RefreshToken;
import com.example.mumentbackend.domain.repository.AccountRepository;
import com.example.mumentbackend.domain.repository.RefreshTokenRepository;
import com.example.mumentbackend.web.dto.account.LoginResponseDto;
import com.example.mumentbackend.web.dto.token.TokenDto;
import com.example.mumentbackend.web.dto.token.TokenRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/*

토큰을 발급하는 모든 상황을 처리하는 Service

*/
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {


    private final AccountRepository accountRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository tokenRepository;

    /* 로그인 된 사용자에게 토큰 발급 : refresh token 은 DB 에 저장 */
    public TokenDto login(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(CEmailLoginFailedException::new);
        System.out.println("SecurityService-login: 계정을 찾았습니다. " + account);
        // 토큰 발행
//        List<String> Role = Collections.singletonList("USER");
        TokenDto tokenDto = jwtProvider.createTokenDto(account.getId(), "USER");

        // RefreshToken 만 DB에 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(account.getId())
                .token(tokenDto.getRefreshToken())
                .build();
        tokenRepository.save(refreshToken);
        System.out.println("토큰 발급과 저장을 완료했습니다.");
        return tokenDto;
    }


    /* 회원 정보를 저장한 뒤 해당 회원 정보로 토큰 발급 */
    public TokenDto signup(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(CUserNotFoundException::new);
        TokenDto tokenDto = jwtProvider.createTokenDto(account.getId(), "USER");

        RefreshToken refreshToken = RefreshToken.builder()
                .key(accountId)
                .token(tokenDto.getRefreshToken())
                .build();
        tokenRepository.save(refreshToken);

        return tokenDto;
    }

    /* 토큰의 유효성을 검사한 뒤 토큰 재발급 */
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {

        // refreshToken 만료 검사
        if (!jwtProvider.validationToken(tokenRequestDto.getRefreshToken())) {
            throw new CRefreshTokenException();
        }

        // AccessToken 으로 accountId 가져오기
        String accessToken = tokenRequestDto.getAccessToken();
        Authentication authentication = jwtProvider.getAuthentication(accessToken);

        // accountId 로 계정 정보와 refresh token 검색 -> 없을 경우 에러 발생
        Long accountId = Long.parseLong(authentication.getName());
        Account account = accountRepository.findById(accountId)
                .orElseThrow(CUserNotFoundException::new);
        RefreshToken refreshToken = tokenRepository.findByKey(accountId)
                .orElseThrow(CRefreshTokenException::new);

        // 가져온 refreshToken 이 들어온 refreshToken 과 일치하지 않을 경우 에러 발생
        if (!refreshToken.getToken().equals(tokenRequestDto.getRefreshToken()))
            throw new CRefreshTokenException();

        // 문제없으면 토큰 재발행
        TokenDto newToken = jwtProvider.createTokenDto(accountId, "USER");
        refreshToken.setToken(newToken.getRefreshToken());
        tokenRepository.save(refreshToken);

        return newToken;
    }
}
