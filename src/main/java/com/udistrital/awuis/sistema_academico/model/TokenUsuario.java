package com.udistrital.awuis.sistema_academico.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad TokenUsuario
 */
@Entity
@Table(name = "\"TokenUsuario\"")
public class TokenUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idToken\"")
    private int idToken;

    private String contenido;

    private boolean estado;

    private LocalDateTime expiracion;

    @ManyToOne
    @JoinColumn(name = "\"idRol\"", nullable = false)
    private Rol rol;

    public TokenUsuario() {
    }

    public int getIdToken() {
        return idToken;
    }

    public void setIdToken(int idToken) {
        this.idToken = idToken;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public LocalDateTime getExpiracion() {
        return expiracion;
    }

    public void setExpiracion(LocalDateTime expiracion) {
        this.expiracion = expiracion;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean esValido() {
        if (!estado) return false;
        if (expiracion == null) return true;
        return LocalDateTime.now().isBefore(expiracion);
    }

    public void generarToken() {
        this.contenido = UUID.randomUUID().toString();
        this.estado = true;
        this.expiracion = LocalDateTime.now().plusHours(1);
    }

    public void quitarToken() {
        this.contenido = null;
        this.estado = false;
        this.expiracion = null;
    }
}

