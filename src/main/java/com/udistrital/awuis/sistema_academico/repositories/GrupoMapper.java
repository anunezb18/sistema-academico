package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.Grupo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Mapper para la entidad Grupo.
 * Implementa las operaciones CRUD usando EntityManager
 */
@Repository
@Transactional
public class GrupoMapper {

    @PersistenceContext
    private EntityManager em;

    public GrupoMapper() {
    }

    /**
     * Guarda un nuevo grupo
     * @param grupo El grupo a guardar
     * @return El grupo guardado con su ID generado
     */
    public Grupo save(Grupo grupo) {
        if (grupo == null) {
            throw new IllegalArgumentException("El grupo no puede ser null");
        }
        if (grupo.getIdGrupo() == 0) {
            em.persist(grupo);
        } else {
            grupo = em.merge(grupo);
        }
        em.flush();
        return grupo;
    }

    /**
     * Busca un grupo por su ID
     * @param id El ID del grupo
     * @return Optional con el grupo si existe
     */
    public Optional<Grupo> findById(int id) {
        Grupo grupo = em.find(Grupo.class, id);
        return Optional.ofNullable(grupo);
    }

    /**
     * Obtiene todos los grupos
     * @return Lista de todos los grupos
     */
    public List<Grupo> findAll() {
        TypedQuery<Grupo> query = em.createQuery(
            "SELECT g FROM Grupo g", Grupo.class);
        return query.getResultList();
    }

    /**
     * Obtiene grupos por ID de grado
     * @param idGrado El ID del grado
     * @return Lista de grupos del grado
     */
    public List<Grupo> findByIdGrado(int idGrado) {
        TypedQuery<Grupo> query = em.createQuery(
            "SELECT g FROM Grupo g WHERE g.idGrado = :idGrado", Grupo.class);
        query.setParameter("idGrado", idGrado);
        return query.getResultList();
    }

    /**
     * Obtiene grupo por ID de profesor
     * @param idProfesor El ID del profesor
     * @return Optional con el grupo si existe
     */
    public Optional<Grupo> findByIdProfesor(int idProfesor) {
        try {
            TypedQuery<Grupo> query = em.createQuery(
                "SELECT g FROM Grupo g WHERE g.idProfesor = :idProfesor", Grupo.class);
            query.setParameter("idProfesor", idProfesor);
            Grupo grupo = query.getSingleResult();
            return Optional.of(grupo);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Actualiza un grupo existente
     * @param grupo El grupo a actualizar
     */
    public void actualizar(Grupo grupo) {
        if (grupo == null) return;
        em.merge(grupo);
    }

    /**
     * Elimina un grupo
     * @param id El ID del grupo a eliminar
     */
    public void deleteById(int id) {
        Grupo grupo = em.find(Grupo.class, id);
        if (grupo != null) {
            em.remove(grupo);
        }
    }

    /**
     * Verifica si existe un grupo con el ID dado
     * @param id El ID a verificar
     * @return true si existe, false si no
     */
    public boolean existsById(int id) {
        Long count = em.createQuery(
            "SELECT COUNT(g) FROM Grupo g WHERE g.idGrupo = :id", Long.class)
            .setParameter("id", id)
            .getSingleResult();
        return count > 0;
    }

    /**
     * Cuenta el número total de grupos
     * @return El número de grupos
     */
    public long count() {
        return em.createQuery(
            "SELECT COUNT(g) FROM Grupo g", Long.class)
            .getSingleResult();
    }
}

