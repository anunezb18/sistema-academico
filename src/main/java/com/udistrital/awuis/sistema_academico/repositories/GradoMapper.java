package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.Grado;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Mapper para la entidad Grado.
 * Implementa las operaciones CRUD usando EntityManager
 */
@Repository
@Transactional
public class GradoMapper {

    @PersistenceContext
    private EntityManager em;

    public GradoMapper() {
    }

    /**
     * Guarda un nuevo grado
     * @param grado El grado a guardar
     * @return El grado guardado con su ID generado
     */
    public Grado save(Grado grado) {
        if (grado == null) {
            throw new IllegalArgumentException("El grado no puede ser null");
        }
        if (grado.getIdGrado() == 0) {
            em.persist(grado);
        } else {
            grado = em.merge(grado);
        }
        em.flush();
        return grado;
    }

    /**
     * Busca un grado por su ID
     * @param id El ID del grado
     * @return Optional con el grado si existe
     */
    public Optional<Grado> findById(int id) {
        Grado grado = em.find(Grado.class, id);
        return Optional.ofNullable(grado);
    }

    /**
     * Busca un grado por su nombre
     * @param nombre El nombre del grado
     * @return Optional con el grado si existe
     */
    public Optional<Grado> findByNombre(String nombre) {
        try {
            TypedQuery<Grado> query = em.createQuery(
                "SELECT g FROM Grado g WHERE g.nombre = :nombre", Grado.class);
            query.setParameter("nombre", nombre);
            Grado grado = query.getSingleResult();
            return Optional.of(grado);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Obtiene todos los grados
     * @return Lista de todos los grados
     */
    public List<Grado> findAll() {
        TypedQuery<Grado> query = em.createQuery(
            "SELECT g FROM Grado g ORDER BY g.nombre", Grado.class);
        return query.getResultList();
    }

    /**
     * Actualiza un grado existente
     * @param grado El grado a actualizar
     */
    public void actualizar(Grado grado) {
        if (grado == null) return;
        em.merge(grado);
    }

    /**
     * Elimina un grado
     * @param id El ID del grado a eliminar
     */
    public void deleteById(int id) {
        Grado grado = em.find(Grado.class, id);
        if (grado != null) {
            em.remove(grado);
        }
    }

    /**
     * Verifica si existe un grado con el ID dado
     * @param id El ID a verificar
     * @return true si existe, false si no
     */
    public boolean existsById(int id) {
        Long count = em.createQuery(
            "SELECT COUNT(g) FROM Grado g WHERE g.idGrado = :id", Long.class)
            .setParameter("id", id)
            .getSingleResult();
        return count > 0;
    }

    /**
     * Cuenta el número total de grados
     * @return El número de grados
     */
    public long count() {
        return em.createQuery(
            "SELECT COUNT(g) FROM Grado g", Long.class)
            .getSingleResult();
    }
}

