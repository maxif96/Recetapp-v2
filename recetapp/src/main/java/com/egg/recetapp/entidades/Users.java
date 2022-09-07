package com.egg.recetapp.entidades;

import com.egg.recetapp.enumeracion.Category;
import com.egg.recetapp.enumeracion.Rol;
import lombok.*;

import javax.persistence.*;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable= false)
    private String name;

    private String nickName;
    
    @Enumerated(EnumType.STRING)
     private Category category;
    
    @Enumerated(EnumType.STRING)
    private Rol rol;
    
//el user para el login es el e-mail
    @Column(unique = true)
    private String mail;

    @Column(nullable= false)
    private String password;
    
    @OneToOne
    private Photo photo;

   private boolean isOn;

}
