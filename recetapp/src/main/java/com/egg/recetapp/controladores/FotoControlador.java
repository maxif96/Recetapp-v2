/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.recetapp.controladores;


import com.egg.recetapp.entidades.Foto;
import com.egg.recetapp.excepciones.ErrorServicio;
import com.egg.recetapp.repositorios.FotoRepositorio;
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
@RequestMapping("/foto")

public class FotoControlador {
    @Autowired
    private FotoRepositorio fr;
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> fotoReceta(@PathVariable Long id) throws ErrorServicio {
        try{
            System.out.println("foto");
        Foto foto = fr.findById(id).get();

        if (foto == null) {
            throw new ErrorServicio("la receta no tiene foto ");
        }
        byte[] fotoo = foto.getContenido();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        return new ResponseEntity<>(fotoo,headers,HttpStatus.OK);
        }catch (ErrorServicio e){
            e.getLocalizedMessage();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }
}

    

