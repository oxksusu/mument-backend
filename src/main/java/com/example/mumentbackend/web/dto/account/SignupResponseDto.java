package com.example.mumentbackend.web.dto.account;

import lombok.Data;

@Data
public class SignupResponseDto {

    String result;

    String email;
    String accountName;
    String picture;
}
