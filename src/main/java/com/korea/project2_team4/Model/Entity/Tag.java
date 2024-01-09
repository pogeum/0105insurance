package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;

@Entity
@Getter
@Setter
public class Tag {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.REMOVE)
    private Set<TagMap> tagMaps;
}
