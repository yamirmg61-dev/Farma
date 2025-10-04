package com.solvegrades.farma.app.clientes.infrastructure.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.solvegrades.farma.app.clientes.domain.entities.Clientes;
import com.solvegrades.farma.app.clientes.domain.repositories.IClientesRepository;
import com.solvegrades.farma.app.clientes.infrastructure.model.ClientesModel;

@Component
public class ClientesRepository {

    @Autowired
    private IClientesRepository iClientesRepository;

    public List<Clientes> findAll() {
        return iClientesRepository.findAll();
    }

    public Optional<Clientes> findById(Integer id) {
        return iClientesRepository.findById(id);
    }

    public Clientes save(Clientes cliente) {
        return iClientesRepository.save(cliente);
    }

    public void deleteById(Integer id) {
        iClientesRepository.deleteById(id);
    }

    public List<Clientes> findByNombreContaining(String nombre) {
        return iClientesRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Optional<Clientes> findByNombre(String nombre) {
        return iClientesRepository.findByNombre(nombre);
    }

    public Optional<Clientes> findByDni(String dni) {
        return iClientesRepository.findByDni(dni);
    }

    public boolean existsById(Integer id) {
        return iClientesRepository.existsById(id);
    }

    public boolean existsByDni(String dni) {
        return iClientesRepository.existsByDni(dni);
    }

    public long count() {
        return iClientesRepository.count();
    }

    // Model mapping (opcional)
    public ClientesModel toModel(Clientes cliente) {
        return new ClientesModel(cliente.getId(), cliente.getNombre(), cliente.getDni(), cliente.getTelefono(), cliente.getPassword(), cliente.getCreatedAt());
    }

    public Clientes toEntity(ClientesModel model) {
        return new Clientes(model.getId(), model.getNombre(), model.getDni(), model.getTelefono(), model.getPassword(), model.getCreatedAt());
    }
}
