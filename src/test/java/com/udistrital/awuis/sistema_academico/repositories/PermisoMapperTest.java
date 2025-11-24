package com.udistrital.awuis.sistema_academico.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.udistrital.awuis.sistema_academico.model.Permiso;

@SpringBootTest
@Transactional
class PermisoMapperTest {

    @Autowired
    private PermisoMapper permisoMapper;

    @Test
    void agregarYListarPermiso() {
        Permiso p = new Permiso();
        p.setDescripcion("PRUEBA_PERMISO");

        permisoMapper.agregarPermiso(p);

        List<Permiso> lista = permisoMapper.listarPermisos();
        assertThat(lista).isNotEmpty();
        assertThat(lista).extracting("descripcion").contains("PRUEBA_PERMISO");
    }

    @Test
    void agregarPermisoDescripcionNull() {
        Permiso p = new Permiso();
        p.setDescripcion(null);
        permisoMapper.agregarPermiso(p);

        List<Permiso> lista = permisoMapper.listarPermisos();
        assertThat(lista).isNotEmpty();
    }
}
