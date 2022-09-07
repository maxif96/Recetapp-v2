package com.egg.recetapp.controladores;

import com.egg.recetapp.entidades.Usuario;
import com.egg.recetapp.enumeracion.Categoria;
import com.egg.recetapp.excepciones.ErrorServicio;
import com.egg.recetapp.servicios.UsuarioServicio;
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
@RequestMapping("/usuario")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio us;


    @GetMapping("/modificar-usuario/{id}")
    public String modificarUsuario(@PathVariable Long id, ModelMap modelo, HttpSession session) throws ErrorServicio {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");
        if (us.buscarUsuarioPorId(id).getId().equals(usuarioLogueado.getId())) {
            modelo.addAttribute("usuario", us.buscarUsuarioPorId(id));
            modelo.addAttribute("categoria", Categoria.values());
           
            return "modificarUsuario";
        } else {
            return "redirect:/index";
        }
    }

    @PostMapping("/{id}")
    public String modificarUsuario(@PathVariable Long id, @RequestParam String nombre, @RequestParam String apodo, @RequestParam Categoria categoria, @RequestParam String mail, @RequestParam String contrasena, @RequestParam MultipartFile foto, HttpSession session) throws ErrorServicio, IOException {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");
        if (us.buscarUsuarioPorId(id).getId().equals(usuarioLogueado.getId())) {
            us.modificarUsuario(id, nombre, apodo, categoria, mail, contrasena, foto);
            session.setAttribute("usuariosession", us.buscarUsuarioPorId(id));
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
        model.addAttribute("exito", us.buscarUsuarioPorNombre(nombre));
        return "ingreso";
    }


    @GetMapping("/dar-de-baja/{id}")
    public String darDeBajaUsuarioPorId(@PathVariable Long id, HttpSession session) throws Exception {
        us.darDeBaja(id);
        session.setAttribute("usuariosession", us.buscarUsuarioPorId(id));

        return "redirect:/logout";
    }

    @GetMapping("/dar-de-alta/{id}")
    public String darDeAltaUsuarioPorId(@PathVariable Long id, HttpSession session) throws Exception {
        us.darDeAlta(id);
        session.setAttribute("usuariosession", us.buscarUsuarioPorId(id));

        return "redirect:/index";
    }

    @GetMapping("/buscar-usuario/categoria")
    public String buscarUsuarioPorCategiria() throws Exception {

        return "ingreso";
    }

    @PostMapping("/buscar-usuario/categoria")
    public String buscarUsuarioPorCategoria(ModelMap model, @RequestParam Categoria categoria) { //Completar luego
        model.addAttribute("exito", us.buscarUsuariosPorCategoria(categoria));
        return "ingreso";
    }


    @GetMapping("/miperfil")
    public String perfilUsuario(ModelMap model, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuariosession");
        if (usuarioLogueado == null) {
            return "redirect:/ingreso";
        } else {
            model.addAttribute("usuario", usuarioLogueado);
            return "miPerfil";
        }

    }

    @GetMapping("/foto/{id}")
    public ResponseEntity<byte[]> fotoUsuario(HttpSession session) throws ErrorServicio {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");

        if (usuario.getFoto() == null) {
            throw new ErrorServicio("El Usuario no tiene foto asignada");
        }
        byte[] foto = usuario.getFoto().getContenido();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(foto, headers, HttpStatus.OK);

    }

    //que traiga la lista de recetas del usuario


}
