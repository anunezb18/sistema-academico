package com.udistrital.awuis.sistema_academico.controller;

import com.udistrital.awuis.sistema_academico.model.Usuario;
import com.udistrital.awuis.sistema_academico.repositories.RolMapper;
import com.udistrital.awuis.sistema_academico.repositories.UsuarioMapper;
import com.udistrital.awuis.sistema_academico.repositories.EstudianteMapper;
import com.udistrital.awuis.sistema_academico.repositories.ProfesorMapper;
import com.udistrital.awuis.sistema_academico.repositories.DirectivoMapper;
import com.udistrital.awuis.sistema_academico.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/administrador")
public class AdministradorController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private RolMapper rolMapper;

    @Autowired
    private EstudianteMapper estudianteMapper;

    @Autowired
    private ProfesorMapper profesorMapper;

    @Autowired
    private DirectivoMapper directivoMapper;

    @GetMapping
    public String mostrarPanelAdministrador(
            @SessionAttribute(value = "usuario", required = false) Usuario usuario,
            Model model) {

        // Validar que haya sesión activa
        if (usuario == null) {
            return "redirect:/login";
        }

        // Validar que sea administrador (rol 3)
        if (usuario.getToken() != null && usuario.getToken().getRol() != null) {
            int idRol = usuario.getToken().getRol().getIdRol();
            // Si NO es administrador, redirigir a su panel correspondiente
            if (idRol == 1) {
                return "redirect:/directivo";
            } else if (idRol == 2) {
                return "redirect:/profesor";
            } else if (idRol == 4) {
                return "redirect:/estudiante";
            }
            // Si es rol 3 (Administrador), continuar normalmente
        }

        // Obtener estadísticas reales
        long cantidadEstudiantes = estudianteMapper.listarEstudiantes().size();
        long cantidadProfesores = profesorMapper.findAll().size();
        long cantidadDirectivos = directivoMapper.findAll().size();
        long cantidadAdministradores = 1; // Por defecto 1 administrador
        long cantidadTotal = usuarioMapper.findAll().size();

        model.addAttribute("cantidadEstudiantes", cantidadEstudiantes);
        model.addAttribute("cantidadProfesores", cantidadProfesores);
        model.addAttribute("cantidadAdministradores", cantidadAdministradores);
        model.addAttribute("cantidadTotal", cantidadTotal);

        return "administrador";
    }

    @PostMapping("/crear-cuenta")
    public String crearCuenta(
            @RequestParam String correo,
            @RequestParam String contrasena,
            @RequestParam int idRol,
            @RequestParam(required = false) String nombre,
            RedirectAttributes redirectAttributes) {

        try {
            // Validaciones
            if (correo == null || correo.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El correo es obligatorio");
                return "redirect:/administrador";
            }

            if (contrasena == null || contrasena.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "La contraseña es obligatoria");
                return "redirect:/administrador";
            }

            if (idRol < 1 || idRol > 4) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un rol válido");
                return "redirect:/administrador";
            }

            // Validar nombre para profesores y directivos (roles 1, 2, 3)
            if ((idRol == 1 || idRol == 2 || idRol == 3) && (nombre == null || nombre.trim().isEmpty())) {
                redirectAttributes.addFlashAttribute("error", "El nombre es obligatorio para Profesores y Directivos");
                return "redirect:/administrador";
            }

            // Crear usuario
            Usuario nuevoUsuario = usuarioService.crearUsuario(correo, contrasena, idRol, nombre);

            redirectAttributes.addFlashAttribute("mensaje",
                "Cuenta creada exitosamente para " + correo);

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al crear la cuenta: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/administrador";
    }

    @PostMapping("/inhabilitar-cuenta")
    public String inhabilitarCuenta(
            @RequestParam int idUsuario,
            RedirectAttributes redirectAttributes) {

        try {
            usuarioService.inhabilitarCuenta(idUsuario);
            redirectAttributes.addFlashAttribute("mensaje",
                "Cuenta inhabilitada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al inhabilitar la cuenta: " + e.getMessage());
        }

        return "redirect:/administrador";
    }
}

