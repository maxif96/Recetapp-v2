/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.egg.recetapp.servicios;

import com.egg.recetapp.entidades.Foto;
import com.egg.recetapp.excepciones.ErrorServicio;
import com.egg.recetapp.repositorios.FotoRepositorio;
import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Fabi
 */
@Service
public class FotoServicio {

    @Autowired
    FotoRepositorio fr;

    //este metodo guarda la foto,tanto del usuario como de la receta
    public Foto guardar(MultipartFile archivo) throws ErrorServicio, IOException {
        if (archivo != null) { //si el archivo no es nulo,entra al try y al catch
            try {

                Foto foto = new Foto();
                foto.setMime(archivo.getContentType());
                foto.setContenido(archivo.getBytes());
                return fr.save(foto);

            } catch (Exception e) {
                e.getMessage();
                throw new ErrorServicio("No se ha podido guardar la foto.");
            }
        }
        return null;//si el archivo es nulo,nunca entra al if y retorna null
    }

    public Foto modificar(Long idFoto, MultipartFile archivo) throws ErrorServicio, IOException {

        if (archivo != null) {
            try {

                Optional<Foto> respuesta = fr.findById(idFoto);

                Foto foto = respuesta.get();
                foto.setMime(archivo.getContentType());
                foto.setContenido(archivo.getBytes());

                return fr.save(foto);

            } catch (Exception e) {
                e.getMessage();
                throw new ErrorServicio("No se ha podido modificar la foto.");
            }
        }
        return null;

    }

    public void eliminarFoto (Long id){
        fr.deleteById(id);
    }

}
