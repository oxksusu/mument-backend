package com.example.mumentbackend.service;

import com.example.mumentbackend.domain.Account;
import com.example.mumentbackend.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor

public class AccountService {
    private final AccountRepository accountRepository;

    /* 사용자 정보 가져오는 메소드
    요청 헤더에 Authorization 항목으로 토큰이 오면,
    인증된 사용자에 대해 정보를 가져와 Account 타입으로 반환 */
    public Account getAccountInfo(HttpServletRequest request) {

        String authenticAccount = (String) request.getAttribute("authenticAccount");
        System.out.println(authenticAccount);

        Account account = accountRepository.findByEmail(authenticAccount);
        return account;
    }
}
