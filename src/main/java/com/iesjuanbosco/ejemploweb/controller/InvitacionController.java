package com.iesjuanbosco.ejemploweb.controller;

import com.iesjuanbosco.ejemploweb.entity.Invitacion;
import com.iesjuanbosco.ejemploweb.entity.Repositorio;
import com.iesjuanbosco.ejemploweb.entity.User;
import com.iesjuanbosco.ejemploweb.repository.InvitacionRepository;
import com.iesjuanbosco.ejemploweb.repository.RepositorioRepository;
import com.iesjuanbosco.ejemploweb.repository.UsuarioRepository;
import com.iesjuanbosco.ejemploweb.service.InvitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/invitaciones")
public class InvitacionController {

    @Autowired private InvitacionService invitacionService;
    @Autowired private UsuarioRepository usuarioRepository;

    @GetMapping
    public String verInvitaciones(Model model,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  @RequestParam(defaultValue = "0") int page) {
        User user = usuarioRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        Pageable pageable = PageRequest.of(page, 5); // 5 elementos por página

        Page<Repositorio> repositoriosInvitado = invitacionService.obtenerRepositoriosInvitado(user, pageable);
        Page<Invitacion> invitacionesEnviadas = invitacionService.obtenerInvitacionesEnviadas(user, pageable);

        model.addAttribute("repositoriosInvitado", repositoriosInvitado);
        model.addAttribute("invitacionesEnviadas", invitacionesEnviadas);
        model.addAttribute("titulo", "Tus invitaciones");

        return "invitaciones";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarInvitacion(@PathVariable Long id,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        User user = usuarioRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        invitacionService.eliminarInvitacion(id, user);
        return "redirect:/invitaciones";
    }
}

