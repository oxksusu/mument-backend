package com.example.mumentbackend.config.auth;

import com.example.mumentbackend.config.auth.jwt.CustomAuthenticationEntryPoint;
import com.example.mumentbackend.config.auth.jwt.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint = new CustomAuthenticationEntryPoint();
    private final JwtRequestFilter jwtRequestFilter = new JwtRequestFilter();
    // h2 database 테스트가 원활하도록 관련 API 들은 전부 무시
    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers("/h2-console/**", "/favicon.ico");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // CSRF 설정 Disable
        http.csrf().disable()

            // h2-console 을 위한 설정을 추가
            .headers()
            .frameOptions()
            .sameOrigin()
            .and()
            .cors() // CORS 에러 방지용

            // 시큐리티는 기본적으로 세션을 사용
            // 세션을 사용하지 않을거라 세션 설정을 Stateless 로 설정
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            // 접근 권한 설정부
            .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS).permitAll() // 열어두어야 CORS Preflight 막을 수 있음
            .antMatchers("/api/**").permitAll()
            .anyRequest().permitAll()

            // JWT 토큰 예외처리부
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(customAuthenticationEntryPoint);

        /* 필터 걸어주기 : UsernamePasswordAuthenticationFilter 이전에 실행되도록 */
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}