package com.udistrital.awuis.sistema_academico.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.udistrital.awuis.sistema_academico.model.Estudiante;
import com.udistrital.awuis.sistema_academico.model.Formulario;
import com.udistrital.awuis.sistema_academico.model.Profesor;
import com.udistrital.awuis.sistema_academico.model.Usuario;
import com.udistrital.awuis.sistema_academico.repositories.EstudianteMapper;
import com.udistrital.awuis.sistema_academico.repositories.FormularioMapper;
import com.udistrital.awuis.sistema_academico.repositories.GradoMapper;
import com.udistrital.awuis.sistema_academico.repositories.GrupoMapper;
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
                    map.put("correo", estudiante.getCorreo());

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
            response.put("correo", estudiante.getCorreo());
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
}

