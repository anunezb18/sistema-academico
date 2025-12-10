package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entidad Observador
 * Se crea automáticamente cuando se registra un nuevo estudiante
 * Solo contiene el ID del observador y tiene una relación con las anotaciones
 * Nota: La relación con Estudiante es a través de HistorialAcademico
 */
@Entity
@Table(name = "\"Observador\"")
public class Observador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idObservador\"")
    private int idObservador;

    @OneToMany(mappedBy = "observador", fetch = FetchType.LAZY)
    private List<Anotacion> anotaciones;

    public Observador() {
    }

    public int getIdObservador() {
        return idObservador;
    }

    public void setIdObservador(int idObservador) {
        this.idObservador = idObservador;
    }


    public List<Anotacion> getAnotaciones() {
        return anotaciones;
    }

    public void setAnotaciones(List<Anotacion> anotaciones) {
        this.anotaciones = anotaciones;
    }
}

