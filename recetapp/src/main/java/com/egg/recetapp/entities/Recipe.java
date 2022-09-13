package com.egg.recetapp.entities;

import com.egg.recetapp.enumerations.Origin;
import com.egg.recetapp.enumerations.Type;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable= false)
    private String name;
    @Column(nullable= false,length=8000)
    private String body;

    @Enumerated(EnumType.STRING)
    private Origin origin;
    
    @Column(nullable= false)
    private Integer difficulty;
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<Photo> photo;
    
    @OneToOne
    private Users users;
    
    @Enumerated(EnumType.STRING)
    private Type type;

    private Integer cookingTime;

}
