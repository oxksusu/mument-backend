package com.example.mumentbackend.web;

import com.example.mumentbackend.service.AccountService;
import com.example.mumentbackend.web.dto.AccountResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/mypage")
    public ResponseEntity<AccountResponseDto> getMyAccountInfo() {
        return ResponseEntity.ok(accountService.getMyInfo());
    }

    @GetMapping("/{email}")
    public ResponseEntity<AccountResponseDto> getAccountInfo(@PathVariable String email) {
        return ResponseEntity.ok(accountService.getAccountInfo(email));
    }
}
