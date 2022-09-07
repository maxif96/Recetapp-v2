package com.egg.recetapp.entidades;

import com.egg.recetapp.enumeracion.Origen;
import com.egg.recetapp.enumeracion.Tipo;
import java.util.List;
import javafx.scene.text.Text;
import javax.persistence.*;
import static javax.swing.text.StyleConstants.Size;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
public class Receta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable= false)
    private String nombre;
    @Column(nullable= false,length=8000)
    private String cuerpoReceta;

    @Enumerated(EnumType.STRING)
    private Origen origen;
    
    @Column(nullable= false)
    private Integer dificultad;
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<Foto> foto;
    
    @OneToOne
    private Usuario usuario;
    
    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    private Integer tiempoCoccion;

}
