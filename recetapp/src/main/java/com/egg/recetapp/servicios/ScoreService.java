/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.recetapp.servicios;

import com.egg.recetapp.entidades.Score;
import com.egg.recetapp.entidades.Recipe;
import com.egg.recetapp.entidades.Users;
import com.egg.recetapp.excepciones.ErrorServicio;
import com.egg.recetapp.repositorios.ScoreRepository;
import com.egg.recetapp.repositorios.RecipeRepository;
import com.egg.recetapp.repositorios.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ScoreService {

    //Usamos repos calificacion
    @Autowired
    private ScoreRepository cr;
    //Usamos repos receta
    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void guardarCalificar(Integer valor, Long idReceta, Users users, String comentario) throws ErrorServicio {

        Optional<Recipe> respuesta = recipeRepository.findById(idReceta);
        if (respuesta.isPresent()) {
            Recipe recipe = respuesta.get();
            if (recipe.getUsers() == users) {
                throw new ErrorServicio("No puede auto calificarse");
            } else {
                //creo entidad de score vacia
                Score score = new Score();
                //esto va a guardar la hora y fecha en que se guardo la score
                score.setDate(new Date());
                score.setScore(valor);
                score.setRecipe(recipe);
                score.setUser(users);
                score.setComment(comentario);

                cr.save(score);
            }
        }
    }

    public List<Score> listaComentariosPorRecetaId(Long id) {
        List<Score> c = cr.buscarCalificacionesRecibidas(id);
        return c;
    }

    public void eliminarComentarios(Long recetaId) {
        cr.eliminarTodosPorRecetaId(recetaId);
    }
}

//***metodos de modificar***
//***metodos de borrar***

