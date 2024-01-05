package com.korea.project2_team4.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class DmPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne //(mappedBy = "myDm", cascade = CascadeType.REMOVE)
    private Profile me; // 여기서 receivedmessage불러와서 출력하면 되나??

//    @OneToOne
//    private Profile partner;

    private LocalDateTime createDate;
}
