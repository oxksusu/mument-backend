package com.example.mumentbackend.config.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.mumentbackend.domain.repository.AccountRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@NoArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter { //doFilterInternal 오버라이드 해야 함


    private AccountRepository accountRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /* 요청 헤더를 가져옴 */
        String jwtHeader = ((HttpServletRequest) request).getHeader(JwtProperties.HEADER_STRING);

        /* 헤더가 형식에 맞지 않는 경우 필터링되도록 */
        if (jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        /* 헤더에서 JWT 꺼내오기 (헤더 앞 'Bearer ' 떼어내야 JWT 나옴) */
        String token = jwtHeader.replace(JwtProperties.TOKEN_PREFIX, "");

        /* 유효성 검증된 토큰을 복호화하면 이메일이 나옴 -> authenticAccount 에 담자 */
        String authenticAccount = null;

        /* 복호화, 유효성 검증 - 만료된 토큰과 유효하지 않은 토큰 필터링 */
        try {
            authenticAccount = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET))
                    .build()
                    .verify(token)//복호화
                    .getClaim("email").asString(); //토큰에서 이메일가져오기
        } catch (TokenExpiredException e) {
            e.printStackTrace();
            request.setAttribute(JwtProperties.HEADER_STRING, "토큰이 만료되었습니다.");
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            request.setAttribute(JwtProperties.HEADER_STRING, "유효하지 않은 토큰입니다.");
        }

        request.setAttribute("authenticAccount", authenticAccount);

        filterChain.doFilter(request, response);
    }
}
