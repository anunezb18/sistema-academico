package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.TokenUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenUsuarioMapper extends JpaRepository<TokenUsuario, Integer> {

    Optional<TokenUsuario> findByContenidoAndEstadoTrue(String contenido);

    @Modifying
    @Query("UPDATE TokenUsuario t SET t.estado = false WHERE t.idToken = :idToken")
    void invalidar(@Param("idToken") int idToken);
}

