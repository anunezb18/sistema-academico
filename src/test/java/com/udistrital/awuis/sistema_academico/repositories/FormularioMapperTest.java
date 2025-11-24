package com.udistrital.awuis.sistema_academico.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.udistrital.awuis.sistema_academico.model.Formulario;

@SpringBootTest
@Transactional
class FormularioMapperTest {

    @Autowired
    private FormularioMapper formularioMapper;

    @Test
    void agregarYListarFormulario() {
        Formulario f = new Formulario();
        f.setEstado("PENDIENTE");
        formularioMapper.agregarFormulario(f);

        List<Formulario> lista = formularioMapper.listarFormularios();
        assertThat(lista).isNotEmpty();
        assertThat(lista).extracting("estado").contains("PENDIENTE");
    }

    @Test
    void cambiarEstado_funciona() {
        Formulario f = new Formulario();
        f.setEstado("NUEVO");
        formularioMapper.agregarFormulario(f);

        int id = f.getIdFormulario();
        formularioMapper.cambiarEstado(id, "INACTIVO");

        Formulario encontrado = formularioMapper.obtenerPorId(id);
        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getEstado()).isEqualTo("INACTIVO");
    }
}
