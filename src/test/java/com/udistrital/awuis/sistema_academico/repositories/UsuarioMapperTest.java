package com.udistrital.awuis.sistema_academico.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.udistrital.awuis.sistema_academico.model.TokenUsuario;
import com.udistrital.awuis.sistema_academico.model.Usuario;

@SpringBootTest
@Transactional
class UsuarioMapperTest {

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private TokenUsuarioMapper tokenUsuarioMapper;

    @Test
    void agregarEInhabilitarUsuario_funcionaCorrectamente() {
        Usuario u = new Usuario();
        u.setNombre("TestUser");
        u.setCorreo("test@example.com");
        u.setContrasena("pwd");

        TokenUsuario t = new TokenUsuario();
        t.generarToken();
        tokenUsuarioMapper.guardarToken(t);

        TokenUsuario buscadoAntes = tokenUsuarioMapper.obtenerToken(t.getContenido());
        assertThat(buscadoAntes).isNotNull();

        u.setToken(t);

        usuarioMapper.agregarUsuario(321, u);

        usuarioMapper.inhabilitarUsuario(321);

        TokenUsuario buscadoDespues = tokenUsuarioMapper.obtenerToken(t.getContenido());
        assertThat(buscadoDespues).isNull();
    }

    @Test
    void agregarUsuarioNull_noProvocaExcepcion() {
        // no debe lanzar excepción
        assertDoesNotThrow(() -> usuarioMapper.agregarUsuario(111, null));
    }
}
