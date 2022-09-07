package com.egg.recetapp.repositorios;

import com.egg.recetapp.entidades.Users;
import com.egg.recetapp.enumeracion.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<Users, Long>{

    boolean existsByMail(String mail);

    Users findByMail(String mail);

    Users findByName(String name);

    List<Users> findByCategory(Category category);
}
