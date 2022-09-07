package com.egg.recetapp.repositorios;

import com.egg.recetapp.entidades.Receta;
import com.egg.recetapp.entidades.Usuario;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecetaRepositorio extends JpaRepository<Receta, Long>{
    
    @Query("SELECT r FROM Receta r  WHERE r.nombre LIKE %:nombre%" 
    + " OR r.origen LIKE %:nombre%" + " OR r.tipo LIKE %:nombre%")
    public List<Receta> BuscarPorNombre(@Param("nombre") String nombre);
    
    @Query("SELECT l From Receta l WHERE l.usuario.id=:id")
     public List<Receta> BuscarRecetaPorUsuario(@Param("id") Long id);

}
