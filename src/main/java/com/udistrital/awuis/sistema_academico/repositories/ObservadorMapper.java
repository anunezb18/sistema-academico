package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.Observador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObservadorMapper extends JpaRepository<Observador, Integer> {
}

