/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.recetapp.service;

import com.egg.recetapp.entities.Recipe;
import com.egg.recetapp.entities.Score;
import com.egg.recetapp.entities.Users;
import com.egg.recetapp.exceptions.ServiceError;
import com.egg.recetapp.repositories.RecipeRepository;
import com.egg.recetapp.repositories.ScoreRepository;
import com.egg.recetapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Date;
import java.util.List;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void saveScore(Integer score, Long idReceta, Users users, String comment) throws ServiceError {

        Recipe recipe = recipeRepository.findById(idReceta).orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));
        if (recipe.getUsers() == users) throw new ServiceError("No puedes auto-calificarte.");

        Score scoreToSave = new Score();
        scoreToSave.setDate(new Date());
        scoreToSave.setScore(score);
        scoreToSave.setRecipe(recipe);
        scoreToSave.setUser(users);
        scoreToSave.setComment(comment);

        scoreRepository.save(scoreToSave);
    }


    public List<Score> findAllByRecipeId(Long id) {
        List<Score> score = scoreRepository.findAllByRecipeId(id);
        return score;
    }

    public void deleteScores(Long recetaId) {
        scoreRepository.deleteAllByRecipeId(recetaId);
    }
}
