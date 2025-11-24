package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.Entrevista;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class EntrevistaMapper {

    @PersistenceContext
    private EntityManager em;

    public EntrevistaMapper() {
    }

    /**
     * Agrega una nueva entrevista
     * @param entrevista La entrevista a agregar
     * @return La entrevista agregada con su ID generado
     */
    public Entrevista agregarEntrevista(Entrevista entrevista) {
        if (entrevista == null) {
            throw new IllegalArgumentException("La entrevista no puede ser null");
        }
        em.persist(entrevista);
        em.flush();
        return entrevista;
    }

    /**
     * Obtiene una entrevista por su ID
     * @param id El ID de la entrevista
     * @return La entrevista encontrada o null
     */
    public Entrevista obtenerPorId(int id) {
        return em.find(Entrevista.class, id);
    }

    /**
     * Lista todas las entrevistas
     * @return Lista de entrevistas
     */
    public List<Entrevista> listarEntrevistas() {
        TypedQuery<Entrevista> query = em.createQuery(
            "SELECT e FROM Entrevista e ORDER BY e.fechaHora", Entrevista.class);
        return query.getResultList();
    }

    /**
     * Verifica si existe un cruce de horario con otras entrevistas
     * @param fechaHora La fecha y hora propuesta
     * @param margenMinutos Margen de minutos antes y después para considerar cruce
     * @return true si hay cruce, false si no hay cruce
     */
    public boolean verificarCruceHorario(LocalDateTime fechaHora, int margenMinutos) {
        LocalDateTime inicio = fechaHora.minusMinutes(margenMinutos);
        LocalDateTime fin = fechaHora.plusMinutes(margenMinutos);

        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(e) FROM Entrevista e WHERE " +
            "e.fechaHora BETWEEN :inicio AND :fin AND " +
            "e.estado != 'CANCELADA'", Long.class);
        query.setParameter("inicio", inicio);
        query.setParameter("fin", fin);

        Long count = query.getSingleResult();
        return count > 0;
    }

    /**
     * Obtiene entrevistas de un aspirante
     * @param idAspirante El ID del aspirante
     * @return Lista de entrevistas del aspirante
     */
    public List<Entrevista> obtenerPorAspirante(int idAspirante) {
        TypedQuery<Entrevista> query = em.createQuery(
            "SELECT e FROM Entrevista e WHERE e.idAspirante = :idAspirante " +
            "ORDER BY e.fechaHora DESC", Entrevista.class);
        query.setParameter("idAspirante", idAspirante);
        return query.getResultList();
    }

    /**
     * Actualiza una entrevista
     * @param entrevista La entrevista a actualizar
     * @return La entrevista actualizada
     */
    public Entrevista actualizarEntrevista(Entrevista entrevista) {
        if (entrevista == null) {
            throw new IllegalArgumentException("La entrevista no puede ser null");
        }
        return em.merge(entrevista);
    }

    /**
     * Cancela una entrevista
     * @param id El ID de la entrevista
     */
    public void cancelarEntrevista(int id) {
        Entrevista entrevista = obtenerPorId(id);
        if (entrevista != null) {
            entrevista.setEstado("CANCELADA");
            em.merge(entrevista);
        }
    }
}

