package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.Observador;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Mapper para la entidad Observador.
 * Implementa las operaciones CRUD usando EntityManager
 */
@Repository
@Transactional
public class ObservadorMapper {

    @PersistenceContext
    private EntityManager em;

    public ObservadorMapper() {
    }

    /**
     * Guarda un nuevo observador
     * @param observador El observador a guardar
     * @return El observador guardado con su ID generado
     */
    public Observador save(Observador observador) {
        if (observador == null) {
            throw new IllegalArgumentException("El observador no puede ser null");
        }
        if (observador.getIdObservador() == 0) {
            em.persist(observador);
        } else {
            observador = em.merge(observador);
        }
        em.flush();
        return observador;
    }

    /**
     * Busca un observador por su ID
     * @param id El ID del observador
     * @return Optional con el observador si existe
     */
    public Optional<Observador> findById(int id) {
        Observador observador = em.find(Observador.class, id);
        return Optional.ofNullable(observador);
    }

    /**
     * Obtiene todos los observadores
     * @return Lista de todos los observadores
     */
    public List<Observador> findAll() {
        TypedQuery<Observador> query = em.createQuery(
            "SELECT o FROM Observador o", Observador.class);
        return query.getResultList();
    }

    /**
     * Elimina un observador
     * @param id El ID del observador a eliminar
     */
    public void deleteById(int id) {
        Observador observador = em.find(Observador.class, id);
        if (observador != null) {
            em.remove(observador);
        }
    }

    /**
     * Elimina un observador (sobrecarga que acepta el objeto)
     * @param observador El observador a eliminar
     */
    public void delete(Observador observador) {
        if (observador != null) {
            Observador managed = em.merge(observador);
            em.remove(managed);
        }
    }

    /**
     * Verifica si existe un observador con el ID dado
     * @param id El ID a verificar
     * @return true si existe, false si no
     */
    public boolean existsById(int id) {
        Long count = em.createQuery(
            "SELECT COUNT(o) FROM Observador o WHERE o.idObservador = :id", Long.class)
            .setParameter("id", id)
            .getSingleResult();
        return count > 0;
    }

    /**
     * Cuenta el número total de observadores
     * @return El número de observadores
     */
    public long count() {
        return em.createQuery(
            "SELECT COUNT(o) FROM Observador o", Long.class)
            .getSingleResult();
    }
}

