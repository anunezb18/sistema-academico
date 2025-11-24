package com.udistrital.awuis.sistema_academico.repositories;

import org.springframework.stereotype.Repository;

import com.udistrital.awuis.sistema_academico.model.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

/**
 * UsuarioMapper:
 * - agregarUsuario(int, Usuario): void
 * - inhabilitarUsuario(int): void
 */
@Repository
@Transactional
public class UsuarioMapper {

    @PersistenceContext
    private EntityManager em;

    public UsuarioMapper() {
    }

    public void agregarUsuario(int id, Usuario usuario) {
        if (usuario == null) return;
        usuario.setIdUsuario(id);
        em.persist(usuario);
    }

    public void inhabilitarUsuario(int id) {
        Usuario u = em.find(Usuario.class, id);
        if (u != null) {
            if (u.getToken() != null) {
                u.getToken().quitarToken();
                em.merge(u);
            }
        }
    }
}
