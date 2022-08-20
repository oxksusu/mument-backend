package com.example.mumentbackend.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    /* 아이디 등을 찾는 건 이미 JPA에서 구현하고 있어서
    Optional 사용해 이메일로 찾는 것만 추가로 구현한 코드인듯
    원래는 인터페이스 선언만 하고 내용물은 없더라구 .. */
    Optional<Users> findByEmail(String email);
}
