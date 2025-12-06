package com.udistrital.awuis.sistema_academico.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.udistrital.awuis.sistema_academico.model.Aspirante;
import com.udistrital.awuis.sistema_academico.model.Entrevista;
import com.udistrital.awuis.sistema_academico.model.Estudiante;
import com.udistrital.awuis.sistema_academico.model.Formulario;
import com.udistrital.awuis.sistema_academico.model.HistorialAcademico;
import com.udistrital.awuis.sistema_academico.model.Observador;
import com.udistrital.awuis.sistema_academico.model.Usuario;
import com.udistrital.awuis.sistema_academico.repositories.AspiranteMapper;
import com.udistrital.awuis.sistema_academico.repositories.EntrevistaMapper;
import com.udistrital.awuis.sistema_academico.repositories.EstudianteMapper;
import com.udistrital.awuis.sistema_academico.repositories.FormularioMapper;
import com.udistrital.awuis.sistema_academico.repositories.HistorialAcademicoMapper;
import com.udistrital.awuis.sistema_academico.repositories.ObservadorMapper;
import com.udistrital.awuis.sistema_academico.service.EmailService;
import com.udistrital.awuis.sistema_academico.service.UsuarioService;

@Controller
public class DirectivoController {

    @Autowired
    private AspiranteMapper aspiranteMapper;

    @Autowired
    private EstudianteMapper estudianteMapper;

    @Autowired
    private EntrevistaMapper entrevistaMapper;

    @Autowired
    private FormularioMapper formularioMapper;

    @Autowired
    private HistorialAcademicoMapper historialAcademicoMapper;

    @Autowired
    private ObservadorMapper observadorMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsuarioService usuarioService;

    private static final int MARGEN_MINUTOS_ENTREVISTA = 60;

    @GetMapping("/directivo")
    public String directivo(@SessionAttribute(value = "usuario", required = false) Usuario usuario, Model model) {
        if (usuario == null) {
            return "redirect:/login";
        }
        model.addAttribute("correoDirectivo", usuario.getCorreo());
        return "directivo";
    }

    @GetMapping("/directivo/aspirantes")
    public String aspirantes(@SessionAttribute(value = "usuario", required = false) Usuario usuario, Model model) {
        if (usuario == null) {
            return "redirect:/login";
        }
        List<Aspirante> aspirantes = aspiranteMapper.listarAspirantes();
        model.addAttribute("aspirantes", aspirantes);
        model.addAttribute("correoDirectivo", usuario.getCorreo());
        return "aspirantes";
    }

    @GetMapping("/directivo/aspirantes/buscar")
    @ResponseBody
    public List<Aspirante> buscarAspirantes(@RequestParam String query) {
        List<Aspirante> todos = aspiranteMapper.listarAspirantes();

        // Filtrar por nombre o grado
        return todos.stream()
            .filter(a -> a.getFormulario() != null && (
                a.getFormulario().getNombreCompleto().toLowerCase().contains(query.toLowerCase()) ||
                a.getFormulario().getGradoAspira().toLowerCase().equals(query.toLowerCase())
            ))
            .collect(Collectors.toList());
    }

    @GetMapping("/directivo/aspirantes/{id}/revisar")
    public String revisarAspirante(@PathVariable int id, Model model) {
        Aspirante aspirante = aspiranteMapper.obtenerAspirantePorId(id);
        if (aspirante == null) {
            return "redirect:/directivo/aspirantes";
        }
        model.addAttribute("aspirante", aspirante);
        return "revisar-aspirante";
    }

    @PostMapping("/directivo/aspirantes/{id}/aceptar")
    public String aceptarAspirante(
            @PathVariable int id,
            @RequestParam(required = false) String fechaHoraEntrevista,
            RedirectAttributes redirectAttributes) {

        try {
            // PASO 1: Seleccionar opción "Aceptar estudiante" - Obtener aspirante
            Aspirante aspirante = aspiranteMapper.obtenerAspirantePorId(id);
            if (aspirante == null || aspirante.getFormulario() == null) {
                redirectAttributes.addFlashAttribute("error",
                    "Aspirante no encontrado o sin formulario asociado");
                return "redirect:/directivo/aspirantes";
            }

            Formulario formulario = aspirante.getFormulario();

            // Verificar si ya fue aceptado previamente
            if (estudianteMapper.obtenerPorIdFormulario(formulario.getIdFormulario()) != null) {
                redirectAttributes.addFlashAttribute("error",
                    "Este aspirante ya ha sido aceptado como estudiante");
                return "redirect:/directivo/aspirantes";
            }

            // PASO 2: Crear registro del aspirante en la tabla Estudiante

            // 1. Crear Observador vacío primero
            Observador observador = new Observador();
            observador = observadorMapper.save(observador);

            // 2. Crear HistorialAcademico con el ID del observador
            HistorialAcademico historial = new HistorialAcademico();
            historial.setIdObservador(observador.getIdObservador());
            historial = historialAcademicoMapper.save(historial);

            // 3. Crear Estudiante con todos los datos requeridos
            Estudiante estudiante = new Estudiante();
            estudiante.setIdFormulario(formulario.getIdFormulario());
            estudiante.setFechaIngreso(LocalDate.now());
            estudiante.setIdHistorialAcademico(historial.getIdHistorialAcademico());

            // NUEVO: Crear usuario con credenciales para el estudiante
            com.udistrital.awuis.sistema_academico.model.Usuario usuarioEstudiante =
                usuarioService.crearEstudianteDesdeAspirante(
                    formulario.getCorreoResponsable(),
                    formulario.getNombreCompleto()
                );

            // Asignar el ID de usuario al estudiante
            estudiante.setIdUsuario(usuarioEstudiante.getIdUsuario());

            estudianteMapper.agregarEstudiante(estudiante);


            // PASO 4: Asignar fecha y hora de la entrevista
            LocalDateTime fechaHora;
            if (fechaHoraEntrevista != null && !fechaHoraEntrevista.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                fechaHora = LocalDateTime.parse(fechaHoraEntrevista, formatter);
            } else {
                // Fecha por defecto: 3 días después a las 10:00 AM
                fechaHora = LocalDateTime.now().plusDays(3).withHour(10).withMinute(0).withSecond(0);
            }

            // PASO 5 y 6: Verificar que no presente cruce con otras entrevistas
            int intentos = 0;
            int maxIntentos = 10;
            boolean entrevistaAgendada = false;

            while (intentos < maxIntentos && !entrevistaAgendada) {
                // PASO 6: Decisión - ¿Presenta cruce?
                boolean presentaCruce = entrevistaMapper.verificarCruceHorario(
                    fechaHora, MARGEN_MINUTOS_ENTREVISTA);

                if (!presentaCruce) {
                    // No hay cruce -> Guardar en el calendario
                    Entrevista entrevista = new Entrevista(id, fechaHora, "PROGRAMADA");
                    entrevistaMapper.agregarEntrevista(entrevista);
                    entrevistaAgendada = true;
                } else {
                    // Sí hay cruce -> Volver a asignar fecha y hora (incrementar 1 hora)
                    fechaHora = fechaHora.plusHours(1);
                    intentos++;
                }
            }

            if (!entrevistaAgendada) {
                throw new Exception("No se pudo encontrar un horario disponible para la entrevista");
            }

            // PASO 7: Enviar notificación por correo
            boolean correoEnviado = emailService.enviarNotificacionEntrevista(
                formulario.getNombreCompleto(),
                formulario.getCorreoResponsable(),
                fechaHora
            );

            if (!correoEnviado) {
                System.err.println("Advertencia: No se pudo enviar el correo de notificacion");
            }

            // Actualizar estado del formulario a ACEPTADO
            formulario.setEstado("ACEPTADO");
            formularioMapper.actualizarFormulario(formulario);

            // PASO 8: Mostrar "Estudiante aceptado"
            redirectAttributes.addFlashAttribute("mensaje",
                "Estudiante aceptado exitosamente. Se ha enviado notificacion por correo.");
            redirectAttributes.addFlashAttribute("estudianteId", estudiante.getIdEstudiante());

        } catch (Exception e) {
            // PASO 3 (EXCEPCIÓN): Mostrar mensaje de error con opción de aceptar
            redirectAttributes.addFlashAttribute("error",
                "Fallo en la base de datos: " + e.getMessage());
            System.err.println("Error al aceptar aspirante: " + e.getMessage());
            e.printStackTrace();
        }

        // PASO 9: Volver a la lista de aspirantes
        return "redirect:/directivo/aspirantes";
    }

    /**
     * Rechaza un aspirante según el diagrama de actividades
     *
     * @param id ID del aspirante
     * @param razonRechazo Razón del rechazo (requerida)
     * @param redirectAttributes Atributos para mensajes flash
     * @return Redirección a la lista de aspirantes
     */
    @PostMapping("/directivo/aspirantes/{id}/rechazar")
    public String rechazarAspirante(
            @PathVariable int id,
            @RequestParam(required = false) String razonRechazo,
            RedirectAttributes redirectAttributes) {

        try {
            // PASO 1: Seleccionar opción "Rechazar estudiante" - Obtener aspirante
            Aspirante aspirante = aspiranteMapper.obtenerAspirantePorId(id);
            if (aspirante == null || aspirante.getFormulario() == null) {
                redirectAttributes.addFlashAttribute("error",
                    "Aspirante no encontrado o sin formulario asociado");
                return "redirect:/directivo/aspirantes";
            }

            Formulario formulario = aspirante.getFormulario();

            // Validar que el aspirante no haya sido procesado previamente
            if ("ACEPTADO".equals(formulario.getEstado())) {
                redirectAttributes.addFlashAttribute("error",
                    "No se puede rechazar un aspirante que ya fue aceptado");
                return "redirect:/directivo/aspirantes";
            }

            if ("RECHAZADO".equals(formulario.getEstado())) {
                redirectAttributes.addFlashAttribute("error",
                    "Este aspirante ya ha sido rechazado previamente");
                return "redirect:/directivo/aspirantes";
            }

            // PASO 2: Insertar razón del rechazo (validación)
            if (razonRechazo == null || razonRechazo.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error",
                    "Debe proporcionar una razon para el rechazo");
                return "redirect:/directivo/aspirantes/" + id + "/revisar";
            }

            // Variable para tracking de errores de correo
            boolean correoEnviado = false;

            // PASO 3: Enviar correo al aspirante con la razón del rechazo
            try {
                correoEnviado = emailService.enviarNotificacionRechazo(
                    formulario.getNombreCompleto(),
                    formulario.getCorreoResponsable(),
                    razonRechazo
                );

                if (!correoEnviado) {
                    // PASO 4 (EXCEPCIÓN): Error en el envío del correo
                    System.err.println("ADVERTENCIA: No se pudo enviar el correo de rechazo");
                    // Continuar de todas formas (no es crítico)
                }

            } catch (Exception e) {
                // PASO 4 (EXCEPCIÓN): Error en el envío del correo
                System.err.println("ERROR al enviar correo de rechazo: " + e.getMessage());
                // Continuar de todas formas
            }

            // PASO 5: Cambiar estado del aspirante a "Rechazado"
            formulario.setEstado("RECHAZADO");
            formulario.setObservaciones(razonRechazo); // Guardar la razón

            formularioMapper.actualizarFormulario(formulario);

            // Mensaje de éxito con advertencia si el correo falló
            if (correoEnviado) {
                redirectAttributes.addFlashAttribute("mensaje",
                    "Aspirante rechazado exitosamente. Se ha enviado notificacion por correo.");
            } else {
                redirectAttributes.addFlashAttribute("mensaje",
                    "Aspirante rechazado. ADVERTENCIA: No se pudo enviar el correo. Intente nuevamente.");
            }

        } catch (Exception e) {
            // PASO 6 (EXCEPCIÓN): Error al cambiar estado
            redirectAttributes.addFlashAttribute("error",
                "No se pudo actualizar el estado del aspirante: " + e.getMessage());
            System.err.println("Error al rechazar aspirante: " + e.getMessage());
            e.printStackTrace();
        }

        // PASO 7: Volver a la lista de aspirantes
        return "redirect:/directivo/aspirantes";
    }
}

