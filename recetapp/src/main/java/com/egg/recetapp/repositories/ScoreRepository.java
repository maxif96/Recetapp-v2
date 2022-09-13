package com.egg.recetapp.repositories;

import com.egg.recetapp.entities.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    @Query(value = "SELECT * FROM Score c WHERE c.receta_id = :id", nativeQuery = true)
    List<Score> findAllByRecipeId(@Param("id") Long id);

    @Modifying
    @Query(value = "DELETE FROM `recetappdb`.`calificacion` WHERE receta_id = :recipeId", nativeQuery = true)
    void deleteAllByRecipeId(@Param("recipeId") Long recipeId);
}
