package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Mapper para la entidad Usuario.
 * Implementa las operaciones CRUD usando EntityManager
 */
@Repository
@Transactional
public class UsuarioMapper {

    @PersistenceContext
    private EntityManager em;

    public UsuarioMapper() {
    }

    /**
     * Guarda un nuevo usuario
     * @param usuario El usuario a guardar
     * @return El usuario guardado con su ID generado
     */
    public Usuario save(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser null");
        }
        if (usuario.getIdUsuario() == 0) {
            em.persist(usuario);
        } else {
            usuario = em.merge(usuario);
        }
        em.flush();
        return usuario;
    }

    /**
     * Busca un usuario por su correo
     * @param correo El correo del usuario
     * @return Optional con el usuario si existe
     */
    public Optional<Usuario> findByCorreo(String correo) {
        try {
            TypedQuery<Usuario> query = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.correo = :correo", Usuario.class);
            query.setParameter("correo", correo);
            Usuario usuario = query.getSingleResult();
            return Optional.of(usuario);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Busca un usuario por su ID
     * @param id El ID del usuario
     * @return Optional con el usuario si existe
     */
    public Optional<Usuario> findById(int id) {
        Usuario usuario = em.find(Usuario.class, id);
        return Optional.ofNullable(usuario);
    }

    /**
     * Obtiene todos los usuarios
     * @return Lista de todos los usuarios
     */
    public List<Usuario> findAll() {
        TypedQuery<Usuario> query = em.createQuery(
            "SELECT u FROM Usuario u", Usuario.class);
        return query.getResultList();
    }

    /**
     * Actualiza el token de un usuario
     * @param idUsuario El ID del usuario
     * @param idToken El ID del nuevo token
     */
    public void actualizarToken(int idUsuario, int idToken) {
        em.createQuery("UPDATE Usuario u SET u.token.idToken = :idToken WHERE u.idUsuario = :idUsuario")
            .setParameter("idUsuario", idUsuario)
            .setParameter("idToken", idToken)
            .executeUpdate();
    }

    /**
     * Elimina un usuario
     * @param id El ID del usuario a eliminar
     */
    public void deleteById(int id) {
        Usuario usuario = em.find(Usuario.class, id);
        if (usuario != null) {
            em.remove(usuario);
        }
    }

    /**
     * Verifica si existe un usuario con el ID dado
     * @param id El ID a verificar
     * @return true si existe, false si no
     */
    public boolean existsById(int id) {
        Long count = em.createQuery(
            "SELECT COUNT(u) FROM Usuario u WHERE u.idUsuario = :id", Long.class)
            .setParameter("id", id)
            .getSingleResult();
        return count > 0;
    }
}

