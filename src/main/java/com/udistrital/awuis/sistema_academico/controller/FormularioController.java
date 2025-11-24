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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.udistrital.awuis.sistema_academico.model.Aspirante;
import com.udistrital.awuis.sistema_academico.model.Formulario;
import com.udistrital.awuis.sistema_academico.repositories.AspiranteMapper;
import com.udistrital.awuis.sistema_academico.repositories.FormularioMapper;

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
        model.addAttribute("intentos", 0);
        return "formulario";
    }

    @PostMapping("/formularios/guardar")
    public String guardarFormulario(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, Model model, RedirectAttributes redirectAttributes) {

        // Obtener contador de intentos desde parámetro enviado por JavaScript
        Integer attempts = 0;
        String intentosParam = request.getParameter("intentos");

        if (intentosParam != null && !intentosParam.isEmpty()) {
            try {
                attempts = Integer.parseInt(intentosParam);
            } catch (NumberFormatException e) {
                attempts = 0;
            }
        }


        // construir DTO/Formulario desde parámetros
        Formulario formulario = new Formulario();
        formulario.setNombreCompleto(trimNull(request.getParameter("nombre")));
        formulario.setFechaNacimiento(parseDateSafe(request.getParameter("fechaNacimiento")));
        formulario.setEdad(parseIntSafe(request.getParameter("edad")));
        formulario.setGradoAspira(trimNull(request.getParameter("grado")));
        formulario.setDireccion(trimNull(request.getParameter("direccion")));
        formulario.setTipoSangre(trimNull(request.getParameter("tipoSangre")));
        formulario.setAlergias(trimNull(request.getParameter("alergias")));
        formulario.setCondicionesMedicas(trimNull(request.getParameter("condiciones")));
        formulario.setCondicionesUsadas(trimNull(request.getParameter("condicionesUso")));
        formulario.setMedicamentos(trimNull(request.getParameter("medicamentos")));
        formulario.setObservaciones(trimNull(request.getParameter("observaciones")));
        formulario.setNombreResponsable(trimNull(request.getParameter("nombreResp")));
        formulario.setTelefonoResponsable(trimNull(request.getParameter("telefono")));
        formulario.setCorreoResponsable(trimNull(request.getParameter("correo")));
        formulario.setParentescoResponsable(trimNull(request.getParameter("parentesco")));

        // SIEMPRE enviar intentos al modelo
        model.addAttribute("intentos", attempts);
        model.addAttribute("formulario", formulario);

        // Validación básica
        String validationError = validarFormulario(formulario);

        if (validationError != null) {
            attempts++;

            // Actualizar intentos en el modelo
            model.addAttribute("intentos", attempts);

            if (attempts >= 3) {
                model.addAttribute("bloqueado", true);
                model.addAttribute("message", "Máximo de intentos alcanzado. El formulario se bloqueará por 5 segundos...");
                return "formulario";
            }
            model.addAttribute("message", "Información inválida: " + validationError + " (intento " + attempts + ")");
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
            model.addAttribute("intentos", attempts);
            model.addAttribute("message", "Error al guardar en la base de datos. Intenta más tarde.");
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
        if (f.getNombreCompleto() == null || f.getNombreCompleto().trim().isEmpty()) return "Nombre es obligatorio";
        if (f.getFechaNacimiento() == null) return "Fecha de nacimiento es obligatoria";
        if (f.getGradoAspira() == null || f.getGradoAspira().trim().isEmpty()) return "Grado al que aspira es obligatorio";
        if (f.getNombreResponsable() == null || f.getNombreResponsable().trim().isEmpty()) return "Nombre del responsable es obligatorio";
        if (f.getTelefonoResponsable() == null || f.getTelefonoResponsable().trim().isEmpty()) return "Teléfono del responsable es obligatorio";
        return null;
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
}
