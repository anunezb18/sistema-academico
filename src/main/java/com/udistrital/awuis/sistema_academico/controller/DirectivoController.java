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
import com.udistrital.awuis.sistema_academico.model.Directivo;
import com.udistrital.awuis.sistema_academico.model.Entrevista;
import com.udistrital.awuis.sistema_academico.model.Estudiante;
import com.udistrital.awuis.sistema_academico.model.Formulario;
import com.udistrital.awuis.sistema_academico.model.HistorialAcademico;
import com.udistrital.awuis.sistema_academico.model.Observador;
import com.udistrital.awuis.sistema_academico.model.Usuario;
import com.udistrital.awuis.sistema_academico.repositories.AspiranteMapper;
import com.udistrital.awuis.sistema_academico.repositories.DirectivoMapper;
import com.udistrital.awuis.sistema_academico.repositories.EntrevistaMapper;
import com.udistrital.awuis.sistema_academico.repositories.EstudianteMapper;
import com.udistrital.awuis.sistema_academico.repositories.FormularioMapper;
import com.udistrital.awuis.sistema_academico.repositories.HistorialAcademicoMapper;
import com.udistrital.awuis.sistema_academico.repositories.ObservadorMapper;
import com.udistrital.awuis.sistema_academico.repositories.GrupoMapper;
import com.udistrital.awuis.sistema_academico.repositories.GradoMapper;
import com.udistrital.awuis.sistema_academico.repositories.ProfesorMapper;
import com.udistrital.awuis.sistema_academico.service.EmailService;
import com.udistrital.awuis.sistema_academico.service.UsuarioService;

@Controller
public class DirectivoController {

    @Autowired
    private AspiranteMapper aspiranteMapper;

    @Autowired
    private DirectivoMapper directivoMapper;

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
    private GrupoMapper grupoMapper;

    @Autowired
    private GradoMapper gradoMapper;

    @Autowired
    private ProfesorMapper profesorMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsuarioService usuarioService;

    private static final int MARGEN_MINUTOS_ENTREVISTA = 60;

    @GetMapping("/directivo")
    public String directivo(@SessionAttribute(value = "usuario", required = false) Usuario usuario,
                           @RequestParam(value = "panel", required = false) String panel,
                           Model model) {
        if (usuario == null) {
            return "redirect:/login";
        }

        // Validar que sea directivo (idRol = 1)
        if (usuario.getToken() == null || usuario.getToken().getRol() == null) {
            return "redirect:/login";
        }

        int idRol = usuario.getToken().getRol().getIdRol();
        if (idRol != 1) {
            // No es directivo, redirigir a su panel correspondiente
            if (idRol == 2) {
                return "redirect:/profesor";
            } else if (idRol == 4) {
                return "redirect:/estudiante";
            } else if (idRol == 3) {
                return "redirect:/administrador";
            } else {
                return "redirect:/login";
            }
        }

        // Obtener el nombre del directivo desde la BD
        String nombreDirectivo = "Directivo";
        try {
            Directivo directivo = directivoMapper.findByIdUsuario(usuario.getIdUsuario()).orElse(null);
            if (directivo != null && directivo.getNombre() != null && !directivo.getNombre().trim().isEmpty()) {
                nombreDirectivo = directivo.getNombre();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener nombre del directivo: " + e.getMessage());
        }

        model.addAttribute("correoDirectivo", usuario.getCorreo());
        model.addAttribute("nombreDirectivo", nombreDirectivo);
        model.addAttribute("rolDirectivo", "Directivo");

        // Obtener estadísticas reales
        long cantidadEstudiantes = estudianteMapper.listarEstudiantes().size();
        long cantidadProfesores = profesorMapper.findAll().size();
        long cantidadGrupos = grupoMapper.findAll().size();
        long cantidadAspirantes = aspiranteMapper.listarAspirantes().stream()
            .filter(a -> a.getFormulario() != null && "PENDIENTE".equals(a.getFormulario().getEstado()))
            .count();

        model.addAttribute("cantidadEstudiantes", cantidadEstudiantes);
        model.addAttribute("cantidadProfesores", cantidadProfesores);
        model.addAttribute("cantidadGrupos", cantidadGrupos);
        model.addAttribute("cantidadAspirantes", cantidadAspirantes);

        // Determinar qué panel mostrar
        String panelAMostrar = (panel != null && !panel.trim().isEmpty()) ? panel : "principal";
        model.addAttribute("panelInicial", panelAMostrar);

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

    /**
     * API REST: Obtener todos los estudiantes agrupados por grupo
     */
    @GetMapping("/directivo/api/estudiantes-por-grupo")
    @ResponseBody
    public List<java.util.Map<String, Object>> obtenerEstudiantesPorGrupo() {
        try {
            List<Estudiante> todosEstudiantes = estudianteMapper.listarEstudiantes();

            return todosEstudiantes.stream()
                .map(estudiante -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("idEstudiante", estudiante.getIdEstudiante());
                    map.put("idGrupo", estudiante.getIdGrupo());

                    // Obtener nombre del estudiante desde el formulario
                    if (estudiante.getIdFormulario() != null) {
                        try {
                            Formulario formulario = formularioMapper.obtenerPorId(estudiante.getIdFormulario());
                            if (formulario != null) {
                                map.put("nombre", formulario.getNombreCompleto());
                            } else {
                                map.put("nombre", "Sin nombre");
                            }
                        } catch (Exception e) {
                            map.put("nombre", "Sin nombre");
                        }
                    } else {
                        map.put("nombre", "Sin nombre");
                    }

                    // Obtener nombre del grupo
                    if (estudiante.getIdGrupo() != null) {
                        try {
                            var grupo = grupoMapper.findById(estudiante.getIdGrupo()).orElse(null);
                            if (grupo != null) {
                                var grado = gradoMapper.findById(grupo.getIdGrado()).orElse(null);
                                if (grado != null) {
                                    map.put("nombreGrupo", grado.getNombre() + " " + grupo.getNombre());
                                } else {
                                    map.put("nombreGrupo", grupo.getNombre());
                                }
                            } else {
                                map.put("nombreGrupo", "Sin grupo");
                            }
                        } catch (Exception e) {
                            map.put("nombreGrupo", "Sin grupo");
                        }
                    } else {
                        map.put("nombreGrupo", "Sin grupo");
                    }

                    return map;
                }).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error al obtener estudiantes por grupo: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    /**
     * API REST: Obtener hoja de vida de un estudiante
     */
    @GetMapping("/directivo/api/estudiante/{id}/hoja-vida")
    @ResponseBody
    public java.util.Map<String, Object> obtenerHojaVidaEstudiante(@PathVariable int id) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();

        try {
            Estudiante estudiante = estudianteMapper.obtenerPorId(id);
            if (estudiante == null) {
                response.put("success", false);
                response.put("message", "Estudiante no encontrado");
                return response;
            }

            response.put("success", true);
            response.put("idEstudiante", estudiante.getIdEstudiante());
            response.put("fechaIngreso", estudiante.getFechaIngreso());

            // Obtener información del grupo
            if (estudiante.getIdGrupo() != null) {
                try {
                    var grupo = grupoMapper.findById(estudiante.getIdGrupo()).orElse(null);
                    if (grupo != null) {
                        var grado = gradoMapper.findById(grupo.getIdGrado()).orElse(null);
                        if (grado != null) {
                            response.put("nombreGrupo", grado.getNombre() + " " + grupo.getNombre());
                        } else {
                            response.put("nombreGrupo", grupo.getNombre());
                        }
                    }
                } catch (Exception e) {
                    response.put("nombreGrupo", "Sin grupo");
                }
            } else {
                response.put("nombreGrupo", "Sin grupo");
            }

            // Obtener información del formulario
            if (estudiante.getIdFormulario() != null) {
                try {
                    Formulario formulario = formularioMapper.obtenerPorId(estudiante.getIdFormulario());
                    if (formulario != null) {
                        response.put("nombreCompleto", formulario.getNombreCompleto());
                        response.put("fechaNacimiento", formulario.getFechaNacimiento());
                        response.put("edad", formulario.getEdad());
                        response.put("tipoSangre", formulario.getTipoSangre());
                        response.put("direccion", formulario.getDireccion());
                        response.put("gradoAspira", formulario.getGradoAspira());
                        response.put("nombreResponsable", formulario.getNombreResponsable());
                        response.put("correoResponsable", formulario.getCorreoResponsable());
                        response.put("telefonoResponsable", formulario.getTelefonoResponsable());
                        response.put("parentescoResponsable", formulario.getParentescoResponsable());
                        response.put("alergias", formulario.getAlergias());
                        response.put("condicionesMedicas", formulario.getCondicionesMedicas());
                        response.put("medicamentos", formulario.getMedicamentos());
                        response.put("observaciones", formulario.getObservaciones());
                    }
                } catch (Exception e) {
                    System.err.println("Error al obtener formulario: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Error al obtener hoja de vida: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al obtener hoja de vida: " + e.getMessage());
        }

        return response;
    }

    /**
     * API REST: Obtener todos los estudiantes con hoja de vida
     */
    @GetMapping("/directivo/estudiantes-hoja-vida")
    @ResponseBody
    public List<java.util.Map<String, Object>> obtenerEstudiantesHojaVida() {
        try {
            List<Estudiante> estudiantes = estudianteMapper.listarEstudiantes();

            return estudiantes.stream()
                .map(estudiante -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("idEstudiante", estudiante.getIdEstudiante());
                    map.put("correo", estudiante.getCorreo());

                    // Obtener nombre del grupo
                    if (estudiante.getIdGrupo() != null) {
                        try {
                            var grupo = grupoMapper.findById(estudiante.getIdGrupo()).orElse(null);
                            if (grupo != null) {
                                var grado = gradoMapper.findById(grupo.getIdGrado()).orElse(null);
                                if (grado != null) {
                                    map.put("grupoNombre", grado.getNombre() + " " + grupo.getNombre());
                                } else {
                                    map.put("grupoNombre", grupo.getNombre());
                                }
                            } else {
                                map.put("grupoNombre", "Sin Grupo");
                            }
                        } catch (Exception e) {
                            map.put("grupoNombre", "Sin Grupo");
                        }
                    } else {
                        map.put("grupoNombre", "Sin Grupo");
                    }

                    // Obtener nombre del estudiante desde el formulario
                    if (estudiante.getIdFormulario() != null) {
                        try {
                            Formulario formulario = formularioMapper.obtenerPorId(estudiante.getIdFormulario());
                            if (formulario != null) {
                                map.put("nombre", formulario.getNombreCompleto());
                            } else {
                                map.put("nombre", "Sin nombre");
                            }
                        } catch (Exception e) {
                            map.put("nombre", "Sin nombre");
                        }
                    } else {
                        map.put("nombre", "Sin nombre");
                    }

                    return map;
                }).collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error al obtener estudiantes para hoja de vida: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    /**
     * API REST: Obtener detalle completo de un estudiante para hoja de vida
     */
    @GetMapping("/directivo/estudiante-detalle/{id}")
    @ResponseBody
    public java.util.Map<String, Object> obtenerDetalleEstudiante(@PathVariable int id) {
        java.util.Map<String, Object> response = new java.util.HashMap<>();

        try {
            Estudiante estudiante = estudianteMapper.obtenerPorId(id);
            if (estudiante == null) {
                response.put("error", "Estudiante no encontrado");
                return response;
            }

            response.put("idEstudiante", estudiante.getIdEstudiante());
            response.put("correo", estudiante.getCorreo());
            response.put("fechaIngreso", estudiante.getFechaIngreso() != null ?
                estudiante.getFechaIngreso().toString() : "No registrada");

            // Obtener información del grupo
            if (estudiante.getIdGrupo() != null) {
                try {
                    var grupo = grupoMapper.findById(estudiante.getIdGrupo()).orElse(null);
                    if (grupo != null) {
                        var grado = gradoMapper.findById(grupo.getIdGrado()).orElse(null);
                        if (grado != null) {
                            response.put("grupoNombre", grado.getNombre() + " " + grupo.getNombre());
                        } else {
                            response.put("grupoNombre", grupo.getNombre());
                        }
                    } else {
                        response.put("grupoNombre", "Sin grupo asignado");
                    }
                } catch (Exception e) {
                    response.put("grupoNombre", "Sin grupo asignado");
                }
            } else {
                response.put("grupoNombre", "Sin grupo asignado");
            }

            // Obtener información del formulario
            if (estudiante.getIdFormulario() != null) {
                try {
                    Formulario formulario = formularioMapper.obtenerPorId(estudiante.getIdFormulario());
                    if (formulario != null) {
                        response.put("nombre", formulario.getNombreCompleto());
                        response.put("fechaNacimiento", formulario.getFechaNacimiento() != null ?
                            formulario.getFechaNacimiento().toString() : "No registrada");
                        response.put("edad", formulario.getEdad() != null ? formulario.getEdad().toString() : "No registrada");
                        response.put("tipoSangre", formulario.getTipoSangre() != null ? formulario.getTipoSangre() : "No registrado");
                        response.put("direccion", formulario.getDireccion() != null ? formulario.getDireccion() : "No registrada");
                        response.put("telefono", "No registrado"); // El estudiante no tiene teléfono directo
                        response.put("gradoAspira", formulario.getGradoAspira() != null ? formulario.getGradoAspira() : "No registrado");
                        response.put("nombreResponsable", formulario.getNombreResponsable() != null ? formulario.getNombreResponsable() : "No registrado");
                        response.put("correoResponsable", formulario.getCorreoResponsable() != null ? formulario.getCorreoResponsable() : "No registrado");
                        response.put("telefonoResponsable", formulario.getTelefonoResponsable() != null ? formulario.getTelefonoResponsable() : "No registrado");
                        response.put("parentescoResponsable", formulario.getParentescoResponsable() != null ? formulario.getParentescoResponsable() : "No registrado");
                        response.put("alergias", formulario.getAlergias() != null ? formulario.getAlergias() : "Ninguna registrada");
                        response.put("condicionesMedicas", formulario.getCondicionesMedicas() != null ? formulario.getCondicionesMedicas() : "Ninguna registrada");
                        response.put("medicamentos", formulario.getMedicamentos() != null ? formulario.getMedicamentos() : "Ninguno registrado");
                        response.put("observaciones", formulario.getObservaciones());
                    } else {
                        response.put("nombre", "Sin nombre");
                    }
                } catch (Exception e) {
                    System.err.println("Error al obtener formulario: " + e.getMessage());
                    response.put("nombre", "Sin nombre");
                }
            } else {
                response.put("nombre", "Sin nombre");
            }

        } catch (Exception e) {
            System.err.println("Error al obtener detalle del estudiante: " + e.getMessage());
            e.printStackTrace();
            response.put("error", "Error al obtener información del estudiante");
        }

        return response;
    }
}

