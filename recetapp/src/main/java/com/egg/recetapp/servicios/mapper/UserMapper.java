package com.egg.recetapp.servicios.mapper;

import com.egg.recetapp.entidades.Photo;
import com.egg.recetapp.entidades.Users;
import com.egg.recetapp.enumeracion.Category;
import com.egg.recetapp.enumeracion.Rol;
import com.egg.recetapp.excepciones.ErrorServicio;
import com.egg.recetapp.servicios.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class UserMapper {

    @Autowired
    private PhotoService photoService;

    public Users buildUser (String name, String nickName, Category category, String mail, String password, MultipartFile photo) throws ErrorServicio, IOException {
        String encrypt = new BCryptPasswordEncoder().encode(password);
        Photo photoSaved = photoService.savePhoto(photo);
        return Users.builder()
                .name(name)
                .nickName(nickName)
                .category(category)
                .mail(mail)
                .password(encrypt)
                .photo(photoSaved)
                .rol(Rol.USER)
                .build();
    }

}
