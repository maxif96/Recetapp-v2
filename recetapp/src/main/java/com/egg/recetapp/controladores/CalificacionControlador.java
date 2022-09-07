/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.recetapp.controladores;

import com.egg.recetapp.entidades.Usuario;
import com.egg.recetapp.excepciones.ErrorServicio;
import com.egg.recetapp.servicios.CalificacionServicio;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Fabi
 */
 @PreAuthorize("hasAnyRole('USUARIO')")
@Controller
@RequestMapping("/calificacion")
public class CalificacionControlador {

     @Autowired
    private CalificacionServicio cs;
    @PostMapping("/{id}")
    public String calificar(@RequestParam String puntuacion, @PathVariable Long id,HttpSession session, @RequestParam String comentario) throws ErrorServicio {
        Integer puntuacionn = Integer.parseInt (puntuacion);

       Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");
        cs.guardarCalificar(puntuacionn, id, usuarioLogueado, comentario);
        return "redirect:/receta/" + id.toString();
    }
}
    

