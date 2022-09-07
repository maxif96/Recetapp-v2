package com.egg.recetapp.controladores;

import com.egg.recetapp.enumeracion.Categoria;
import com.egg.recetapp.excepciones.ErrorServicio;
import com.egg.recetapp.servicios.UsuarioServicio;
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
public class PortalControlador {

    @Autowired
    private UsuarioServicio us;

    @GetMapping("/ingreso")
    public String ingreso() {
        return "ingreso";
    }

    @GetMapping("/registro")
    public String registro(ModelMap modelo) {
        modelo.addAttribute("categoria", Categoria.values());
        return "registro";
    }

    //metodo para crear el usuario
    @PostMapping("/registro")//cuando entra a la barra del servidor ejecuta el metodo
    public String guardarUsuario(ModelMap modelo,
                                 @RequestParam String nombre,
                                 @RequestParam String apodo,
                                 @RequestParam Categoria categoria,
                                 @RequestParam String mail,
                                 @RequestParam String contrasena,
                                 @RequestParam MultipartFile foto) throws Exception {
        try {
            us.crearUsuario(nombre, apodo, categoria, mail, contrasena, foto);
            return "ingreso";
        } catch (Exception e) {
            modelo.addAttribute("categoria", Categoria.values());
            modelo.put("error", e.getMessage());
            return "registro";
        }
        //una vez que se registre que le devuelva la vista para loguearse
    }

    @GetMapping("/cambiarcontrasena")
    public String CambiarContraseña() {
        return "cambiarContrasena.html";
    }

    @PostMapping("/recibirMail")
    public String mailVerificacion(@RequestParam String mail, HttpSession session, ModelMap modelo) throws ErrorServicio {
        int codigoDeRecuperacion = us.enviar(mail);
        session.setAttribute("codigoDeRecuperacion", codigoDeRecuperacion);
        modelo.put("email", "Email de recuperación enviado!");

        return "cambiarContrasena";
    }

    @PostMapping("/cambiarcontrasena")
    public String cambiarContraseña(HttpSession session, @RequestParam Integer codigoIngresado, @RequestParam String contrasena, @RequestParam String mail) throws ErrorServicio, IOException {
        if (codigoIngresado == (int) session.getAttribute("codigoDeRecuperacion")) {//esta linea de codigo tira null
            us.cambiarContraseña(codigoIngresado, contrasena, mail);
        }
        return "/ingreso";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }


}
