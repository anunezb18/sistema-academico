package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.LogroBoletin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Mapper para la entidad LogroBoletin.
 * Implementa las operaciones CRUD usando EntityManager
 */
@Repository
@Transactional
public class LogroBoletinMapper {

    @PersistenceContext
    private EntityManager em;

    public LogroBoletinMapper() {
    }

    /**
     * Guarda un nuevo LogroBoletin
     */
    public LogroBoletin save(LogroBoletin logroBoletin) {
        em.persist(logroBoletin);
        em.flush();
        return logroBoletin;
    }

    /**
     * Actualiza un LogroBoletin existente
     */
    public LogroBoletin update(LogroBoletin logroBoletin) {
        return em.merge(logroBoletin);
    }

    /**
     * Busca un LogroBoletin por ID
     */
    public Optional<LogroBoletin> findById(int id) {
        LogroBoletin logroBoletin = em.find(LogroBoletin.class, id);
        return Optional.ofNullable(logroBoletin);
    }

    /**
     * Obtiene todos los LogroBoletin
     */
    public List<LogroBoletin> findAll() {
        TypedQuery<LogroBoletin> query = em.createQuery(
                "SELECT lb FROM LogroBoletin lb",
                LogroBoletin.class
        );
        return query.getResultList();
    }

    /**
     * Busca LogroBoletin por boletín
     */
    public List<LogroBoletin> findByBoletin(int idBoletin) {
        TypedQuery<LogroBoletin> query = em.createQuery(
                "SELECT lb FROM LogroBoletin lb WHERE lb.idBoletin = :idBoletin",
                LogroBoletin.class
        );
        query.setParameter("idBoletin", idBoletin);
        return query.getResultList();
    }

    /**
     * Busca LogroBoletin por logro
     */
    public List<LogroBoletin> findByLogro(int idLogro) {
        TypedQuery<LogroBoletin> query = em.createQuery(
                "SELECT lb FROM LogroBoletin lb WHERE lb.idLogro = :idLogro",
                LogroBoletin.class
        );
        query.setParameter("idLogro", idLogro);
        return query.getResultList();
    }

    /**
     * Busca un LogroBoletin específico por logro y boletín
     */
    public Optional<LogroBoletin> findByLogroAndBoletin(int idLogro, int idBoletin) {
        TypedQuery<LogroBoletin> query = em.createQuery(
                "SELECT lb FROM LogroBoletin lb WHERE lb.idLogro = :idLogro AND lb.idBoletin = :idBoletin",
                LogroBoletin.class
        );
        query.setParameter("idLogro", idLogro);
        query.setParameter("idBoletin", idBoletin);
        List<LogroBoletin> resultados = query.getResultList();
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
    }

    /**
     * Elimina un LogroBoletin
     */
    public void delete(int id) {
        LogroBoletin logroBoletin = em.find(LogroBoletin.class, id);
        if (logroBoletin != null) {
            em.remove(logroBoletin);
        }
    }
}

