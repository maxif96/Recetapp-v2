package com.egg.recetapp.entidades;

import com.egg.recetapp.enumeracion.Categoria;
import com.egg.recetapp.enumeracion.Rol;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Data;


@Data
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable= false)
    private String nombre;

    private String apodo;
    
    @Enumerated(EnumType.STRING)
     private Categoria categoria;
    
    @Enumerated(EnumType.STRING)
    private Rol rol;
    
//el user para el login es el e-mail
    @Column(unique = true)
    private String mail;

    @Column(nullable= false)
    private String contrasena;
    
    @OneToOne
    private Foto foto;
    
//    no va
//    @OneToMany
//    private List<Receta> receta;
    
   private Boolean estaActivo; 

}
