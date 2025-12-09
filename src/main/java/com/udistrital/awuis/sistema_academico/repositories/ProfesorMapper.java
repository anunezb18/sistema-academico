package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.Profesor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Mapper para la entidad Profesor.
 * Implementa las operaciones CRUD usando EntityManager
 */
@Repository
@Transactional
public class ProfesorMapper {

    @PersistenceContext
    private EntityManager em;

    public ProfesorMapper() {
    }

    /**
     * Guarda un nuevo profesor
     * @param profesor El profesor a guardar
     * @return El profesor guardado con su ID generado
     */
    public Profesor save(Profesor profesor) {
        if (profesor == null) {
            throw new IllegalArgumentException("El profesor no puede ser null");
        }
        if (profesor.getIdProfesor() == 0) {
            em.persist(profesor);
        } else {
            profesor = em.merge(profesor);
        }
        em.flush();
        return profesor;
    }

    /**
     * Guarda un nuevo profesor (alias para compatibilidad)
     * @param profesor El profesor a guardar
     */
    public void guardar(Profesor profesor) {
        save(profesor);
    }

    /**
     * Busca un profesor por su ID
     * @param id El ID del profesor
     * @return Optional con el profesor si existe
     */
    public Optional<Profesor> findById(int id) {
        Profesor profesor = em.find(Profesor.class, id);
        return Optional.ofNullable(profesor);
    }

    /**
     * Obtiene un profesor por su ID
     * @param id El ID del profesor
     * @return El profesor si existe, null si no
     */
    public Profesor obtenerPorId(int id) {
        return em.find(Profesor.class, id);
    }

    /**
     * Busca un profesor por ID de usuario
     * @param idUsuario El ID del usuario
     * @return Optional con el profesor si existe
     */
    public Optional<Profesor> findByIdUsuario(int idUsuario) {
        try {
            TypedQuery<Profesor> query = em.createQuery(
                "SELECT p FROM Profesor p WHERE p.idUsuario = :idUsuario", Profesor.class);
            query.setParameter("idUsuario", idUsuario);
            Profesor profesor = query.getSingleResult();
            return Optional.of(profesor);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Obtiene todos los profesores
     * @return Lista de todos los profesores
     */
    public List<Profesor> findAll() {
        TypedQuery<Profesor> query = em.createQuery(
            "SELECT p FROM Profesor p", Profesor.class);
        return query.getResultList();
    }

    /**
     * Obtiene todos los profesores (alias para compatibilidad)
     * @return Lista de todos los profesores
     */
    public List<Profesor> obtenerTodos() {
        return findAll();
    }

    /**
     * Actualiza un profesor existente
     * @param profesor El profesor a actualizar
     */
    public void actualizar(Profesor profesor) {
        if (profesor == null) return;
        em.merge(profesor);
    }

    /**
     * Elimina un profesor
     * @param id El ID del profesor a eliminar
     */
    public void deleteById(int id) {
        Profesor profesor = em.find(Profesor.class, id);
        if (profesor != null) {
            em.remove(profesor);
        }
    }

    /**
     * Elimina un profesor (alias para compatibilidad)
     * @param id El ID del profesor a eliminar
     */
    public void eliminar(int id) {
        deleteById(id);
    }

    /**
     * Verifica si existe un profesor con el ID dado
     * @param id El ID a verificar
     * @return true si existe, false si no
     */
    public boolean existsById(int id) {
        Long count = em.createQuery(
            "SELECT COUNT(p) FROM Profesor p WHERE p.idProfesor = :id", Long.class)
            .setParameter("id", id)
            .getSingleResult();
        return count > 0;
    }

    /**
     * Cuenta el número total de profesores
     * @return El número de profesores
     */
    public long count() {
        return em.createQuery(
            "SELECT COUNT(p) FROM Profesor p", Long.class)
            .getSingleResult();
    }

    /**
     * Obtiene profesores por ID de grupo
     * @param idGrupo El ID del grupo
     * @return Lista de profesores asignados al grupo
     */
    public List<Profesor> findByIdGrupo(int idGrupo) {
        TypedQuery<Profesor> query = em.createQuery(
            "SELECT p FROM Profesor p WHERE p.idGrupo = :idGrupo", Profesor.class);
        query.setParameter("idGrupo", idGrupo);
        return query.getResultList();
    }
}

