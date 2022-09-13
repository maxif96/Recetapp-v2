package com.egg.recetapp.controllers;

import com.egg.recetapp.entities.Users;
import com.egg.recetapp.enumerations.Category;
import com.egg.recetapp.exceptions.ServiceError;
import com.egg.recetapp.service.UserService;
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


    @GetMapping("/modify-user/{id}")
    public String update(@PathVariable Long id, ModelMap model, HttpSession session) throws ServiceError {
        Users loggedUser = (Users) session.getAttribute("usuariosession");
        if (userService.findUserById(id).getId().equals(loggedUser.getId())) {
            model.addAttribute("users", userService.findUserById(id));
            model.addAttribute("category", Category.values());
            return "modifyUser";
        } else {
            return "redirect:/index";
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam String nickName,
                         @RequestParam Category category,
                         @RequestParam String mail,
                         @RequestParam String password,
                         @RequestParam MultipartFile photo,
                         HttpSession session) throws ServiceError, IOException {

        Users loggedUser = (Users) session.getAttribute("usuariosession");
        if (userService.findUserById(id).getId().equals(loggedUser.getId())) {
            userService.updateUser(id, name, nickName, category, mail, password, photo);
            session.setAttribute("usuariosession", userService.findUserById(id));
        } else {
            throw new ServiceError("No puedes modificar este perfil");
        }

        return "redirect:/users/profile";
    }


    @GetMapping("/search-user/name")
    public String findUserByName() throws Exception {
        return "login";
    }

    @PostMapping("/search-user/name")
    public String findUserByName(ModelMap model, @RequestParam String name) throws Exception {
        model.addAttribute("exito", userService.findUserByName(name));
        return "login";
    }


    @GetMapping("/deactivate-account/{id}")
    public String deactivateAccount(@PathVariable Long id, HttpSession session) throws Exception {
        userService.softDelete(id);
        session.setAttribute("usuariosession", userService.findUserById(id));

        return "redirect:/logout";
    }

    @GetMapping("/search-users/category")
    public String findUsersByCategory() throws Exception {
        return "login";
    }

    @PostMapping("/search-users/category")
    public String findUsersByCategory(ModelMap model, @RequestParam Category category) {
        model.addAttribute("exito", userService.findUserByCategory(category));
        return "login";
    }


    @GetMapping("/profile")
    public String profile(ModelMap model, HttpSession session) {
        Users loggedUser = (Users) session.getAttribute("usuariosession");
        if (loggedUser == null) {
            return "redirect:/login";
        } else {
            model.addAttribute("users", loggedUser);
            return "myProfile";
        }

    }

    @GetMapping("/photo/{id}")
    public ResponseEntity<byte[]> photoUser(HttpSession session) throws ServiceError {
        Users users = (Users) session.getAttribute("usuariosession");

        if (users.getPhoto() == null) {
            throw new ServiceError("El Users no tiene foto asignada");
        }
        byte[] foto = users.getPhoto().getContent();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(foto, headers, HttpStatus.OK);

    }



}
