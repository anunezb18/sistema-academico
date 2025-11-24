package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.Estudiante;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(EstudianteMapper.class)
class EstudianteMapperTest {

    @Autowired
    private EstudianteMapper estudianteMapper;

    @Test
    void testAgregarEstudiante() {
        Estudiante estudiante = new Estudiante();
        estudiante.setIdFormulario(1);
        estudiante.setFechaIngreso(LocalDate.now());

        Estudiante resultado = estudianteMapper.agregarEstudiante(estudiante);

        assertNotNull(resultado);
        assertTrue(resultado.getIdEstudiante() > 0);
        assertEquals(1, resultado.getIdFormulario());
    }

    @Test
    void testObtenerPorId() {
        Estudiante estudiante = new Estudiante();
        estudiante.setIdFormulario(1);
        estudiante.setFechaIngreso(LocalDate.now());

        Estudiante guardado = estudianteMapper.agregarEstudiante(estudiante);
        Estudiante obtenido = estudianteMapper.obtenerPorId(guardado.getIdEstudiante());

        assertNotNull(obtenido);
        assertEquals(guardado.getIdEstudiante(), obtenido.getIdEstudiante());
    }

    @Test
    void testObtenerPorIdFormulario() {
        Estudiante estudiante = new Estudiante();
        estudiante.setIdFormulario(100);
        estudiante.setFechaIngreso(LocalDate.now());

        estudianteMapper.agregarEstudiante(estudiante);
        Estudiante obtenido = estudianteMapper.obtenerPorIdFormulario(100);

        assertNotNull(obtenido);
        assertEquals(100, obtenido.getIdFormulario());
    }

    @Test
    void testAgregarEstudiante_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            estudianteMapper.agregarEstudiante(null);
        });
    }
}

