package com.iesjuanbosco.ejemploweb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "repositorios")
@Entity
public class Repositorio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private boolean visibilidad;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User propietario;

    @OneToMany(mappedBy = "repositorio", cascade = CascadeType.ALL)
    private List<Invitacion> invitaciones;

    @ManyToMany
    @JoinTable(
            name = "repositorio_pagina",
            joinColumns = @JoinColumn(name = "repositorio_id"),
            inverseJoinColumns = @JoinColumn(name = "pagina_id")
    )
    private List<Pagina> paginas;

    // Getters y setters
}

