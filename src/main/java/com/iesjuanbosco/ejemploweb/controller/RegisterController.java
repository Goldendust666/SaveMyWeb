package com.iesjuanbosco.ejemploweb.controller;

import com.iesjuanbosco.ejemploweb.config.SecurityConfig;
import com.iesjuanbosco.ejemploweb.entity.User;
import com.iesjuanbosco.ejemploweb.service.CustomUserDetailsService;
import com.iesjuanbosco.ejemploweb.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegisterController {
    @Autowired
    private SecurityConfig securityConfig;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @GetMapping()
    public String showRegistrationForm(Model model) {
        model.addAttribute("usuario", new User());
        return "register";
    }

    @PostMapping()
    public String processRegistration(@Valid User usuario, BindingResult result, Model model) {
        model.addAttribute("usuario", new User());
        if (result.hasErrors()) {
            return "register";
        }

        try {
            // Encriptar la contraseña antes de guardar
            usuario.setPassword(securityConfig.passwordEncoder().encode(usuario.getPassword()));

            // Asignar el rol
            usuario.setRol("USER");

            // Guardar en la base de datos
            customUserDetailsService.saveUser(usuario);

            // Si toh salió bien, redirigir al usuario
            return "redirect:/";
        } catch (Exception e) {
            // Manejar cualquier error que pueda ocurrir durante la persistencia
            model.addAttribute("error", "Error al registrar el usuario: " + e.getMessage());
            return "register";
        }
    }
}
