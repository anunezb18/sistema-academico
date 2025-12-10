package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.*;

/**
 * Entidad LogroBoletin.
 * Registra la valoración que un profesor entrega a un estudiante en un boletín específico.
 */
@Entity
@Table(name = "\"LogroBoletin\"")
public class LogroBoletin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idLogroBoletin\"")
    private int idLogroBoletin;

    @Column(name = "\"idLogro\"")
    private Integer idLogro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idLogro\"", insertable = false, updatable = false)
    private Logro logro;

    @Column(name = "\"idBoletin\"")
    private Integer idBoletin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idBoletin\"", insertable = false, updatable = false)
    private Boletin boletin;

    @Column(name = "\"valoracion\"")
    private String valoracion;

    @Column(name = "\"comentario\"")
    private String comentario;

    public LogroBoletin() {
        super();
    }

    public LogroBoletin(Integer idLogro, Integer idBoletin, String valoracion) {
        this.idLogro = idLogro;
        this.idBoletin = idBoletin;
        this.valoracion = valoracion;
    }

    // Getters y Setters
    public int getIdLogroBoletin() {
        return idLogroBoletin;
    }

    public void setIdLogroBoletin(int idLogroBoletin) {
        this.idLogroBoletin = idLogroBoletin;
    }

    public Integer getIdLogro() {
        return idLogro;
    }

    public void setIdLogro(Integer idLogro) {
        this.idLogro = idLogro;
    }

    public Logro getLogro() {
        return logro;
    }

    public void setLogro(Logro logro) {
        this.logro = logro;
    }

    public Integer getIdBoletin() {
        return idBoletin;
    }

    public void setIdBoletin(Integer idBoletin) {
        this.idBoletin = idBoletin;
    }

    public Boletin getBoletin() {
        return boletin;
    }

    public void setBoletin(Boletin boletin) {
        this.boletin = boletin;
    }

    public String getValoracion() {
        return valoracion;
    }

    public void setValoracion(String valoracion) {
        this.valoracion = valoracion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    @Override
    public String toString() {
        return "LogroBoletin{" +
                "idLogroBoletin=" + idLogroBoletin +
                ", idLogro=" + idLogro +
                ", idBoletin=" + idBoletin +
                ", valoracion='" + valoracion + '\'' +
                ", comentario='" + comentario + '\'' +
                '}';
    }
}

