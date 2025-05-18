package com.iesjuanbosco.ejemploweb.service;

import com.iesjuanbosco.ejemploweb.entity.Pagina;
import com.iesjuanbosco.ejemploweb.entity.Repositorio;
import com.iesjuanbosco.ejemploweb.entity.User;
import com.iesjuanbosco.ejemploweb.repository.PaginaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaginaService {
    @Autowired
    private PaginaRepository paginaRepository;

    public Pagina save(Pagina pagina) {
        return paginaRepository.save(pagina);
    }

    public List<Pagina> findAll() {
        return paginaRepository.findAll();
    }

    public Optional<Pagina> findById(Long id) {
        return paginaRepository.findById(id);
    }

    public void deleteById(Long id) {
        paginaRepository.deleteById(id);
    }
    public List<Pagina> findAllByUser(Optional<User> user) {
        return paginaRepository.findByPropietario_Id(user.get().getId());
    }
    public void editarPagina(Pagina paginaModificada, String username) {
        Pagina original = paginaRepository.findById(paginaModificada.getId())
                .orElseThrow(() -> new RuntimeException("Página no encontrada"));

        if (!original.getPropietario().getEmail().equals(username)) {
            throw new AccessDeniedException("No tienes permiso para editar esta página");
        }

        original.setHtml(paginaModificada.getHtml());
        original.setCss(paginaModificada.getCss());
        original.setJavascript(paginaModificada.getJavascript());

        paginaRepository.save(original);
    }
    @Transactional
    public void eliminarPorIdYUsuario(Long id, String username) {
        Pagina pagina = paginaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Página no encontrada"));

        // Verificar que el usuario sea el propietario de la página si es necesario
        if (!pagina.getPropietario().getEmail().equals(username)) {
            throw new AccessDeniedException("No puedes eliminar esta página");
        }

        // Eliminar la relación con los repositorios
        for (Repositorio repositorio : pagina.getRepositorios()) {
            repositorio.getPaginas().remove(pagina);
        }
        pagina.getRepositorios().clear(); // muy importante para evitar que JPA intente mantener la relación

        // Guardar cambios en los repositorios
        paginaRepository.save(pagina);

        // Ahora sí, eliminar la página
        paginaRepository.delete(pagina);
    }
}

