package com.udistrital.awuis.sistema_academico.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Formulario")
public class Formulario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idFormulario;

    // estado como texto (ej. NUEVO, REVISAR, APROBADO)
    @Column(name = "estado")
    private String estado;

    @Column(name = "creado_en")
    private OffsetDateTime creadoEn;

    @Column(name = "nombre_completo")
    private String nombreCompleto;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "edad")
    private Integer edad;

    @Column(name = "grado_aspira")
    private String gradoAspira;

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "tipo_sangre")
    private String tipoSangre;

    @Column(name = "alergias")
    private String alergias;

    @Column(name = "condiciones_medicas")
    private String condicionesMedicas;

    @Column(name = "condiciones_usadas")
    private String condicionesUsadas;

    @Column(name = "medicamentos")
    private String medicamentos;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "nombre_responsable")
    private String nombreResponsable;

    @Column(name = "telefono_responsable")
    private String telefonoResponsable;

    @Column(name = "correo_responsable")
    private String correoResponsable;

    @Column(name = "parentesco_responsable")
    private String parentescoResponsable;
    public Formulario() {
    }

    // getters / setters
    public int getIdFormulario() { return idFormulario; }
    public void setIdFormulario(int idFormulario) { this.idFormulario = idFormulario; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public OffsetDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getGradoAspira() { return gradoAspira; }
    public void setGradoAspira(String gradoAspira) { this.gradoAspira = gradoAspira; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTipoSangre() { return tipoSangre; }
    public void setTipoSangre(String tipoSangre) { this.tipoSangre = tipoSangre; }

    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }

    public String getCondicionesMedicas() { return condicionesMedicas; }
    public void setCondicionesMedicas(String condicionesMedicas) { this.condicionesMedicas = condicionesMedicas; }

    public String getCondicionesUsadas() { return condicionesUsadas; }
    public void setCondicionesUsadas(String condicionesUsadas) { this.condicionesUsadas = condicionesUsadas; }

    public String getMedicamentos() { return medicamentos; }
    public void setMedicamentos(String medicamentos) { this.medicamentos = medicamentos; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getNombreResponsable() { return nombreResponsable; }
    public void setNombreResponsable(String nombreResponsable) { this.nombreResponsable = nombreResponsable; }

    public String getTelefonoResponsable() { return telefonoResponsable; }
    public void setTelefonoResponsable(String telefonoResponsable) { this.telefonoResponsable = telefonoResponsable; }

    public String getCorreoResponsable() { return correoResponsable; }
    public void setCorreoResponsable(String correoResponsable) { this.correoResponsable = correoResponsable; }

    public String getParentescoResponsable() { return parentescoResponsable; }
    public void setParentescoResponsable(String parentescoResponsable) { this.parentescoResponsable = parentescoResponsable; }

    @Override
    public String toString() {
        return "Formulario{" +
                "idFormulario=" + idFormulario +
                ", estado='" + estado + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                '}';
    }
}
