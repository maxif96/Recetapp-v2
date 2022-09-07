package com.egg.recetapp.repositorios;

import com.egg.recetapp.entidades.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CalificacionRepositorio extends JpaRepository<Calificacion, Long> {

    //listo todas las calificaciones recibidas
//    @Query("SELECT c FROM Calificacion c WHERE c.valor.id = :id ORDER BY c.fecha DESC")
//    public List<Calificacion> buscarCalificacionesRecibidas(@Param("id") Long Id);
    @Query(value = "SELECT * FROM Calificacion c WHERE c.receta_id = :id", nativeQuery = true)
    List<Calificacion> buscarCalificacionesRecibidas(@Param("id") Long id);

    @Modifying
    @Query(value = "DELETE FROM `recetappdb`.`calificacion` WHERE receta_id = :recetaId", nativeQuery = true)
    void eliminarTodosPorRecetaId(@Param("recetaId") Long recetaId);
}
//AND c.comentario<>null"