package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class FollowingMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //팔로우 하는 사용자 ID
    @ManyToOne
    @JoinColumn(name = "follower_id")
    private Member follower;

    //팔로우 받는 사용자 ID
    @ManyToOne
    @JoinColumn(name = "following_id")
    private Member followee;

    //팔로우 한 시간
    private LocalDateTime followedAt;
}
}
