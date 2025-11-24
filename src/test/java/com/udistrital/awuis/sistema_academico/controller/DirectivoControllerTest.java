package com.udistrital.awuis.sistema_academico.controller;

import com.udistrital.awuis.sistema_academico.model.Aspirante;
import com.udistrital.awuis.sistema_academico.model.Entrevista;
import com.udistrital.awuis.sistema_academico.model.Estudiante;
import com.udistrital.awuis.sistema_academico.model.Formulario;
import com.udistrital.awuis.sistema_academico.repositories.AspiranteMapper;
import com.udistrital.awuis.sistema_academico.repositories.EntrevistaMapper;
import com.udistrital.awuis.sistema_academico.repositories.EstudianteMapper;
import com.udistrital.awuis.sistema_academico.repositories.FormularioMapper;
import com.udistrital.awuis.sistema_academico.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DirectivoController.class)
class DirectivoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AspiranteMapper aspiranteMapper;

    @MockBean
    private EstudianteMapper estudianteMapper;

    @MockBean
    private EntrevistaMapper entrevistaMapper;

    @MockBean
    private FormularioMapper formularioMapper;

    @MockBean
    private EmailService emailService;

    private Aspirante aspiranteTest;
    private Formulario formularioTest;

    @BeforeEach
    void setUp() {
        formularioTest = new Formulario();
        formularioTest.setIdFormulario(1);
        formularioTest.setNombreCompleto("Juan Perez");
        formularioTest.setCorreoResponsable("responsable@test.com");
        formularioTest.setEstado("NUEVO");

        aspiranteTest = new Aspirante();
        aspiranteTest.setIdAspirante(1);
        aspiranteTest.setFormulario(formularioTest);
    }

    @Test
    void testAceptarAspirante_Exitoso() throws Exception {
        // Configurar mocks
        when(aspiranteMapper.obtenerAspirantePorId(1)).thenReturn(aspiranteTest);
        when(estudianteMapper.obtenerPorIdFormulario(1)).thenReturn(null);
        when(entrevistaMapper.verificarCruceHorario(any(LocalDateTime.class), anyInt())).thenReturn(false);
        when(emailService.enviarNotificacionEntrevista(anyString(), anyString(), any(LocalDateTime.class)))
            .thenReturn(true);

        Estudiante estudianteCreado = new Estudiante();
        estudianteCreado.setIdEstudiante(1);
        when(estudianteMapper.agregarEstudiante(any(Estudiante.class))).thenReturn(estudianteCreado);

        // Ejecutar
        mockMvc.perform(post("/directivo/aspirantes/1/aceptar"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directivo/aspirantes"))
            .andExpect(flash().attributeExists("mensaje"));

        // Verificar
        verify(estudianteMapper, times(1)).agregarEstudiante(any(Estudiante.class));
        verify(entrevistaMapper, times(1)).agregarEntrevista(any(Entrevista.class));
        verify(emailService, times(1)).enviarNotificacionEntrevista(anyString(), anyString(), any(LocalDateTime.class));
        verify(formularioMapper, times(1)).actualizarFormulario(any(Formulario.class));
    }

    @Test
    void testAceptarAspirante_AspiranteNoEncontrado() throws Exception {
        when(aspiranteMapper.obtenerAspirantePorId(999)).thenReturn(null);

        mockMvc.perform(post("/directivo/aspirantes/999/aceptar"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directivo/aspirantes"))
            .andExpect(flash().attributeExists("error"));

        verify(estudianteMapper, never()).agregarEstudiante(any(Estudiante.class));
    }

    @Test
    void testAceptarAspirante_YaAceptado() throws Exception {
        Estudiante estudianteExistente = new Estudiante();
        estudianteExistente.setIdEstudiante(1);

        when(aspiranteMapper.obtenerAspirantePorId(1)).thenReturn(aspiranteTest);
        when(estudianteMapper.obtenerPorIdFormulario(1)).thenReturn(estudianteExistente);

        mockMvc.perform(post("/directivo/aspirantes/1/aceptar"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directivo/aspirantes"))
            .andExpect(flash().attributeExists("error"));

        verify(estudianteMapper, never()).agregarEstudiante(any(Estudiante.class));
    }

    @Test
    void testAceptarAspirante_ConCruceDeHorario() throws Exception {
        when(aspiranteMapper.obtenerAspirantePorId(1)).thenReturn(aspiranteTest);
        when(estudianteMapper.obtenerPorIdFormulario(1)).thenReturn(null);

        // Primer intento tiene cruce, segundo no
        when(entrevistaMapper.verificarCruceHorario(any(LocalDateTime.class), anyInt()))
            .thenReturn(true, false);
        when(emailService.enviarNotificacionEntrevista(anyString(), anyString(), any(LocalDateTime.class)))
            .thenReturn(true);

        Estudiante estudianteCreado = new Estudiante();
        estudianteCreado.setIdEstudiante(1);
        when(estudianteMapper.agregarEstudiante(any(Estudiante.class))).thenReturn(estudianteCreado);

        mockMvc.perform(post("/directivo/aspirantes/1/aceptar"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directivo/aspirantes"))
            .andExpect(flash().attributeExists("mensaje"));

        // Verificar que se intentó verificar el horario al menos 2 veces
        verify(entrevistaMapper, atLeast(2)).verificarCruceHorario(any(LocalDateTime.class), anyInt());
    }

    @Test
    void testListarAspirantes() throws Exception {
        mockMvc.perform(get("/directivo/aspirantes"))
            .andExpect(status().isOk())
            .andExpect(view().name("aspirantes"))
            .andExpect(model().attributeExists("aspirantes"));

        verify(aspiranteMapper, times(1)).listarAspirantes();
    }

    @Test
    void testRevisarAspirante() throws Exception {
        when(aspiranteMapper.obtenerAspirantePorId(1)).thenReturn(aspiranteTest);

        mockMvc.perform(get("/directivo/aspirantes/1/revisar"))
            .andExpect(status().isOk())
            .andExpect(view().name("revisar-aspirante"))
            .andExpect(model().attributeExists("aspirante"));
    }

    @Test
    void testRechazarAspirante() throws Exception {
        when(aspiranteMapper.obtenerAspirantePorId(1)).thenReturn(aspiranteTest);

        mockMvc.perform(post("/directivo/aspirantes/1/rechazar")
                .param("razonRechazo", "No cumple con requisitos"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directivo/aspirantes"));

        verify(formularioMapper, times(1)).actualizarFormulario(any(Formulario.class));
    }
}

