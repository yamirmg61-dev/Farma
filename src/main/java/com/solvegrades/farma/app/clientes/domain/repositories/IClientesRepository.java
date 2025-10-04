package com.solvegrades.farma.app.clientes.domain.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.solvegrades.farma.app.clientes.domain.entities.Clientes;

@Repository
public interface IClientesRepository extends JpaRepository<Clientes, Integer> {
    List<Clientes> findByNombreContainingIgnoreCase(String nombre);
    Optional<Clientes> findByNombre(String nombre);

    // nuevos
    Optional<Clientes> findByDni(String dni);
    boolean existsByDni(String dni);
}
