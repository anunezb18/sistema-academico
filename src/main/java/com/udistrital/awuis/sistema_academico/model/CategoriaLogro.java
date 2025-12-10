package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.*;

/**
 * Entidad CategoriaLogro.
 * Define las categorías a las que pertenecen los logros (ej: Matemáticas, Español, etc.)
 */
@Entity
@Table(name = "\"CategoriaLogro\"")
public class CategoriaLogro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idCategoria\"")
    private int idCategoria;

    @Column(name = "\"nombre\"", nullable = false)
    private String nombre;

    public CategoriaLogro() {
        super();
    }

    public CategoriaLogro(String nombre) {
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "CategoriaLogro{" +
                "idCategoria=" + idCategoria +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}

