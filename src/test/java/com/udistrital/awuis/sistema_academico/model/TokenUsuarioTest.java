package com.udistrital.awuis.sistema_academico.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TokenUsuarioTest {

    @Test
    void generarYQuitarToken_y_esValido() {
        TokenUsuario t = new TokenUsuario();
        t.generarToken();
        assertThat(t.getContenido()).isNotNull();
        assertThat(t.isEstado()).isTrue();
        assertThat(t.getExpiracion()).isNotNull();
        assertThat(t.esValido()).isTrue();

        t.setExpiracion(LocalDateTime.now().minusHours(1));
        assertThat(t.esValido()).isFalse();

        t.quitarToken();
        assertThat(t.getContenido()).isNull();
        assertThat(t.isEstado()).isFalse();
        assertThat(t.getExpiracion()).isNull();
    }

    @Test
    void esValido_conEstadoFalse() {
        TokenUsuario t = new TokenUsuario();
        t.setEstado(false);
        t.setExpiracion(LocalDateTime.now().plusHours(1));
        assertThat(t.esValido()).isFalse();
    }
}
