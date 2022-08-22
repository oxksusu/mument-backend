package com.example.mumentbackend.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/*

@Entity 어노테이션 붙이면 알아서 JPA 연동됨

 */
@Getter
@NoArgsConstructor
@Table(name = "account")
@Entity
public class Account { // 예약어가 이미 존재하므로 users로 바꾸어 지정해야함

    @Id
    @Column(name = "account_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY) // db의 id 값이 자동으로 생성되도록 한 경우 꼭 붙여줘야 하는 어노테이션
    private Long id;

    @Column
    private String nickname;

    @Column(nullable = false)
    private String email;

    /* 회원가입 과정에서는 프로필 사진을 나중에 등록할 수 있게 nullable */
    @Column
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority; // Authority 이 열거형이었으므로

    @Enumerated(EnumType.STRING)
    @Column
    private SocialLoginType socialLoginType;


    // 소셜 로그인 종류에 따라 분리하여 처리하기 위해서 socialLoginType 컬럼 추가했음
    @Builder
    public Account(SocialLoginType socialLoginType, String email, String nickname, String picture, Authority authority) {
        this.socialLoginType = socialLoginType;
        this.email = email;
        this.nickname = nickname;
        this.picture = picture;
        this.authority = authority;
    }

}
