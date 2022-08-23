package com.example.mumentbackend.config.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.mumentbackend.domain.Account;
import com.example.mumentbackend.domain.KakaoAccountInfo;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;


@NoArgsConstructor
@Component
public class JwtProvider {

    /*

    jwt 생성 메소드
    jjwt 아닌 java-jwt 라이브러리를 사용하여 문법이 쫌 다를 수 있다는 점...

    */
    public String createJwtToken(Account account) {
        String jwtToken = null; // 예외처리때문에 try-catch 문 추가함
        try {
            jwtToken = JWT.create()

                    //
                    .withSubject(account.getEmail())
                    .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))

                    //
                    .withClaim("id", account.getId())
                    .withClaim("email", account.getEmail())
                    .withClaim("nickname", account.getNickname())

                    .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return jwtToken;
    }

    /*

    리프레시 토큰 생성 메소드

    만료기간이 있는 리프레시 토큰을 생성
    만료기간 외의 다른 정보는 담지 않았음

    */
    public String createRefreshToken() {
        String refreshToken = null;
        try {
            refreshToken = JWT.create()

                    .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))

                    .sign(Algorithm.HMAC512(JwtProperties.SECRET));

        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return refreshToken;
    }

}
