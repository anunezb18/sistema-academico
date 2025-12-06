package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.*;

/**
 * Entidad HistorialAcademico
 * Se crea automáticamente cuando se registra un nuevo estudiante
 */
@Entity
@Table(name = "\"HistorialAcademico\"")
public class HistorialAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idHistorialAcademico\"")
    private int idHistorialAcademico;

    @Column(name = "\"idObservador\"")
    private Integer idObservador;

    // Agregar otros campos según el esquema de la base de datos

    public HistorialAcademico() {
    }

    public int getIdHistorialAcademico() {
        return idHistorialAcademico;
    }

    public void setIdHistorialAcademico(int idHistorialAcademico) {
        this.idHistorialAcademico = idHistorialAcademico;
    }

    public Integer getIdObservador() {
        return idObservador;
    }

    public void setIdObservador(Integer idObservador) {
        this.idObservador = idObservador;
    }
}
