package com.udistrital.awuis.sistema_academico.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import com.udistrital.awuis.sistema_academico.model.Usuario;

@Controller
public class ProfesorController {

    @GetMapping("/profesor")
    public String profesor(@SessionAttribute(value = "usuario", required = false) Usuario usuario, Model model) {
        if (usuario == null) {
            return "redirect:/login";
        }

        // Obtener el nombre del profesor del correo
        String nombreProfesor = "Profesor";
        if (usuario.getCorreo() != null) {
            String[] partesCorreo = usuario.getCorreo().split("@");
            if (partesCorreo.length > 0) {
                nombreProfesor = partesCorreo[0];
                // Capitalizar primera letra
                nombreProfesor = nombreProfesor.substring(0, 1).toUpperCase() + nombreProfesor.substring(1);
            }
        }

        model.addAttribute("nombreProfesor", nombreProfesor);
        model.addAttribute("correoProfesor", usuario.getCorreo());
        return "profesor";
    }
}

