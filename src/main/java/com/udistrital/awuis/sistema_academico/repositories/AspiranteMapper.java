package com.udistrital.awuis.sistema_academico.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.udistrital.awuis.sistema_academico.model.Aspirante;
import com.udistrital.awuis.sistema_academico.model.Formulario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class AspiranteMapper {

    @PersistenceContext
    private EntityManager em;

    public AspiranteMapper() {
    }

    public Aspirante agregarAspirante(Aspirante aspirante) {
        if (aspirante == null) return null;
        em.persist(aspirante);
        return aspirante;
    }

    public Aspirante obtenerPorId(int id) {
        return em.find(Aspirante.class, id);
    }

    public List<Aspirante> listarAspirantes() {
        TypedQuery<Aspirante> q = em.createQuery("SELECT a FROM Aspirante a", Aspirante.class);
        return q.getResultList();
    }

    public void asignarFormulario(int idAspirante, Formulario formulario) {
        Aspirante a = em.find(Aspirante.class, idAspirante);
        if (a != null) {
            a.setFormulario(formulario);
            em.merge(a);
        }
    }
}

