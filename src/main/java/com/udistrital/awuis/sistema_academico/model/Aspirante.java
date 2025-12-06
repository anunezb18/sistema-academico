package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"Aspirante\"")
public class Aspirante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idAspirante\"")
    private int idAspirante;

    @OneToOne
    @JoinColumn(name = "\"idFormulario\"")
    private Formulario formulario;

    public Aspirante() {
    }

    public Aspirante(Formulario formulario) {
        this.formulario = formulario;
    }

    public int getIdAspirante() {
        return idAspirante;
    }

    public void setIdAspirante(int idAspirante) {
        this.idAspirante = idAspirante;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    @Override
    public String toString() {
        return "Aspirante{" +
                "idAspirante=" + idAspirante +
                ", formulario=" + (formulario != null ? formulario.getIdFormulario() : null) +
                '}';
    }
}

