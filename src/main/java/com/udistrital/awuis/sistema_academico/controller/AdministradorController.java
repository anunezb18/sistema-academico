package com.udistrital.awuis.sistema_academico.controller;

import com.udistrital.awuis.sistema_academico.model.Usuario;
import com.udistrital.awuis.sistema_academico.repositories.RolMapper;
import com.udistrital.awuis.sistema_academico.repositories.UsuarioMapper;
import com.udistrital.awuis.sistema_academico.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping
    public String mostrarPanelAdministrador(Model model) {
        // Obtener estadísticas
        List<Usuario> todosUsuarios = usuarioMapper.findAll();

        // Contar por rol (esto es simplificado, ajustar según tu lógica)
        long estudiantes = todosUsuarios.size(); // TODO: Filtrar por rol estudiante
        long profesores = 15; // TODO: Obtener de BD
        long administradores = 5; // TODO: Obtener de BD

        model.addAttribute("cantidadEstudiantes", estudiantes);
        model.addAttribute("cantidadProfesores", profesores);
        model.addAttribute("cantidadAdministradores", administradores);
        model.addAttribute("cantidadTotal", estudiantes + profesores + administradores);

        return "administrador";
    }

    @PostMapping("/crear-cuenta")
    public String crearCuenta(
            @RequestParam String correo,
            @RequestParam String contrasena,
            @RequestParam int idRol,
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

            // Crear usuario
            Usuario nuevoUsuario = usuarioService.crearUsuario(correo, contrasena, idRol);

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

