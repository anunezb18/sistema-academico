package com.udistrital.awuis.sistema_academico.controller;

import com.udistrital.awuis.sistema_academico.model.Estudiante;
import com.udistrital.awuis.sistema_academico.model.Formulario;
import com.udistrital.awuis.sistema_academico.model.Usuario;
import com.udistrital.awuis.sistema_academico.repositories.DirectivoMapper;
import com.udistrital.awuis.sistema_academico.repositories.EstudianteMapper;
import com.udistrital.awuis.sistema_academico.repositories.FormularioMapper;
import com.udistrital.awuis.sistema_academico.repositories.UsuarioMapper;
import com.udistrital.awuis.sistema_academico.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/correo")
public class CorreoController {

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private EstudianteMapper estudianteMapper;

    @Autowired
    private DirectivoMapper directivoMapper;

    @Autowired
    private FormularioMapper formularioMapper;

    @Autowired
    private EmailService emailService;

    /**
     * Muestra el formulario de selección de destinatarios
     */
    @GetMapping("/seleccionar-destinatarios")
    public String seleccionarDestinatarios(@SessionAttribute(value = "usuario", required = false) Usuario usuario,
                                           Model model) {
        if (usuario == null) {
            return "redirect:/login";
        }

        // Determinar el rol del usuario actual
        int rolId = usuario.getToken() != null && usuario.getToken().getRol() != null
                ? usuario.getToken().getRol().getIdRol()
                : 0;

        // Obtener los destinatarios disponibles según el rol
        Map<String, List<Map<String, String>>> destinatarios = obtenerDestinatariosPorRol(rolId);

        model.addAttribute("destinatarios", destinatarios);
        model.addAttribute("rolId", rolId);
        model.addAttribute("correoRemitente", usuario.getCorreo());

        return "seleccionar-destinatarios";
    }

    /**
     * Muestra el formulario de composición de correo
     */
    @PostMapping("/componer")
    public String componer(@SessionAttribute(value = "usuario", required = false) Usuario usuario,
                          @RequestParam("destinatarios[]") List<String> destinatariosSeleccionados,
                          Model model) {
        if (usuario == null) {
            return "redirect:/login";
        }

        // Obtener los detalles de los destinatarios seleccionados
        List<Map<String, String>> detallesDestinatarios = new ArrayList<>();
        for (String correo : destinatariosSeleccionados) {
            Map<String, String> detalle = new HashMap<>();
            detalle.put("correo", correo);
            detallesDestinatarios.add(detalle);
        }

        model.addAttribute("destinatarios", detallesDestinatarios);
        model.addAttribute("correoRemitente", usuario.getCorreo());

        return "componer-correo";
    }

    /**
     * Envía el correo electrónico
     */
    @PostMapping("/enviar")
    public String enviar(@SessionAttribute(value = "usuario", required = false) Usuario usuario,
                        @RequestParam("destinatarios") String destinatarios,
                        @RequestParam("asunto") String asunto,
                        @RequestParam("mensaje") String mensaje,
                        @RequestParam(value = "adjunto", required = false) MultipartFile adjunto,
                        RedirectAttributes redirectAttributes) {
        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            // Dividir los destinatarios por coma
            String[] listaDestinatarios = destinatarios.split(",");

            int enviados = 0;
            int fallidos = 0;

            // Enviar correo a cada destinatario
            for (String destinatario : listaDestinatarios) {
                destinatario = destinatario.trim();
                if (!destinatario.isEmpty()) {
                    // Crear el cuerpo del correo
                    String cuerpoCompleto = String.format(
                            "Remitente: %s\n\n%s\n\n---\nEste correo fue enviado desde el Sistema Académico AWUIS",
                            usuario.getCorreo(),
                            mensaje
                    );

                    boolean exito = emailService.enviarCorreo(destinatario, asunto, cuerpoCompleto);
                    if (exito) {
                        enviados++;
                    } else {
                        fallidos++;
                    }
                }
            }

            if (enviados > 0) {
                redirectAttributes.addFlashAttribute("exito",
                        String.format("Correo enviado exitosamente a %d destinatario(s)", enviados));
            }
            if (fallidos > 0) {
                redirectAttributes.addFlashAttribute("advertencia",
                        String.format("No se pudo enviar a %d destinatario(s)", fallidos));
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al enviar el correo: " + e.getMessage());
            e.printStackTrace();
        }

        // Redirigir según el rol
        int rolId = usuario.getToken() != null && usuario.getToken().getRol() != null
                ? usuario.getToken().getRol().getIdRol()
                : 0;

        return switch (rolId) {
            case 1 -> "redirect:/directivo";
            case 2 -> "redirect:/profesor";
            case 3 -> "redirect:/administrador";
            case 4 -> "redirect:/estudiante";
            default -> "redirect:/login";
        };
    }

    /**
     * Obtiene los destinatarios disponibles según el rol del usuario
     */
    private Map<String, List<Map<String, String>>> obtenerDestinatariosPorRol(int rolId) {
        Map<String, List<Map<String, String>>> resultado = new HashMap<>();

        try {
            switch (rolId) {
                case 1: // Directivo - puede enviar a estudiantes y profesores
                    resultado.put("Estudiantes", obtenerEstudiantes());
                    resultado.put("Profesores", obtenerProfesores());
                    break;

                case 2: // Profesor - puede enviar a estudiantes y directivos
                    resultado.put("Estudiantes", obtenerEstudiantes());
                    resultado.put("Directivos", obtenerDirectivos());
                    break;

                case 4: // Estudiante - puede enviar a profesores y directivos
                    resultado.put("Profesores", obtenerProfesores());
                    resultado.put("Directivos", obtenerDirectivos());
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            System.err.println("Error al obtener destinatarios: " + e.getMessage());
            e.printStackTrace();
        }

        return resultado;
    }

    /**
     * Obtiene la lista de estudiantes
     */
    private List<Map<String, String>> obtenerEstudiantes() {
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
        }

        return estudiantes;
    }

    /**
     * Obtiene la lista de profesores
     */
    private List<Map<String, String>> obtenerProfesores() {
        List<Map<String, String>> profesores = new ArrayList<>();

        try {
            // Obtener todos los usuarios con rol de profesor (idRol = 2)
            List<Usuario> todosUsuarios = usuarioMapper.findAll();

            for (Usuario usuario : todosUsuarios) {
                if (usuario.getToken() != null && usuario.getToken().getRol() != null
                        && usuario.getToken().getRol().getIdRol() == 2) {
                    Map<String, String> info = new HashMap<>();

                    // Usar el nombre del correo como identificador
                    String nombre = usuario.getCorreo().split("@")[0];
                    nombre = nombre.substring(0, 1).toUpperCase() + nombre.substring(1);

                    info.put("nombre", "Prof. " + nombre);
                    info.put("correo", usuario.getCorreo());
                    profesores.add(info);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener profesores: " + e.getMessage());
        }

        return profesores;
    }

    /**
     * Obtiene la lista de directivos
     */
    private List<Map<String, String>> obtenerDirectivos() {
        List<Map<String, String>> directivos = new ArrayList<>();

        try {
            // Obtener todos los usuarios con rol de directivo (idRol = 1)
            List<Usuario> todosUsuarios = usuarioMapper.findAll();

            for (Usuario usuario : todosUsuarios) {
                if (usuario.getToken() != null && usuario.getToken().getRol() != null
                        && usuario.getToken().getRol().getIdRol() == 1) {
                    Map<String, String> info = new HashMap<>();

                    // Usar el nombre del correo como identificador
                    String nombre = usuario.getCorreo().split("@")[0];
                    nombre = nombre.substring(0, 1).toUpperCase() + nombre.substring(1);

                    info.put("nombre", nombre);
                    info.put("correo", usuario.getCorreo());
                    directivos.add(info);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al obtener directivos: " + e.getMessage());
        }

        return directivos;
    }
}

