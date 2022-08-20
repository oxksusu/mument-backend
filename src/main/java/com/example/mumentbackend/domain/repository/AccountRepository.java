package com.example.mumentbackend.domain.repository;

import com.example.mumentbackend.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /* 아이디 등을 찾는 건 이미 JPA에서 구현하고 있어서
    Optional 사용해 이메일로 찾는 것만 추가로 구현한 코드인듯
    원래는 인터페이스 선언만 하고 내용물은 없더라구 .. */

    /* 이메일이 Login ID의 역할을 하기 때문에 이메일로 계정 찾는 메소드 구현 */
    Optional<Account> findByEmail(String email);

    /* 중복 가입 방지용 */
    boolean existsByEmail(String email);
}
