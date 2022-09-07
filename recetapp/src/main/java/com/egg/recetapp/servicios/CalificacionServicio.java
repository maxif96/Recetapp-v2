/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.recetapp.servicios;

import com.egg.recetapp.entidades.Calificacion;
import com.egg.recetapp.entidades.Receta;
import com.egg.recetapp.entidades.Usuario;
import com.egg.recetapp.excepciones.ErrorServicio;
import com.egg.recetapp.repositorios.CalificacionRepositorio;
import com.egg.recetapp.repositorios.RecetaRepositorio;
import com.egg.recetapp.repositorios.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CalificacionServicio {

    //Usamos repos calificacion
    @Autowired
    private CalificacionRepositorio cr;
    //Usamos repos receta
    @Autowired
    private RecetaRepositorio recetaRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Transactional
    public void guardarCalificar(Integer valor, Long idReceta, Usuario usuario, String comentario) throws ErrorServicio {

        Optional<Receta> respuesta = recetaRepositorio.findById(idReceta);
        if (respuesta.isPresent()) {
            Receta receta = respuesta.get();
            if (receta.getUsuario() == usuario) {
                throw new ErrorServicio("No puede auto calificarse");
            } else {
                //creo entidad de calificacion vacia
                Calificacion calificacion = new Calificacion();
                //esto va a guardar la hora y fecha en que se guardo la calificacion
                calificacion.setFecha(new Date());
                calificacion.setValor(valor);
                calificacion.setReceta(receta);
                calificacion.setUsuario(usuario);
                calificacion.setComentario(comentario);

                cr.save(calificacion);
            }
        }
    }

    public List<Calificacion> listaComentariosPorRecetaId(Long id) {
        List<Calificacion> c = cr.buscarCalificacionesRecibidas(id);
        return c;
    }

    public void eliminarComentarios(Long recetaId) {
        cr.eliminarTodosPorRecetaId(recetaId);
    }
}

//***metodos de modificar***
//***metodos de borrar***

