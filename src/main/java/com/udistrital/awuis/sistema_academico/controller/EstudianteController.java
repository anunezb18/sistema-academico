package com.udistrital.awuis.sistema_academico.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.udistrital.awuis.sistema_academico.model.Usuario;
import com.udistrital.awuis.sistema_academico.repositories.*;
import com.udistrital.awuis.sistema_academico.model.*;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class EstudianteController {

    @Autowired
    private EstudianteMapper estudianteMapper;

    @Autowired
    private FormularioMapper formularioMapper;

    @Autowired
    private ObservadorMapper observadorMapper;

    @Autowired
    private HistorialAcademicoMapper historialAcademicoMapper;

    @Autowired
    private GrupoMapper grupoMapper;

    @Autowired
    private GradoMapper gradoMapper;

    @Autowired
    private ProfesorMapper profesorMapper;

    @Autowired
    private LogroBoletinMapper logroBoletinMapper;

    @Autowired
    private LogroMapper logroMapper;

    @Autowired
    private BoletinMapper boletinMapper;

    @Autowired
    private CategoriaLogroMapper categoriaLogroMapper;

    @GetMapping("/estudiante")
    public String estudiante(@SessionAttribute(value = "usuario", required = false) Usuario usuario, Model model) {
        if (usuario == null) {
            return "redirect:/login";
        }

        // Validar que sea estudiante (idRol = 4)
        if (usuario.getToken() == null || usuario.getToken().getRol() == null) {
            return "redirect:/login";
        }

        int idRol = usuario.getToken().getRol().getIdRol();
        if (idRol != 4) {
            // No es estudiante, redirigir a su panel correspondiente
            if (idRol == 1 || idRol == 3) {
                return "redirect:/directivo";
            } else if (idRol == 2) {
                return "redirect:/profesor";
            } else {
                return "redirect:/administrador";
            }
        }

        // Buscar el estudiante por idUsuario
        String nombreEstudiante = "Estudiante";
        String correoEstudiante = usuario.getCorreo();

        try {
            // Buscar estudiante por idUsuario usando el método del mapper
            Estudiante estudiante = estudianteMapper.findByIdUsuario(usuario.getIdUsuario()).orElse(null);

            if (estudiante != null && estudiante.getIdFormulario() != null) {
                // Obtener el nombre del formulario asociado
                Formulario formulario = formularioMapper.obtenerPorId(estudiante.getIdFormulario());
                if (formulario != null && formulario.getNombreCompleto() != null) {
                    nombreEstudiante = formulario.getNombreCompleto();
                }
            } else if (estudiante == null) {
                // Si no se encuentra el estudiante, usar el correo
                nombreEstudiante = usuario.getCorreo().split("@")[0];
                nombreEstudiante = nombreEstudiante.substring(0, 1).toUpperCase() +
                                 nombreEstudiante.substring(1);
            }
        } catch (Exception e) {
            System.err.println("Error al buscar estudiante: " + e.getMessage());
            // Si hay error, usar el correo sin @dominio
            nombreEstudiante = usuario.getCorreo().split("@")[0];
            nombreEstudiante = nombreEstudiante.substring(0, 1).toUpperCase() +
                             nombreEstudiante.substring(1);
        }

        model.addAttribute("nombreEstudiante", nombreEstudiante);
        model.addAttribute("correoEstudiante", correoEstudiante);
        return "estudiante";
    }

    /**
     * API REST: Obtener observador del estudiante logueado
     */
    @GetMapping("/estudiante/api/observador")
    @ResponseBody
    public Map<String, Object> obtenerObservador(@SessionAttribute(value = "usuario", required = false) Usuario usuario) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return response;
            }

            // Obtener estudiante por idUsuario
            Estudiante estudiante = estudianteMapper.findByIdUsuario(usuario.getIdUsuario()).orElse(null);
            if (estudiante == null) {
                response.put("success", false);
                response.put("message", "Estudiante no encontrado");
                return response;
            }

            // Obtener historial académico del estudiante
            Integer idHistorialAcademico = estudiante.getIdHistorialAcademico();
            if (idHistorialAcademico == null) {
                response.put("success", true);
                response.put("anotaciones", new ArrayList<>());
                response.put("message", "No hay historial académico");
                return response;
            }

            HistorialAcademico historial = historialAcademicoMapper.findById(idHistorialAcademico).orElse(null);
            if (historial == null) {
                response.put("success", true);
                response.put("anotaciones", new ArrayList<>());
                response.put("message", "No se encontró el historial académico");
                return response;
            }

            // Obtener observador del historial académico
            Integer idObservador = historial.getIdObservador();
            if (idObservador == null) {
                response.put("success", true);
                response.put("anotaciones", new ArrayList<>());
                response.put("message", "No hay observador asignado");
                return response;
            }

            Observador observador = observadorMapper.findById(idObservador).orElse(null);
            if (observador == null) {
                response.put("success", true);
                response.put("anotaciones", new ArrayList<>());
                return response;
            }

            // Obtener anotaciones del observador
            List<Anotacion> anotaciones = observadorMapper.obtenerAnotacionesPorObservador(observador.getIdObservador());

            // Convertir a lista de mapas
            List<Map<String, Object>> anotacionesData = anotaciones.stream()
                    .map(a -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("idAnotacion", a.getIdAnotacion());
                        map.put("tipo", a.getTipo());
                        map.put("descripcion", a.getDescripcion());
                        map.put("fecha", a.getFecha().toString());
                        return map;
                    })
                    .collect(java.util.stream.Collectors.toList());

            response.put("success", true);
            response.put("anotaciones", anotacionesData);

        } catch (Exception e) {
            System.err.println("Error al obtener observador: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al obtener observador: " + e.getMessage());
        }

        return response;
    }

    /**
     * API REST: Obtener hoja de vida del estudiante logueado
     */
    @GetMapping("/estudiante/api/hoja-vida")
    @ResponseBody
    public Map<String, Object> obtenerHojaVida(@SessionAttribute(value = "usuario", required = false) Usuario usuario) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return response;
            }

            // Obtener estudiante
            Estudiante estudiante = estudianteMapper.findByIdUsuario(usuario.getIdUsuario()).orElse(null);
            if (estudiante == null) {
                response.put("success", false);
                response.put("message", "Estudiante no encontrado");
                return response;
            }

            // Obtener formulario
            Formulario formulario = null;
            if (estudiante.getIdFormulario() != null) {
                formulario = formularioMapper.obtenerPorId(estudiante.getIdFormulario());
            }

            // Información personal
            response.put("nombreCompleto", formulario != null ? formulario.getNombreCompleto() : "N/A");
            response.put("correo", formulario != null ? formulario.getCorreoResponsable() : usuario.getCorreo());
            response.put("telefono", formulario != null ? formulario.getTelefonoResponsable() : "N/A");
            response.put("direccion", formulario != null ? formulario.getDireccion() : "N/A");
            response.put("fechaNacimiento", formulario != null && formulario.getFechaNacimiento() != null ? formulario.getFechaNacimiento().toString() : "N/A");

            // Información académica
            String grado = "N/A";
            String grupo = "N/A";
            String profesor = "N/A";

            if (estudiante.getIdGrupo() != null) {
                Grupo grupoObj = grupoMapper.findById(estudiante.getIdGrupo()).orElse(null);
                if (grupoObj != null) {
                    grupo = grupoObj.getNombre();

                    // Obtener grado
                    if (grupoObj.getIdGrado() != null) {
                        Grado gradoObj = gradoMapper.findById(grupoObj.getIdGrado()).orElse(null);
                        if (gradoObj != null) {
                            grado = gradoObj.getNombre();
                        }
                    }

                    // Obtener profesor - usar campo nombre directo
                    if (grupoObj.getIdProfesor() != null) {
                        Profesor profesorObj = profesorMapper.findById(grupoObj.getIdProfesor()).orElse(null);
                        if (profesorObj != null) {
                            // Usar el nombre del profesor si está disponible
                            if (profesorObj.getNombre() != null && !profesorObj.getNombre().isEmpty()) {
                                profesor = profesorObj.getNombre();
                            } else if (profesorObj.getIdUsuario() != null) {
                                // Fallback: usar correo del usuario
                                Usuario usuarioProfesor = profesorObj.getUsuario();
                                if (usuarioProfesor != null && usuarioProfesor.getCorreo() != null) {
                                    profesor = usuarioProfesor.getCorreo().split("@")[0];
                                    // Capitalizar primera letra
                                    profesor = profesor.substring(0, 1).toUpperCase() + profesor.substring(1);
                                }
                            }
                        }
                    }
                }
            }

            response.put("grado", grado);
            response.put("grupo", grupo);
            response.put("profesor", profesor);
            response.put("fechaIngreso", estudiante.getFechaIngreso() != null ? estudiante.getFechaIngreso().toString() : "N/A");

            response.put("success", true);

        } catch (Exception e) {
            System.err.println("Error al obtener hoja de vida: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al obtener información: " + e.getMessage());
        }

        return response;
    }

    /**
     * API REST: Obtener logros del estudiante por periodo
     */
    @GetMapping("/estudiante/api/logros")
    @ResponseBody
    public Map<String, Object> obtenerLogros(
            @RequestParam(required = false) Integer periodo,
            @SessionAttribute(value = "usuario", required = false) Usuario usuario) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (usuario == null) {
                response.put("success", false);
                response.put("message", "Usuario no autenticado");
                return response;
            }

            if (periodo == null || periodo < 1 || periodo > 4) {
                response.put("success", false);
                response.put("message", "Periodo inválido");
                return response;
            }

            // Obtener estudiante
            Estudiante estudiante = estudianteMapper.findByIdUsuario(usuario.getIdUsuario()).orElse(null);
            if (estudiante == null) {
                response.put("success", false);
                response.put("message", "Estudiante no encontrado");
                return response;
            }

            // Obtener historial académico
            Integer idHistorialAcademico = estudiante.getIdHistorialAcademico();
            if (idHistorialAcademico == null) {
                response.put("success", true);
                response.put("logros", new ArrayList<>());
                return response;
            }

            // Buscar boletín del periodo
            java.util.Optional<Boletin> boletinOpt = boletinMapper.findByHistorialAcademicoAndPeriodo(idHistorialAcademico, periodo);
            if (!boletinOpt.isPresent()) {
                response.put("success", true);
                response.put("logros", new ArrayList<>());
                response.put("message", "No hay logros registrados para este periodo");
                return response;
            }

            Boletin boletin = boletinOpt.get();

            // Obtener logros del boletín
            List<LogroBoletin> logrosBoletines = logroBoletinMapper.findByBoletin(boletin.getIdBoletin());

            // Convertir a formato con categoría
            List<Map<String, Object>> logrosData = new ArrayList<>();
            for (LogroBoletin lb : logrosBoletines) {
                Logro logro = logroMapper.findById(lb.getIdLogro()).orElse(null);
                if (logro != null) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("idLogro", logro.getIdLogro());
                    map.put("descripcion", logro.getDescripcion());
                    map.put("valoracion", lb.getValoracion());

                    // Obtener nombre de categoría
                    String nombreCategoria = "Sin categoría";
                    if (logro.getIdCategoria() != null) {
                        CategoriaLogro categoria = categoriaLogroMapper.findById(logro.getIdCategoria()).orElse(null);
                        if (categoria != null) {
                            nombreCategoria = categoria.getNombre();
                        }
                    }
                    map.put("categoria", nombreCategoria);

                    logrosData.add(map);
                }
            }

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
     * Descargar boletín de logros en PDF
     */
    @GetMapping("/estudiante/api/logros/pdf")
    public void descargarBoletinPDF(
            @RequestParam(required = false) Integer periodo,
            @SessionAttribute(value = "usuario", required = false) Usuario usuario,
            jakarta.servlet.http.HttpServletResponse response) {

        try {
            if (usuario == null || periodo == null || periodo < 1 || periodo > 4) {
                response.sendError(jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST, "Parámetros inválidos");
                return;
            }

            // Obtener estudiante
            Estudiante estudiante = estudianteMapper.findByIdUsuario(usuario.getIdUsuario()).orElse(null);
            if (estudiante == null) {
                response.sendError(jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND, "Estudiante no encontrado");
                return;
            }

            // Obtener datos del estudiante
            String nombreEstudiante = "Estudiante";
            String grado = "N/A";
            String grupo = "N/A";

            Formulario formulario = null;
            if (estudiante.getIdFormulario() != null) {
                formulario = formularioMapper.obtenerPorId(estudiante.getIdFormulario());
                if (formulario != null && formulario.getNombreCompleto() != null) {
                    nombreEstudiante = formulario.getNombreCompleto();
                }
            }

            // Obtener grado y grupo
            if (estudiante.getIdGrupo() != null) {
                Grupo grupoObj = grupoMapper.findById(estudiante.getIdGrupo()).orElse(null);
                if (grupoObj != null) {
                    grupo = grupoObj.getNombre();
                    if (grupoObj.getIdGrado() != null) {
                        Grado gradoObj = gradoMapper.findById(grupoObj.getIdGrado()).orElse(null);
                        if (gradoObj != null) {
                            grado = gradoObj.getNombre();
                        }
                    }
                }
            }

            // Obtener logros del periodo
            Integer idHistorialAcademico = estudiante.getIdHistorialAcademico();
            if (idHistorialAcademico == null) {
                response.sendError(jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND, "No hay historial académico");
                return;
            }

            java.util.Optional<Boletin> boletinOpt = boletinMapper.findByHistorialAcademicoAndPeriodo(idHistorialAcademico, periodo);
            if (boletinOpt.isEmpty()) {
                response.sendError(jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND, "No hay logros para este periodo");
                return;
            }

            Boletin boletin = boletinOpt.get();
            List<LogroBoletin> logrosBoletines = logroBoletinMapper.findByBoletin(boletin.getIdBoletin());

            if (logrosBoletines.isEmpty()) {
                response.sendError(jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND, "No hay logros registrados");
                return;
            }

            // Agrupar logros por categoría
            Map<String, List<Map<String, String>>> logrosPorCategoria = new java.util.LinkedHashMap<>();

            for (LogroBoletin lb : logrosBoletines) {
                Logro logro = logroMapper.findById(lb.getIdLogro()).orElse(null);
                if (logro != null) {
                    String nombreCategoria = "Sin categoría";
                    if (logro.getIdCategoria() != null) {
                        CategoriaLogro categoria = categoriaLogroMapper.findById(logro.getIdCategoria()).orElse(null);
                        if (categoria != null) {
                            nombreCategoria = categoria.getNombre();
                        }
                    }

                    logrosPorCategoria.putIfAbsent(nombreCategoria, new ArrayList<>());
                    Map<String, String> logroData = new HashMap<>();
                    logroData.put("descripcion", logro.getDescripcion());
                    logroData.put("valoracion", lb.getValoracion());
                    logrosPorCategoria.get(nombreCategoria).add(logroData);
                }
            }

            // Generar PDF
            generarPDFBoletin(response, nombreEstudiante, grado, grupo, periodo, logrosPorCategoria);

        } catch (Exception e) {
            System.err.println("Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
            try {
                response.sendError(jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error al generar PDF");
            } catch (Exception ex) {
                // Ignorar si ya se envió respuesta
            }
        }
    }

    /**
     * Genera el PDF del boletín con diseño atractivo
     */
    private void generarPDFBoletin(
            jakarta.servlet.http.HttpServletResponse response,
            String nombreEstudiante,
            String grado,
            String grupo,
            int periodo,
            Map<String, List<Map<String, String>>> logrosPorCategoria) throws Exception {

        // Configurar respuesta HTTP
        String nombreArchivo = "Boletin_" + nombreEstudiante.replaceAll(" ", "_") + "_Periodo" + periodo + ".pdf";
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");

        // Crear documento PDF
        com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(response.getOutputStream());
        com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
        com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf, com.itextpdf.kernel.geom.PageSize.A4);
        document.setMargins(40, 40, 40, 40);

        // Colores
        com.itextpdf.kernel.colors.Color colorPrimario = new com.itextpdf.kernel.colors.DeviceRgb(0, 131, 143);
        com.itextpdf.kernel.colors.Color colorSecundario = new com.itextpdf.kernel.colors.DeviceRgb(77, 208, 225);
        com.itextpdf.kernel.colors.Color colorTexto = new com.itextpdf.kernel.colors.DeviceRgb(55, 71, 79);

        // Título principal
        com.itextpdf.layout.element.Paragraph titulo = new com.itextpdf.layout.element.Paragraph("AWUIS - Arte y Aprendizaje")
                .setFontSize(24)
                .setBold()
                .setFontColor(colorPrimario)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(titulo);

        com.itextpdf.layout.element.Paragraph subtitulo = new com.itextpdf.layout.element.Paragraph("Boletín de Logros Académicos")
                .setFontSize(16)
                .setFontColor(colorSecundario)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subtitulo);

        // Línea separadora
        com.itextpdf.layout.element.LineSeparator linea = new com.itextpdf.layout.element.LineSeparator(
                new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(2));
        linea.setMarginBottom(20);
        document.add(linea);

        // Información del estudiante
        com.itextpdf.layout.element.Table tablaInfo = new com.itextpdf.layout.element.Table(new float[]{1, 2});
        tablaInfo.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
        tablaInfo.setMarginBottom(20);

        agregarCeldaInfo(tablaInfo, "Estudiante:", nombreEstudiante, colorPrimario, colorTexto);
        agregarCeldaInfo(tablaInfo, "Grado:", grado, colorPrimario, colorTexto);
        agregarCeldaInfo(tablaInfo, "Grupo:", grupo, colorPrimario, colorTexto);
        agregarCeldaInfo(tablaInfo, "Periodo:", periodo + "° Periodo", colorPrimario, colorTexto);
        agregarCeldaInfo(tablaInfo, "Fecha:", java.time.LocalDate.now().toString(), colorPrimario, colorTexto);

        document.add(tablaInfo);

        // Línea separadora
        document.add(new com.itextpdf.layout.element.LineSeparator(
                new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(1)).setMarginBottom(15));

        // Logros por categoría
        for (Map.Entry<String, List<Map<String, String>>> entry : logrosPorCategoria.entrySet()) {
            String categoria = entry.getKey();
            List<Map<String, String>> logros = entry.getValue();

            // Título de categoría
            com.itextpdf.layout.element.Paragraph tituloCategoria = new com.itextpdf.layout.element.Paragraph(categoria)
                    .setFontSize(14)
                    .setBold()
                    .setFontColor(colorPrimario)
                    .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(224, 247, 250))
                    .setPadding(8)
                    .setMarginTop(10)
                    .setMarginBottom(10);
            document.add(tituloCategoria);

            // Tabla de logros
            com.itextpdf.layout.element.Table tablaLogros = new com.itextpdf.layout.element.Table(new float[]{4, 1});
            tablaLogros.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            tablaLogros.setMarginBottom(15);

            for (Map<String, String> logro : logros) {
                String descripcion = logro.get("descripcion");
                String valoracion = logro.get("valoracion");

                // Celda descripción
                com.itextpdf.layout.element.Cell celdaDesc = new com.itextpdf.layout.element.Cell()
                        .add(new com.itextpdf.layout.element.Paragraph(descripcion)
                                .setFontSize(10)
                                .setFontColor(colorTexto))
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(
                                new com.itextpdf.kernel.colors.DeviceRgb(224, 224, 224), 1))
                        .setPadding(8);
                tablaLogros.addCell(celdaDesc);

                // Celda valoración con color
                com.itextpdf.kernel.colors.Color colorValoracion = obtenerColorValoracion(valoracion);
                com.itextpdf.layout.element.Cell celdaVal = new com.itextpdf.layout.element.Cell()
                        .add(new com.itextpdf.layout.element.Paragraph(valoracion)
                                .setFontSize(10)
                                .setBold()
                                .setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE))
                        .setBackgroundColor(colorValoracion)
                        .setBorder(new com.itextpdf.layout.borders.SolidBorder(
                                new com.itextpdf.kernel.colors.DeviceRgb(224, 224, 224), 1))
                        .setPadding(8)
                        .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
                tablaLogros.addCell(celdaVal);
            }

            document.add(tablaLogros);
        }

        // Pie de página
        document.add(new com.itextpdf.layout.element.LineSeparator(
                new com.itextpdf.kernel.pdf.canvas.draw.SolidLine(1)).setMarginTop(20).setMarginBottom(10));

        com.itextpdf.layout.element.Paragraph piePagina = new com.itextpdf.layout.element.Paragraph(
                "Este documento certifica el desempeño académico del estudiante en el periodo señalado.")
                .setFontSize(9)
                .setFontColor(new com.itextpdf.kernel.colors.DeviceRgb(120, 144, 156))
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)
                .setItalic();
        document.add(piePagina);

        // Cerrar documento
        document.close();
    }

    /**
     * Agrega una fila de información a la tabla
     */
    private void agregarCeldaInfo(
            com.itextpdf.layout.element.Table tabla,
            String etiqueta,
            String valor,
            com.itextpdf.kernel.colors.Color colorEtiqueta,
            com.itextpdf.kernel.colors.Color colorValor) {

        tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new com.itextpdf.layout.element.Paragraph(etiqueta)
                        .setFontSize(11)
                        .setBold()
                        .setFontColor(colorEtiqueta))
                .setBorderBottom(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBorderTop(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBorderLeft(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBorderRight(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(5));

        tabla.addCell(new com.itextpdf.layout.element.Cell()
                .add(new com.itextpdf.layout.element.Paragraph(valor)
                        .setFontSize(11)
                        .setFontColor(colorValor))
                .setBorderBottom(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBorderTop(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBorderLeft(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setBorderRight(com.itextpdf.layout.borders.Border.NO_BORDER)
                .setPadding(5));
    }

    /**
     * Obtiene el color según la valoración
     */
    private com.itextpdf.kernel.colors.Color obtenerColorValoracion(String valoracion) {
        switch (valoracion) {
            case "Superior":
                return new com.itextpdf.kernel.colors.DeviceRgb(76, 175, 80); // Verde
            case "Alto":
                return new com.itextpdf.kernel.colors.DeviceRgb(33, 150, 243); // Azul
            case "Básico":
                return new com.itextpdf.kernel.colors.DeviceRgb(255, 152, 0); // Naranja
            case "Bajo":
                return new com.itextpdf.kernel.colors.DeviceRgb(244, 67, 54); // Rojo
            default:
                return new com.itextpdf.kernel.colors.DeviceRgb(158, 158, 158); // Gris
        }
    }
}

