package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolMapper extends JpaRepository<Rol, Integer> {

    Optional<Rol> findByNombre(String nombre);
}

