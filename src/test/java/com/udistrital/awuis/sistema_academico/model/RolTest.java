package com.udistrital.awuis.sistema_academico.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RolTest {

    @Test
    void agregarYEliminarPermisos() throws Exception {
        Rol r = new Rol();
        Permiso p1 = new Permiso("A");
        Permiso p2 = new Permiso("B");

        r.agregarPermiso(p1);
        r.agregarPermiso(p2);
        r.agregarPermiso(p1); // duplicado no debe agregarse

        java.lang.reflect.Field f = Rol.class.getDeclaredField("permisos");
        f.setAccessible(true);
        Permiso[] permisos = (Permiso[]) f.get(r);

        assertThat(permisos).isNotNull();
        assertThat(permisos).hasSize(2);

        r.eliminarPermiso(p1);
        permisos = (Permiso[]) f.get(r);
        assertThat(permisos).hasSize(1);
        assertThat(permisos[0].getDescripcion()).isEqualTo("B");
    }

    @Test
    void agregarNull_noProvocaExcepcion() throws Exception {
        Rol r = new Rol();
        r.agregarPermiso(null);
        java.lang.reflect.Field f = Rol.class.getDeclaredField("permisos");
        f.setAccessible(true);
        Permiso[] permisos = (Permiso[]) f.get(r);
        // para un rol nuevo, agregar null no debe crear el arreglo de permisos
        assertThat(permisos).isNull();
    }
}
