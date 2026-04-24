package com.marcosvors.gestores_crud.services;

import com.marcosvors.gestores_crud.entities.Gestor;
import com.marcosvors.gestores_crud.repositories.GestorRepository;
import com.marcosvors.gestores_crud.services.exceptions.DatabaseException;
import com.marcosvors.gestores_crud.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GestorService {

    @Autowired
    private GestorRepository repository;

    public List<Gestor> findAll() {
        return repository.findAll();
    }

    public Gestor findById(Long id) {
        Optional<Gestor> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public Gestor inserir(Gestor obj) {
        return repository.save(obj);
    }

    public void deletar(Long id) {
        try {
            if (!repository.existsById(id)) {
                throw new ResourceNotFoundException(id);
            }
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public Gestor atualizar(Long id, Gestor obj) {
        try {
            Gestor entity = repository.getReferenceById(id);
            atualizarDados(entity, obj);
            return repository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException(id); // → 404 ✅
        }
    }

    private void atualizarDados(Gestor entity, Gestor obj) {
        entity.setNome(obj.getNome());
        entity.setEmail(obj.getEmail());
    }
}