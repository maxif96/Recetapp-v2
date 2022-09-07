package com.egg.recetapp.controladores;

import com.egg.recetapp.entidades.Foto;
import com.egg.recetapp.entidades.Receta;
import com.egg.recetapp.entidades.Usuario;
import com.egg.recetapp.enumeracion.Origen;
import com.egg.recetapp.enumeracion.Tipo;
import com.egg.recetapp.excepciones.ErrorServicio;
import com.egg.recetapp.servicios.CalificacionServicio;
import com.egg.recetapp.servicios.RecetaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/receta")
public class RecetaControlador {

    @Autowired
    private RecetaServicio rs;
    @Autowired
    private CalificacionServicio cs;


    //    CARGAR RECETA
    @PreAuthorize("hasAnyRole('USUARIO')")
    @GetMapping("/cargarReceta")
    public String cargarReceta(ModelMap Modelo) {
        Modelo.addAttribute("origen", Origen.values());
        Modelo.addAttribute("tipo", Tipo.values());

        return "cargarReceta";
    }

    @PostMapping("/cargarReceta")
    public String cargarReceta(@RequestParam String nombre,
                               @RequestParam String cuerpoReceta,
                               @RequestParam Origen origen,
                               @RequestParam(defaultValue = "0") Integer dificultad,
                               @RequestParam List<MultipartFile> foto,
                               @RequestParam Tipo tipo,
                               @RequestParam(defaultValue = "0") Integer tiempoCoccion,
                               HttpSession session,
                               ModelMap modelMap) throws ErrorServicio, IOException {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");
        try {
            Receta recetaCreada = rs.crearReceta(nombre, cuerpoReceta, origen, dificultad, foto, usuarioLogueado, tipo, tiempoCoccion);
            return "redirect:/receta/" + recetaCreada.getId().toString();
        } catch (ErrorServicio e) {
            modelMap.put("error", e.getMessage());
            modelMap.addAttribute("origen", Origen.values());
            modelMap.addAttribute("tipo", Tipo.values());
            return "cargarReceta";
        }

    }

    @GetMapping("/modificarReceta/{id}")
    public String modificarReceta(@PathVariable Long id,
                                  ModelMap modelo, HttpSession session) throws ErrorServicio {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");
        if (rs.buscarRecetaPorId(id).getUsuario().getId().equals(usuarioLogueado.getId())) {
            modelo.addAttribute("recet", rs.buscarRecetaPorId(id));
            modelo.addAttribute("origen", Origen.values());
            modelo.addAttribute("tipo", Tipo.values());
            return "modificarReceta.html";
        } else {
            return "redirect:/receta/" + id.toString();
        }
    }

    @PostMapping("/modificar/{id}") //lo que recibe es el id de la receta con el path variable
    public String modificarReceta(ModelMap modelo,
                                  @PathVariable Long id,
                                  @RequestParam String nombre,
                                  @RequestParam String cuerpoReceta,
                                  @RequestParam Origen origen,
                                  @RequestParam Integer dificultad,
                                  @RequestParam List<MultipartFile> foto,
                                  @RequestParam Tipo tipo,
                                  @RequestParam Integer tiempoCoccion,
                                  HttpSession session
    ) throws ErrorServicio {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");
        try {
            if (rs.buscarRecetaPorId(id).getUsuario().getId().equals(usuarioLogueado.getId())) {
                rs.modificarReceta(id, nombre, cuerpoReceta, origen, dificultad, foto, tipo, usuarioLogueado, tiempoCoccion);
                modelo.put("exito", "la receta se ha modificado con exito");
            }

        } catch (Exception e) {
            modelo.put("error", "hubo un error al modificar la receta");

        }
        return "redirect:/receta/" + id.toString();
    }

    @GetMapping("/eliminarReceta/{id}")
    public String eliminarReceta(ModelMap modelo, @PathVariable Long id, HttpSession session) throws Exception {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");
        if (rs.buscarRecetaPorId(id).getUsuario().getId().equals(usuarioLogueado.getId())) {
            modelo.addAttribute("recet", rs.buscarRecetaPorId(id));
            rs.eliminarReceta(usuarioLogueado.getId(), id);

            return "redirect:/receta/";
        } else {
            throw new ErrorServicio("No se pudo eliminar la receta");
        }


    }

    @PreAuthorize("hasAnyRole('USUARIO')")
    @GetMapping("/{id}")
    public String mostrarRecetaPorId(ModelMap modelo, @PathVariable Long id, HttpSession session) throws ErrorServicio {
        Receta receta = rs.buscarRecetaPorId(id);
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");
        modelo.addAttribute("usuarioLogueadoId", usuarioLogueado.getId());
        modelo.addAttribute("receta", receta);
        modelo.addAttribute("calificaciones", cs.listaComentariosPorRecetaId(id));
        modelo.addAttribute("foto",receta.getFoto());
        return "/receta";
    }

    //    LISTA RECETAS

    @GetMapping("")
    public String listarTodasLasRecetas(ModelMap modelMap) {
        modelMap.put("todas","Algunas coincidencias con lo que buscas");
        modelMap.addAttribute("nombreReceta", rs.listarTodasLasRecetas());
        return "listaRecetas";
    }

    @PostMapping("/listaRecetasPorNombre")
    public String listaRecetasPorNombre(@RequestParam String nombre, ModelMap modelo) {
        modelo.addAttribute("nombreReceta", rs.listaPorNombre(nombre));
        return "listaRecetas";
    }


    @GetMapping("/listarPorUsuario")
    public String listarPorUsuario(ModelMap modelMap, HttpSession session) throws ErrorServicio {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");
        
        modelMap.put("delUsuario","Mis recetas");
        modelMap.addAttribute("nombreReceta", rs.listaPorUsuario(usuarioLogueado.getId()));
        return "listaRecetas";
    }

//    @PostMapping("/{id}")
//    public String calificar(@RequestParam Integer valor, @PathVariable Long id, @RequestParam Long idUsuario, @RequestParam String comentario) throws ErrorServicio {
//        rs.guardarCalificacion(valor, id, idUsuario, comentario);
//        return "receta";
//    }


}
