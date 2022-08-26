package com.example.mumentbackend.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/*

@Entity 어노테이션 붙이면 알아서 JPA 연동됨

 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account")
@Entity
public class Account extends BaseTimeEntity { // 예약어가 이미 존재하므로 users로 바꾸어 지정해야함

    @Id
    @Column(name="account_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // db의 id 값이 자동으로 생성되도록 한 경우 꼭 붙여줘야 하는 어노테이션
    private Long id;

    @Column
    private Long socialId;

    @Column
    private String nickname; //카카오닉네임

    @Column
    private String accountName; //사용자별명

    @Column(nullable = false)
    private String email;

    /* 회원가입 과정에서는 프로필 사진을 나중에 등록할 수 있게 nullable */
    @Column
    private String picture;

    /* 오버라이딩 충돌 이슈로 닫아놨음.. */
    @Column(nullable = false)
    private String role; // Role - USER, ADMIN

}
