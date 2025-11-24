package com.udistrital.awuis.sistema_academico.repositories;

import org.springframework.stereotype.Repository;

import com.udistrital.awuis.sistema_academico.model.Directivo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

/**
 * DirectivoMapper:
 * - actualizar(Directivo): void
 * - guardar(Directivo): void
 * - inhabilitar(int): void
 * - obtenerPorId(int): Directivo
 */
@Repository
@Transactional
public class DirectivoMapper {

    @PersistenceContext
    private EntityManager em;

    public DirectivoMapper() {
    }

    public void guardar(Directivo directivo) {
        if (directivo == null) return;
        em.persist(directivo);
    }

    public void actualizar(Directivo directivo) {
        if (directivo == null) return;
        em.merge(directivo);
    }

    public void inhabilitar(int id) {
        Directivo d = em.find(Directivo.class, id);
        if (d != null) {
            if (d.getToken() != null) {
                d.getToken().quitarToken();
                em.merge(d);
            }
        }
    }

    public Directivo obtenerPorId(int id) {
        return em.find(Directivo.class, id);
    }
}

