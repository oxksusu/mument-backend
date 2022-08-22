package com.example.mumentbackend.config.auth.jwt;

import lombok.Value;

public interface JwtProperties {

    /* JWT Signature 해싱용 시크릿 키 : 512비트 이상, 환경변수로 불러오고 싶은데... */
    String SECRET = "eyJSb2xlIjoiQWRtaW4iLCJJc3N1ZXIiOiJzaWVycmFoIiwiVXNlcm5hbWUiOiJKdW5nbWluIEh3YW5nIiwiZXhwIjoxNjYwOTY1NTg4LCJpYXQiOjE2NjA5NjU1ODh9";

    /* JWT 만료 기간 (단위: 초)
    지금 단계에서는 refresh token 을 사용하지 않을 것이므로 길게 설정(14일)했다. */
    int EXPIRATION_TIME = 60 * 60 * 24 * 14;

    /* 토큰 앞쪽에 붙여줄 값. Bearer 다음에 한 칸 띄어쓰는 거 잊지 말기 */
    String TOKEN_PREFIX = "Bearer ";

    /* 헤더 Authorization 항목에 토큰을 넣어주기 위해 */
    String HEADER_STRING = "Authorization";
}
