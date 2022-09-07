/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.recetapp.servicios;

import com.egg.recetapp.entidades.Photo;
import com.egg.recetapp.entidades.Recipe;
import com.egg.recetapp.entidades.Users;
import com.egg.recetapp.enumeracion.Origin;
import com.egg.recetapp.enumeracion.Type;
import com.egg.recetapp.excepciones.ErrorServicio;
import com.egg.recetapp.repositorios.PhotoRepository;
import com.egg.recetapp.repositorios.RecipeRepository;
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

public class RecipeService {

    @Autowired
    private ScoreService cs;

    @Autowired
    private RecipeRepository rr;
    
    @Autowired
    private PhotoRepository fr;

    @Autowired
    private PhotoService fs;

    @Transactional
    public Recipe crearReceta(String nombre, String cuerpoReceta, Origin origin, Integer dificultad, List<MultipartFile> foto, Users users, Type type, Integer tiempoCoccion) throws ErrorServicio {
        try {
            validarReceta(nombre, cuerpoReceta, origin, dificultad, type, tiempoCoccion);
            Recipe r1 = new Recipe();

            r1.setName(nombre);
            r1.setBody(cuerpoReceta);

            r1.setOrigin(origin);
            r1.setDifficulty(dificultad);
            r1.setUsers(users);
            r1.setCookingTime(tiempoCoccion);
            r1.setType(type);
            List fotos = new ArrayList();
            for (MultipartFile f : foto) {
                Photo fo = fs.savePhoto(f);
                fotos.add(fo);
            }
            r1.setPhoto(fotos);
            return rr.save(r1);
        } catch (ErrorServicio a) {
            throw new ErrorServicio(a.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Transactional
    public Recipe modificarReceta(Long id, String nombre, String cuerpoReceta, Origin origin, Integer dificultad, List<MultipartFile> foto, Type type, Users u, Integer tiempoCoccion) throws ErrorServicio, IOException {
        validarReceta(nombre, cuerpoReceta, origin, dificultad, type, tiempoCoccion);
        Optional<Recipe> respuesta = rr.findById(id);
        if (respuesta.isPresent()) {
            Recipe recipe = respuesta.get();
            recipe.setBody(cuerpoReceta);
            recipe.setDifficulty(dificultad);
            recipe.setName(nombre);
            recipe.setOrigin(origin);
            recipe.setType(type);
            recipe.setCookingTime(tiempoCoccion);
            List<Photo> photos = recipe.getPhoto();
            for (MultipartFile f : foto) {
                Photo fo = fs.savePhoto(f);
                photos.add(fo);
            }
            recipe.setPhoto(photos);
            rr.save(recipe);
            return recipe;
        } else {
            throw new ErrorServicio("No se ha podido modificar la receta.");

        }
    }

    @Transactional
    public void eliminarReceta(Long idusuario, Long id) throws Exception {
        Optional<Recipe> respuesta = rr.findById(id);
        if (respuesta.isPresent()) {
            Recipe recipe = respuesta.get();
            if (recipe.getUsers().getId() == idusuario) {
                if (cs.listaComentariosPorRecetaId(recipe.getId()) != null) {
                    cs.eliminarComentarios(recipe.getId());
                }
                rr.delete(recipe);
            } else {
                throw new Exception("No se ha podido eliminar, debe ser el usuario dueño de la recipe");
            }
        } else {
            throw new Exception("no se encontro la receta que desea eliminar");
        }
    }

    public Recipe buscarRecetaPorId(Long id) throws ErrorServicio {
        if (rr.existsById(id)) {
            return rr.findById(id).get();
        } else {
            throw new ErrorServicio("no se ha podido encontrar la receta");
        }
    }

    public List<Recipe> listarTodasLasRecetas() {
        return rr.findAll();
    }

    public List<Recipe> listaPorNombre(String nombre) {
        List<Recipe> r = rr.BuscarPorNombre(nombre);
        return r;
    }


//    @Transactional
//    public void guardarCalificacion(Integer valor, Long idReceta, Long idUsuario,String comentario) throws ErrorServicio{
//        cs.guardarCalificar(valor, idReceta, idUsuario, comentario);
//    }

    public void validarReceta(String nombre, String cuerpoReceta, Origin origin, Integer dificultad, Type type, Integer tiempoCoccion) throws ErrorServicio {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ErrorServicio("El nombre no puede estar vacío");

        }
        if (cuerpoReceta == null || cuerpoReceta.trim().isEmpty()) {
            throw new ErrorServicio("Debe ingresar los ingredientes y procedimientos de la receta");

        }
        if (origin == null) {
            throw new ErrorServicio("Debe seleccionar un origin");
        }
////        if (usuario == null) {
////            throw new ErrorServicio("Users incorrecto");
//        }
        if (type == null) {
            throw new ErrorServicio("Debe seleccionar un type");
        }

        if (dificultad == 0) {
            throw new ErrorServicio("Debe ingresar la dificultad");
        }
        if (tiempoCoccion == 0 || tiempoCoccion.toString().isEmpty()) {
            throw new ErrorServicio("Debe ingresar el tiempo de cocción");
        }

    }

    public List<Recipe> listaPorUsuario(Long id) {
        List<Recipe> r = rr.BuscarRecetaPorUsuario(id);
        return r;

    }
   
}
