package com.egg.recetapp.controladores;

import com.egg.recetapp.enumeracion.Category;
import com.egg.recetapp.excepciones.ErrorServicio;
import com.egg.recetapp.servicios.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("")
public class PortalController {

    @Autowired
    private UserService userService;

    @GetMapping("/ingreso")
    public String ingreso() {
        return "ingreso";
    }

    @GetMapping("/register")
    public String register(ModelMap model) {
        model.addAttribute("categoria", Category.values());
        return "register";
    }

    @PostMapping("/register")
    public String save (ModelMap model,
                       @RequestParam String name,
                       @RequestParam String nickName,
                       @RequestParam Category category,
                       @RequestParam String mail,
                       @RequestParam String password,
                       @RequestParam MultipartFile photo) throws Exception {
        try {
            userService.createUser(name, nickName, category, mail, password, photo);
            return "ingreso";
        } catch (Exception e) {
            model.addAttribute("category", Category.values());
            model.put("error", e.getMessage());
            return "register";
        }

    }

    @GetMapping("/cambiarcontrasena")
    public String CambiarContraseña() {
        return "cambiarContrasena.html";
    }

    @PostMapping("/recibirMail")
    public String mailVerificacion(@RequestParam String mail, HttpSession session, ModelMap modelo) throws ErrorServicio {
        int codigoDeRecuperacion = userService.enviar(mail);
        session.setAttribute("codigoDeRecuperacion", codigoDeRecuperacion);
        modelo.put("email", "Email de recuperación enviado!");

        return "cambiarContrasena";
    }

    @PostMapping("/cambiarcontrasena")
    public String cambiarContraseña(HttpSession session, @RequestParam Integer codigoIngresado, @RequestParam String contrasena, @RequestParam String mail) throws ErrorServicio, IOException {
        if (codigoIngresado == (int) session.getAttribute("codigoDeRecuperacion")) {//esta linea de codigo tira null
            userService.cambiarContraseña(codigoIngresado, contrasena, mail);
        }
        return "/ingreso";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }


}
