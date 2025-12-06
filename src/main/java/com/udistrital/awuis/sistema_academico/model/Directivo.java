package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entidad Directivo que hereda de Usuario:
 * - Hereda: correo, contraseña, token, idUsuario
 * - Mapea a tabla Directivo (estrategia JOINED)
 * - Se une a Usuario mediante JOIN en lugar de UNION
 */
@Entity
@Table(name = "\"Directivo\"")
public class Directivo extends Usuario {

    public Directivo() {
        super();
    }

    public int getIdDirectivo() {
        return super.getIdUsuario();
    }

    public void setIdDirectivo(int idDirectivo) {
        super.setIdUsuario(idDirectivo);
    }

    @Override
    public String toString() {
        return "Directivo{" +
                "idDirectivo=" + getIdUsuario() +
                ", correo='" + getCorreo() + '\'' +
                '}';
    }
}



