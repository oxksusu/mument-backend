package com.example.mumentbackend.web.dto;

import lombok.Data;

@Data
public class TokenDto {

    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpireDate;
}
