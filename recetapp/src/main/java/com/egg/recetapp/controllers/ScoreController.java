/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.recetapp.controllers;

import com.egg.recetapp.entities.Users;
import com.egg.recetapp.exceptions.ServiceError;
import com.egg.recetapp.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * @author Fabi
 */
@PreAuthorize("hasAnyRole('USER')")
@Controller
@RequestMapping("/score")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @PostMapping("/{id}")
    public String score(@RequestParam String score, @PathVariable Long id, HttpSession session, @RequestParam String body) throws ServiceError {
        Integer scoreToShow = Integer.parseInt(score);

        Users loggedUser = (Users) session.getAttribute("usuariosession");
        scoreService.saveScore(scoreToShow, id, loggedUser, body);
        return "redirect:/recipe/" + id.toString();
    }
}
    

