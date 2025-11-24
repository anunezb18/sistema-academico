package com.udistrital.awuis.sistema_academico.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PermisoTest {

    @Test
    void gettersSettersYToString() {
        Permiso p = new Permiso();
        p.setIdPermiso(7);
        p.setDescripcion("TEST_DESC");

        assertThat(p.getIdPermiso()).isEqualTo(7);
        assertThat(p.getDescripcion()).isEqualTo("TEST_DESC");
        assertThat(p.toString()).contains("TEST_DESC");
    }

    @Test
    void descripcionNula_noProvocaNPE() {
        Permiso p = new Permiso();
        p.setDescripcion(null);
        assertThat(p.getDescripcion()).isNull();
        // toString debe funcionar aunque la descripcion sea null
        assertThat(p.toString()).contains("idPermiso");
    }
}
