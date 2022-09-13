package com.egg.recetapp.service.mapper;

import com.egg.recetapp.entities.Photo;
import com.egg.recetapp.entities.Users;
import com.egg.recetapp.enumerations.Category;
import com.egg.recetapp.enumerations.Rol;
import com.egg.recetapp.exceptions.ServiceError;
import com.egg.recetapp.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class UserMapper {

    @Autowired
    private PhotoService photoService;

    public Users buildUser (String name, String nickName, Category category, String mail, String password, MultipartFile photo) throws ServiceError, IOException {
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
