package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidad Estudiante.
 * COMPOSICIÓN: Tiene una referencia a Usuario en lugar de heredar.
 *
 * JUSTIFICACIÓN: Aunque en el diagrama de clases mostramos herencia (IS-A),
 * implementamos composición (HAS-A) porque:
 * 1. La BD tiene PKs independientes (idEstudiante != idUsuario)
 * 2. JPA no soporta esta estructura con herencia
 * 3. Principio "Composition over Inheritance" (Gang of Four)
 */
@Entity
@Table(name = "\"Estudiante\"")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idEstudiante\"")
    private int idEstudiante;

    @Column(name = "\"idUsuario\"")
    private Integer idUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idUsuario\"", insertable = false, updatable = false)
    private Usuario usuario;

    @Column(name = "\"idHistorialAcademico\"")
    private Integer idHistorialAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idHistorialAcademico\"", insertable = false, updatable = false)
    private HistorialAcademico historialAcademico;

    @Column(name = "\"fechaIngreso\"")
    private LocalDate fechaIngreso;

    @Column(name = "\"idGrupo\"")
    private Integer idGrupo;

    @Column(name = "\"idFormulario\"")
    private Integer idFormulario;

    public Estudiante() {
        super();
    }

    public Estudiante(Integer idHistorialAcademico, LocalDate fechaIngreso, Integer idGrupo, Integer idFormulario) {
        super();
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // Métodos delegados a Usuario (simulan herencia)
    public String getCorreo() {
        return usuario != null ? usuario.getCorreo() : null;
    }

    public String getContrasena() {
        return usuario != null ? usuario.getContrasena() : null;
    }

    public TokenUsuario getToken() {
        return usuario != null ? usuario.getToken() : null;
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
                ", correo='" + getCorreo() + '\'' +
                ", idHistorialAcademico=" + idHistorialAcademico +
                ", fechaIngreso=" + fechaIngreso +
                ", idGrupo=" + idGrupo +
                ", idFormulario=" + idFormulario +
                '}';
    }
}

