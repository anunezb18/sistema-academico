package com.udistrital.awuis.sistema_academico.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.udistrital.awuis.sistema_academico.model.TokenUsuario;

@SpringBootTest
@Transactional
class TokenUsuarioMapperTest {

    @Autowired
    private TokenUsuarioMapper tokenUsuarioMapper;

    @Test
    void guardarYObtenerYDesactivarToken() {
        TokenUsuario t = new TokenUsuario();
        t.generarToken();

        tokenUsuarioMapper.guardarToken(t);

        TokenUsuario b = tokenUsuarioMapper.obtenerToken(t.getContenido());
        assertThat(b).isNotNull();
        assertThat(b.isEstado()).isTrue();

        tokenUsuarioMapper.desactivarToken(b.getIdToken());

        TokenUsuario after = tokenUsuarioMapper.obtenerToken(t.getContenido());
        assertThat(after).isNull();
    }

    @Test
    void obtenerToken_inexistente_devuelveNull() {
        TokenUsuario b = tokenUsuarioMapper.obtenerToken("NO_EXISTE");
        assertThat(b).isNull();
    }
}
