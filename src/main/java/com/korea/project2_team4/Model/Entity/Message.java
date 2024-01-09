package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Profile author;

    @ManyToOne
    private Profile receiver;

    private String content;


    private LocalDateTime createDate;

}
