package com.iesjuanbosco.ejemploweb.service;

import com.iesjuanbosco.ejemploweb.entity.Invitacion;
import com.iesjuanbosco.ejemploweb.entity.Repositorio;
import com.iesjuanbosco.ejemploweb.entity.User;
import com.iesjuanbosco.ejemploweb.repository.InvitacionRepository;
import com.iesjuanbosco.ejemploweb.repository.RepositorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvitacionService {
    @Autowired
    private InvitacionRepository invitacionRepository;
    private RepositorioRepository repositorioRepository;

    public Invitacion save(Invitacion invitacion) {
        return invitacionRepository.save(invitacion);
    }

    public List<Invitacion> findAll() {
        return invitacionRepository.findAll();
    }

    public Page<Repositorio> obtenerRepositoriosInvitado(User user, Pageable pageable) {
        return invitacionRepository.findByDestinatario(user, pageable)
                .map(Invitacion::getRepositorio);
    }

    public Page<Invitacion> obtenerInvitacionesEnviadas(User user, Pageable pageable) {
        return invitacionRepository.findByUsuario(user, pageable);
    }

    public void eliminarInvitacion(Long id, User userActual) {
        Invitacion invitacion = invitacionRepository.findById(id).orElseThrow();
        if (!invitacion.getUsuario().getId().equals(userActual.getId())) {
            throw new RuntimeException("No autorizado");
        }
        invitacionRepository.delete(invitacion);
    }
}

