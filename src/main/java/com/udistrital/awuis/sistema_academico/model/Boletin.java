package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.*;

/**
 * Entidad Boletin.
 * Representa el boletín académico de cada estudiante por periodo.
 */
@Entity
@Table(name = "\"Boletin\"")
public class Boletin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idBoletin\"")
    private int idBoletin;

    @Column(name = "\"idHistorialAcademico\"")
    private Integer idHistorialAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idHistorialAcademico\"", insertable = false, updatable = false)
    private HistorialAcademico historialAcademico;

    @Column(name = "\"periodo\"")
    private Integer periodo; // 1, 2, 3 o 4

    public Boletin() {
        super();
    }

    public Boletin(Integer idHistorialAcademico) {
        this.idHistorialAcademico = idHistorialAcademico;
    }

    public Boletin(Integer idHistorialAcademico, Integer periodo) {
        this.idHistorialAcademico = idHistorialAcademico;
        this.periodo = periodo;
    }

    // Getters y Setters
    public int getIdBoletin() {
        return idBoletin;
    }

    public void setIdBoletin(int idBoletin) {
        this.idBoletin = idBoletin;
    }

    public Integer getIdHistorialAcademico() {
        return idHistorialAcademico;
    }

    public void setIdHistorialAcademico(Integer idHistorialAcademico) {
        this.idHistorialAcademico = idHistorialAcademico;
    }

    public HistorialAcademico getHistorialAcademico() {
        return historialAcademico;
    }

    public void setHistorialAcademico(HistorialAcademico historialAcademico) {
        this.historialAcademico = historialAcademico;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    @Override
    public String toString() {
        return "Boletin{" +
                "idBoletin=" + idBoletin +
                ", idHistorialAcademico=" + idHistorialAcademico +
                ", periodo=" + periodo +
                '}';
    }
}

