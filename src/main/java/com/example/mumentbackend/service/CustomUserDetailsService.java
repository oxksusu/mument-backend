package com.example.mumentbackend.service;

import com.example.mumentbackend.config.exception.excase.CUserNotFoundException;
import com.example.mumentbackend.domain.Account;
import com.example.mumentbackend.domain.UserDetailsImpl;
import com.example.mumentbackend.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Account account = accountRepository.findById(Long.parseLong(id))
                        .orElseThrow(CUserNotFoundException::new);
        log.info(id + " - 사용자를 찾는 데 성공했습니다.");

        return new UserDetailsImpl(account);
    }
}