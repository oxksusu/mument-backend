package com.example.mumentbackend.config.auth.jwt;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*

토큰 예외처리용 엔트리포인트

🤔 to-do : 만료기간 짧게 해서 만료된 토큰으로 예외처리 되는지 테스트해볼것!

*/
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        String exception = (String) request.getAttribute(JwtProperties.HEADER_STRING);
        String errorCode;

        /* 에러 처리 - 헤더에 꼭 글자를 담아야 할까? 에러를 문서화해서 코드로 나타낼 순 없을까... */
        if (exception.equals("토큰이 만료되었습니다.")) {
            errorCode = exception;
            setResponse(response, errorCode);
        }

        if (exception.equals("유효하지 않은 토큰입니다.")) {
            errorCode = exception;
            setResponse(response, errorCode);
        }
    }

    private void setResponse(HttpServletResponse response, String errorCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401 unauthorized 에러 발생시키기
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(JwtProperties.HEADER_STRING + " : " + errorCode); //에러내용 응답에 기재하기
    }
}
