package com.udistrital.awuis.sistema_academico.controller;

import com.udistrital.awuis.sistema_academico.model.Usuario;
import com.udistrital.awuis.sistema_academico.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String loginPage() {
        return "index"; // Asume que index.html es la página de login
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String correo,
            @RequestParam String contrasena,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // Validar credenciales
            Usuario usuario = usuarioService.validarCredenciales(correo, contrasena);

            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Credenciales incorrectas");
                return "redirect:/login";
            }

            // Verificar que el token existe y tiene rol
            if (usuario.getToken() == null || usuario.getToken().getRol() == null) {
                redirectAttributes.addFlashAttribute("error", "Error en la configuración del usuario");
                return "redirect:/login";
            }

            // Guardar usuario en la sesión
            session.setAttribute("usuario", usuario);

            // Redirigir según el rol
            int idRol = usuario.getToken().getRol().getIdRol();

            switch (idRol) {
                case 1: // Directivo
                    return "redirect:/directivo";
                case 2: // Profesor
                    return "redirect:/profesor";
                case 3: // Administrador
                    return "redirect:/administrador";
                case 4: // Estudiante
                    return "redirect:/estudiante";
                default:
                    redirectAttributes.addFlashAttribute("error", "Rol no reconocido");
                    return "redirect:/login";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al iniciar sesión: " + e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

