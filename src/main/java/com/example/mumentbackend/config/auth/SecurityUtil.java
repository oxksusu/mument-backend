package com.example.mumentbackend.config.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    private SecurityUtil() { }

    /*
    Security Context 는 유저 정보가 저장될 공간
    Request 가 들어오면 JwtFilter 의 doFilter 메소드가 유저 정보를 저장함
     */
    public static Long getCurrentAccountId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context 에 인증 정보가 없습니다.");
        }

        return Long.parseLong(authentication.getName());
    }
}
