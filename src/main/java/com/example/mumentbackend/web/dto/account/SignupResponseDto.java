package com.example.mumentbackend.web.dto.account;

import com.example.mumentbackend.domain.Account;
import lombok.Data;

@Data
public class SignupResponseDto {

    Account account;
    String result;
}
