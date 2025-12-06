package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.*;

/**
 * Entidad Observador
 * Se crea automáticamente cuando se registra un nuevo estudiante
 * Solo contiene el ID del observador
 */
@Entity
@Table(name = "\"Observador\"")
public class Observador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idObservador\"")
    private int idObservador;

    public Observador() {
    }

    public int getIdObservador() {
        return idObservador;
    }

    public void setIdObservador(int idObservador) {
        this.idObservador = idObservador;
    }
}

