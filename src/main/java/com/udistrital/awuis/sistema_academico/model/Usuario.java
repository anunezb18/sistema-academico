package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entidad Usuario:
 * - contraseña: String
 * - correo: String
 * - idUsuario: int
 * - nombre: String
 * - token: TokenUsuario
 */
@Entity
@Table(name = "Usuario")
public class Usuario {

    private String contrasena;

    private String correo;

    @Id
    private int idUsuario;

    private String nombre;

    @OneToOne
    @JoinColumn(name = "idToken")
    private TokenUsuario token;

    public Usuario() {
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TokenUsuario getToken() {
        return token;
    }

    public void setToken(TokenUsuario token) {
        this.token = token;
    }
}

