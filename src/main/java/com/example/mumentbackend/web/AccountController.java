package com.example.mumentbackend.web;

import com.example.mumentbackend.domain.Account;
import com.example.mumentbackend.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

/*
@sierrah
request header 에 authorization 항목으로 토큰 정보가 들어오면
만료 여부와 유효성을 검사하여 필터링된 정보에 한해 계정 데이터를 제공합니다.

🤔 생각해볼 것 :
   - 이 컨트롤러 단에서 구현해야 할 기능 : 로그인, 마이페이지, 또?
   -

*/
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AccountController {

    private final AccountService accountService;

    /* AccountController 단으로 분리하자 */
    @GetMapping("/account/mypage")
    public ResponseEntity getCurrentAccounInfo(HttpServletRequest request) {

        Account account = accountService.getAccountInfo(request);
        return ResponseEntity.ok().body(account);
    }

}
