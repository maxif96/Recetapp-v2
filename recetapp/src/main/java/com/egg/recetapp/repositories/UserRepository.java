package com.egg.recetapp.repositories;

import com.egg.recetapp.entities.Users;
import com.egg.recetapp.enumerations.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<Users, Long>{

    boolean existsByMail(String mail);

    Users findByMail(String mail);

    Optional<Users> findByName(String name);

    List<Users> findByCategory(Category category);
}
