package com.udistrital.awuis.sistema_academico.repositories;

import com.udistrital.awuis.sistema_academico.model.HistorialAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialAcademicoMapper extends JpaRepository<HistorialAcademico, Integer> {
}
