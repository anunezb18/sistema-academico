package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.Rol;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Mapper para la entidad Rol.
 * Implementa las operaciones CRUD usando EntityManager
 */
@Repository
@Transactional
public class RolMapper {

    @PersistenceContext
    private EntityManager em;

    public RolMapper() {
    }

    /**
     * Guarda un nuevo rol
     * @param rol El rol a guardar
     * @return El rol guardado con su ID generado
     */
    public Rol save(Rol rol) {
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede ser null");
        }
        if (rol.getIdRol() == 0) {
            em.persist(rol);
        } else {
            rol = em.merge(rol);
        }
        em.flush();
        return rol;
    }

    /**
     * Busca un rol por su nombre
     * @param nombre El nombre del rol
     * @return Optional con el rol si existe
     */
    public Optional<Rol> findByNombre(String nombre) {
        try {
            TypedQuery<Rol> query = em.createQuery(
                "SELECT r FROM Rol r WHERE r.nombre = :nombre", Rol.class);
            query.setParameter("nombre", nombre);
            Rol rol = query.getSingleResult();
            return Optional.of(rol);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Busca un rol por su ID
     * @param id El ID del rol
     * @return Optional con el rol si existe
     */
    public Optional<Rol> findById(int id) {
        Rol rol = em.find(Rol.class, id);
        return Optional.ofNullable(rol);
    }

    /**
     * Obtiene todos los roles
     * @return Lista de todos los roles
     */
    public List<Rol> findAll() {
        TypedQuery<Rol> query = em.createQuery(
            "SELECT r FROM Rol r", Rol.class);
        return query.getResultList();
    }

    /**
     * Elimina un rol
     * @param id El ID del rol a eliminar
     */
    public void deleteById(int id) {
        Rol rol = em.find(Rol.class, id);
        if (rol != null) {
            em.remove(rol);
        }
    }

    /**
     * Verifica si existe un rol con el ID dado
     * @param id El ID a verificar
     * @return true si existe, false si no
     */
    public boolean existsById(int id) {
        Long count = em.createQuery(
            "SELECT COUNT(r) FROM Rol r WHERE r.idRol = :id", Long.class)
            .setParameter("id", id)
            .getSingleResult();
        return count > 0;
    }
}

