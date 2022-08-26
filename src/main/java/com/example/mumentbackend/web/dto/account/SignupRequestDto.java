package com.example.mumentbackend.web.dto.account;

import lombok.Data;

@Data
public class SignupRequestDto {

    public String kakaoAccessToken;
    public String accountName;
    public String picture;
}
