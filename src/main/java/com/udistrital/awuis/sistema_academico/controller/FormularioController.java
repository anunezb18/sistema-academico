package com.udistrital.awuis.sistema_academico.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.udistrital.awuis.sistema_academico.model.Aspirante;
import com.udistrital.awuis.sistema_academico.model.Formulario;
import com.udistrital.awuis.sistema_academico.model.Grado;
import com.udistrital.awuis.sistema_academico.repositories.AspiranteMapper;
import com.udistrital.awuis.sistema_academico.repositories.FormularioMapper;
import com.udistrital.awuis.sistema_academico.repositories.GradoMapper;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class FormularioController {

    private static final String SESSION_ATTEMPTS = "formAttempts";
    // Rastrear intentos por IP con timestamp (key: IP, value: {attempts, timestamp})
    private static final Map<String, Map<String, Object>> ATTEMPTS_TRACKER = new HashMap<>();
    private static final long ATTEMPT_RESET_TIME = 5 * 60 * 1000; // 5 minutos

    @Autowired
    private FormularioMapper formularioMapper;

    @Autowired
    private AspiranteMapper aspiranteMapper;

    @Autowired
    private GradoMapper gradoMapper;

    // setters para pruebas
    public void setFormularioMapper(FormularioMapper formularioMapper) {
        this.formularioMapper = formularioMapper;
    }

    public void setAspiranteMapper(AspiranteMapper aspiranteMapper) {
        this.aspiranteMapper = aspiranteMapper;
    }

    @GetMapping("/formulario")
    public String mostrarFormulario(Model model, @ModelAttribute("message") String message) {
        if (!model.containsAttribute("formulario")) {
            model.addAttribute("formulario", new Formulario());
        }
        model.addAttribute("message", message);
        return "formulario";
    }

    @PostMapping("/formularios/guardar")
    public String guardarFormulario(@ModelAttribute("formulario") Formulario formulario,
            Model model, RedirectAttributes redirectAttributes) {

        // Validación básica
        String validationError = validarFormulario(formulario);

        if (validationError != null) {
            model.addAttribute("error", "Información inválida: " + validationError);
            model.addAttribute("formulario", formulario);
            return "formulario";
        }

        // Si válido, intentar persistir
        try {
            formulario.setEstado("PENDIENTE");
            formulario.setCreadoEn(java.time.OffsetDateTime.now());
            formularioMapper.agregarFormulario(formulario);

            Aspirante a = new Aspirante();
            a.setFormulario(formulario);
            aspiranteMapper.agregarAspirante(a);

            redirectAttributes.addFlashAttribute("message", "¡Información guardada con éxito!");
            redirectAttributes.addFlashAttribute("formularioId", formulario.getIdFormulario());
            return "redirect:/formulario/confirmacion";
        } catch (DataAccessException ex) {
            model.addAttribute("formulario", formulario);
            model.addAttribute("error", "Error al guardar en la base de datos. Intenta más tarde.");
            return "formulario";
        }
    }

    @GetMapping("/formulario/confirmacion")
    public String mostrarConfirmacion(Model model, @ModelAttribute("message") String message, @ModelAttribute("formularioId") Integer formularioId) {
        model.addAttribute("message", message);
        model.addAttribute("formularioId", formularioId);
        return "confirmacion";
    }

    @PostMapping("/formulario/confirmacion")
    public String aceptarConfirmacion() {
        return "redirect:/";
    }

    private String validarFormulario(Formulario f) {
        // Validación mínima - HTML5 ya valida campos requeridos
        // Solo validamos que los datos esenciales existan y tengan formato correcto
        if (f.getNombreCompleto() != null && f.getNombreCompleto().trim().length() > 100) {
            return "El nombre es demasiado largo (máximo 100 caracteres)";
        }
        if (f.getCorreoResponsable() != null && !f.getCorreoResponsable().contains("@")) {
            return "El correo electrónico no es válido";
        }
        return null; // Todo OK
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    private String trimNull(String s) {
        return s == null ? null : s.trim();
    }

    private Integer parseIntSafe(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Integer.parseInt(s); } catch (NumberFormatException ex) { return null; }
    }

    private java.time.LocalDate parseDateSafe(String s) {
        if (s == null || s.isBlank()) return null;
        try { return java.time.LocalDate.parse(s); } catch (DateTimeParseException ex) { return null; }
    }

    /**
     * API REST: Obtener grados disponibles desde la base de datos
     */
    @GetMapping("/api/grados")
    @ResponseBody
    public List<String> obtenerGrados() {
        try {
            return gradoMapper.findAll().stream()
                .map(Grado::getNombre)
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error al obtener grados: " + e.getMessage());
            // Retornar grados por defecto en caso de error
            return List.of("Semillas", "Jardin", "Transicion", "Primero");
        }
    }
}
