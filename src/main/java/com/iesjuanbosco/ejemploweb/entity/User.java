package com.iesjuanbosco.ejemploweb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Entity
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;





    private String rol;

    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL)
    private List<Repositorio> repositorios;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Invitacion> invitaciones;

    // Getters y setters
}

