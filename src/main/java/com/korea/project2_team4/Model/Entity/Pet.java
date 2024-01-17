package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Profile owner;

    private String name;
    private String content;

    @OneToOne(mappedBy = "petImage", cascade = CascadeType.REMOVE)
    private Image petImage;


    @ManyToMany
    private Set<Profile> likes;

}
