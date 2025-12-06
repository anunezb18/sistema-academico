package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entidad Usuario:
 * - contrasena: String → mapea a columna "contraseña" (con comillas y tilde en DB)
 * - correo: String (columna sin comillas en DB)
 * - idUsuario: int → mapea a columna "idUsuario" (con comillas en DB)
 * - token: TokenUsuario → FK "idToken" (con comillas en DB)
 *
 * Estrategia JOINED: Usuario y Directivo tienen tablas separadas unidas por JOIN
 */
@Entity
@Table(name = "\"Usuario\"")
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {

    @Column(name = "\"contraseña\"")
    private String contrasena;

    private String correo;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idUsuario\"")
    private int idUsuario;

    @OneToOne
    @JoinColumn(name = "\"idToken\"")
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


    public TokenUsuario getToken() {
        return token;
    }

    public void setToken(TokenUsuario token) {
        this.token = token;
    }
}
