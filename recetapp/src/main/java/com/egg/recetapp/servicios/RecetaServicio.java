/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.recetapp.servicios;

import com.egg.recetapp.entidades.Foto;
import com.egg.recetapp.entidades.Receta;
import com.egg.recetapp.entidades.Usuario;
import com.egg.recetapp.enumeracion.Origen;
import com.egg.recetapp.enumeracion.Tipo;
import com.egg.recetapp.excepciones.ErrorServicio;
import com.egg.recetapp.repositorios.FotoRepositorio;
import com.egg.recetapp.repositorios.RecetaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Fabi
 */
@Service

public class RecetaServicio {

    @Autowired
    private CalificacionServicio cs;

    @Autowired
    private RecetaRepositorio rr;
    
    @Autowired
    private FotoRepositorio fr;

    @Autowired
    private FotoServicio fs;

    @Transactional
    public Receta crearReceta(String nombre, String cuerpoReceta, Origen origen, Integer dificultad, List<MultipartFile> foto, Usuario usuario, Tipo tipo, Integer tiempoCoccion) throws ErrorServicio {
        try {
            validarReceta(nombre, cuerpoReceta, origen, dificultad, tipo, tiempoCoccion);
            Receta r1 = new Receta();

            r1.setNombre(nombre);
            r1.setCuerpoReceta(cuerpoReceta);

            r1.setOrigen(origen);
            r1.setDificultad(dificultad);
            r1.setUsuario(usuario);
            r1.setTiempoCoccion(tiempoCoccion);
            r1.setTipo(tipo);
            List fotos = new ArrayList();
            for (MultipartFile f : foto) {
                Foto fo = fs.guardar(f);
                fotos.add(fo);
            }
            r1.setFoto(fotos);
            return rr.save(r1);
        } catch (ErrorServicio a) {
            throw new ErrorServicio(a.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Transactional
    public Receta modificarReceta(Long id, String nombre, String cuerpoReceta, Origen origen, Integer dificultad, List<MultipartFile> foto, Tipo tipo, Usuario u, Integer tiempoCoccion) throws ErrorServicio, IOException {
        validarReceta(nombre, cuerpoReceta, origen, dificultad, tipo, tiempoCoccion);
        Optional<Receta> respuesta = rr.findById(id);
        if (respuesta.isPresent()) {
            Receta receta = respuesta.get();
            receta.setCuerpoReceta(cuerpoReceta);
            receta.setDificultad(dificultad);
            receta.setNombre(nombre);
            receta.setOrigen(origen);
            receta.setTipo(tipo);
            receta.setTiempoCoccion(tiempoCoccion);
            List<Foto> fotos = receta.getFoto();
            for (MultipartFile f : foto) {
                Foto fo = fs.guardar(f);
                fotos.add(fo);
            }
            receta.setFoto(fotos);
            rr.save(receta);
            return receta;
        } else {
            throw new ErrorServicio("No se ha podido modificar la receta.");

        }
    }

    @Transactional
    public void eliminarReceta(Long idusuario, Long id) throws Exception {
        Optional<Receta> respuesta = rr.findById(id);
        if (respuesta.isPresent()) {
            Receta receta = respuesta.get();
            if (receta.getUsuario().getId() == idusuario) {
                if (cs.listaComentariosPorRecetaId(receta.getId()) != null) {
                    cs.eliminarComentarios(receta.getId());
                }
                rr.delete(receta);
            } else {
                throw new Exception("No se ha podido eliminar, debe ser el usuario dueño de la receta");
            }
        } else {
            throw new Exception("no se encontro la receta que desea eliminar");
        }
    }

    public Receta buscarRecetaPorId(Long id) throws ErrorServicio {
        if (rr.existsById(id)) {
            return rr.findById(id).get();
        } else {
            throw new ErrorServicio("no se ha podido encontrar la receta");
        }
    }

    public List<Receta> listarTodasLasRecetas() {
        return rr.findAll();
    }

    public List<Receta> listaPorNombre(String nombre) {
        List<Receta> r = rr.BuscarPorNombre(nombre);
        return r;
    }


//    @Transactional
//    public void guardarCalificacion(Integer valor, Long idReceta, Long idUsuario,String comentario) throws ErrorServicio{
//        cs.guardarCalificar(valor, idReceta, idUsuario, comentario);
//    }

    public void validarReceta(String nombre, String cuerpoReceta, Origen origen, Integer dificultad, Tipo tipo, Integer tiempoCoccion) throws ErrorServicio {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ErrorServicio("El nombre no puede estar vacío");

        }
        if (cuerpoReceta == null || cuerpoReceta.trim().isEmpty()) {
            throw new ErrorServicio("Debe ingresar los ingredientes y procedimientos de la receta");

        }
        if (origen == null) {
            throw new ErrorServicio("Debe seleccionar un origen");
        }
////        if (usuario == null) {
////            throw new ErrorServicio("Usuario incorrecto");
//        }
        if (tipo == null) {
            throw new ErrorServicio("Debe seleccionar un tipo");
        }

        if (dificultad == 0) {
            throw new ErrorServicio("Debe ingresar la dificultad");
        }
        if (tiempoCoccion == 0 || tiempoCoccion.toString().isEmpty()) {
            throw new ErrorServicio("Debe ingresar el tiempo de cocción");
        }

    }

    public List<Receta> listaPorUsuario(Long id) {
        List<Receta> r = rr.BuscarRecetaPorUsuario(id);
        return r;

    }
   
}
