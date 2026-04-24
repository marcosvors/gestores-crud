package com.marcosvors.gestores_crud.resources;

import com.marcosvors.gestores_crud.entities.Gestor;
import com.marcosvors.gestores_crud.services.GestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/gestores")
public class GestorResource {

    @Autowired
    private GestorService service;

    @GetMapping
    public ResponseEntity<List<Gestor>> findAll() {
        List<Gestor> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Gestor> findById(@PathVariable Long id) {
        Gestor obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @PostMapping
    public ResponseEntity<Gestor> inserir(@RequestBody Gestor obj) {
        obj = service.inserir(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(obj.getId())
                .toUri();
        return ResponseEntity.created(uri).body(obj);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Gestor> atualizar(@PathVariable Long id, @RequestBody Gestor obj) {
        obj = service.atualizar(id, obj);
        return ResponseEntity.ok().body(obj);
    }
}