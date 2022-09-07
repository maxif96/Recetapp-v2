package com.egg.recetapp.repositorios;

import com.egg.recetapp.entidades.Recipe;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>{
    
    @Query("SELECT r FROM Recipe r  WHERE r.nombre LIKE %:nombre%"
    + " OR r.origen LIKE %:nombre%" + " OR r.tipo LIKE %:nombre%")
    public List<Recipe> BuscarPorNombre(@Param("nombre") String nombre);
    
    @Query("SELECT l From Recipe l WHERE l.usuario.id=:id")
     public List<Recipe> BuscarRecetaPorUsuario(@Param("id") Long id);

}
