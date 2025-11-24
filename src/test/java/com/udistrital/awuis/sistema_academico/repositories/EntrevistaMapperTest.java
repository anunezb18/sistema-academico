package com.udistrital.awuis.sistema_academico.repositories;
}
    }
        });
            entrevistaMapper.agregarEntrevista(null);
        assertThrows(IllegalArgumentException.class, () -> {
    void testAgregarEntrevista_Null() {
    @Test

    }
        assertEquals("CANCELADA", cancelada.getEstado());
        Entrevista cancelada = entrevistaMapper.obtenerPorId(guardada.getIdEntrevista());

        entrevistaMapper.cancelarEntrevista(guardada.getIdEntrevista());

        Entrevista guardada = entrevistaMapper.agregarEntrevista(entrevista);
        Entrevista entrevista = new Entrevista(1, fechaHora, "PROGRAMADA");
        LocalDateTime fechaHora = LocalDateTime.now().plusDays(4);
    void testCancelarEntrevista() {
    @Test

    }
        assertEquals(5, entrevistas.get(0).getIdAspirante());
        assertEquals(1, entrevistas.size());
        assertFalse(entrevistas.isEmpty());

        var entrevistas = entrevistaMapper.obtenerPorAspirante(5);

        entrevistaMapper.agregarEntrevista(entrevista);
        Entrevista entrevista = new Entrevista(5, fechaHora, "PROGRAMADA");
        LocalDateTime fechaHora = LocalDateTime.now().plusDays(3);
    void testObtenerPorAspirante() {
    @Test

    }
        assertTrue(cruce, "Deberia detectar cruce de horario");

        boolean cruce = entrevistaMapper.verificarCruceHorario(fechaHoraCruce, 60);
        LocalDateTime fechaHoraCruce = fechaHora.plusMinutes(30);
        // Verificar cruce 30 minutos después

        entrevistaMapper.agregarEntrevista(entrevista);
        Entrevista entrevista = new Entrevista(1, fechaHora, "PROGRAMADA");
        LocalDateTime fechaHora = LocalDateTime.now().plusDays(2).withHour(10).withMinute(0);
    void testVerificarCruceHorario_ConCruce() {
    @Test

    }
        assertFalse(cruce, "No deberia haber cruce cuando no hay entrevistas");

        boolean cruce = entrevistaMapper.verificarCruceHorario(fechaHora, 60);

        LocalDateTime fechaHora = LocalDateTime.now().plusDays(5);
    void testVerificarCruceHorario_NoCruce() {
    @Test

    }
        assertEquals("PROGRAMADA", resultado.getEstado());
        assertTrue(resultado.getIdEntrevista() > 0);
        assertNotNull(resultado);

        Entrevista resultado = entrevistaMapper.agregarEntrevista(entrevista);

        Entrevista entrevista = new Entrevista(1, fechaHora, "PROGRAMADA");
        LocalDateTime fechaHora = LocalDateTime.now().plusDays(1);
    void testAgregarEntrevista() {
    @Test

    private EntrevistaMapper entrevistaMapper;
    @Autowired

class EntrevistaMapperTest {
@Import(EntrevistaMapper.class)
@DataJpaTest

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import com.udistrital.awuis.sistema_academico.model.Entrevista;


