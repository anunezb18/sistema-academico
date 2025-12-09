package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.HistorialAcademico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Mapper para la entidad HistorialAcademico.
 * Implementa las operaciones CRUD usando EntityManager
 */
@Repository
@Transactional
public class HistorialAcademicoMapper {

    @PersistenceContext
    private EntityManager em;

    public HistorialAcademicoMapper() {
    }

    /**
     * Guarda un nuevo historial académico
     * @param historial El historial a guardar
     * @return El historial guardado con su ID generado
     */
    public HistorialAcademico save(HistorialAcademico historial) {
        if (historial == null) {
            throw new IllegalArgumentException("El historial académico no puede ser null");
        }
        if (historial.getIdHistorialAcademico() == 0) {
            em.persist(historial);
        } else {
            historial = em.merge(historial);
        }
        em.flush();
        return historial;
    }

    /**
     * Busca un historial académico por su ID
     * @param id El ID del historial
     * @return Optional con el historial si existe
     */
    public Optional<HistorialAcademico> findById(int id) {
        HistorialAcademico historial = em.find(HistorialAcademico.class, id);
        return Optional.ofNullable(historial);
    }

    /**
     * Obtiene todos los historiales académicos
     * @return Lista de todos los historiales
     */
    public List<HistorialAcademico> findAll() {
        TypedQuery<HistorialAcademico> query = em.createQuery(
            "SELECT h FROM HistorialAcademico h", HistorialAcademico.class);
        return query.getResultList();
    }

    /**
     * Elimina un historial académico
     * @param id El ID del historial a eliminar
     */
    public void deleteById(int id) {
        HistorialAcademico historial = em.find(HistorialAcademico.class, id);
        if (historial != null) {
            em.remove(historial);
        }
    }

    /**
     * Elimina un historial académico (sobrecarga que acepta el objeto)
     * @param historial El historial a eliminar
     */
    public void delete(HistorialAcademico historial) {
        if (historial != null) {
            HistorialAcademico managed = em.merge(historial);
            em.remove(managed);
        }
    }

    /**
     * Verifica si existe un historial académico con el ID dado
     * @param id El ID a verificar
     * @return true si existe, false si no
     */
    public boolean existsById(int id) {
        Long count = em.createQuery(
            "SELECT COUNT(h) FROM HistorialAcademico h WHERE h.idHistorialAcademico = :id", Long.class)
            .setParameter("id", id)
            .getSingleResult();
        return count > 0;
    }

    /**
     * Cuenta el número total de historiales académicos
     * @return El número de historiales
     */
    public long count() {
        return em.createQuery(
            "SELECT COUNT(h) FROM HistorialAcademico h", Long.class)
            .getSingleResult();
    }

    /**
     * Obtiene historiales académicos por ID de estudiante
     * @param idEstudiante El ID del estudiante
     * @return Lista de historiales del estudiante
     */
    public List<HistorialAcademico> findByIdEstudiante(int idEstudiante) {
        TypedQuery<HistorialAcademico> query = em.createQuery(
            "SELECT h FROM HistorialAcademico h WHERE h.idEstudiante = :idEstudiante",
            HistorialAcademico.class);
        query.setParameter("idEstudiante", idEstudiante);
        return query.getResultList();
    }
}
