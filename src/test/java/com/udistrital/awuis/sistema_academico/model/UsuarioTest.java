package com.udistrital.awuis.sistema_academico.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UsuarioTest {

    @Test
    void gettersSetters_y_token() {
        Usuario u = new Usuario();
        u.setIdUsuario(99);
        u.setCorreo("ana@x.com");
        u.setContrasena("p123");

        TokenUsuario t = new TokenUsuario();
        t.generarToken();
        u.setToken(t);

        assertThat(u.getIdUsuario()).isEqualTo(99);
        assertThat(u.getCorreo()).isEqualTo("ana@x.com");
        assertThat(u.getContrasena()).isEqualTo("p123");
        assertThat(u.getToken()).isNotNull();
        assertThat(u.getToken().esValido()).isTrue();
    }
}
