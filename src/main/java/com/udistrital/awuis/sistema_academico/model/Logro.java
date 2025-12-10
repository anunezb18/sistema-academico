package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.*;

/**
 * Entidad Logro.
 * Representa un aprendizaje o competencia evaluable dentro de una categoría.
 */
@Entity
@Table(name = "\"Logro\"")
public class Logro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idLogro\"")
    private int idLogro;

    @Column(name = "\"idCategoria\"")
    private Integer idCategoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idCategoria\"", insertable = false, updatable = false)
    private CategoriaLogro categoriaLogro;

    @Column(name = "\"descripcion\"", nullable = false)
    private String descripcion;

    @Column(name = "\"valoracion\"")
    private String valoracion;

    public Logro() {
        super();
    }

    public Logro(Integer idCategoria, String descripcion) {
        this.idCategoria = idCategoria;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public int getIdLogro() {
        return idLogro;
    }

    public void setIdLogro(int idLogro) {
        this.idLogro = idLogro;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public CategoriaLogro getCategoriaLogro() {
        return categoriaLogro;
    }

    public void setCategoriaLogro(CategoriaLogro categoriaLogro) {
        this.categoriaLogro = categoriaLogro;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getValoracion() {
        return valoracion;
    }

    public void setValoracion(String valoracion) {
        this.valoracion = valoracion;
    }

    @Override
    public String toString() {
        return "Logro{" +
                "idLogro=" + idLogro +
                ", idCategoria=" + idCategoria +
                ", descripcion='" + descripcion + '\'' +
                ", valoracion='" + valoracion + '\'' +
                '}';
    }
}

