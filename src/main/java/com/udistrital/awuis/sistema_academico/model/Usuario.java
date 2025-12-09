package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * Entidad Usuario - Representa un usuario en el sistema.
 * Usado mediante COMPOSICIÓN por Profesor, Directivo y Estudiante.
 *
 * NOTA IMPORTANTE: En el diagrama de clases UML mostramos herencia (IS-A) porque
 * semánticamente un Profesor ES-UN Usuario. Sin embargo, en la implementación
 * usamos COMPOSICIÓN (HAS-A) siguiendo el principio "Composition over Inheritance"
 * debido a:
 * 1. Incompatibilidad de nuestra estructura de BD con herencia JPA
 * 2. Mayor flexibilidad y menor acoplamiento
 * 3. Práctica estándar en sistemas empresariales con ORM
 */
@Entity
@Table(name = "\"Usuario\"")
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
