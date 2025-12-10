package com.udistrital.awuis.sistema_academico.controller;

import java.util.List;
import java.util.Optional;
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

import com.udistrital.awuis.sistema_academico.model.Boletin;
import com.udistrital.awuis.sistema_academico.model.CategoriaLogro;
import com.udistrital.awuis.sistema_academico.model.Estudiante;
import com.udistrital.awuis.sistema_academico.model.Formulario;
import com.udistrital.awuis.sistema_academico.model.HistorialAcademico;
import com.udistrital.awuis.sistema_academico.model.Logro;
import com.udistrital.awuis.sistema_academico.model.LogroBoletin;
import com.udistrital.awuis.sistema_academico.model.Profesor;
import com.udistrital.awuis.sistema_academico.model.Usuario;
import com.udistrital.awuis.sistema_academico.repositories.BoletinMapper;
import com.udistrital.awuis.sistema_academico.repositories.CategoriaLogroMapper;
import com.udistrital.awuis.sistema_academico.repositories.EstudianteMapper;
import com.udistrital.awuis.sistema_academico.repositories.FormularioMapper;
import com.udistrital.awuis.sistema_academico.repositories.GradoMapper;
import com.udistrital.awuis.sistema_academico.repositories.GrupoMapper;
import com.udistrital.awuis.sistema_academico.repositories.HistorialAcademicoMapper;
import com.udistrital.awuis.sistema_academico.repositories.LogroBoletinMapper;
import com.udistrital.awuis.sistema_academico.repositories.LogroMapper;
import com.udistrital.awuis.sistema_academico.repositories.ObservadorMapper;
import com.udistrital.awuis.sistema_academico.repositories.ProfesorMapper;

@Controller
public class ProfesorController {

    @Autowired
    private ProfesorMapper profesorMapper;

    @Autowired
    private EstudianteMapper estudianteMapper;

    @Autowired
    private FormularioMapper formularioMapper;

    @Autowired
    private GrupoMapper grupoMapper;

    @Autowired
    private GradoMapper gradoMapper;

    @Autowired
    private HistorialAcademicoMapper historialAcademicoMapper;

    @Autowired
    private ObservadorMapper observadorMapper;

    @Autowired
    private CategoriaLogroMapper categoriaLogroMapper;

    @Autowired
    private LogroMapper logroMapper;

    @Autowired
    private BoletinMapper boletinMapper;

    @Autowired
    private LogroBoletinMapper logroBoletinMapper;

    @GetMapping("/profesor")
    public String profesor(@SessionAttribute(value = "usuario", required = false) Usuario usuario, Model model) {
        if (usuario == null) {
            return "redirect:/login";
        }

        // Validar que sea profesor (idRol = 2)
        if (usuario.getToken() == null || usuario.getToken().getRol() == null) {
            return "redirect:/login";
        }

        int idRol = usuario.getToken().getRol().getIdRol();
        if (idRol != 2) {
            // No es profesor, redirigir a su panel correspondiente
            if (idRol == 1 || idRol == 3) {
                return "redirect:/directivo";
            } else if (idRol == 4) {
                return "redirect:/estudiante";
            } else {
                return "redirect:/administrador";
            }
        }

        // Obtener el nombre del profesor desde la BD
        String nombreProfesor = "Profesor";
        try {
            Profesor profesor = profesorMapper.findByIdUsuario(usuario.getIdUsuario()).orElse(null);
            if (profesor != null && profesor.getNombre() != null && !profesor.getNombre().trim().isEmpty()) {
                nombreProfesor = profesor.getNombre();
            }
        } catch (Exception e) {
            System.err.println("Error al obtener nombre del profesor: " + e.getMessage());
        }

        model.addAttribute("nombreProfesor", nombreProfesor);
        model.addAttribute("correoProfesor", usuario.getCorreo());
        model.addAttribute("rolProfesor", "Profesor");
        return "profesor";
    }

    /**
     * API REST: Obtener todos los estudiantes agrupados por grupo (solo del grupo del profesor)
     */
    @GetMapping("/profesor/api/estudiantes-por-grupo")
    @ResponseBody
    public List<java.util.Map<String, Object>> obtenerEstudiantesPorGrupo(
            @SessionAttribute(value = "usuario", required = false) Usuario usuario) {

        if (usuario == null) {
            return new java.util.ArrayList<>();
        }

        try {
            // Obtener el profesor
            Profesor profesor = profesorMapper.findByIdUsuario(usuario.getIdUsuario()).orElse(null);
            if (profesor == null) {
                return new java.util.ArrayList<>();
            }

            // Obtener el grupo del profesor
            var grupoProfesor = grupoMapper.findByIdProfesor(profesor.getIdProfesor()).orElse(null);
            if (grupoProfesor == null) {
                return new java.util.ArrayList<>();
            }

            // Obtener estudiantes del grupo
            List<Estudiante> estudiantesDelGrupo = estudianteMapper.listarEstudiantes().stream()
                .filter(e -> e.getIdGrupo() != null && e.getIdGrupo() == grupoProfesor.getIdGrupo())
                .collect(Collectors.toList());

            return estudiantesDelGrupo.stream()
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
                    try {
                        var grado = gradoMapper.findById(grupoProfesor.getIdGrado()).orElse(null);
                        if (grado != null) {
                            map.put("nombreGrupo", grado.getNombre() + " " + grupoProfesor.getNombre());
                        } else {
                            map.put("nombreGrupo", grupoProfesor.getNombre());
                        }
                    } catch (Exception e) {
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
    @GetMapping("/profesor/api/estudiante/{id}/hoja-vida")
    @ResponseBody
    public java.util.Map<String, Object> obtenerHojaVidaEstudiante(
            @SessionAttribute(value = "usuario", required = false) Usuario usuario,
            @PathVariable int id) {

        java.util.Map<String, Object> response = new java.util.HashMap<>();

        if (usuario == null) {
            response.put("success", false);
            response.put("message", "Usuario no autenticado");
            return response;
        }

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
     * API REST: Obtener lista de estudiantes para hoja de vida (solo del grupo del profesor)
     */
    @GetMapping("/profesor/estudiantes-hoja-vida")
    @ResponseBody
    public List<java.util.Map<String, Object>> obtenerEstudiantesHojaVidaProf(
            @SessionAttribute(value = "usuario", required = false) Usuario usuario) {

        if (usuario == null) {
            return new java.util.ArrayList<>();
        }

        try {
            // Obtener el profesor
            Profesor profesor = profesorMapper.findByIdUsuario(usuario.getIdUsuario()).orElse(null);
            if (profesor == null) {
                return new java.util.ArrayList<>();
            }

            // Obtener el grupo del profesor
            var grupoProfesor = grupoMapper.findByIdProfesor(profesor.getIdProfesor()).orElse(null);
            if (grupoProfesor == null) {
                return new java.util.ArrayList<>();
            }

            // Obtener estudiantes del grupo
            List<Estudiante> estudiantesDelGrupo = estudianteMapper.listarEstudiantes().stream()
                .filter(e -> e.getIdGrupo() != null && e.getIdGrupo() == grupoProfesor.getIdGrupo())
                .collect(Collectors.toList());

            return estudiantesDelGrupo.stream()
                .map(estudiante -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("idEstudiante", estudiante.getIdEstudiante());

                    // Obtener nombre y correo del estudiante desde el formulario
                    if (estudiante.getIdFormulario() != null) {
                        try {
                            Formulario formulario = formularioMapper.obtenerPorId(estudiante.getIdFormulario());
                            if (formulario != null) {
                                map.put("nombre", formulario.getNombreCompleto());
                                map.put("correo", formulario.getCorreoResponsable());
                            } else {
                                map.put("nombre", "Sin nombre");
                                map.put("correo", "Sin correo");
                            }
                        } catch (Exception e) {
                            map.put("nombre", "Sin nombre");
                            map.put("correo", "Sin correo");
                        }
                    } else {
                        map.put("nombre", "Sin nombre");
                        map.put("correo", "Sin correo");
                    }

                    // Obtener nombre del grupo
                    try {
                        var grado = gradoMapper.findById(grupoProfesor.getIdGrado()).orElse(null);
                        if (grado != null) {
                            map.put("grupoNombre", grado.getNombre() + " " + grupoProfesor.getNombre());
                        } else {
                            map.put("grupoNombre", grupoProfesor.getNombre());
                        }
                    } catch (Exception e) {
                        map.put("grupoNombre", "Sin grupo");
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
     * API REST: Obtener detalles completos de un estudiante para hoja de vida
     */
    @GetMapping("/profesor/estudiante-detalle/{id}")
    @ResponseBody
    public java.util.Map<String, Object> obtenerDetalleEstudiante(
            @SessionAttribute(value = "usuario", required = false) Usuario usuario,
            @PathVariable int id) {

        java.util.Map<String, Object> response = new java.util.HashMap<>();

        if (usuario == null) {
            response.put("error", "Usuario no autenticado");
            return response;
        }

        try {
            Estudiante estudiante = estudianteMapper.obtenerPorId(id);
            if (estudiante == null) {
                response.put("error", "Estudiante no encontrado");
                return response;
            }

            // Verificar que el estudiante pertenezca al grupo del profesor
            Profesor profesor = profesorMapper.findByIdUsuario(usuario.getIdUsuario()).orElse(null);
            if (profesor != null) {
                var grupoProfesor = grupoMapper.findByIdProfesor(profesor.getIdProfesor()).orElse(null);
                if (grupoProfesor != null && estudiante.getIdGrupo() != null) {
                    if (estudiante.getIdGrupo() != grupoProfesor.getIdGrupo()) {
                        response.put("error", "No tienes permiso para ver este estudiante");
                        return response;
                    }
                }
            }

            response.put("idEstudiante", estudiante.getIdEstudiante());
            response.put("fechaIngreso", estudiante.getFechaIngreso());

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
                    }
                } catch (Exception e) {
                    response.put("grupoNombre", "Sin grupo");
                }
            } else {
                response.put("grupoNombre", "Sin grupo");
            }

            // Obtener información del formulario
            if (estudiante.getIdFormulario() != null) {
                try {
                    Formulario formulario = formularioMapper.obtenerPorId(estudiante.getIdFormulario());
                    if (formulario != null) {
                        response.put("nombre", formulario.getNombreCompleto());
                        response.put("fechaNacimiento", formulario.getFechaNacimiento());
                        response.put("edad", formulario.getEdad());
                        response.put("tipoSangre", formulario.getTipoSangre());
                        response.put("direccion", formulario.getDireccion());
                        response.put("gradoAspira", formulario.getGradoAspira());
                        response.put("nombreResponsable", formulario.getNombreResponsable());
                        response.put("correoResponsable", formulario.getCorreoResponsable());
                        response.put("telefonoResponsable", formulario.getTelefonoResponsable());
                        response.put("parentescoResponsable", formulario.getParentescoResponsable());
                        response.put("telefono", formulario.getTelefonoResponsable());
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
            System.err.println("Error al obtener detalles del estudiante: " + e.getMessage());
            e.printStackTrace();
            response.put("error", "Error al obtener detalles del estudiante");
        }

        return response;
    }

    /**
     * API REST: Obtener estudiantes del grupo del profesor
     */
    @GetMapping("/profesor/api/estudiantes")
    @ResponseBody
    public List<java.util.Map<String, Object>> obtenerEstudiantes(@SessionAttribute(value = "usuario", required = false) Usuario usuario) {
        if (usuario == null) {
            return new java.util.ArrayList<>();
        }

        try {
            // Obtener el profesor
            Profesor profesor = profesorMapper.findByIdUsuario(usuario.getIdUsuario()).orElse(null);
            if (profesor == null) {
                return new java.util.ArrayList<>();
            }

            // Obtener el grupo del profesor
            var grupoProfesor = grupoMapper.findByIdProfesor(profesor.getIdProfesor()).orElse(null);
            if (grupoProfesor == null) {
                return new java.util.ArrayList<>();
            }

            // Filtrar estudiantes solo del grupo del profesor
            List<Estudiante> estudiantes = estudianteMapper.listarEstudiantes().stream()
                .filter(e -> e.getIdGrupo() != null && e.getIdGrupo() == grupoProfesor.getIdGrupo())
                .collect(Collectors.toList());
            return estudiantes.stream()
                .map(est -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("idEstudiante", est.getIdEstudiante());
                    map.put("idUsuario", est.getIdUsuario());
                    map.put("idGrupo", est.getIdGrupo());
                    map.put("idHistorialAcademico", est.getIdHistorialAcademico());
                    map.put("fechaIngreso", est.getFechaIngreso());

                    // Crear objeto usuario con correo y nombre
                    java.util.Map<String, Object> usuarioMap = new java.util.HashMap<>();

                    // Obtener información del formulario
                    String correo = "Sin correo";
                    String nombreCompleto = "Sin nombre";
                    if (est.getIdFormulario() != null) {
                        try {
                            Formulario formulario = formularioMapper.obtenerPorId(est.getIdFormulario());
                            if (formulario != null) {
                                if (formulario.getCorreoResponsable() != null) {
                                    correo = formulario.getCorreoResponsable();
                                }
                                if (formulario.getNombreCompleto() != null) {
                                    nombreCompleto = formulario.getNombreCompleto();
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Error al obtener formulario: " + e.getMessage());
                        }
                    }

                    usuarioMap.put("idUsuario", est.getIdUsuario());
                    usuarioMap.put("correo", correo);
                    usuarioMap.put("nombreCompleto", nombreCompleto);
                    map.put("usuario", usuarioMap);

                    // Obtener nombre del grado para el grupo del profesor
                    try {
                        var grado = gradoMapper.findById(grupoProfesor.getIdGrado()).orElse(null);
                        if (grado != null) {
                            map.put("nombreGrado", grado.getNombre());
                            map.put("nombreGrupo", grado.getNombre() + " " + grupoProfesor.getNombre());
                        } else {
                            map.put("nombreGrupo", grupoProfesor.getNombre());
                        }
                    } catch (Exception e) {
                        map.put("nombreGrupo", "Sin grupo");
                    }

                    return map;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error al obtener estudiantes: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    /**
     * API REST: Obtener todos los grupos
     */
    @GetMapping("/profesor/api/grupos")
    @ResponseBody
    public List<java.util.Map<String, Object>> obtenerGrupos(
            @SessionAttribute(value = "usuario", required = false) Usuario usuario) {
        if (usuario == null) {
            return new java.util.ArrayList<>();
        }

        try {
            return grupoMapper.findAll().stream()
                .map(grupo -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("idGrupo", grupo.getIdGrupo());
                    map.put("idGrado", grupo.getIdGrado());
                    map.put("idProfesor", grupo.getIdProfesor());

                    // Obtener nombre completo con grado
                    String nombreCompleto = grupo.getNombre();
                    try {
                        var grado = gradoMapper.findById(grupo.getIdGrado()).orElse(null);
                        if (grado != null) {
                            nombreCompleto = grado.getNombre() + " " + grupo.getNombre();
                        }
                    } catch (Exception e) {
                        System.err.println("Error al obtener grado: " + e.getMessage());
                    }

                    map.put("nombre", nombreCompleto);
                    return map;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error al obtener grupos: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    /**
     * API REST: Guardar nueva anotación en el observador
     */
    @PostMapping("/profesor/api/observador/guardar")
    @ResponseBody
    public java.util.Map<String, Object> guardarAnotacionObservador(
            @RequestParam int idEstudiante,
            @RequestParam String tipo,
            @RequestParam String descripcion,
            @SessionAttribute(value = "usuario", required = false) Usuario usuario) {

        java.util.Map<String, Object> response = new java.util.HashMap<>();

        try {
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return response;
            }

            // Obtener el estudiante
            Estudiante estudiante = estudianteMapper.obtenerPorId(idEstudiante);
            if (estudiante == null) {
                response.put("success", false);
                response.put("message", "Estudiante no encontrado");
                return response;
            }

            // Obtener el observador desde el historial académico
            Integer idObservador = null;
            if (estudiante.getIdHistorialAcademico() != null) {
                HistorialAcademico historial = historialAcademicoMapper.findById(estudiante.getIdHistorialAcademico()).orElse(null);
                if (historial != null) {
                    idObservador = historial.getIdObservador();
                }
            }

            if (idObservador == null) {
                response.put("success", false);
                response.put("message", "No se encontró el observador del estudiante");
                return response;
            }

            // Crear la anotación
            com.udistrital.awuis.sistema_academico.model.Anotacion anotacion =
                new com.udistrital.awuis.sistema_academico.model.Anotacion();
            anotacion.setIdObservador(idObservador);
            anotacion.setTipo(tipo);
            anotacion.setDescripcion(descripcion);
            anotacion.setFecha(java.time.LocalDate.now());

            // Guardar la anotación
            observadorMapper.insertarAnotacion(anotacion);

            response.put("success", true);
            response.put("message", "Anotación guardada exitosamente");

        } catch (Exception e) {
            System.err.println("Error al guardar anotación: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al guardar la anotación: " + e.getMessage());
        }

        return response;
    }

    /**
     * API REST: Obtener anotaciones del observador de un estudiante
     */
    @GetMapping("/profesor/api/observador/{idEstudiante}")
    @ResponseBody
    public java.util.Map<String, Object> obtenerAnotacionesObservador(
            @PathVariable int idEstudiante,
            @SessionAttribute(value = "usuario", required = false) Usuario usuario) {

        java.util.Map<String, Object> response = new java.util.HashMap<>();

        try {
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return response;
            }

            // Obtener el estudiante
            Estudiante estudiante = estudianteMapper.obtenerPorId(idEstudiante);
            if (estudiante == null) {
                response.put("success", false);
                response.put("message", "Estudiante no encontrado");
                return response;
            }

            // Obtener el observador desde el historial académico
            Integer idObservador = null;
            if (estudiante.getIdHistorialAcademico() != null) {
                HistorialAcademico historial = historialAcademicoMapper.findById(estudiante.getIdHistorialAcademico()).orElse(null);
                if (historial != null) {
                    idObservador = historial.getIdObservador();
                }
            }

            if (idObservador == null) {
                response.put("success", true);
                response.put("anotaciones", new java.util.ArrayList<>());
                response.put("message", "No hay anotaciones para este estudiante");
                return response;
            }

            // Obtener las anotaciones
            List<com.udistrital.awuis.sistema_academico.model.Anotacion> anotaciones =
                observadorMapper.obtenerAnotacionesPorObservador(idObservador);

            List<java.util.Map<String, Object>> anotacionesData = anotaciones.stream()
                .map(a -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("idAnotacion", a.getIdAnotacion());
                    map.put("tipo", a.getTipo());
                    map.put("descripcion", a.getDescripcion());
                    map.put("fecha", a.getFecha().toString());
                    return map;
                })
                .collect(Collectors.toList());

            response.put("success", true);
            response.put("anotaciones", anotacionesData);

        } catch (Exception e) {
            System.err.println("Error al obtener anotaciones: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al obtener anotaciones: " + e.getMessage());
        }

        return response;
    }

    // ========================================
    // ENDPOINTS PARA GESTIÓN DE LOGROS
    // ========================================

    /**
     * API REST: Obtener todas las categorías de logros
     */
    @GetMapping("/profesor/api/categorias-logros")
    @ResponseBody
    public java.util.Map<String, Object> obtenerCategoriasLogros(
            @SessionAttribute(value = "usuario", required = false) Usuario usuario) {

        java.util.Map<String, Object> response = new java.util.HashMap<>();

        try {
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return response;
            }

            List<CategoriaLogro> categorias = categoriaLogroMapper.findAll();

            List<java.util.Map<String, Object>> categoriasData = categorias.stream()
                .map(c -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("idCategoria", c.getIdCategoria());
                    map.put("nombre", c.getNombre());
                    return map;
                })
                .collect(Collectors.toList());

            response.put("success", true);
            response.put("categorias", categoriasData);

        } catch (Exception e) {
            System.err.println("Error al obtener categorías de logros: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al obtener categorías: " + e.getMessage());
        }

        return response;
    }

    /**
     * API REST: Obtener logros por categoría
     */
    @GetMapping("/profesor/api/logros/categoria/{idCategoria}")
    @ResponseBody
    public java.util.Map<String, Object> obtenerLogrosPorCategoria(
            @PathVariable int idCategoria,
            @SessionAttribute(value = "usuario", required = false) Usuario usuario) {

        java.util.Map<String, Object> response = new java.util.HashMap<>();

        try {
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return response;
            }

            List<Logro> logros = logroMapper.findByCategoria(idCategoria);

            List<java.util.Map<String, Object>> logrosData = logros.stream()
                .map(l -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("idLogro", l.getIdLogro());
                    map.put("descripcion", l.getDescripcion());
                    map.put("valoracion", l.getValoracion());
                    return map;
                })
                .collect(Collectors.toList());

            response.put("success", true);
            response.put("logros", logrosData);

        } catch (Exception e) {
            System.err.println("Error al obtener logros: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al obtener logros: " + e.getMessage());
        }

        return response;
    }

    /**
     * API REST: Obtener todos los logros de todas las categorías
     */
    @GetMapping("/profesor/api/logros")
    @ResponseBody
    public java.util.Map<String, Object> obtenerTodosLosLogros(
            @SessionAttribute(value = "usuario", required = false) Usuario usuario) {

        java.util.Map<String, Object> response = new java.util.HashMap<>();

        try {
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return response;
            }

            List<Logro> logros = logroMapper.findAll();

            List<java.util.Map<String, Object>> logrosData = logros.stream()
                .map(l -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("idLogro", l.getIdLogro());
                    map.put("idCategoria", l.getIdCategoria());
                    map.put("descripcion", l.getDescripcion());
                    map.put("valoracion", l.getValoracion());
                    return map;
                })
                .collect(Collectors.toList());

            response.put("success", true);
            response.put("logros", logrosData);

        } catch (Exception e) {
            System.err.println("Error al obtener logros: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al obtener logros: " + e.getMessage());
        }

        return response;
    }

    /**
     * API REST: Obtener las valoraciones de logros de un estudiante
     */
    @GetMapping("/profesor/api/estudiante/{idEstudiante}/logros")
    @ResponseBody
    public java.util.Map<String, Object> obtenerLogrosEstudiante(
            @PathVariable int idEstudiante,
            @SessionAttribute(value = "usuario", required = false) Usuario usuario) {

        java.util.Map<String, Object> response = new java.util.HashMap<>();

        try {
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return response;
            }

            // Obtener el estudiante
            Estudiante estudiante = estudianteMapper.obtenerPorId(idEstudiante);
            if (estudiante == null) {
                response.put("success", false);
                response.put("message", "Estudiante no encontrado");
                return response;
            }

            // Obtener o crear el boletín del estudiante
            Integer idHistorialAcademico = estudiante.getIdHistorialAcademico();
            if (idHistorialAcademico == null) {
                response.put("success", true);
                response.put("logros", new java.util.ArrayList<>());
                response.put("message", "El estudiante no tiene historial académico");
                return response;
            }

            Boletin boletin = boletinMapper.findOrCreateByHistorialAcademico(idHistorialAcademico);

            // Obtener las valoraciones de logros del boletín
            List<LogroBoletin> logrosBoletines = logroBoletinMapper.findByBoletin(boletin.getIdBoletin());

            List<java.util.Map<String, Object>> logrosData = logrosBoletines.stream()
                .map(lb -> {
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("idLogroBoletin", lb.getIdLogroBoletin());
                    map.put("idLogro", lb.getIdLogro());
                    map.put("valoracion", lb.getValoracion());
                    map.put("comentario", lb.getComentario());

                    // Obtener información del logro
                    if (lb.getIdLogro() != null) {
                        logroMapper.findById(lb.getIdLogro()).ifPresent(logro -> {
                            map.put("descripcionLogro", logro.getDescripcion());
                            map.put("idCategoria", logro.getIdCategoria());
                        });
                    }

                    return map;
                })
                .collect(Collectors.toList());

            response.put("success", true);
            response.put("logros", logrosData);
            response.put("idBoletin", boletin.getIdBoletin());

        } catch (Exception e) {
            System.err.println("Error al obtener logros del estudiante: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al obtener logros: " + e.getMessage());
        }

        return response;
    }

    /**
     * POST: Guardar o actualizar valoración de un logro para un estudiante
     * Requiere que se califiquen los 12 logros antes de crear el boletín
     * IMPORTANTE: Solo crea el boletín cuando se asegura que hay datos válidos
     */
    @PostMapping("/profesor/api/estudiante/{idEstudiante}/logro/guardar")
    @ResponseBody
    public java.util.Map<String, Object> guardarValoracionLogro(
            @PathVariable int idEstudiante,
            @RequestParam int idLogro,
            @RequestParam String valoracion,
            @RequestParam(required = false) Integer periodo,
            @RequestParam(required = false) String comentario,
            @SessionAttribute(value = "usuario", required = false) Usuario usuario) {

        java.util.Map<String, Object> response = new java.util.HashMap<>();

        try {
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return response;
            }

            // Validar periodo PRIMERO - NO crear boletín si periodo es inválido
            if (periodo == null || periodo < 1 || periodo > 4) {
                response.put("success", false);
                response.put("message", "Debe seleccionar un periodo académico válido (1-4)");
                return response;
            }

            // Validar valoración no vacía
            if (valoracion == null || valoracion.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "La valoración no puede estar vacía");
                return response;
            }

            // Validar que el logro existe
            Logro logro = logroMapper.findById(idLogro).orElse(null);
            if (logro == null) {
                response.put("success", false);
                response.put("message", "Logro no encontrado");
                return response;
            }

            // Obtener el estudiante
            Estudiante estudiante = estudianteMapper.obtenerPorId(idEstudiante);
            if (estudiante == null) {
                response.put("success", false);
                response.put("message", "Estudiante no encontrado");
                return response;
            }

            // Verificar historial académico
            Integer idHistorialAcademico = estudiante.getIdHistorialAcademico();
            if (idHistorialAcademico == null) {
                response.put("success", false);
                response.put("message", "El estudiante no tiene historial académico");
                return response;
            }

            // AHORA SÍ: Crear o buscar el boletín SOLO después de todas las validaciones
            Boletin boletin = boletinMapper.findOrCreateByHistorialAcademicoAndPeriodo(idHistorialAcademico, periodo);

            // Buscar si ya existe una valoración para este logro y boletín
            Optional<LogroBoletin> logroBoletinExistente =
                logroBoletinMapper.findByLogroAndBoletin(idLogro, boletin.getIdBoletin());

            LogroBoletin logroBoletin;
            if (logroBoletinExistente.isPresent()) {
                // Actualizar existente
                logroBoletin = logroBoletinExistente.get();
                logroBoletin.setValoracion(valoracion);
                logroBoletin.setComentario(comentario);
                logroBoletin = logroBoletinMapper.update(logroBoletin);
            } else {
                // Crear nuevo
                logroBoletin = new LogroBoletin(idLogro, boletin.getIdBoletin(), valoracion);
                logroBoletin.setComentario(comentario);
                logroBoletin = logroBoletinMapper.save(logroBoletin);
            }

            response.put("success", true);
            response.put("message", "Valoración guardada exitosamente");
            response.put("idLogroBoletin", logroBoletin.getIdLogroBoletin());

        } catch (Exception e) {
            System.err.println("Error al guardar valoración: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al guardar valoración: " + e.getMessage());
        }

        return response;
    }

    /**
     * API REST: Obtener periodos académicos ya registrados para un estudiante
     */
    @GetMapping("/profesor/api/estudiante/{idEstudiante}/periodos-registrados")
    @ResponseBody
    public java.util.Map<String, Object> obtenerPeriodosRegistrados(
            @PathVariable int idEstudiante,
            @SessionAttribute(value = "usuario", required = false) Usuario usuario) {

        java.util.Map<String, Object> response = new java.util.HashMap<>();

        try {
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return response;
            }

            // Obtener el estudiante
            Estudiante estudiante = estudianteMapper.obtenerPorId(idEstudiante);
            if (estudiante == null) {
                response.put("success", false);
                response.put("message", "Estudiante no encontrado");
                return response;
            }

            Integer idHistorialAcademico = estudiante.getIdHistorialAcademico();
            if (idHistorialAcademico == null) {
                response.put("success", true);
                response.put("periodos", new java.util.ArrayList<>());
                return response;
            }

            // Obtener todos los boletines del estudiante
            List<Boletin> boletines = boletinMapper.findByHistorialAcademico(idHistorialAcademico);

            // Extraer los periodos registrados (que tengan 12 logros)
            List<Integer> periodosRegistrados = new java.util.ArrayList<>();
            for (Boletin boletin : boletines) {
                if (boletin.getPeriodo() != null) {
                    // Contar logros del boletín
                    List<LogroBoletin> logrosBoletines = logroBoletinMapper.findByBoletin(boletin.getIdBoletin());
                    // Solo incluir si tiene los 12 logros completos
                    if (logrosBoletines.size() >= 12) {
                        periodosRegistrados.add(boletin.getPeriodo());
                    }
                }
            }

            response.put("success", true);
            response.put("periodos", periodosRegistrados);

        } catch (Exception e) {
            System.err.println("Error al obtener periodos registrados: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al obtener periodos: " + e.getMessage());
        }

        return response;
    }

    /**
     * POST: Crear un nuevo logro
     */
    @PostMapping("/profesor/api/logro/crear")
    @ResponseBody
    public java.util.Map<String, Object> crearLogro(
            @RequestParam int idCategoria,
            @RequestParam String descripcion,
            @RequestParam(required = false) String valoracion,
            @SessionAttribute(value = "usuario", required = false) Usuario usuario) {

        java.util.Map<String, Object> response = new java.util.HashMap<>();

        try {
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return response;
            }

            // Validar que la categoría existe
            CategoriaLogro categoria = categoriaLogroMapper.findById(idCategoria).orElse(null);
            if (categoria == null) {
                response.put("success", false);
                response.put("message", "Categoría no encontrada");
                return response;
            }

            // Crear el logro
            Logro logro = new Logro(idCategoria, descripcion);
            logro.setValoracion(valoracion);
            logro = logroMapper.save(logro);

            response.put("success", true);
            response.put("message", "Logro creado exitosamente");
            response.put("idLogro", logro.getIdLogro());

        } catch (Exception e) {
            System.err.println("Error al crear logro: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al crear logro: " + e.getMessage());
        }

        return response;
    }
}
