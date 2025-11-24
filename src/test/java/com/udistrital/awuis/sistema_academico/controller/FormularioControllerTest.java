package com.udistrital.awuis.sistema_academico.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.udistrital.awuis.sistema_academico.repositories.AspiranteMapper;
import com.udistrital.awuis.sistema_academico.repositories.FormularioMapper;

class FormularioControllerTest {

    private MockMvc mvc;
    private FormularioMapper formularioMapper;
    private AspiranteMapper aspiranteMapper;

    @BeforeEach
    void setup() {
        formularioMapper = mock(FormularioMapper.class);
        aspiranteMapper = mock(AspiranteMapper.class);

        FormularioController controller = new FormularioController();
        controller.setFormularioMapper(formularioMapper);
        controller.setAspiranteMapper(aspiranteMapper);

        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void guardarFormulario_exito_redirigeYReseteaIntentos() throws Exception {
        mvc.perform(post("/formularios/guardar")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("nombre", "Juan")
                .param("fechaNacimiento", "2015-05-01")
                .param("grado", "1º Básico")
                .param("nombreResp", "Ana")
                .param("telefono", "1234567"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/formulario/confirmacion"));
    }

    @Test
    void guardarFormulario_errorBD_muestraMensaje() throws Exception {
        MockHttpSession session = new MockHttpSession();

        doThrow(new org.springframework.dao.DataAccessResourceFailureException("DB down"))
            .when(formularioMapper).agregarFormulario(any());

        mvc.perform(post("/formularios/guardar").session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("nombre", "Juan")
                .param("fechaNacimiento", "2015-05-01")
                .param("grado", "1º Básico")
                .param("nombreResp", "Ana")
                .param("telefono", "1234567"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("message"));
    }
}
