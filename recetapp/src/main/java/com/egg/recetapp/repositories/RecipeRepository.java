package com.egg.recetapp.repositories;

import com.egg.recetapp.entities.Recipe;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>{
    List<Recipe> findAllByName(@Param("name") String name);

    List<Recipe> findAllByUser(@Param("id") Long id);

}
