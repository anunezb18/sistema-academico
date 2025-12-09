package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.TokenUsuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Mapper para la entidad TokenUsuario.
 * Implementa las operaciones CRUD usando EntityManager
 */
@Repository
@Transactional
public class TokenUsuarioMapper {

    @PersistenceContext
    private EntityManager em;

    public TokenUsuarioMapper() {
    }

    /**
     * Guarda un nuevo token
     * @param token El token a guardar
     * @return El token guardado con su ID generado
     */
    public TokenUsuario save(TokenUsuario token) {
        if (token == null) {
            throw new IllegalArgumentException("El token no puede ser null");
        }
        if (token.getIdToken() == 0) {
            em.persist(token);
        } else {
            token = em.merge(token);
        }
        em.flush();
        return token;
    }

    /**
     * Guarda un nuevo token (alias de save para compatibilidad con tests)
     * @param token El token a guardar
     */
    public void guardarToken(TokenUsuario token) {
        save(token);
    }

    /**
     * Busca un token por su contenido y que esté activo
     * @param contenido El contenido del token
     * @return Optional con el token si existe y está activo
     */
    public Optional<TokenUsuario> findByContenidoAndEstadoTrue(String contenido) {
        try {
            TypedQuery<TokenUsuario> query = em.createQuery(
                "SELECT t FROM TokenUsuario t WHERE t.contenido = :contenido AND t.estado = true",
                TokenUsuario.class);
            query.setParameter("contenido", contenido);
            TokenUsuario token = query.getSingleResult();
            return Optional.of(token);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Obtiene un token por su contenido (solo si está activo)
     * @param contenido El contenido del token
     * @return El token si existe y está activo, null si no
     */
    public TokenUsuario obtenerToken(String contenido) {
        return findByContenidoAndEstadoTrue(contenido).orElse(null);
    }

    /**
     * Invalida un token (lo marca como inactivo)
     * @param idToken El ID del token a invalidar
     */
    public void invalidar(int idToken) {
        em.createQuery("UPDATE TokenUsuario t SET t.estado = false WHERE t.idToken = :idToken")
            .setParameter("idToken", idToken)
            .executeUpdate();
    }

    /**
     * Desactiva un token (alias de invalidar para compatibilidad con tests)
     * @param idToken El ID del token a desactivar
     */
    public void desactivarToken(int idToken) {
        invalidar(idToken);
    }

    /**
     * Busca un token por su ID
     * @param id El ID del token
     * @return Optional con el token si existe
     */
    public Optional<TokenUsuario> findById(int id) {
        TokenUsuario token = em.find(TokenUsuario.class, id);
        return Optional.ofNullable(token);
    }

    /**
     * Obtiene todos los tokens
     * @return Lista de todos los tokens
     */
    public List<TokenUsuario> findAll() {
        TypedQuery<TokenUsuario> query = em.createQuery(
            "SELECT t FROM TokenUsuario t", TokenUsuario.class);
        return query.getResultList();
    }

    /**
     * Elimina un token
     * @param id El ID del token a eliminar
     */
    public void deleteById(int id) {
        TokenUsuario token = em.find(TokenUsuario.class, id);
        if (token != null) {
            em.remove(token);
        }
    }

    /**
     * Verifica si existe un token con el ID dado
     * @param id El ID a verificar
     * @return true si existe, false si no
     */
    public boolean existsById(int id) {
        Long count = em.createQuery(
            "SELECT COUNT(t) FROM TokenUsuario t WHERE t.idToken = :id", Long.class)
            .setParameter("id", id)
            .getSingleResult();
        return count > 0;
    }
}

