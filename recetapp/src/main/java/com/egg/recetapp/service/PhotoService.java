/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.recetapp.service;

import com.egg.recetapp.entities.Photo;
import com.egg.recetapp.exceptions.ServiceError;
import com.egg.recetapp.repositories.PhotoRepository;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;

/**
 *
 * @author Fabi
 */
@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    public Photo savePhoto(MultipartFile archivo) throws ServiceError, IOException {
        if (archivo != null) {
            try {
                Photo photo = new Photo();
                photo.setMime(archivo.getContentType());
                photo.setContent(archivo.getBytes());
                return photoRepository.save(photo);

            } catch (Exception e) {
                e.getMessage();
                throw new ServiceError("No se ha podido guardar la foto.");
            }
        }
        return null;
    }

    public Photo modifyPhoto(Long idFoto, MultipartFile archivo) throws ServiceError, IOException {
        if (archivo != null) {
            try {
                Photo photo= photoRepository.findById(idFoto).orElseThrow(() -> new EntityNotFoundException("Foto no encontrada."));

                photo.setMime(archivo.getContentType());
                photo.setContent(archivo.getBytes());

                return photoRepository.save(photo);
            } catch (Exception e) {
                e.getMessage();
                throw new ServiceError("No se ha podido modificar la foto.");
            }
        }
        return null;
    }

    public void deletePhoto(Long id){
        photoRepository.deleteById(id);
    }

}
