package com.udistrital.awuis.sistema_academico.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.udistrital.awuis.sistema_academico.model.Permiso;
import com.udistrital.awuis.sistema_academico.model.Rol;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

/**
 * DataType RolMapper
 * - insertarRol(Rol): void
 * - obtenerPermisosPorRol(int): List<Permiso>
 * - obtenerPorId(int): void
 */
@Repository
@Transactional
public class RolMapper {

    @PersistenceContext
    private EntityManager em;

    public RolMapper() {
    }

    public void insertarRol(Rol rol) {
        if (rol != null) {
            em.persist(rol);
        }
    }

    public List<Permiso> obtenerPermisosPorRol(int idRol) {
        TypedQuery<Permiso> q = em.createQuery(
                "SELECT p FROM Rol r JOIN r.permisos p WHERE r.idRol = :idRol", Permiso.class);
        q.setParameter("idRol", idRol);
        return q.getResultList();
    }

    public Rol obtenerPorId(int idRol) {
        return em.find(Rol.class, idRol);
    }
}
