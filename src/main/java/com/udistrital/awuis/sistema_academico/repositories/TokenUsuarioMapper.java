package com.udistrital.awuis.sistema_academico.repositories;

import org.springframework.stereotype.Repository;

import com.udistrital.awuis.sistema_academico.model.TokenUsuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

/**
 * TokenUsuarioMapper
 * - desactivarToken(int): void
 * - guardarToken(TokenUsuario): void
 * - obtenerToken(String): TokenUsuario
 */
@Repository
@Transactional
public class TokenUsuarioMapper {

    @PersistenceContext
    private EntityManager em;

    public TokenUsuarioMapper() {
    }

    public void desactivarToken(int idToken) {
        TokenUsuario t = em.find(TokenUsuario.class, idToken);
        if (t != null) {
            t.quitarToken();
            em.merge(t);
        }
    }

    public void guardarToken(TokenUsuario token) {
        if (token != null) {
            if (token.getIdToken() == 0) {
                em.persist(token);
            } else {
                em.merge(token);
            }
        }
    }

    public TokenUsuario obtenerToken(String contenido) {
        var q = em.createQuery("SELECT t FROM TokenUsuario t WHERE t.contenido = :c", TokenUsuario.class);
        q.setParameter("c", contenido);
        return q.getResultStream().findFirst().orElse(null);
    }
}

