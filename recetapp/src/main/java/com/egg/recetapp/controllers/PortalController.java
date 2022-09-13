package com.egg.recetapp.controllers;

import com.egg.recetapp.enumerations.Category;
import com.egg.recetapp.exceptions.ServiceError;
import com.egg.recetapp.service.UserService;
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

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(ModelMap model) {
        model.addAttribute("category", Category.values());
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
            return "login";
        } catch (Exception e) {
            model.addAttribute("category", Category.values());
            model.put("error", e.getMessage());
            return "register";
        }

    }

    @GetMapping("/changepassword")
    public String changePassword() {
        return "changePassword.html";
    }

    @PostMapping("/getMail")
    public String mailVerificacion(@RequestParam String mail, HttpSession session, ModelMap model) throws ServiceError {
        int recoveryCode = userService.send(mail);
        session.setAttribute("recoveryCode", recoveryCode);
        model.put("mail", "Email de recuperación enviado!");

        return "changePassword";
    }

    @PostMapping("/changepassword")
    public String changePassword(HttpSession session, @RequestParam Integer enteredCode, @RequestParam String password, @RequestParam String mail) throws ServiceError, IOException {
        if (enteredCode == (int) session.getAttribute("recoveryCode")) {
            userService.updatePassword(password, mail);
        }
        return "/login";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }


}
