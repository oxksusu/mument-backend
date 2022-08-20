package com.example.mumentbackend.domain.repository;

import com.example.mumentbackend.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // account ID 값으로 토큰을 가져오기 위해 추가했음
    Optional<RefreshToken> findByKey(String key);
}
