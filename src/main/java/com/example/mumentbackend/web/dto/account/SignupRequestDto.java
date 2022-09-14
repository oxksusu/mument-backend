package com.example.mumentbackend.web.dto.account;

import com.example.mumentbackend.domain.Account;
import lombok.Data;

@Data
public class SignupRequestDto {

    public String nickname;
    public String picture;
    public Account account;
}
