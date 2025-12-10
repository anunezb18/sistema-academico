package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.CategoriaLogro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Mapper para la entidad CategoriaLogro.
 * Implementa las operaciones CRUD usando EntityManager
 */
@Repository
@Transactional
public class CategoriaLogroMapper {

    @PersistenceContext
    private EntityManager em;

    public CategoriaLogroMapper() {
    }

    /**
     * Guarda una nueva categoría de logro
     */
    public CategoriaLogro save(CategoriaLogro categoria) {
        em.persist(categoria);
        em.flush();
        return categoria;
    }

    /**
     * Actualiza una categoría de logro existente
     */
    public CategoriaLogro update(CategoriaLogro categoria) {
        return em.merge(categoria);
    }

    /**
     * Busca una categoría de logro por ID
     */
    public Optional<CategoriaLogro> findById(int id) {
        CategoriaLogro categoria = em.find(CategoriaLogro.class, id);
        return Optional.ofNullable(categoria);
    }

    /**
     * Obtiene todas las categorías de logro
     */
    public List<CategoriaLogro> findAll() {
        TypedQuery<CategoriaLogro> query = em.createQuery(
                "SELECT c FROM CategoriaLogro c ORDER BY c.nombre",
                CategoriaLogro.class
        );
        return query.getResultList();
    }

    /**
     * Busca una categoría por nombre
     */
    public Optional<CategoriaLogro> findByNombre(String nombre) {
        TypedQuery<CategoriaLogro> query = em.createQuery(
                "SELECT c FROM CategoriaLogro c WHERE c.nombre = :nombre",
                CategoriaLogro.class
        );
        query.setParameter("nombre", nombre);
        List<CategoriaLogro> resultados = query.getResultList();
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
    }

    /**
     * Elimina una categoría de logro
     */
    public void delete(int id) {
        CategoriaLogro categoria = em.find(CategoriaLogro.class, id);
        if (categoria != null) {
            em.remove(categoria);
        }
    }
}

