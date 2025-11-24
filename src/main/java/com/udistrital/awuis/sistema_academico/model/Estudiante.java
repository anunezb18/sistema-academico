package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Estudiante")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idEstudiante;

    @Column(name = "idUsuario")
    private Integer idUsuario;

    @Column(name = "idHistorialAcademico")
    private Integer idHistorialAcademico;

    @Column(name = "fechaIngreso")
    private LocalDate fechaIngreso;

    @Column(name = "idGrupo")
    private Integer idGrupo;

    @Column(name = "idFormulario")
    private Integer idFormulario;

    public Estudiante() {
    }

    public Estudiante(Integer idUsuario, Integer idHistorialAcademico, LocalDate fechaIngreso, Integer idGrupo, Integer idFormulario) {
        this.idUsuario = idUsuario;
        this.idHistorialAcademico = idHistorialAcademico;
        this.fechaIngreso = fechaIngreso;
        this.idGrupo = idGrupo;
        this.idFormulario = idFormulario;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdHistorialAcademico() {
        return idHistorialAcademico;
    }

    public void setIdHistorialAcademico(Integer idHistorialAcademico) {
        this.idHistorialAcademico = idHistorialAcademico;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Integer getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }

    public Integer getIdFormulario() {
        return idFormulario;
    }

    public void setIdFormulario(Integer idFormulario) {
        this.idFormulario = idFormulario;
    }

    @Override
    public String toString() {
        return "Estudiante{" +
                "idEstudiante=" + idEstudiante +
                ", idUsuario=" + idUsuario +
                ", idHistorialAcademico=" + idHistorialAcademico +
                ", fechaIngreso=" + fechaIngreso +
                ", idGrupo=" + idGrupo +
                ", idFormulario=" + idFormulario +
                '}';
    }
}

