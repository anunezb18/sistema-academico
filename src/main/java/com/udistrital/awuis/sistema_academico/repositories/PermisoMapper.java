package com.udistrital.awuis.sistema_academico.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.udistrital.awuis.sistema_academico.model.Permiso;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

/**
 * Data Mapper para la entidad Permiso.
 * Implementa las operaciones CRUD usando EntityManager
 */
@Repository
@Transactional
public class PermisoMapper {

    @PersistenceContext
    private EntityManager em;

    public PermisoMapper() {
    }

    public Permiso agregarPermiso(Permiso permiso) {
        em.persist(permiso);
        return permiso;
    }

    public List<Permiso> listarPermisos() {
        TypedQuery<Permiso> q = em.createQuery("SELECT p FROM Permiso p", Permiso.class);
        return q.getResultList();
    }

}

