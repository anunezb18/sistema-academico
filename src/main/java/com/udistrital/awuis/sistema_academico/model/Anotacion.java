package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad Anotacion
 * Representa las anotaciones asociadas a un Observador
 */
@Entity
@Table(name = "\"Anotaciones\"")
public class Anotacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idAnotacion\"")
    private int idAnotacion;

    @Column(name = "\"idObservador\"")
    private Integer idObservador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idObservador\"", insertable = false, updatable = false)
    private Observador observador;

    @Column(name = "\"fecha\"")
    private LocalDate fecha;

    @Column(name = "\"tipo\"", length = 100)
    private String tipo;

    @Column(name = "\"descripcion\"", columnDefinition = "TEXT")
    private String descripcion;

    public Anotacion() {
    }

    public Anotacion(Integer idObservador, LocalDate fecha, String tipo, String descripcion) {
        this.idObservador = idObservador;
        this.fecha = fecha;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public int getIdAnotacion() {
        return idAnotacion;
    }

    public void setIdAnotacion(int idAnotacion) {
        this.idAnotacion = idAnotacion;
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

