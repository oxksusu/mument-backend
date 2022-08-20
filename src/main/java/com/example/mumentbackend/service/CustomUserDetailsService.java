package com.example.mumentbackend.service;

import com.example.mumentbackend.domain.Account;
import com.example.mumentbackend.domain.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService  implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(username + "을 DB에서 찾을 수 없습니다."));
    }

    //DB에 User 값이 존재한다면 UserDetails 객체로 만들어서 리턴함
    private UserDetails createUserDetails(Account account) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(account.getAuthority().toString());

        return new User(
                String.valueOf(account.getId()),
                account.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }

}
