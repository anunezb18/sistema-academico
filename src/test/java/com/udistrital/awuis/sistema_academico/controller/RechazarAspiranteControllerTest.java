package com.udistrital.awuis.sistema_academico.controller;

import com.udistrital.awuis.sistema_academico.model.Aspirante;
import com.udistrital.awuis.sistema_academico.model.Formulario;
import com.udistrital.awuis.sistema_academico.repositories.AspiranteMapper;
import com.udistrital.awuis.sistema_academico.repositories.FormularioMapper;
import com.udistrital.awuis.sistema_academico.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests para el caso de uso "Rechazar Estudiante"
 */
@WebMvcTest(DirectivoController.class)
class RechazarAspiranteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AspiranteMapper aspiranteMapper;

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
        formularioTest.setNombreCompleto("Pedro Gonzalez");
        formularioTest.setCorreoResponsable("pedro.responsable@test.com");
        formularioTest.setEstado("NUEVO");

        aspiranteTest = new Aspirante();
        aspiranteTest.setIdAspirante(1);
        aspiranteTest.setFormulario(formularioTest);
    }

    @Test
    void testRechazarAspirante_Exitoso() throws Exception {
        // Configurar mocks
        when(aspiranteMapper.obtenerAspirantePorId(1)).thenReturn(aspiranteTest);
        when(emailService.enviarNotificacionRechazo(anyString(), anyString(), anyString()))
            .thenReturn(true);

        // Ejecutar con razón de rechazo
        mockMvc.perform(post("/directivo/aspirantes/1/rechazar")
                .param("razonRechazo", "No cumple con los requisitos academicos"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directivo/aspirantes"))
            .andExpect(flash().attributeExists("mensaje"));

        // Verificar que se llamaron los métodos correctos
        verify(emailService, times(1)).enviarNotificacionRechazo(
            eq("Pedro Gonzalez"),
            eq("pedro.responsable@test.com"),
            eq("No cumple con los requisitos academicos")
        );
        verify(formularioMapper, times(1)).actualizarFormulario(any(Formulario.class));
    }

    @Test
    void testRechazarAspirante_SinRazon() throws Exception {
        when(aspiranteMapper.obtenerAspirantePorId(1)).thenReturn(aspiranteTest);

        // Ejecutar sin razón de rechazo
        mockMvc.perform(post("/directivo/aspirantes/1/rechazar"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directivo/aspirantes/1/revisar"))
            .andExpect(flash().attributeExists("error"));

        // Verificar que NO se envió correo ni se actualizó el formulario
        verify(emailService, never()).enviarNotificacionRechazo(anyString(), anyString(), anyString());
        verify(formularioMapper, never()).actualizarFormulario(any(Formulario.class));
    }

    @Test
    void testRechazarAspirante_RazonVacia() throws Exception {
        when(aspiranteMapper.obtenerAspirantePorId(1)).thenReturn(aspiranteTest);

        // Ejecutar con razón vacía
        mockMvc.perform(post("/directivo/aspirantes/1/rechazar")
                .param("razonRechazo", "   "))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directivo/aspirantes/1/revisar"))
            .andExpect(flash().attributeExists("error"));

        // Verificar que NO se procesó
        verify(emailService, never()).enviarNotificacionRechazo(anyString(), anyString(), anyString());
        verify(formularioMapper, never()).actualizarFormulario(any(Formulario.class));
    }

    @Test
    void testRechazarAspirante_AspiranteNoEncontrado() throws Exception {
        when(aspiranteMapper.obtenerAspirantePorId(999)).thenReturn(null);

        mockMvc.perform(post("/directivo/aspirantes/999/rechazar")
                .param("razonRechazo", "Motivo de rechazo"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directivo/aspirantes"))
            .andExpect(flash().attributeExists("error"));

        verify(emailService, never()).enviarNotificacionRechazo(anyString(), anyString(), anyString());
    }

    @Test
    void testRechazarAspirante_YaAceptado() throws Exception {
        formularioTest.setEstado("ACEPTADO");
        when(aspiranteMapper.obtenerAspirantePorId(1)).thenReturn(aspiranteTest);

        mockMvc.perform(post("/directivo/aspirantes/1/rechazar")
                .param("razonRechazo", "Motivo de rechazo"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directivo/aspirantes"))
            .andExpect(flash().attributeExists("error"));

        verify(emailService, never()).enviarNotificacionRechazo(anyString(), anyString(), anyString());
        verify(formularioMapper, never()).actualizarFormulario(any(Formulario.class));
    }

    @Test
    void testRechazarAspirante_YaRechazado() throws Exception {
        formularioTest.setEstado("RECHAZADO");
        when(aspiranteMapper.obtenerAspirantePorId(1)).thenReturn(aspiranteTest);

        mockMvc.perform(post("/directivo/aspirantes/1/rechazar")
                .param("razonRechazo", "Motivo de rechazo"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directivo/aspirantes"))
            .andExpect(flash().attributeExists("error"));

        verify(emailService, never()).enviarNotificacionRechazo(anyString(), anyString(), anyString());
    }

    @Test
    void testRechazarAspirante_ErrorEnvioCorreo() throws Exception {
        // Configurar que el correo falle
        when(aspiranteMapper.obtenerAspirantePorId(1)).thenReturn(aspiranteTest);
        when(emailService.enviarNotificacionRechazo(anyString(), anyString(), anyString()))
            .thenReturn(false);

        mockMvc.perform(post("/directivo/aspirantes/1/rechazar")
                .param("razonRechazo", "Motivo de rechazo"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directivo/aspirantes"))
            .andExpect(flash().attributeExists("mensaje")); // Debe continuar con advertencia

        // Verificar que se intentó enviar el correo
        verify(emailService, times(1)).enviarNotificacionRechazo(anyString(), anyString(), anyString());
        // Y que se actualizó el formulario de todas formas
        verify(formularioMapper, times(1)).actualizarFormulario(any(Formulario.class));
    }

    @Test
    void testRechazarAspirante_ErrorActualizarEstado() throws Exception {
        when(aspiranteMapper.obtenerAspirantePorId(1)).thenReturn(aspiranteTest);
        when(emailService.enviarNotificacionRechazo(anyString(), anyString(), anyString()))
            .thenReturn(true);
        when(formularioMapper.actualizarFormulario(any(Formulario.class)))
            .thenThrow(new RuntimeException("Error de base de datos"));

        mockMvc.perform(post("/directivo/aspirantes/1/rechazar")
                .param("razonRechazo", "Motivo de rechazo"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/directivo/aspirantes"))
            .andExpect(flash().attributeExists("error"));
    }
}

