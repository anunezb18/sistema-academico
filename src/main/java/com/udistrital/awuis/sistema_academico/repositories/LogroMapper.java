package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.Logro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Mapper para la entidad Logro.
 * Implementa las operaciones CRUD usando EntityManager
 */
@Repository
@Transactional
public class LogroMapper {

    @PersistenceContext
    private EntityManager em;

    public LogroMapper() {
    }

    /**
     * Guarda un nuevo logro
     */
    public Logro save(Logro logro) {
        em.persist(logro);
        em.flush();
        return logro;
    }

    /**
     * Actualiza un logro existente
     */
    public Logro update(Logro logro) {
        return em.merge(logro);
    }

    /**
     * Busca un logro por ID
     */
    public Optional<Logro> findById(int id) {
        Logro logro = em.find(Logro.class, id);
        return Optional.ofNullable(logro);
    }

    /**
     * Obtiene todos los logros
     */
    public List<Logro> findAll() {
        TypedQuery<Logro> query = em.createQuery(
                "SELECT l FROM Logro l ORDER BY l.idCategoria, l.descripcion",
                Logro.class
        );
        return query.getResultList();
    }

    /**
     * Busca logros por categoría
     */
    public List<Logro> findByCategoria(int idCategoria) {
        TypedQuery<Logro> query = em.createQuery(
                "SELECT l FROM Logro l WHERE l.idCategoria = :idCategoria ORDER BY l.descripcion",
                Logro.class
        );
        query.setParameter("idCategoria", idCategoria);
        return query.getResultList();
    }

    /**
     * Elimina un logro
     */
    public void delete(int id) {
        Logro logro = em.find(Logro.class, id);
        if (logro != null) {
            em.remove(logro);
        }
    }
}

