package com.egg.recetapp.entities;


import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "scores")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer score;
   
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String comment;
   
    @OneToOne
    private Recipe recipe;
    @OneToOne
    private Users user;

}



