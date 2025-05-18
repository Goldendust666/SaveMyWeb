package com.iesjuanbosco.ejemploweb.service;

import com.iesjuanbosco.ejemploweb.entity.Repositorio;
import com.iesjuanbosco.ejemploweb.repository.RepositorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RepositorioService {
    @Autowired
    private RepositorioRepository repositorioRepository;

    public List<Repositorio> findAll() {
        return repositorioRepository.findAll();
    }

    public Repositorio save(Repositorio repo) {
        return repositorioRepository.save(repo);
    }

    public Optional<Repositorio> findById(Long id) {
        return repositorioRepository.findById(id);
    }

    public void deleteById(Long id) {
        repositorioRepository.deleteById(id);
    }

}
