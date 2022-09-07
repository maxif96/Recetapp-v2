package com.egg.recetapp.entidades;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import lombok.Data;


@Entity
@Data
public class Foto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mime;

    @Lob @Basic (fetch=FetchType.EAGER) //esto es para que traiga la foto instantaneamente sin tener que pedirla
    private byte[]contenido;
}
