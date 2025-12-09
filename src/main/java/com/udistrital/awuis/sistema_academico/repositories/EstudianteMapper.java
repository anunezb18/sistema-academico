package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.Estudiante;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class EstudianteMapper {

    @PersistenceContext
    private EntityManager em;

    public EstudianteMapper() {
    }

    public Estudiante agregarEstudiante(Estudiante estudiante) {
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante no puede ser null");
        }
        em.persist(estudiante);
        em.flush();
        return estudiante;
    }

    public Estudiante obtenerPorId(int id) {
        return em.find(Estudiante.class, id);
    }

    public List<Estudiante> listarEstudiantes() {
        TypedQuery<Estudiante> query = em.createQuery(
            "SELECT e FROM Estudiante e", Estudiante.class);
        return query.getResultList();
    }

    public Estudiante obtenerPorIdFormulario(int idFormulario) {
        TypedQuery<Estudiante> query = em.createQuery(
            "SELECT e FROM Estudiante e WHERE e.idFormulario = :idFormulario",
            Estudiante.class);
        query.setParameter("idFormulario", idFormulario);
        List<Estudiante> resultados = query.getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    /**
     * Busca un estudiante por su idUsuario
     * @param idUsuario El ID del usuario asociado
     * @return Optional con el estudiante si existe
     */
    public Optional<Estudiante> findByIdUsuario(int idUsuario) {
        try {
            TypedQuery<Estudiante> query = em.createQuery(
                "SELECT e FROM Estudiante e WHERE e.idUsuario = :idUsuario",
                Estudiante.class);
            query.setParameter("idUsuario", idUsuario);
            Estudiante estudiante = query.getSingleResult();
            return Optional.of(estudiante);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Estudiante actualizarEstudiante(Estudiante estudiante) {
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante no puede ser null");
        }
        return em.merge(estudiante);
    }

    public void eliminarEstudiante(int id) {
        Estudiante estudiante = obtenerPorId(id);
        if (estudiante != null) {
            em.remove(estudiante);
        }
    }
}

