package com.iesjuanbosco.ejemploweb.controller;


import com.iesjuanbosco.ejemploweb.entity.Pagina;
import com.iesjuanbosco.ejemploweb.entity.Repositorio;
import com.iesjuanbosco.ejemploweb.entity.User;
import com.iesjuanbosco.ejemploweb.repository.PaginaRepository;
import com.iesjuanbosco.ejemploweb.repository.RepositorioRepository;
import com.iesjuanbosco.ejemploweb.repository.UsuarioRepository;

import com.iesjuanbosco.ejemploweb.service.CustomUserDetailsService;
import com.iesjuanbosco.ejemploweb.service.PaginaService;
import com.iesjuanbosco.ejemploweb.service.RepositorioService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/pagina")
public class PaginaController {

    @Autowired private PaginaRepository paginaRepository;
    @Autowired private RepositorioRepository repositorioRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    private final PaginaService paginaService;
    private final RepositorioService repositorioService;


    public PaginaController(PaginaService paginaService, RepositorioService repositorioService, CustomUserDetailsService userService) {
        this.paginaService = paginaService;
        this.repositorioService = repositorioService;

    }


    @PostMapping("/crear")
    public String crearNuevaPagina(@RequestParam String nombre, @RequestParam Long repoId, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Repositorio> repoOpt = repositorioRepository.findById(repoId);
        if (repoOpt.isEmpty()) return "redirect:/misrepositorios";
        Repositorio repo = repoOpt.get();

        if (!repo.getPropietario().getEmail().equals(userDetails.getUsername())) return "redirect:/misrepositorios";

        // Obtenemos el ID del propietario
        Long propietarioId = repo.getPropietario().getId();

        // 🔍 Comprobamos si ese usuario ya tiene una página con ese nombre
        Optional<Pagina> existente = paginaRepository.findByNombreAndPropietario_Id(nombre, propietarioId);
        if (existente.isPresent()) {
            // Ya existe una página con ese nombre para este usuario
            return "redirect:/misrepositorios?error=nombre-duplicado";
        }

        Pagina pagina = new Pagina();
        pagina.setNombre(nombre);
        pagina.setPropietario(repo.getPropietario());

        paginaRepository.save(pagina);
        repo.getPaginas().add(pagina);
        repositorioRepository.save(repo);

        return "redirect:/misrepositorios";
    }


    @PostMapping("/agregarExistente")
    public String agregarExistente(@RequestParam Long paginaId, @RequestParam Long repoId, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Repositorio> repoOpt = repositorioRepository.findById(repoId);
        Optional<Pagina> pagOpt = paginaRepository.findById(paginaId);

        if (repoOpt.isEmpty() || pagOpt.isEmpty()) return "redirect:/misrepositorios";

        Repositorio repo = repoOpt.get();
        Pagina pagina = pagOpt.get();

        if (!repo.getPropietario().getEmail().equals(userDetails.getUsername())) return "redirect:/misrepositorios";

        repo.getPaginas().add(pagina);
        repositorioRepository.save(repo);

        return "redirect:/misrepositorios";
    }

    @PostMapping("/quitar")
    public String quitarPagina(@RequestParam Long paginaId, @RequestParam Long repoId, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Repositorio> repoOpt = repositorioRepository.findById(repoId);
        Optional<Pagina> pagOpt = paginaRepository.findById(paginaId);

        if (repoOpt.isEmpty() || pagOpt.isEmpty()) return "redirect:/misrepositorios";

        Repositorio repo = repoOpt.get();
        Pagina pagina = pagOpt.get();

        if (!repo.getPropietario().getEmail().equals(userDetails.getUsername())) return "redirect:/misrepositorios";

        repo.getPaginas().remove(pagina);
        repositorioRepository.save(repo);

        return "redirect:/misrepositorios";
    }
    //el error esta en que en la base de datos esta como tiny text tienes que cambiar lo que sea en el entity para que guarde o Longtext u uno muy grande.
    @PostMapping("/extraer")
    public String extraerPaginaDesdeUrl(@RequestParam String url, @RequestParam Long repoId, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Iniciando extracción de la página desde URL: " + url);

            Document doc = Jsoup.connect(url).get();
            String html = doc.html();

            // Obtener el título (nombre)
            String title = doc.title();
            if (title == null || title.isEmpty()) {
                title = url;  // fallback si no hay title
            }
            System.out.println("Título obtenido: " + title);

            // Estilos embebidos
            StringBuilder cssBuilder = new StringBuilder();
            for (Element style : doc.select("style")) {
                cssBuilder.append(style.html()).append("\n");
            }

            // Estilos externos
            for (Element link : doc.select("link[rel=stylesheet]")) {
                String href = link.absUrl("href");
                try {
                    String externalCss = Jsoup.connect(href).ignoreContentType(true).execute().body();
                    cssBuilder.append(externalCss).append("\n");
                    System.out.println("CSS externo añadido desde: " + href);
                } catch (IOException e) {
                    System.out.println("Error cargando CSS externo desde: " + href + " -> " + e.getMessage());
                }
            }

            // Scripts embebidos
            StringBuilder jsBuilder = new StringBuilder();
            for (Element script : doc.select("script:not([src])")) {
                jsBuilder.append(script.html()).append("\n");
            }

            // Scripts externos
            for (Element script : doc.select("script[src]")) {
                String src = script.absUrl("src");
                try {
                    String externalJs = Jsoup.connect(src).ignoreContentType(true).execute().body();
                    jsBuilder.append(externalJs).append("\n");
                    System.out.println("JS externo añadido desde: " + src);
                } catch (IOException e) {
                    System.out.println("Error cargando JS externo desde: " + src + " -> " + e.getMessage());
                }
            }

            // Buscar repositorio y usuario
            Repositorio repo = repositorioService.findById(repoId)
                    .orElseThrow(() -> new RuntimeException("Repositorio no encontrado con ID: " + repoId));
            System.out.println("Repositorio encontrado: " + repo.getNombre());

            User usuario = usuarioRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + principal.getName()));
            System.out.println("Usuario encontrado: " + usuario.getEmail());

            // Crear la página
            Pagina pagina = new Pagina();
            pagina.setNombre(title);
            pagina.setHtml(html);
            pagina.setCss(cssBuilder.toString());
            pagina.setJavascript(jsBuilder.toString());
            pagina.setPropietario(usuario);

            // Guardar la página primero para tener ID
            paginaService.save(pagina);
            System.out.println("Página guardada con ID: " + pagina.getId());

            // Añadir la página al repositorio y guardar el repositorio para persistir la relación ManyToMany
            repo.getPaginas().add(pagina);
            repositorioService.save(repo);
            System.out.println("Repositorio actualizado con la nueva página.");

            redirectAttributes.addFlashAttribute("mensaje", "Página '" + title + "' importada con éxito.");
        } catch (Exception e) {
            System.out.println("Error durante la importación de la página:");
            e.printStackTrace();  // Esto mostrará en consola la traza completa del error
            redirectAttributes.addFlashAttribute("error", "Error al importar la página: " + e.getMessage());
        }

        return "redirect:/misrepositorios";
    }
    @GetMapping("/ver/{id}")
    public String verPagina(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        Optional<Pagina> paginaOpt = paginaRepository.findById(id);
        if (paginaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Página no encontrada");
            return "redirect:/";
        }

        Pagina pagina = paginaOpt.get();
        User usuarioActual = null;

        if (principal != null) {
            usuarioActual = usuarioRepository.findByEmail(principal.getName()).orElse(null);
        }

        // Verificar si es el propietario
        if (usuarioActual != null && pagina.getPropietario().getId().equals(usuarioActual.getId())) {
            model.addAttribute("pagina", pagina);
            return "ver-pagina";
        }

        // Buscar todos los repositorios que contienen esta página
        List<Repositorio> repositorios = repositorioRepository.findAllByPaginasContaining(pagina);

        // Si no está en ningún repositorio, solo el propietario puede verla
        if (repositorios.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para ver esta página");
            return "redirect:/";
        }

        // Si algún repositorio es público, la página es pública
        for (Repositorio repo : repositorios) {
            if (repo.isVisibilidad()) {
                model.addAttribute("pagina", pagina);
                return "ver-pagina";
            }
        }

        // Si todos los repos son privados, verificar invitación
        if (usuarioActual != null) {
            for (Repositorio repo : repositorios) {
                User finalUsuarioActual = usuarioActual;
                if (repo.getPropietario().getId().equals(usuarioActual.getId()) ||
                        repo.getInvitaciones().stream().anyMatch(inv -> inv.getUsuario().getId().equals(finalUsuarioActual.getId()))) {
                    model.addAttribute("pagina", pagina);
                    return "ver-pagina";
                }
            }
        }

        redirectAttributes.addFlashAttribute("error", "No tienes permisos para ver esta página");
        return "redirect:/";
    }
    @GetMapping("/mispaginas")
    public String verMisPaginas(Principal principal, Model model) {
        if (principal == null) return "redirect:/";

        String email = principal.getName();
        Optional<User> userOpt = usuarioRepository.findByEmail(email);

        if (userOpt.isEmpty()) return "redirect:/";

        User usuario = userOpt.get();
        List<Pagina> paginas = paginaRepository.findByPropietario_Id(usuario.getId());

        model.addAttribute("paginas", paginas);
        model.addAttribute("titulo", "Mis Páginas");
        return "mispaginas";
    }
    @PostMapping("/editar")
    public String guardarPagina(@ModelAttribute Pagina pagina, @AuthenticationPrincipal UserDetails userDetails) {
        if (pagina.getId() == null) {
            // Crear nueva página
            User user = usuarioRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            pagina.setPropietario(user);
            paginaService.save(pagina);
        } else {
            // Editar página existente
            paginaService.editarPagina(pagina, userDetails.getUsername());
        }

        return "redirect:/pagina/mispaginas";
    }
    @PostMapping("/eliminar/{id}")
    public String eliminarPagina(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        paginaService.eliminarPorIdYUsuario(id, userDetails.getUsername()); // Asegúrate de que solo elimina si es suya
        return "redirect:/pagina/mispaginas";
    }
}
