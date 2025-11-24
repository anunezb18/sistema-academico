package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad Rol
 */
@Entity
@Table(name = "Rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idRol;

    private String nombre;

    private Permiso[] permisos;

    public Rol() {
    }

    public void agregarPermiso(Permiso permiso) {
        if (permiso == null) return;
        if (permisos == null) {
            permisos = new Permiso[] { permiso };
            return;
        }

        for (Permiso p : permisos) {
            if (p != null && p.equals(permiso)) {
                return;
            }
        }
        Permiso[] nuevo = new Permiso[permisos.length + 1];
        System.arraycopy(permisos, 0, nuevo, 0, permisos.length);
        nuevo[permisos.length] = permiso;
        permisos = nuevo;
    }

    public void eliminarPermiso(Permiso permiso) {
        if (permiso == null || permisos == null) return;
        int count = 0;
        for (Permiso p : permisos) if (p != null && p.equals(permiso)) count++;
        if (count == 0) return;
        Permiso[] nuevo = new Permiso[permisos.length - 1];
        int idx = 0;
        boolean removed = false;
        for (Permiso p : permisos) {
            if (!removed && p != null && p.equals(permiso)) {
                removed = true;
                continue;
            }
            if (idx < nuevo.length) {
                nuevo[idx++] = p;
            }
        }
        permisos = nuevo;
    }
}
