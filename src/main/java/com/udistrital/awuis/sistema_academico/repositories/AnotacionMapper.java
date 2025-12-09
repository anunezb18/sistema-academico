package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.Anotacion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Mapper para la entidad Anotacion.
 * Implementa las operaciones CRUD usando EntityManager
 */
@Repository
@Transactional
public class AnotacionMapper {

    @PersistenceContext
    private EntityManager em;

    public AnotacionMapper() {
    }

    /**
     * Guarda una nueva anotación
     * @param anotacion La anotación a guardar
     * @return La anotación guardada con su ID generado
     */
    public Anotacion save(Anotacion anotacion) {
        if (anotacion == null) {
            throw new IllegalArgumentException("La anotación no puede ser null");
        }
        if (anotacion.getIdAnotacion() == 0) {
            em.persist(anotacion);
        } else {
            anotacion = em.merge(anotacion);
        }
        em.flush();
        return anotacion;
    }

    /**
     * Busca una anotación por su ID
     * @param id El ID de la anotación
     * @return Optional con la anotación si existe
     */
    public Optional<Anotacion> findById(int id) {
        Anotacion anotacion = em.find(Anotacion.class, id);
        return Optional.ofNullable(anotacion);
    }

    /**
     * Obtiene todas las anotaciones
     * @return Lista de todas las anotaciones
     */
    public List<Anotacion> findAll() {
        TypedQuery<Anotacion> query = em.createQuery(
            "SELECT a FROM Anotacion a ORDER BY a.fecha DESC", Anotacion.class);
        return query.getResultList();
    }

    /**
     * Busca todas las anotaciones de un observador específico
     * @param idObservador El ID del observador
     * @return Lista de anotaciones del observador
     */
    public List<Anotacion> findByObservadorId(int idObservador) {
        TypedQuery<Anotacion> query = em.createQuery(
            "SELECT a FROM Anotacion a WHERE a.idObservador = :idObservador ORDER BY a.fecha DESC",
            Anotacion.class);
        query.setParameter("idObservador", idObservador);
        return query.getResultList();
    }

    /**
     * Elimina una anotación
     * @param id El ID de la anotación a eliminar
     */
    public void deleteById(int id) {
        Anotacion anotacion = em.find(Anotacion.class, id);
        if (anotacion != null) {
            em.remove(anotacion);
        }
    }

    /**
     * Elimina una anotación (sobrecarga que acepta el objeto)
     * @param anotacion La anotación a eliminar
     */
    public void delete(Anotacion anotacion) {
        if (anotacion != null) {
            Anotacion managed = em.merge(anotacion);
            em.remove(managed);
        }
    }

    /**
     * Verifica si existe una anotación con el ID dado
     * @param id El ID a verificar
     * @return true si existe, false si no
     */
    public boolean existsById(int id) {
        Long count = em.createQuery(
            "SELECT COUNT(a) FROM Anotacion a WHERE a.idAnotacion = :id", Long.class)
            .setParameter("id", id)
            .getSingleResult();
        return count > 0;
    }

    /**
     * Cuenta el número total de anotaciones
     * @return El número total de anotaciones
     */
    public long count() {
        return em.createQuery("SELECT COUNT(a) FROM Anotacion a", Long.class)
            .getSingleResult();
    }

    /**
     * Cuenta el número de anotaciones de un observador específico
     * @param idObservador El ID del observador
     * @return El número de anotaciones del observador
     */
    public long countByObservadorId(int idObservador) {
        return em.createQuery(
            "SELECT COUNT(a) FROM Anotacion a WHERE a.idObservador = :idObservador", Long.class)
            .setParameter("idObservador", idObservador)
            .getSingleResult();
    }
}

