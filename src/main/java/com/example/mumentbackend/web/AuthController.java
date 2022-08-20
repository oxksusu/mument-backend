package com.example.mumentbackend.web;

import com.example.mumentbackend.service.AuthService;
import com.example.mumentbackend.web.dto.AccountRequestDto;
import com.example.mumentbackend.web.dto.AccountResponseDto;
import com.example.mumentbackend.web.dto.TokenDto;
import com.example.mumentbackend.web.dto.TokenRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth") // SecurityConfig 에서 /auth/** 요청을 전부 허용해두었으므로 토큰 검증 로직을 타지 않음
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AccountResponseDto> signup(@RequestBody AccountRequestDto accountRequestDto) {
        return ResponseEntity.ok(authService.signup(accountRequestDto));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody AccountRequestDto accountRequestDto) {
        return ResponseEntity.ok(authService.login(accountRequestDto));
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return ResponseEntity.ok(authService.reissue(tokenRequestDto));
    }
}
