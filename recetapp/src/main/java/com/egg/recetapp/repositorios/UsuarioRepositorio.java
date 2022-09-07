package com.egg.recetapp.repositorios;

import com.egg.recetapp.entidades.Usuario;
import com.egg.recetapp.enumeracion.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long>{

    public Usuario findByMail(String mail);

     public Usuario findByNombre(String nombre); //No hace falta usar query, JPA lee primero el nommbre del método y realiza lo que está escrito (siempre en inglés)

     public List<Usuario> findByCategoria (Categoria categoria);
}
