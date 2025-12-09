package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Grado que representa un nivel educativo en el sistema.
 * Ejemplos: Semillitas, Jardín, Transición, etc.
 * Cada grado contiene múltiples grupos (A, B, C, etc.)
 */
@Entity
@Table(name = "\"Grado\"")
public class Grado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idGrado\"")
    private int idGrado;

    @Column(name = "\"nombre\"", nullable = false, length = 100)
    private String nombre;

    /**
     * Grupos asociados a este grado.
     * Relación OneToMany con Grupo
     */
    @OneToMany(mappedBy = "grado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Grupo> grupos;

    public Grado() {
        this.grupos = new ArrayList<>();
    }

    public Grado(String nombre) {
        this.nombre = nombre;
        this.grupos = new ArrayList<>();
    }

    public int getIdGrado() {
        return idGrado;
    }

    public void setIdGrado(int idGrado) {
        this.idGrado = idGrado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Grupo> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<Grupo> grupos) {
        this.grupos = grupos;
    }

    /**
     * Método helper para agregar un grupo al grado
     */
    public void agregarGrupo(Grupo grupo) {
        grupos.add(grupo);
        grupo.setGrado(this);
    }

    /**
     * Método helper para remover un grupo del grado
     */
    public void removerGrupo(Grupo grupo) {
        grupos.remove(grupo);
        grupo.setGrado(null);
    }

    @Override
    public String toString() {
        return "Grado{" +
                "idGrado=" + idGrado +
                ", nombre='" + nombre + '\'' +
                ", cantidadGrupos=" + grupos.size() +
                '}';
    }
}

