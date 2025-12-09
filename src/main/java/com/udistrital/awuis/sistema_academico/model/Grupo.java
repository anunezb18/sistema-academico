package com.udistrital.awuis.sistema_academico.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Grupo que representa un grupo dentro de un grado.
 * Ejemplos: Semillitas A, Jardín B, etc.
 * Cada grupo tiene un profesor asignado y múltiples estudiantes
 */
@Entity
@Table(name = "\"Grupo\"")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"idGrupo\"")
    private int idGrupo;

    @Column(name = "\"idGrado\"")
    private Integer idGrado;

    @Column(name = "\"idProfesor\"")
    private Integer idProfesor;

    @Column(name = "\"nombre\"", length = 50)
    private String nombre; // Ejemplo: "A", "B", "C"

    /**
     * Relación con Grado
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idGrado\"", insertable = false, updatable = false)
    private Grado grado;

    /**
     * Relación con Profesor
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"idProfesor\"", insertable = false, updatable = false)
    private Profesor profesor;

    /**
     * Estudiantes asociados a este grupo.
     * Relación OneToMany con Estudiante
     */
    @OneToMany(mappedBy = "idGrupo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Estudiante> estudiantes;

    public Grupo() {
        this.estudiantes = new ArrayList<>();
    }

    public Grupo(Integer idGrado, Integer idProfesor, String nombre) {
        this.idGrado = idGrado;
        this.idProfesor = idProfesor;
        this.nombre = nombre;
        this.estudiantes = new ArrayList<>();
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public Integer getIdGrado() {
        return idGrado;
    }

    public void setIdGrado(Integer idGrado) {
        this.idGrado = idGrado;
    }

    public Integer getIdProfesor() {
        return idProfesor;
    }

    public void setIdProfesor(Integer idProfesor) {
        this.idProfesor = idProfesor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Grado getGrado() {
        return grado;
    }

    public void setGrado(Grado grado) {
        this.grado = grado;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    public List<Estudiante> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<Estudiante> estudiantes) {
        this.estudiantes = estudiantes;
    }

    /**
     * Método helper para agregar un estudiante al grupo
     */
    public void agregarEstudiante(Estudiante estudiante) {
        estudiantes.add(estudiante);
        estudiante.setIdGrupo(this.idGrupo);
    }

    /**
     * Método helper para remover un estudiante del grupo
     */
    public void removerEstudiante(Estudiante estudiante) {
        estudiantes.remove(estudiante);
        estudiante.setIdGrupo(null);
    }

    /**
     * Obtiene el nombre completo del grupo (Grado + Grupo)
     * Ejemplo: "Semillitas A", "Jardín B"
     */
    public String getNombreCompleto() {
        if (grado != null) {
            return grado.getNombre() + " " + nombre;
        }
        return nombre;
    }

    @Override
    public String toString() {
        return "Grupo{" +
                "idGrupo=" + idGrupo +
                ", idGrado=" + idGrado +
                ", idProfesor=" + idProfesor +
                ", nombre='" + nombre + '\'' +
                ", cantidadEstudiantes=" + estudiantes.size() +
                '}';
    }
}

