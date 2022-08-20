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

    @Column
    private String picture;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority; // Authority 이 열거형이었으므로


    @Builder
    public Account(String email, String nickname, String picture, String password, Authority authority) {
        this.email = email;
        this.nickname = nickname;
        this.picture = picture;
        this.password = password;
        this.authority = authority;
    }

}
