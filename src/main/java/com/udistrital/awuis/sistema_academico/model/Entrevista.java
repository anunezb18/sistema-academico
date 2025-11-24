package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Entrevista")
public class Entrevista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idEntrevista;

    @Column(name = "idAspirante", nullable = false)
    private Integer idAspirante;

    @Column(name = "fechaHora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "estado")
    private String estado; // PROGRAMADA, COMPLETADA, CANCELADA

    @Column(name = "observaciones")
    private String observaciones;

    public Entrevista() {
    }

    public Entrevista(Integer idAspirante, LocalDateTime fechaHora, String estado) {
        this.idAspirante = idAspirante;
        this.fechaHora = fechaHora;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdEntrevista() {
        return idEntrevista;
    }

    public void setIdEntrevista(int idEntrevista) {
        this.idEntrevista = idEntrevista;
    }

    public Integer getIdAspirante() {
        return idAspirante;
    }

    public void setIdAspirante(Integer idAspirante) {
        this.idAspirante = idAspirante;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "Entrevista{" +
                "idEntrevista=" + idEntrevista +
                ", idAspirante=" + idAspirante +
                ", fechaHora=" + fechaHora +
                ", estado='" + estado + '\'' +
                '}';
    }
}

