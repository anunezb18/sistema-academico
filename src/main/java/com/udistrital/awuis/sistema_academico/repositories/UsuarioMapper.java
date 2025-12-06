package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioMapper extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreo(String correo);

    @Modifying
    @Query("UPDATE Usuario u SET u.token.idToken = :idToken WHERE u.idUsuario = :idUsuario")
    void actualizarToken(@Param("idUsuario") int idUsuario, @Param("idToken") int idToken);
}

