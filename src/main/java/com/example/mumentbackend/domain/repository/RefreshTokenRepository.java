package com.example.mumentbackend.domain.repository;

import com.example.mumentbackend.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /* 토큰 존재 유무 확인 */
    Optional<RefreshToken> findByKey(Long key);
    Optional<RefreshToken> findByToken(String token);
}