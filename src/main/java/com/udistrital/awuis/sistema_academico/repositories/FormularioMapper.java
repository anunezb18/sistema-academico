package com.udistrital.awuis.sistema_academico.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.udistrital.awuis.sistema_academico.model.Formulario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@Repository
@Transactional
public class FormularioMapper {

    @PersistenceContext
    private EntityManager em;

    public FormularioMapper() {
    }

    public Formulario agregarFormulario(Formulario formulario) {
        if (formulario == null) return null;
        em.persist(formulario);
        return formulario;
    }

    public Formulario obtenerPorId(int id) {
        return em.find(Formulario.class, id);
    }

    public List<Formulario> listarFormularios() {
        TypedQuery<Formulario> q = em.createQuery("SELECT f FROM Formulario f", Formulario.class);
        return q.getResultList();
    }

    public void cambiarEstado(int idFormulario, String estado) {
        Formulario f = em.find(Formulario.class, idFormulario);
        if (f != null) {
            f.setEstado(estado);
            em.merge(f);
        }
    }
}
