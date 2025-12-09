package com.udistrital.awuis.sistema_academico.controller;

import com.udistrital.awuis.sistema_academico.model.*;
import com.udistrital.awuis.sistema_academico.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/directivo/grupos")
public class GrupoController {

    @Autowired
    private GrupoMapper grupoMapper;

    @Autowired
    private GradoMapper gradoMapper;

    @Autowired
    private ProfesorMapper profesorMapper;

    @Autowired
    private EstudianteMapper estudianteMapper;

    @Autowired
    private FormularioMapper formularioMapper;

    /**
     * Crear un nuevo grupo
     */
    @PostMapping("/crear")
    public String crearGrupo(
            @SessionAttribute(value = "usuario", required = false) Usuario usuario,
            @RequestParam("idGrado") int idGrado,
            @RequestParam("nombre") String nombre,
            @RequestParam("idProfesor") int idProfesor,
            RedirectAttributes redirectAttributes) {

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            // Validar que el grado existe
            Grado grado = gradoMapper.findById(idGrado).orElse(null);
            if (grado == null) {
                redirectAttributes.addFlashAttribute("error", "El grado seleccionado no existe");
                return "redirect:/directivo?panel=grupos";
            }

            // Validar que el profesor existe y no está asignado a otro grupo
            Profesor profesor = profesorMapper.findById(idProfesor).orElse(null);
            if (profesor == null) {
                redirectAttributes.addFlashAttribute("error", "El profesor seleccionado no existe");
                return "redirect:/directivo?panel=grupos";
            }

            // Verificar si el profesor ya tiene un grupo asignado
            if (grupoMapper.findByIdProfesor(idProfesor).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "El profesor ya está asignado a otro grupo");
                return "redirect:/directivo?panel=grupos";
            }

            // Crear el grupo
            Grupo grupo = new Grupo();
            grupo.setIdGrado(idGrado);
            grupo.setNombre(nombre.toUpperCase());
            grupo.setIdProfesor(idProfesor);

            grupoMapper.save(grupo);

            redirectAttributes.addFlashAttribute("mensaje",
                "Grupo " + grado.getNombre() + " " + nombre + " creado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al crear el grupo: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/directivo?panel=grupos";
    }

    /**
     * Eliminar un grupo
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarGrupo(
            @SessionAttribute(value = "usuario", required = false) Usuario usuario,
            @PathVariable("id") int idGrupo,
            RedirectAttributes redirectAttributes) {

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            Grupo grupo = grupoMapper.findById(idGrupo).orElse(null);
            if (grupo == null) {
                redirectAttributes.addFlashAttribute("error", "El grupo no existe");
                return "redirect:/directivo?panel=grupos";
            }

            // Verificar si hay estudiantes asignados
            List<Estudiante> estudiantes = estudianteMapper.listarEstudiantes().stream()
                .filter(e -> e.getIdGrupo() != null && e.getIdGrupo() == idGrupo)
                .collect(Collectors.toList());

            if (!estudiantes.isEmpty()) {
                redirectAttributes.addFlashAttribute("error",
                    "No se puede eliminar el grupo porque tiene " + estudiantes.size() + " estudiante(s) asignado(s)");
                return "redirect:/directivo?panel=grupos";
            }

            grupoMapper.deleteById(idGrupo);
            redirectAttributes.addFlashAttribute("mensaje", "Grupo eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al eliminar el grupo: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/directivo?panel=grupos";
    }

    /**
     * Asignar profesor a un grupo
     */
    @PostMapping("/asignar-profesor")
    public String asignarProfesor(
            @SessionAttribute(value = "usuario", required = false) Usuario usuario,
            @RequestParam("idGrupo") int idGrupo,
            @RequestParam("idProfesor") int idProfesor,
            RedirectAttributes redirectAttributes) {

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            Grupo grupo = grupoMapper.findById(idGrupo).orElse(null);
            if (grupo == null) {
                redirectAttributes.addFlashAttribute("error", "El grupo no existe");
                return "redirect:/directivo?panel=grupos";
            }

            Profesor profesor = profesorMapper.findById(idProfesor).orElse(null);
            if (profesor == null) {
                redirectAttributes.addFlashAttribute("error", "El profesor no existe");
                return "redirect:/directivo?panel=grupos";
            }

            // Verificar si el profesor ya tiene un grupo asignado
            grupoMapper.findByIdProfesor(idProfesor).ifPresent(g -> {
                if (g.getIdGrupo() != idGrupo) {
                    g.setIdProfesor(null);
                    grupoMapper.actualizar(g);
                }
            });

            grupo.setIdProfesor(idProfesor);
            grupoMapper.actualizar(grupo);

            redirectAttributes.addFlashAttribute("mensaje",
                "Profesor asignado exitosamente al grupo");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al asignar profesor: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/directivo?panel=grupos";
    }

    /**
     * Asignar estudiantes a un grupo
     */
    @PostMapping("/asignar-estudiantes")
    public String asignarEstudiantes(
            @SessionAttribute(value = "usuario", required = false) Usuario usuario,
            @RequestParam("idGrupo") int idGrupo,
            @RequestParam(value = "estudiantes[]", required = false) List<Integer> idsEstudiantes,
            RedirectAttributes redirectAttributes) {

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            Grupo grupo = grupoMapper.findById(idGrupo).orElse(null);
            if (grupo == null) {
                redirectAttributes.addFlashAttribute("error", "El grupo no existe");
                return "redirect:/directivo?panel=grupos";
            }

            if (idsEstudiantes == null || idsEstudiantes.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar al menos un estudiante");
                return "redirect:/directivo?panel=grupos";
            }

            int asignados = 0;
            for (Integer idEstudiante : idsEstudiantes) {
                Estudiante estudiante = estudianteMapper.obtenerPorId(idEstudiante);
                if (estudiante != null) {
                    estudiante.setIdGrupo(idGrupo);
                    estudianteMapper.actualizarEstudiante(estudiante);
                    asignados++;
                }
            }

            redirectAttributes.addFlashAttribute("mensaje",
                asignados + " estudiante(s) asignado(s) exitosamente al grupo");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al asignar estudiantes: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/directivo?panel=grupos";
    }

    /**
     * API REST: Obtener todos los grupos
     */
    @GetMapping("/api/listar")
    @ResponseBody
    public List<Map<String, Object>> listarGrupos() {
        return grupoMapper.findAll().stream().map(grupo -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idGrupo", grupo.getIdGrupo());
            map.put("nombre", grupo.getNombre());
            map.put("idGrado", grupo.getIdGrado());
            map.put("idProfesor", grupo.getIdProfesor());

            // Obtener nombre del grado
            if (grupo.getIdGrado() != null) {
                gradoMapper.findById(grupo.getIdGrado()).ifPresent(g -> {
                    map.put("nombreGrado", g.getNombre());
                    map.put("nombreCompleto", g.getNombre() + " " + grupo.getNombre());
                });
            }

            // Obtener nombre del profesor
            if (grupo.getIdProfesor() != null) {
                profesorMapper.findById(grupo.getIdProfesor()).ifPresent(p -> {
                    map.put("nombreProfesor", p.getNombre());
                });
            }

            // Contar estudiantes
            long cantidadEstudiantes = estudianteMapper.listarEstudiantes().stream()
                .filter(e -> e.getIdGrupo() != null && e.getIdGrupo() == grupo.getIdGrupo())
                .count();
            map.put("cantidadEstudiantes", cantidadEstudiantes);

            return map;
        }).collect(Collectors.toList());
    }

    /**
     * API REST: Obtener todos los grados
     */
    @GetMapping("/api/grados")
    @ResponseBody
    public List<Map<String, Object>> listarGrados() {
        return gradoMapper.findAll().stream().map(grado -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idGrado", grado.getIdGrado());
            map.put("nombre", grado.getNombre());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * API REST: Obtener todos los profesores
     */
    @GetMapping("/api/profesores")
    @ResponseBody
    public List<Map<String, Object>> listarTodosProfesores() {
        try {
            List<Profesor> todosProfesores = profesorMapper.findAll();

            return todosProfesores.stream()
                .map(profesor -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idProfesor", profesor.getIdProfesor());
                    map.put("nombre", profesor.getNombre() != null ? profesor.getNombre() : "Sin nombre");
                    map.put("correo", profesor.getCorreo());

                    // Verificar si tiene grupo asignado
                    boolean tieneGrupo = grupoMapper.findByIdProfesor(profesor.getIdProfesor()).isPresent();
                    map.put("disponible", !tieneGrupo);

                    return map;
                }).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error al obtener profesores: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    /**
     * API REST: Obtener profesores disponibles (sin grupo asignado)
     */
    @GetMapping("/api/profesores-disponibles")
    @ResponseBody
    public List<Map<String, Object>> listarProfesoresDisponibles() {
        try {
            List<Profesor> todosProfesores = profesorMapper.findAll();

            return todosProfesores.stream()
                .filter(p -> grupoMapper.findByIdProfesor(p.getIdProfesor()).isEmpty())
                .map(profesor -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idProfesor", profesor.getIdProfesor());
                    map.put("nombre", profesor.getNombre() != null ? profesor.getNombre() : "Sin nombre");
                    map.put("correo", profesor.getCorreo());
                    return map;
                }).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error al obtener profesores disponibles: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    /**
     * API REST: Obtener estudiantes sin grupo filtrados por grado
     */
    @GetMapping("/api/estudiantes-sin-grupo")
    @ResponseBody
    public List<Map<String, Object>> listarEstudiantesSinGrupo(@RequestParam(required = false) Integer idGrupo) {
        // Obtener el grado del grupo si se proporciona
        String nombreGrado = null;
        if (idGrupo != null) {
            Grupo grupo = grupoMapper.findById(idGrupo).orElse(null);
            if (grupo != null && grupo.getIdGrado() != null) {
                Grado grado = gradoMapper.findById(grupo.getIdGrado()).orElse(null);
                if (grado != null) {
                    nombreGrado = grado.getNombre();
                }
            }
        }

        final String gradoFiltro = nombreGrado;

        return estudianteMapper.listarEstudiantes().stream()
            .filter(e -> e.getIdGrupo() == null)
            .filter(e -> {
                // Si hay filtro de grado, verificar que el estudiante corresponda a ese grado
                if (gradoFiltro == null) return true;
                
                // Obtener el grado del formulario del estudiante
                if (e.getIdFormulario() != null) {
                    try {
                        com.udistrital.awuis.sistema_academico.model.Formulario formulario = 
                            formularioMapper.obtenerPorId(e.getIdFormulario());
                        if (formulario != null && formulario.getGradoAspira() != null) {
                            return formulario.getGradoAspira().equalsIgnoreCase(gradoFiltro);
                        }
                    } catch (Exception ex) {
                        System.err.println("Error al obtener formulario: " + ex.getMessage());
                    }
                }
                return false; // Si no tiene formulario o grado, no mostrar
            })
            .map(estudiante -> {
                Map<String, Object> map = new HashMap<>();
                map.put("idEstudiante", estudiante.getIdEstudiante());
                map.put("correo", estudiante.getCorreo());
                
                // Obtener nombre del formulario si existe
                if (estudiante.getIdFormulario() != null) {
                    try {
                        com.udistrital.awuis.sistema_academico.model.Formulario formulario = 
                            formularioMapper.obtenerPorId(estudiante.getIdFormulario());
                        if (formulario != null) {
                            map.put("nombre", formulario.getNombreCompleto());
                            map.put("grado", formulario.getGradoAspira());
                        }
                    } catch (Exception e) {
                        map.put("nombre", estudiante.getCorreo().split("@")[0]);
                    }
                } else {
                    map.put("nombre", estudiante.getCorreo().split("@")[0]);
                }
                
                return map;
            }).collect(Collectors.toList());
    }

    /**
     * API REST: Obtener estudiantes de un grupo específico
     */
    @GetMapping("/api/estudiantes/{idGrupo}")
    @ResponseBody
    public List<Map<String, Object>> obtenerEstudiantesDelGrupo(@PathVariable int idGrupo) {
        try {
            return estudianteMapper.listarEstudiantes().stream()
                .filter(e -> e.getIdGrupo() != null && e.getIdGrupo() == idGrupo)
                .map(estudiante -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idEstudiante", estudiante.getIdEstudiante());
                    map.put("correo", estudiante.getCorreo());

                    // Obtener nombre del formulario si existe
                    if (estudiante.getIdFormulario() != null) {
                        try {
                            com.udistrital.awuis.sistema_academico.model.Formulario formulario =
                                formularioMapper.obtenerPorId(estudiante.getIdFormulario());
                            if (formulario != null) {
                                map.put("nombre", formulario.getNombreCompleto());
                            } else {
                                map.put("nombre", estudiante.getCorreo().split("@")[0]);
                            }
                        } catch (Exception e) {
                            map.put("nombre", estudiante.getCorreo().split("@")[0]);
                        }
                    } else {
                        map.put("nombre", estudiante.getCorreo().split("@")[0]);
                    }

                    return map;
                }).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error al obtener estudiantes del grupo: " + e.getMessage());
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    /**
     * API REST: Quitar un estudiante de su grupo
     */
    @PostMapping("/api/quitar-estudiante/{idEstudiante}")
    @ResponseBody
    public Map<String, Object> quitarEstudianteDeGrupo(@PathVariable int idEstudiante) {
        Map<String, Object> response = new HashMap<>();

        try {
            Estudiante estudiante = estudianteMapper.obtenerPorId(idEstudiante);
            if (estudiante == null) {
                response.put("success", false);
                response.put("message", "El estudiante no existe");
                return response;
            }

            estudiante.setIdGrupo(null);
            estudianteMapper.actualizarEstudiante(estudiante);

            response.put("success", true);
            response.put("message", "Estudiante removido del grupo exitosamente");
        } catch (Exception e) {
            System.err.println("Error al quitar estudiante del grupo: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al quitar estudiante: " + e.getMessage());
        }

        return response;
    }
}
