package com.example.mumentbackend.web.dto;

import com.example.mumentbackend.domain.Account;
import com.example.mumentbackend.domain.Authority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestDto {

    private String email;
    private String password;

    public Account toAccount(PasswordEncoder passwordEncoder) {
        return Account.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .authority(Authority.USER)
                .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
