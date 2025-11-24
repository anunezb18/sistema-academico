package com.udistrital.awuis.sistema_academico.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.udistrital.awuis.sistema_academico.model.Permiso;
import com.udistrital.awuis.sistema_academico.model.Rol;

import jakarta.persistence.EntityManager;

@SpringBootTest
@Transactional
class RolMapperTest {

    @Autowired
    private RolMapper rolMapper;

    @Autowired
    private EntityManager em;

    @Test
    void insertarRol_y_obtenerPorId_obtenerPermisosPorRol() throws Exception {
        Rol r = new Rol();
        java.lang.reflect.Field nombreF = Rol.class.getDeclaredField("nombre");
        nombreF.setAccessible(true);
        nombreF.set(r, "ROL_TEST_X");

        // preparar permisos
        Permiso p1 = new Permiso("PX1");
        Permiso p2 = new Permiso("PX2");

        java.lang.reflect.Field permisosF = Rol.class.getDeclaredField("permisos");
        permisosF.setAccessible(true);
        permisosF.set(r, new Permiso[] { p1, p2 });

        rolMapper.insertarRol(r);

        // obtener por nombre con EntityManager
        Rol encontrado = em.createQuery("SELECT r FROM Rol r WHERE r.nombre = :n", Rol.class)
                .setParameter("n", "ROL_TEST_X").getSingleResult();

        assertThat(encontrado).isNotNull();

        // obtenerPorId
        java.lang.reflect.Field idF = Rol.class.getDeclaredField("idRol");
        idF.setAccessible(true);
        int id = (int) idF.get(encontrado);

        Rol porId = rolMapper.obtenerPorId(id);
        assertThat(porId).isNotNull();

        // obtenerPermisosPorRol
        var listaPermisos = rolMapper.obtenerPermisosPorRol(id);
        assertThat(listaPermisos).isNotNull();
    }
}
