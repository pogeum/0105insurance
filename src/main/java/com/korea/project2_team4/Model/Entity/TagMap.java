package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class TagMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 포스트 정보
    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;

    // 태그 정보
    @ManyToOne
    @JoinColumn(name="tag_id")
    private Tag tag;
}
