package com.marcosvors.gestores_crud.repositories;

import com.marcosvors.gestores_crud.entities.Gestor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GestorRepository extends JpaRepository<Gestor, Long> {
}
