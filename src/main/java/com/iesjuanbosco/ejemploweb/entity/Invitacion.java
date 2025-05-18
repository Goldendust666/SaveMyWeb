package com.iesjuanbosco.ejemploweb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invitaciones")
@Entity
public class Invitacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "repositorio_id")
    private Repositorio repositorio;
    @ManyToOne
    @JoinColumn(name = "destinatario_id")
    private User destinatario;

    // Getters y setters
}

