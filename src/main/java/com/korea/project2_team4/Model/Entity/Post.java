package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TagMap> tagMaps;

    @Column(columnDefinition = "VARCHAR(100)")
    private String category;

    @ManyToOne
    private Profile author;

    private String title;

    private String content;


    @OneToMany(mappedBy = "postImages", cascade = CascadeType.REMOVE )
    private List<Image> postImages;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE )
    private List<Comment> comments;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    @ManyToMany
    private Set<Member> likeMembers;



    public List<Tag> getTagList() {
        return this.tagMaps.stream()
                .map(TagMap::getTag) // 값 추출
                .collect(Collectors.toList()); // 리스트로 변환
    }

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;

}
