/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.recetapp.controllers;


import com.egg.recetapp.entities.Photo;
import com.egg.recetapp.exceptions.ServiceError;
import com.egg.recetapp.repositories.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/photo")

public class PhotoController {
    @Autowired
    private PhotoRepository fr;
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> fotoReceta(@PathVariable Long id) throws ServiceError {
        try{
            System.out.println("photo");
        Photo photo = fr.findById(id).get();

        if (photo == null) {
            throw new ServiceError("la receta no tiene photo ");
        }
        byte[] fotoo = photo.getContent();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(fotoo,headers,HttpStatus.OK);
        }catch (ServiceError e){
            e.getLocalizedMessage();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }
}

    

