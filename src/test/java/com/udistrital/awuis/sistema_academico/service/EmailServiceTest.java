package com.udistrital.awuis.sistema_academico.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    void testEnviarNotificacionEntrevista() {
        LocalDateTime fechaHora = LocalDateTime.now().plusDays(3);

        boolean resultado = emailService.enviarNotificacionEntrevista(
            "Juan Perez",
            "test@example.com",
            fechaHora
        );

        assertTrue(resultado, "El correo deberia enviarse exitosamente");
    }

    @Test
    void testEnviarNotificacionEntrevista_ConDatosNulos() {
        boolean resultado = emailService.enviarNotificacionEntrevista(
            null,
            null,
            null
        );

        // El servicio debe manejar errores y retornar false
        assertFalse(resultado, "Deberia retornar false cuando hay datos nulos");
    }

    @Test
    void testEnviarNotificacionRechazo() {
        String razonRechazo = "No cumple con los requisitos academicos minimos para el grado solicitado.";

        boolean resultado = emailService.enviarNotificacionRechazo(
            "Carlos Martinez",
            "carlos.padre@example.com",
            razonRechazo
        );

        assertTrue(resultado, "El correo de rechazo deberia enviarse exitosamente");
    }

    @Test
    void testEnviarNotificacionRechazo_SinRazon() {
        boolean resultado = emailService.enviarNotificacionRechazo(
            "Maria Lopez",
            "maria.madre@example.com",
            ""
        );

        // Debe enviar de todas formas con "No especificada"
        assertTrue(resultado, "El correo deberia enviarse incluso sin razon especifica");
    }

    @Test
    void testEnviarNotificacionRechazo_ConDatosNulos() {
        boolean resultado = emailService.enviarNotificacionRechazo(
            null,
            null,
            null
        );

        assertFalse(resultado, "Deberia retornar false cuando hay datos nulos");
    }
}
