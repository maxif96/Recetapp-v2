package com.egg.recetapp.entidades;


import java.util.Date;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import javax.persistence.Id;

import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Data
public class Calificacion {
    @Id
    //cadena de texto que se genera automaticamente y que no se va a repetir nunca
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
//    @Column(nullable= false)
    private Integer valor;
   
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
   

    private String comentario;
   
    @OneToOne
    private Receta receta;
    @OneToOne
    private Usuario usuario;

    public Calificacion() {
       
       
    }

}



