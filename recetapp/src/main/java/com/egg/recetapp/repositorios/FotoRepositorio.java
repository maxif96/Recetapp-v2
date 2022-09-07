package com.egg.recetapp.repositorios;

import com.egg.recetapp.entidades.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FotoRepositorio extends JpaRepository<Foto, Long> {
    
}
