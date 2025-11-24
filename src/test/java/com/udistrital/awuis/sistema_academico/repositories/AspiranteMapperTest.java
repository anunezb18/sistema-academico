package com.udistrital.awuis.sistema_academico.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.udistrital.awuis.sistema_academico.model.Aspirante;
import com.udistrital.awuis.sistema_academico.model.Formulario;

@SpringBootTest
@Transactional
class AspiranteMapperTest {

    @Autowired
    private AspiranteMapper aspiranteMapper;

    @Autowired
    private FormularioMapper formularioMapper;

    @Test
    void agregarYListarAspirante_y_asignarFormulario() {
        Aspirante a = new Aspirante();
        aspiranteMapper.agregarAspirante(a);

        List<Aspirante> lista = aspiranteMapper.listarAspirantes();
        assertThat(lista).isNotEmpty();

        Formulario f = new Formulario();
        f.setEstado("PENDIENTE");
        formularioMapper.agregarFormulario(f);

        int idA = a.getIdAspirante();
        aspiranteMapper.asignarFormulario(idA, f);

        Aspirante actualizado = aspiranteMapper.obtenerPorId(idA);
        assertThat(actualizado).isNotNull();
        assertThat(actualizado.getFormulario()).isNotNull();
        assertThat(actualizado.getFormulario().getIdFormulario()).isEqualTo(f.getIdFormulario());
    }
}
