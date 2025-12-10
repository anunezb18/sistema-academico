package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.Boletin;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Mapper para la entidad Boletin.
 * Implementa las operaciones CRUD usando EntityManager
 */
@Repository
@Transactional
public class BoletinMapper {

    @PersistenceContext
    private EntityManager em;

    public BoletinMapper() {
    }

    /**
     * Guarda un nuevo boletín
     */
    public Boletin save(Boletin boletin) {
        em.persist(boletin);
        em.flush();
        return boletin;
    }

    /**
     * Actualiza un boletín existente
     */
    public Boletin update(Boletin boletin) {
        return em.merge(boletin);
    }

    /**
     * Busca un boletín por ID
     */
    public Optional<Boletin> findById(int id) {
        Boletin boletin = em.find(Boletin.class, id);
        return Optional.ofNullable(boletin);
    }

    /**
     * Obtiene todos los boletines
     */
    public List<Boletin> findAll() {
        TypedQuery<Boletin> query = em.createQuery(
                "SELECT b FROM Boletin b",
                Boletin.class
        );
        return query.getResultList();
    }

    /**
     * Busca boletines por historial académico
     */
    public List<Boletin> findByHistorialAcademico(int idHistorialAcademico) {
        TypedQuery<Boletin> query = em.createQuery(
                "SELECT b FROM Boletin b WHERE b.idHistorialAcademico = :idHistorialAcademico",
                Boletin.class
        );
        query.setParameter("idHistorialAcademico", idHistorialAcademico);
        return query.getResultList();
    }

    /**
     * Busca o crea un boletín para un historial académico
     */
    public Boletin findOrCreateByHistorialAcademico(int idHistorialAcademico) {
        List<Boletin> boletines = findByHistorialAcademico(idHistorialAcademico);
        if (!boletines.isEmpty()) {
            return boletines.get(0);
        }
        // Si no existe, crear uno nuevo
        Boletin nuevoBoletin = new Boletin(idHistorialAcademico);
        return save(nuevoBoletin);
    }

    /**
     * Busca un boletín por historial académico y periodo
     */
    public Optional<Boletin> findByHistorialAcademicoAndPeriodo(int idHistorialAcademico, int periodo) {
        TypedQuery<Boletin> query = em.createQuery(
                "SELECT b FROM Boletin b WHERE b.idHistorialAcademico = :idHistorialAcademico AND b.periodo = :periodo",
                Boletin.class
        );
        query.setParameter("idHistorialAcademico", idHistorialAcademico);
        query.setParameter("periodo", periodo);
        List<Boletin> resultados = query.getResultList();
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
    }

    /**
     * Busca o crea un boletín para un historial académico y periodo específico
     */
    public Boletin findOrCreateByHistorialAcademicoAndPeriodo(int idHistorialAcademico, int periodo) {
        Optional<Boletin> boletinExistente = findByHistorialAcademicoAndPeriodo(idHistorialAcademico, periodo);
        if (boletinExistente.isPresent()) {
            return boletinExistente.get();
        }
        // Si no existe, crear uno nuevo
        Boletin nuevoBoletin = new Boletin(idHistorialAcademico, periodo);
        return save(nuevoBoletin);
    }

    /**
     * Elimina un boletín
     */
    public void delete(int id) {
        Boletin boletin = em.find(Boletin.class, id);
        if (boletin != null) {
            em.remove(boletin);
        }
    }
}

