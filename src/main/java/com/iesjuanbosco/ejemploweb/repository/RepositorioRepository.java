package com.iesjuanbosco.ejemploweb.repository;


import com.iesjuanbosco.ejemploweb.entity.Pagina;
import com.iesjuanbosco.ejemploweb.entity.Repositorio;
import com.iesjuanbosco.ejemploweb.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RepositorioRepository extends JpaRepository<Repositorio, Long> {
    @Query("SELECT r FROM Repositorio r LEFT JOIN FETCH r.paginas WHERE r.visibilidad = true")
    Page<Repositorio> findByVisibilidadTrue(Pageable pageable);

    List<Repositorio> findByPropietario(User user);
    List<Repositorio> findAllByPaginasContaining(Pagina pagina);

}

