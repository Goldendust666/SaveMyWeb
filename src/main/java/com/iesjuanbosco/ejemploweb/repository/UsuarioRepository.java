package com.iesjuanbosco.ejemploweb.repository;


import com.iesjuanbosco.ejemploweb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);


}
