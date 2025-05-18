package com.iesjuanbosco.ejemploweb.controller;

import com.iesjuanbosco.ejemploweb.entity.Invitacion;
import com.iesjuanbosco.ejemploweb.entity.Pagina;
import com.iesjuanbosco.ejemploweb.entity.Repositorio;
import com.iesjuanbosco.ejemploweb.entity.User;
import com.iesjuanbosco.ejemploweb.repository.InvitacionRepository;
import com.iesjuanbosco.ejemploweb.repository.PaginaRepository;
import com.iesjuanbosco.ejemploweb.repository.RepositorioRepository;
import com.iesjuanbosco.ejemploweb.repository.UsuarioRepository;


import com.iesjuanbosco.ejemploweb.service.CustomUserDetailsService;
import com.iesjuanbosco.ejemploweb.service.PaginaService;
import com.iesjuanbosco.ejemploweb.service.RepositorioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.util.*;

@Controller
public class RepositorioController {

    @Autowired private RepositorioRepository repositorioRepository;
    @Autowired private UsuarioRepository userRepository;
    @Autowired private InvitacionRepository invitationRepository;



    private final PaginaService paginaService;
    private final RepositorioService repositorioService;
    private final CustomUserDetailsService userService;

    public RepositorioController(PaginaService paginaService, RepositorioService repositorioService, CustomUserDetailsService userService) {
        this.paginaService = paginaService;
        this.repositorioService = repositorioService;
        this.userService = userService;
    }
    @GetMapping("/")
    public String mostrarRepositorios(Model model, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Repositorio> repositorios = repositorioRepository.findByVisibilidadTrue(pageable);
        model.addAttribute("titulo", "Inicio - SaveMyWeb");
        model.addAttribute("repositorios", repositorios);
        return "index";
    }

    @GetMapping("/misrepositorios")
    public String mostrarMisRepositorios(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) return "redirect:/";
        Optional<User> usuario = userRepository.findByEmail(userDetails.getUsername());
        if (usuario.isEmpty()) return "redirect:/";

        List<Repositorio> misRepos = repositorioRepository.findByPropietario(usuario.get());
        List<Pagina> paginasDelUsuario = paginaService.findAllByUser(usuario);
        model.addAttribute("titulo", "Mis Repositorios");
        model.addAttribute("repositorios", misRepos);
        model.addAttribute("paginasDisponibles", paginasDelUsuario);
        return "misrepositorios";
    }

    @PostMapping("/repositorio/crear")
    public String crearRepositorio(@RequestParam String nombre, @RequestParam(required = false, defaultValue = "false") boolean visibilidad, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> userOpt = userRepository.findByEmail(userDetails.getUsername());
        if (userOpt.isPresent()) {
            Repositorio repo = new Repositorio();
            repo.setNombre(nombre);
            repo.setVisibilidad(visibilidad);
            repo.setPropietario(userOpt.get());
            repositorioRepository.save(repo);
        }
        return "redirect:/misrepositorios";
    }

    @PostMapping("/repositorio/editar")
    public String editarRepositorio(@RequestParam Long id, @RequestParam String nombre, @RequestParam(required = false, defaultValue = "false") boolean visibilidad, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Repositorio> repoOpt = repositorioRepository.findById(id);
        if (repoOpt.isPresent() && userDetails != null) {
            Repositorio repo = repoOpt.get();
            if (repo.getPropietario().getEmail().equals(userDetails.getUsername())) {
                repo.setNombre(nombre);
                repo.setVisibilidad(visibilidad);
                repositorioRepository.save(repo);
            }
        }
        return "redirect:/misrepositorios";
    }

    @PostMapping("/repositorio/eliminar/{id}")
    public String eliminarRepositorio(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        repositorioRepository.findById(id).ifPresent(repo -> {
            if (repo.getPropietario().getEmail().equals(userDetails.getUsername())) {
                repositorioRepository.delete(repo);
            }
        });
        return "redirect:/misrepositorios";
    }

    @PostMapping("/repositorio/invitar")
    @ResponseBody
    public ResponseEntity<?> enviarInvitacion(@RequestBody Map<String, String> body, @AuthenticationPrincipal UserDetails userDetails) {
        Long repoId = Long.parseLong(body.get("repoId"));
        String email = body.get("email");

        Optional<Repositorio> repoOpt = repositorioRepository.findById(repoId);
        Optional<User> destinatarioOpt = userRepository.findByEmail(email);
        Optional<User> emisorOpt = userRepository.findByEmail(userDetails.getUsername());

        if (repoOpt.isEmpty() || destinatarioOpt.isEmpty() || emisorOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Datos inválidos");
        }

        Repositorio repo = repoOpt.get();
        User emisor = emisorOpt.get();

        if (!repo.getPropietario().getEmail().equals(emisor.getEmail())) {
            return ResponseEntity.status(403).build();
        }

        Invitacion invitacion = new Invitacion();
        invitacion.setRepositorio(repo);
        invitacion.setUsuario(emisor); // Usuario que envía la invitación
        invitacion.setDestinatario(destinatarioOpt.get()); // Usuario que la recibe

        invitationRepository.save(invitacion);

        return ResponseEntity.ok("Invitación enviada");
    }

    // Ya existente
    @GetMapping("/repositorio/{id}/paginas")
    @ResponseBody
    public ResponseEntity<?> obtenerPaginasDelRepositorio(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Repositorio> optionalRepositorio = repositorioRepository.findById(id);
        if (optionalRepositorio.isEmpty()) return ResponseEntity.notFound().build();

        Repositorio repo = optionalRepositorio.get();

        if (!repo.isVisibilidad()) {
            if (userDetails == null || !repo.getPropietario().getEmail().equals(userDetails.getUsername())) {
                return ResponseEntity.status(403).body("Acceso denegado");
            }
        }

        List<Map<String, Object>> paginas = new ArrayList<>();
        for (var pagina : repo.getPaginas()) {
            Map<String, Object> paginaMap = new HashMap<>();
            paginaMap.put("id", pagina.getId());
            paginaMap.put("nombre", pagina.getNombre());
            paginas.add(paginaMap);
        }

        return ResponseEntity.ok(paginas);
    }
}
