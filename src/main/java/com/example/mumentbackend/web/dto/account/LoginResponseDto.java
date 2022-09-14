package com.example.mumentbackend.web.dto.account;

import com.example.mumentbackend.domain.Account;
import lombok.Data;

@Data
public class LoginResponseDto {

    public boolean loginSuccess;
    public Account account;
    public String kakaoAccessToken;
}
