package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Entidad Rol
 */
@Entity
@Table(name = "\"Rol\"")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idRol\"")
    private int idRol;

    @Column(name = "\"nombre\"")
    private String nombre;

    @Transient  // No es una columna en la base de datos
    private Permiso[] permisos;

    public Rol() {
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Permiso[] getPermisos() {
        return permisos;
    }

    public void setPermisos(Permiso[] permisos) {
        this.permisos = permisos;
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
