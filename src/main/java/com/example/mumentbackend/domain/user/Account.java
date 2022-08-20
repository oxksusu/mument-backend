package com.example.mumentbackend.domain.user;

import com.example.mumentbackend.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/*

@Entity 어노테이션 붙이면 알아서 JPA 연동됨

 */
@Getter
@NoArgsConstructor
@Entity
public class Users extends BaseTimeEntity { // 예약어가 이미 존재하므로 users로 바꾸어 지정해야함

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // db의 id 값이 자동으로 생성되도록 한 경우 꼭 붙여줘야 하는 어노테이션
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column
    private String picture;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // Role 이 열거형이었으므로

    @Column
    private String refreshToken;

    @Column
    private String provider; //OAuth2를 이용한 로그인 사용 시 어떤 플랫폼을 이용하는지

    @Column
    private String providerId; // OAuth2를 이용할 경우 아이디값

    @Builder
    public Users(String nickname, String email, String picture, String password, Role role) {
        this.nickname = nickname;
        this.email = email;
        this.picture = picture;
        this.password = password;
        this.role = role;
    }

    /* 리프레시 토큰 추가 */
    @Builder
    public Users(String nickname, String email, String picture, String password, Role role, String refreshToken) {
        this.nickname = nickname;
        this.email = email;
        this.picture = picture;
        this.password = password;
        this.role = role;
        this.refreshToken = refreshToken;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
