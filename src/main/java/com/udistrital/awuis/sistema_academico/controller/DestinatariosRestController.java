package com.udistrital.awuis.sistema_academico.controller;

import com.udistrital.awuis.sistema_academico.model.Aspirante;
import com.udistrital.awuis.sistema_academico.model.Estudiante;
import com.udistrital.awuis.sistema_academico.model.Formulario;
import com.udistrital.awuis.sistema_academico.model.Usuario;
import com.udistrital.awuis.sistema_academico.repositories.AspiranteMapper;
import com.udistrital.awuis.sistema_academico.repositories.DirectivoMapper;
import com.udistrital.awuis.sistema_academico.repositories.EstudianteMapper;
import com.udistrital.awuis.sistema_academico.repositories.FormularioMapper;
import com.udistrital.awuis.sistema_academico.repositories.ProfesorMapper;
import com.udistrital.awuis.sistema_academico.repositories.UsuarioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/destinatarios")
public class DestinatariosRestController {

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private ProfesorMapper profesorMapper;

    @Autowired
    private DirectivoMapper directivoMapper;

    @Autowired
    private EstudianteMapper estudianteMapper;

    @Autowired
    private FormularioMapper formularioMapper;

    @Autowired
    private AspiranteMapper aspiranteMapper;

    /**
     * Obtiene la lista de estudiantes desde la base de datos
     */
    @GetMapping("/estudiantes")
    public List<Map<String, String>> obtenerEstudiantes() {
        List<Map<String, String>> estudiantes = new ArrayList<>();

        try {
            List<Estudiante> listaEstudiantes = estudianteMapper.listarEstudiantes();

            for (Estudiante est : listaEstudiantes) {
                if (est.getIdEstudiante() > 0) {
                    Map<String, String> info = new HashMap<>();

                    // Obtener el nombre del formulario si existe
                    String nombre = "Estudiante";
                    if (est.getIdFormulario() != null) {
                        Formulario formulario = formularioMapper.obtenerPorId(est.getIdFormulario());
                        if (formulario != null && formulario.getNombreCompleto() != null) {
                            nombre = formulario.getNombreCompleto();
                        }
                    }

                    info.put("nombre", nombre);
                    info.put("correo", est.getCorreo());
                    estudiantes.add(info);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener estudiantes: " + e.getMessage());
            e.printStackTrace();
        }

        return estudiantes;
    }

    /**
     * Obtiene la lista de profesores desde la base de datos
     */
    @GetMapping("/profesores")
    public List<Map<String, String>> obtenerProfesores() {
        List<Map<String, String>> profesores = new ArrayList<>();

        try {
            //  Obtener todos los usuarios con rol de profesor (idRol = 2)
            List<Usuario> todosUsuarios = usuarioMapper.findAll();

            for (Usuario usuario : todosUsuarios) {
                if (usuario.getToken() != null && usuario.getToken().getRol() != null
                        && usuario.getToken().getRol().getIdRol() == 2) {
                    Map<String, String> info = new HashMap<>();

                    // Buscar el Profesor para obtener su nombre
                    String nombreProfesor = "Profesor";
                    try {
                        com.udistrital.awuis.sistema_academico.model.Profesor profesor =
                            profesorMapper.findByIdUsuario(usuario.getIdUsuario()).orElse(null);
                        if (profesor != null && profesor.getNombre() != null && !profesor.getNombre().trim().isEmpty()) {
                            nombreProfesor = profesor.getNombre();
                        }
                    } catch (Exception e) {
                        System.err.println("Error al buscar nombre del profesor: " + e.getMessage());
                    }

                    info.put("nombre", "Prof. " + nombreProfesor);
                    info.put("correo", usuario.getCorreo());
                    profesores.add(info);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener profesores: " + e.getMessage());
            e.printStackTrace();
        }

        return profesores;
    }

    /**
     * Obtiene la lista de directivos desde la base de datos
     */
    @GetMapping("/directivos")
    public List<Map<String, String>> obtenerDirectivos() {
        List<Map<String, String>> directivos = new ArrayList<>();

        try {
            // Obtener todos los usuarios con rol de directivo (idRol = 1)
            List<Usuario> todosUsuarios = usuarioMapper.findAll();

            for (Usuario usuario : todosUsuarios) {
                if (usuario.getToken() != null && usuario.getToken().getRol() != null
                        && usuario.getToken().getRol().getIdRol() == 1) {
                    Map<String, String> info = new HashMap<>();

                    // Buscar el Directivo para obtener su nombre
                    String nombreDirectivo = "Directivo";
                    try {
                        com.udistrital.awuis.sistema_academico.model.Directivo directivo =
                            directivoMapper.findByIdUsuario(usuario.getIdUsuario()).orElse(null);
                        if (directivo != null && directivo.getNombre() != null && !directivo.getNombre().trim().isEmpty()) {
                            nombreDirectivo = directivo.getNombre();
                        }
                    } catch (Exception e) {
                        System.err.println("Error al buscar nombre del directivo: " + e.getMessage());
                    }

                    info.put("nombre", "Dir. " + nombreDirectivo);
                    info.put("correo", usuario.getCorreo());
                    directivos.add(info);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener directivos: " + e.getMessage());
            e.printStackTrace();
        }

        return directivos;
    }

    /**
     * Obtiene la lista de aspirantes desde la base de datos
     */
    @GetMapping("/aspirantes")
    public List<Map<String, Object>> obtenerAspirantes() {
        List<Map<String, Object>> aspirantes = new ArrayList<>();

        try {
            List<Aspirante> listaAspirantes = aspiranteMapper.listarAspirantes();

            for (Aspirante asp : listaAspirantes) {
                Map<String, Object> info = new HashMap<>();

                info.put("idAspirante", asp.getIdAspirante());

                // Obtener información del formulario si existe
                if (asp.getFormulario() != null) {
                    Formulario formulario = asp.getFormulario();
                    info.put("idFormulario", formulario.getIdFormulario());
                    info.put("nombre", formulario.getNombreCompleto() != null ? formulario.getNombreCompleto() : "Sin nombre");
                    info.put("correo", formulario.getCorreoResponsable() != null ? formulario.getCorreoResponsable() : "");
                    info.put("grado", formulario.getGradoAspira() != null ? formulario.getGradoAspira() : "");
                    info.put("estado", formulario.getEstado() != null ? formulario.getEstado() : "PENDIENTE");
                    info.put("edad", formulario.getEdad());
                    info.put("fechaNacimiento", formulario.getFechaNacimiento() != null ? formulario.getFechaNacimiento().toString() : "");
                    info.put("direccion", formulario.getDireccion());
                    info.put("tipoSangre", formulario.getTipoSangre());
                    info.put("alergias", formulario.getAlergias());
                    info.put("condicionesMedicas", formulario.getCondicionesMedicas());
                    info.put("medicamentos", formulario.getMedicamentos());
                    info.put("observaciones", formulario.getObservaciones());
                    info.put("nombreResponsable", formulario.getNombreResponsable());
                    info.put("telefonoResponsable", formulario.getTelefonoResponsable());
                    info.put("parentescoResponsable", formulario.getParentescoResponsable());
                } else {
                    info.put("nombre", "Sin información");
                    info.put("correo", "");
                    info.put("grado", "");
                    info.put("estado", "PENDIENTE");
                }

                aspirantes.add(info);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener aspirantes: " + e.getMessage());
            e.printStackTrace();
        }

        return aspirantes;
    }
}

