package com.iesjuanbosco.ejemploweb.repository;


import com.iesjuanbosco.ejemploweb.entity.Invitacion;
import com.iesjuanbosco.ejemploweb.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvitacionRepository extends JpaRepository<Invitacion, Long> {
    Page<Invitacion> findByUsuario(User usuario, Pageable pageable);

    // Para las enviadas (usuario dueño del repositorio)
    Page<Invitacion> findByDestinatario(User destinatario, Pageable pageable);


}

