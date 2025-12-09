package com.udistrital.awuis.sistema_academico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import com.udistrital.awuis.sistema_academico.model.Usuario;
import com.udistrital.awuis.sistema_academico.repositories.EstudianteMapper;
import com.udistrital.awuis.sistema_academico.repositories.FormularioMapper;
import com.udistrital.awuis.sistema_academico.model.Estudiante;
import com.udistrital.awuis.sistema_academico.model.Formulario;

@Controller
public class EstudianteController {

    @Autowired
    private EstudianteMapper estudianteMapper;

    @Autowired
    private FormularioMapper formularioMapper;

    @GetMapping("/estudiante")
    public String estudiante(@SessionAttribute(value = "usuario", required = false) Usuario usuario, Model model) {
        if (usuario == null) {
            return "redirect:/login";
        }

        // Validar que sea estudiante (idRol = 4)
        if (usuario.getToken() == null || usuario.getToken().getRol() == null) {
            return "redirect:/login";
        }

        int idRol = usuario.getToken().getRol().getIdRol();
        if (idRol != 4) {
            // No es estudiante, redirigir a su panel correspondiente
            if (idRol == 1 || idRol == 3) {
                return "redirect:/directivo";
            } else if (idRol == 2) {
                return "redirect:/profesor";
            } else {
                return "redirect:/administrador";
            }
        }

        // Buscar el estudiante por idUsuario
        String nombreEstudiante = "Estudiante";
        String correoEstudiante = usuario.getCorreo();

        try {
            // Buscar estudiante por idUsuario usando el método del mapper
            Estudiante estudiante = estudianteMapper.findByIdUsuario(usuario.getIdUsuario()).orElse(null);

            if (estudiante != null && estudiante.getIdFormulario() != null) {
                // Obtener el nombre del formulario asociado
                Formulario formulario = formularioMapper.obtenerPorId(estudiante.getIdFormulario());
                if (formulario != null && formulario.getNombreCompleto() != null) {
                    nombreEstudiante = formulario.getNombreCompleto();
                }
            } else if (estudiante == null) {
                // Si no se encuentra el estudiante, usar el correo
                nombreEstudiante = usuario.getCorreo().split("@")[0];
                nombreEstudiante = nombreEstudiante.substring(0, 1).toUpperCase() +
                                 nombreEstudiante.substring(1);
            }
        } catch (Exception e) {
            System.err.println("Error al buscar estudiante: " + e.getMessage());
            // Si hay error, usar el correo sin @dominio
            nombreEstudiante = usuario.getCorreo().split("@")[0];
            nombreEstudiante = nombreEstudiante.substring(0, 1).toUpperCase() +
                             nombreEstudiante.substring(1);
        }

        model.addAttribute("nombreEstudiante", nombreEstudiante);
        model.addAttribute("correoEstudiante", correoEstudiante);
        return "estudiante";
    }
}

