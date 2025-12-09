package com.udistrital.awuis.sistema_academico.repositories;

import org.springframework.stereotype.Repository;

import com.udistrital.awuis.sistema_academico.model.Directivo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Data Mapper para la entidad Directivo.
 * Implementa las operaciones CRUD usando EntityManager
 */
@Repository
@Transactional
public class DirectivoMapper {

    @PersistenceContext
    private EntityManager em;

    public DirectivoMapper() {
    }

    /**
     * Guarda un nuevo directivo
     * @param directivo El directivo a guardar
     * @return El directivo guardado con su ID generado
     */
    public Directivo save(Directivo directivo) {
        if (directivo == null) {
            throw new IllegalArgumentException("El directivo no puede ser null");
        }

        if (directivo.getIdDirectivo() == 0) {
            em.persist(directivo);
        } else {
            directivo = em.merge(directivo);
        }

        em.flush();
        return directivo;
    }

    /**
     * Guarda un nuevo directivo (alias para compatibilidad)
     */
    public void guardar(Directivo directivo) {
        save(directivo);
    }

    /**
     * Busca un directivo por su ID
     */
    public Optional<Directivo> findById(int id) {
        Directivo directivo = em.find(Directivo.class, id);
        return Optional.ofNullable(directivo);
    }

    /**
     * Obtiene un directivo por su ID
     */
    public Directivo obtenerPorId(int id) {
        return em.find(Directivo.class, id);
    }

    /**
     * Busca un directivo por ID de usuario
     */
    public Optional<Directivo> findByIdUsuario(int idUsuario) {
        try {
            TypedQuery<Directivo> query = em.createQuery(
                "SELECT d FROM Directivo d WHERE d.idUsuario = :idUsuario", Directivo.class);
            query.setParameter("idUsuario", idUsuario);
            Directivo directivo = query.getSingleResult();
            return Optional.of(directivo);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Obtiene todos los directivos
     */
    public List<Directivo> findAll() {
        TypedQuery<Directivo> query = em.createQuery(
            "SELECT d FROM Directivo d", Directivo.class);
        return query.getResultList();
    }

    /**
     * Actualiza un directivo existente
     */
    public void actualizar(Directivo directivo) {
        if (directivo == null) return;
        em.merge(directivo);
    }

    /**
     * Inhabilita un directivo
     */
    public void inhabilitar(int id) {
        Directivo d = em.find(Directivo.class, id);
        if (d != null) {
            if (d.getToken() != null) {
                d.getToken().quitarToken();
                em.merge(d);
            }
        }
    }

    /**
     * Elimina un directivo
     */
    public void deleteById(int id) {
        Directivo directivo = em.find(Directivo.class, id);
        if (directivo != null) {
            em.remove(directivo);
        }
    }

    /**
     * Verifica si existe un directivo con el ID dado
     */
    public boolean existsById(int id) {
        Long count = em.createQuery(
            "SELECT COUNT(d) FROM Directivo d WHERE d.idDirectivo = :id", Long.class)
            .setParameter("id", id)
            .getSingleResult();
        return count > 0;
    }

    /**
     * Cuenta el número total de directivos
     */
    public long count() {
        return em.createQuery(
            "SELECT COUNT(d) FROM Directivo d", Long.class)
            .getSingleResult();
    }
}


