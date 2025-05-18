package com.iesjuanbosco.ejemploweb.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


import jakarta.persistence.*;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name = "paginas", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nombre", "propietario_id"})
})
public class Pagina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String html;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String css;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String javascript;

    @ManyToMany(mappedBy = "paginas")
    private List<Repositorio> repositorios;

    @ManyToOne
    @JoinColumn(name = "propietario_id")
    private User propietario;

    // === GETTERS ===
    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getHtml() {
        return html;
    }

    public String getCss() {
        return css;
    }

    public String getJavascript() {
        return javascript;
    }

    public List<Repositorio> getRepositorios() {
        return repositorios;
    }

    public User getPropietario() {
        return propietario;
    }

    // === SETTERS ===
    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public void setJavascript(String javascript) {
        this.javascript = javascript;
    }

    public void setRepositorios(List<Repositorio> repositorios) {
        this.repositorios = repositorios;
    }

    public void setPropietario(User propietario) {
        this.propietario = propietario;
    }
}
