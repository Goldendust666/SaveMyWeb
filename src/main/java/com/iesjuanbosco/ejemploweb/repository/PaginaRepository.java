package com.iesjuanbosco.ejemploweb.repository;



import com.iesjuanbosco.ejemploweb.entity.Pagina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaginaRepository extends JpaRepository<Pagina, Long> {
    Optional<Pagina> findByNombreAndPropietario_Id(String nombre, Long propietarioId);
    List<Pagina> findByPropietario_Id(Long propietarioId);

}

