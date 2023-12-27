package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="post_id", nullable = false)
    private Post post;

    @ManyToOne
    private Profile author;

    private String content;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @ManyToMany
    private Set<Member> likeMembers;

}
