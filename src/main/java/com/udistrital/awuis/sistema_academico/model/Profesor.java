package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.*;

/**
 * Entidad Profesor.
 * COMPOSICIÓN: Tiene una referencia a Usuario en lugar de heredar.
 * Ver justificación en clase Estudiante.
 */
@Entity
@Table(name = "\"Profesor\"")
public class Profesor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idProfesor\"")
    private int idProfesor;

    @Column(name = "\"idUsuario\"")
    private Integer idUsuario;

    @Column(name = "nombre")
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idUsuario\"", insertable = false, updatable = false)
    private Usuario usuario;

    public Profesor() {
    }

    public int getIdProfesor() {
        return idProfesor;
    }

    public void setIdProfesor(int idProfesor) {
        this.idProfesor = idProfesor;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // Métodos delegados a Usuario
    public String getCorreo() {
        return usuario != null ? usuario.getCorreo() : null;
    }

    public String getContrasena() {
        return usuario != null ? usuario.getContrasena() : null;
    }

    public TokenUsuario getToken() {
        return usuario != null ? usuario.getToken() : null;
    }

    @Override
    public String toString() {
        return "Profesor{" +
                "idProfesor=" + idProfesor +
                ", idUsuario=" + idUsuario +
                ", correo='" + getCorreo() + '\'' +
                '}';
    }
}






