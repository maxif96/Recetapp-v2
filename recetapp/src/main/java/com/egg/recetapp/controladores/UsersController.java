package com.egg.recetapp.controladores;

import com.egg.recetapp.entidades.Users;
import com.egg.recetapp.enumeracion.Category;
import com.egg.recetapp.excepciones.ErrorServicio;
import com.egg.recetapp.servicios.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UserService userService;


    @GetMapping("/modificar-usuario/{id}")
    public String modificarUsuario(@PathVariable Long id, ModelMap modelo, HttpSession session) throws ErrorServicio {
        Users usersLogueado = (Users) session.getAttribute("usuariosession");
        if (userService.buscarUsuarioPorId(id).getId().equals(usersLogueado.getId())) {
            modelo.addAttribute("usuario", userService.buscarUsuarioPorId(id));
            modelo.addAttribute("categoria", Category.values());
           
            return "modificarUsuario";
        } else {
            return "redirect:/index";
        }
    }

    @PostMapping("/{id}")
    public String modificarUsuario(@PathVariable Long id, @RequestParam String nombre, @RequestParam String apodo, @RequestParam Category category, @RequestParam String mail, @RequestParam String contrasena, @RequestParam MultipartFile foto, HttpSession session) throws ErrorServicio, IOException {
        Users usersLogueado = (Users) session.getAttribute("usuariosession");
        if (userService.buscarUsuarioPorId(id).getId().equals(usersLogueado.getId())) {
            userService.modificarUsuario(id, nombre, apodo, category, mail, contrasena, foto);
            session.setAttribute("usuariosession", userService.buscarUsuarioPorId(id));
        } else {
            throw new ErrorServicio("No puedes modificar este perfil");
        }

        return "redirect:/usuario/miperfil";
    }


    @GetMapping("/buscar-usuario/nombre")
    public String buscarUsuarioPorNombre() throws Exception {

        return "ingreso";
    }

    @PostMapping("/buscar-usuario/nombre")
    public String buscarUsuarioPorNombre(ModelMap model, @RequestParam String nombre) throws Exception {
        model.addAttribute("exito", userService.buscarUsuarioPorNombre(nombre));
        return "ingreso";
    }


    @GetMapping("/dar-de-baja/{id}")
    public String darDeBajaUsuarioPorId(@PathVariable Long id, HttpSession session) throws Exception {
        userService.darDeBaja(id);
        session.setAttribute("usuariosession", userService.buscarUsuarioPorId(id));

        return "redirect:/logout";
    }

    @GetMapping("/dar-de-alta/{id}")
    public String darDeAltaUsuarioPorId(@PathVariable Long id, HttpSession session) throws Exception {
        userService.darDeAlta(id);
        session.setAttribute("usuariosession", userService.buscarUsuarioPorId(id));

        return "redirect:/index";
    }

    @GetMapping("/buscar-usuario/categoria")
    public String buscarUsuarioPorCategiria() throws Exception {

        return "ingreso";
    }

    @PostMapping("/buscar-usuario/category")
    public String buscarUsuarioPorCategoria(ModelMap model, @RequestParam Category category) { //Completar luego
        model.addAttribute("exito", userService.buscarUsuariosPorCategoria(category));
        return "ingreso";
    }


    @GetMapping("/miperfil")
    public String perfilUsuario(ModelMap model, HttpSession session) {
        Users usersLogueado = (Users) session.getAttribute("usuariosession");
        if (usersLogueado == null) {
            return "redirect:/ingreso";
        } else {
            model.addAttribute("usuario", usersLogueado);
            return "miPerfil";
        }

    }

    @GetMapping("/foto/{id}")
    public ResponseEntity<byte[]> fotoUsuario(HttpSession session) throws ErrorServicio {
        Users users = (Users) session.getAttribute("usuariosession");

        if (users.getPhoto() == null) {
            throw new ErrorServicio("El Users no tiene foto asignada");
        }
        byte[] foto = users.getPhoto().getContent();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(foto, headers, HttpStatus.OK);

    }

    //que traiga la lista de recetas del usuario


}
