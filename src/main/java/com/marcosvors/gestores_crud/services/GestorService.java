package com.marcosvors.gestores_crud.services;

import com.marcosvors.gestores_crud.entities.Gestor;
import com.marcosvors.gestores_crud.repositories.GestorRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        return obj.get();
    }

    public Gestor inserir(Gestor obj) {
        return repository.save(obj);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public Gestor atualizar(Long id, Gestor obj) {
        Gestor entity = repository.getReferenceById(id);
        updateData(entity, obj);
        return repository.save(entity);
    }

    private void updateData(Gestor entity, Gestor obj) {
        entity.setNome(obj.getNome());
        entity.setEmail(obj.getEmail());
    }
}