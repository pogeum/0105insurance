package com.korea.project2_team4.Model.Entity;

import com.korea.project2_team4.Config.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class  Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 연동 프로필
    @OneToOne(mappedBy = "member", cascade = CascadeType.REMOVE)
    private Profile profile;

    // 아이디
    @Column(unique = true, columnDefinition = "TEXT")
    private String userName;

    // 패스워드
    private String password;

    // 이메일(중복 불가)
    @Column(unique = true)
    private String email;

    // 사용자 실명
    @Column(length = 50)
    private String realName;

    // 사용자 닉네임
    @Column(length = 50)
    private String nickName;

    // 사용자 핸드폰 번호
    private String phoneNum;

    // 회원가입 일시
    private LocalDateTime createDate;


    // sns 연동 로그인에 사용되는 provider 종류(GOOGLE, NAVER, KAKAO)
    private String provider;

    // sns 연동 로그인에 사용되는 provider ID
    private String providerId;

    @OneToMany(mappedBy = "follower")
    private List<Follow> followers;

    @OneToMany(mappedBy = "following")
    private List<Follow> followings;

    private String role;

}