package com.egg.recetapp.controllers;

import com.egg.recetapp.entities.Recipe;
import com.egg.recetapp.entities.Users;
import com.egg.recetapp.enumerations.Origin;
import com.egg.recetapp.enumerations.Type;
import com.egg.recetapp.exceptions.ServiceError;
import com.egg.recetapp.service.ScoreService;
import com.egg.recetapp.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/recipe")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;
    @Autowired
    private ScoreService scoreService;


    @PreAuthorize("hasAnyRole('USUARIO')")
    @GetMapping("/uploadRecipe")
    public String saveRecipe(ModelMap model) {
        model.addAttribute("origin", Origin.values());
        model.addAttribute("type", Type.values());

        return "uploadRecipe";
    }

    @PostMapping("/uploadRecipe")
    public String saveRecipe(@RequestParam String name,
                             @RequestParam String body,
                             @RequestParam Origin origin,
                             @RequestParam(defaultValue = "0") Integer difficulty,
                             @RequestParam List<MultipartFile> photo,
                             @RequestParam Type type,
                             @RequestParam(defaultValue = "0") Integer cookingTime,
                             HttpSession session,
                             ModelMap modelMap) throws ServiceError, IOException {
        Users loggedUser = (Users) session.getAttribute("usuariosession");
        try {
            Recipe createdRecipe = recipeService.saveRecipe(name, body, origin, difficulty, photo, loggedUser, type, cookingTime);
            return "redirect:/recipe/" + createdRecipe.getId().toString();
        } catch (ServiceError e) {
            modelMap.put("error", e.getMessage());
            modelMap.addAttribute("origin", Origin.values());
            modelMap.addAttribute("type", Type.values());
            return "uploadRecipe";
        }

    }

    @GetMapping("/modifyRecipe/{id}")
    public String modifyRecipe(@PathVariable Long id,
                               ModelMap model,
                               HttpSession session) throws ServiceError {
        Users loggedUser = (Users) session.getAttribute("usuariosession");
        if (recipeService.findRecipeById(id).getUsers().getId().equals(loggedUser.getId())) {
            model.addAttribute("recipe", recipeService.findRecipeById(id));
            model.addAttribute("origin", Origin.values());
            model.addAttribute("type", Type.values());
            return "modifyRecipe.html";
        } else {
            return "redirect:/recipe/" + id.toString();
        }
    }

    @PostMapping("/modify/{id}")
    public String modifyRecipe(ModelMap model,
                               @PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam String body,
                               @RequestParam Origin origin,
                               @RequestParam Integer difficulty,
                               @RequestParam List<MultipartFile> photo,
                               @RequestParam Type type,
                               @RequestParam Integer cookingTime,
                               HttpSession session
    ) throws ServiceError {
        Users loggedUser = (Users) session.getAttribute("usuariosession");
        try {
            if (recipeService.findRecipeById(id).getUsers().getId().equals(loggedUser.getId())) {
                recipeService.modify(id, name, body, origin, difficulty, photo, type, loggedUser, cookingTime);
                model.put("exito", "La receta se ha modificado con exito.");
            }

        } catch (Exception e) {
            model.put("error", "Hubo un error al modificar la receta.");

        }
        return "redirect:/recipe/" + id.toString();
    }

    @GetMapping("/deleteRecipe/{id}")
    public String deleteRecipe(ModelMap model, @PathVariable Long id, HttpSession session) throws Exception {
        Users loggedUser = (Users) session.getAttribute("usuariosession");
        if (recipeService.findRecipeById(id).getUsers().getId().equals(loggedUser.getId())) {
            model.addAttribute("recet", recipeService.findRecipeById(id));
            recipeService.delete(loggedUser.getId(), id);

            return "redirect:/recipe/";
        } else {
            throw new ServiceError("No se pudo eliminar la receta");
        }


    }

    @PreAuthorize("hasAnyRole('USUARIO')")
    @GetMapping("/{id}")
    public String getRecipeById(ModelMap model, @PathVariable Long id, HttpSession session) throws ServiceError {
        Recipe recipe = recipeService.findRecipeById(id);
        Users loggedUser = (Users) session.getAttribute("usuariosession");
        model.addAttribute("loggedUserId", loggedUser.getId());
        model.addAttribute("recipe", recipe);
        model.addAttribute("scores", scoreService.findAllByRecipeId(id));
        model.addAttribute("photo", recipe.getPhoto());
        return "/recipe";
    }

    @GetMapping
    public String getAllRecipes(ModelMap modelMap) {
        modelMap.put("all","Algunas coincidencias con lo que buscas");
        modelMap.addAttribute("recipeName", recipeService.findAllRecipes());
        return "recipeList";
    }

    @PostMapping("/getRecipesByName")
    public String getRecipesByName (@RequestParam String name, ModelMap model) {
        model.addAttribute("recipeName", recipeService.findByName(name));
        return "recipeList";
    }


    @GetMapping("/getByUser")
    public String getByUser(ModelMap modelMap, HttpSession session) throws ServiceError {

        Users loggedUser = (Users) session.getAttribute("usuariosession");
        
        modelMap.put("ofUser","Mis recetas");
        modelMap.addAttribute("recipeName", recipeService.findByUser(loggedUser.getId()));
        return "recipeList";
    }



}
