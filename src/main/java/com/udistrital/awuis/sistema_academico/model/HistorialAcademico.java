package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.*;

/**
 * Entidad HistorialAcademico
 * Se crea automáticamente cuando se registra un nuevo estudiante
 * Tiene una relación con Observador y puede contener boletines
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idObservador\"", insertable = false, updatable = false)
    private Observador observador;

    // Agregar otros campos según el esquema de la base de datos
    // TODO: Agregar relación con Boletines cuando se implemente esa entidad

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

    public Observador getObservador() {
        return observador;
    }

    public void setObservador(Observador observador) {
        this.observador = observador;
    }
}
