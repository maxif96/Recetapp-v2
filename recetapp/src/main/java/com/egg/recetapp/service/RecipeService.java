/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.recetapp.service;

import com.egg.recetapp.entities.Photo;
import com.egg.recetapp.entities.Recipe;
import com.egg.recetapp.entities.Users;
import com.egg.recetapp.enumerations.Origin;
import com.egg.recetapp.enumerations.Type;
import com.egg.recetapp.exceptions.ServiceError;
import com.egg.recetapp.repositories.PhotoRepository;
import com.egg.recetapp.repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fabi
 */
@Service

public class RecipeService {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private PhotoService photoService;


    @Transactional
    public Recipe saveRecipe(String name, String body, Origin origin, Integer difficult, List<MultipartFile> photo, Users user, Type type, Integer cookingTime) throws ServiceError {
        try {
            validateRecipe(name, body, origin, difficult, type, cookingTime);
            Recipe recipe = new Recipe();

            recipe.setName(name);
            recipe.setBody(body);

            recipe.setOrigin(origin);
            recipe.setDifficulty(difficult);
            recipe.setUsers(user);
            recipe.setCookingTime(cookingTime);
            recipe.setType(type);
            List<Photo> photos = new ArrayList<>();
            for (MultipartFile f : photo) {
                Photo photo1 = photoService.savePhoto(f);
                photos.add(photo1);
            }
            recipe.setPhoto(photos);
            return recipeRepository.save(recipe);
        } catch (ServiceError a) {
            throw new ServiceError(a.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Transactional
    public Recipe modify(Long id, String name, String body, Origin origin, Integer difficulty, List<MultipartFile> photo, Type type, Users loggedUser, Integer cookingTime) throws ServiceError, IOException {
        validateRecipe(name, body, origin, difficulty, type, cookingTime);
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Receta no encontrada."));
            recipe.setBody(body);
            recipe.setDifficulty(difficulty);
            recipe.setName(name);
            recipe.setOrigin(origin);
            recipe.setType(type);
            recipe.setCookingTime(cookingTime);
            List<Photo> photos = recipe.getPhoto();
            for (MultipartFile f : photo) {
                Photo fo = photoService.savePhoto(f);
                photos.add(fo);
            }
            recipe.setPhoto(photos);
            recipeRepository.save(recipe);
            return recipe;
    }

    @Transactional
    public void delete(Long idusuario, Long id) throws Exception {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Receta no encontrada."));
            if (recipe.getUsers().getId() == idusuario) {
                if (scoreService.findAllByRecipeId(recipe.getId()) != null) {
                    scoreService.deleteScores(recipe.getId());
                }
                recipeRepository.delete(recipe);
            } else {
                throw new Exception("No se ha podido eliminar. Debe ser el usuario dueño de la receta.");
            }
    }

    public Recipe findRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Receta no encontrada."));
    }

    public List<Recipe> findAllRecipes() {
        if (recipeRepository.count() < 1) throw  new EntityNotFoundException("Ninguna receta ha sido encontrada.");
        return recipeRepository.findAll();
    }

    public List<Recipe> findByName(String name) {
        if (recipeRepository.findAllByName(name).isEmpty()) throw new EntityNotFoundException("Ninguna receta ha sido encontrada.");
        return recipeRepository.findAllByName(name);
    }

    public void validateRecipe(String name, String body, Origin origin, Integer difficulty, Type type, Integer cookingTime) throws ServiceError {
        if (name == null || name.trim().isEmpty()) {
            throw new ServiceError("El nombre no puede estar vacío");

        }
        if (body == null || body.trim().isEmpty()) {
            throw new ServiceError("Debe ingresar los ingredientes y procedimientos de la receta");

        }
        if (origin == null) {
            throw new ServiceError("Debe seleccionar un origen");
        }
        if (type == null) {
            throw new ServiceError("Debe seleccionar un tipo");
        }
        if (difficulty == 0) {
            throw new ServiceError("Debe ingresar la difficulty");
        }
        if (cookingTime == 0 || cookingTime.toString().isEmpty()) {
            throw new ServiceError("Debe ingresar el tiempo de cocción");
        }

    }

    public List<Recipe> findByUser(Long id) {
        if (recipeRepository.findAllByUser(id).isEmpty()) throw new EntityNotFoundException("Ninguna receta ha sido encontrada.");
        return recipeRepository.findAllByUser(id);
    }
   
}
