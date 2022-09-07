package com.egg.recetapp.controladores;

import com.egg.recetapp.entidades.Recipe;
import com.egg.recetapp.entidades.Users;
import com.egg.recetapp.enumeracion.Origin;
import com.egg.recetapp.enumeracion.Type;
import com.egg.recetapp.excepciones.ErrorServicio;
import com.egg.recetapp.servicios.ScoreService;
import com.egg.recetapp.servicios.RecipeService;
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
@RequestMapping("/receta")
public class RecipeController {

    @Autowired
    private RecipeService rs;
    @Autowired
    private ScoreService cs;


    //    CARGAR RECETA
    @PreAuthorize("hasAnyRole('USUARIO')")
    @GetMapping("/cargarReceta")
    public String cargarReceta(ModelMap Modelo) {
        Modelo.addAttribute("origen", Origin.values());
        Modelo.addAttribute("tipo", Type.values());

        return "cargarReceta";
    }

    @PostMapping("/cargarReceta")
    public String cargarReceta(@RequestParam String nombre,
                               @RequestParam String cuerpoReceta,
                               @RequestParam Origin origin,
                               @RequestParam(defaultValue = "0") Integer dificultad,
                               @RequestParam List<MultipartFile> foto,
                               @RequestParam Type type,
                               @RequestParam(defaultValue = "0") Integer tiempoCoccion,
                               HttpSession session,
                               ModelMap modelMap) throws ErrorServicio, IOException {
        Users usersLogueado = (Users) session.getAttribute("usuariosession");
        try {
            Recipe recipeCreada = rs.crearReceta(nombre, cuerpoReceta, origin, dificultad, foto, usersLogueado, type, tiempoCoccion);
            return "redirect:/receta/" + recipeCreada.getId().toString();
        } catch (ErrorServicio e) {
            modelMap.put("error", e.getMessage());
            modelMap.addAttribute("origin", Origin.values());
            modelMap.addAttribute("type", Type.values());
            return "cargarReceta";
        }

    }

    @GetMapping("/modificarReceta/{id}")
    public String modificarReceta(@PathVariable Long id,
                                  ModelMap modelo, HttpSession session) throws ErrorServicio {
        Users usersLogueado = (Users) session.getAttribute("usuariosession");
        if (rs.buscarRecetaPorId(id).getUsers().getId().equals(usersLogueado.getId())) {
            modelo.addAttribute("recet", rs.buscarRecetaPorId(id));
            modelo.addAttribute("origen", Origin.values());
            modelo.addAttribute("tipo", Type.values());
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
                                  @RequestParam Origin origin,
                                  @RequestParam Integer dificultad,
                                  @RequestParam List<MultipartFile> foto,
                                  @RequestParam Type type,
                                  @RequestParam Integer tiempoCoccion,
                                  HttpSession session
    ) throws ErrorServicio {
        Users usersLogueado = (Users) session.getAttribute("usuariosession");
        try {
            if (rs.buscarRecetaPorId(id).getUsers().getId().equals(usersLogueado.getId())) {
                rs.modificarReceta(id, nombre, cuerpoReceta, origin, dificultad, foto, type, usersLogueado, tiempoCoccion);
                modelo.put("exito", "la receta se ha modificado con exito");
            }

        } catch (Exception e) {
            modelo.put("error", "hubo un error al modificar la receta");

        }
        return "redirect:/receta/" + id.toString();
    }

    @GetMapping("/eliminarReceta/{id}")
    public String eliminarReceta(ModelMap modelo, @PathVariable Long id, HttpSession session) throws Exception {
        Users usersLogueado = (Users) session.getAttribute("usuariosession");
        if (rs.buscarRecetaPorId(id).getUsers().getId().equals(usersLogueado.getId())) {
            modelo.addAttribute("recet", rs.buscarRecetaPorId(id));
            rs.eliminarReceta(usersLogueado.getId(), id);

            return "redirect:/receta/";
        } else {
            throw new ErrorServicio("No se pudo eliminar la receta");
        }


    }

    @PreAuthorize("hasAnyRole('USUARIO')")
    @GetMapping("/{id}")
    public String mostrarRecetaPorId(ModelMap modelo, @PathVariable Long id, HttpSession session) throws ErrorServicio {
        Recipe recipe = rs.buscarRecetaPorId(id);
        Users usersLogueado = (Users) session.getAttribute("usuariosession");
        modelo.addAttribute("usuarioLogueadoId", usersLogueado.getId());
        modelo.addAttribute("recipe", recipe);
        modelo.addAttribute("calificaciones", cs.listaComentariosPorRecetaId(id));
        modelo.addAttribute("foto", recipe.getPhoto());
        return "/recipe";
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

        Users usersLogueado = (Users) session.getAttribute("usuariosession");
        
        modelMap.put("delUsuario","Mis recetas");
        modelMap.addAttribute("nombreReceta", rs.listaPorUsuario(usersLogueado.getId()));
        return "listaRecetas";
    }

//    @PostMapping("/{id}")
//    public String calificar(@RequestParam Integer valor, @PathVariable Long id, @RequestParam Long idUsuario, @RequestParam String comentario) throws ErrorServicio {
//        rs.guardarCalificacion(valor, id, idUsuario, comentario);
//        return "receta";
//    }


}
