package com.iesjuanbosco.ejemploweb.DTO;

public class PaginaDTO {
    private Long id;
    private String nombre;

    public PaginaDTO() {
    }

    public PaginaDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

