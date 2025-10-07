package com.solvegrades.farma.app.clientes.application.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.solvegrades.farma.app.clientes.application.dto.ClientesDTO;
import com.solvegrades.farma.app.clientes.domain.entities.Clientes;
import com.solvegrades.farma.app.clientes.domain.repositories.IClientesRepository;

@Service
@Transactional
public class ClientesService {

    @Autowired
    private IClientesRepository clientesRepository;

    @Transactional(readOnly = true)
    public List<ClientesDTO> findAll() {
        return clientesRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public ClientesDTO findById(Integer id) {
        return clientesRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ClientesDTO> findByNombreContaining(String nombre) {
        return clientesRepository.findByNombreContainingIgnoreCase(nombre)
                .stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public long countClientes() {
        return clientesRepository.count();
    }

    /**
     * Guarda un cliente.
     * No se encarga de crear el usuario, solo maneja la creación del cliente.
     */
    public ClientesDTO save(ClientesDTO dto) {
        validateCliente(dto);

        // Verifica unicidad por DNI
        if (dto.getDni() != null && clientesRepository.existsByDni(dto.getDni())) {
            throw new RuntimeException("Ya existe un cliente con el DNI: " + dto.getDni());
        }
        Optional<Clientes> existing = clientesRepository.findByNombre(dto.getNombre());
        if (existing.isPresent()) {
            throw new RuntimeException("Ya existe un cliente con el nombre: " + dto.getNombre());
        }

        Clientes cliente = toEntity(dto);
        
        // Guardar cliente en la base de datos
        Clientes saved;
        try {
            saved = clientesRepository.save(cliente);
        } catch (DataIntegrityViolationException ex) {
            throw new RuntimeException("Error de integridad en la base de datos al crear cliente: " + ex.getMessage(), ex);
        }

        // Retornar el DTO sin la contraseña
        ClientesDTO res = toDTO(saved);
        res.setPassword(null); // No devolver contraseña en la respuesta
        return res;
    }

    public ClientesDTO update(Integer id, ClientesDTO dto) {
        validateCliente(dto);

        // Buscar cliente existente
        Clientes existing = clientesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));

        // Verificar unicidad del nombre
        Optional<Clientes> clienteConMismoNombre = clientesRepository.findByNombre(dto.getNombre());
        if (clienteConMismoNombre.isPresent() && !clienteConMismoNombre.get().getId().equals(id)) {
            throw new RuntimeException("Ya existe otro cliente con el nombre: " + dto.getNombre());
        }

        // Validar y actualizar DNI si es necesario
        if (dto.getDni() != null && !dto.getDni().equals(existing.getDni())) {
            if (clientesRepository.existsByDni(dto.getDni())) {
                throw new RuntimeException("Ya existe otro cliente con el DNI: " + dto.getDni());
            }
            existing.setDni(dto.getDni().trim());
        }

        // Actualizar otros campos
        existing.setNombre(dto.getNombre());
        existing.setTelefono(dto.getTelefono());

        // Actualizar contraseña si se proporciona
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            existing.setPassword(dto.getPassword().trim());
        }

        // Guardar cliente actualizado
        Clientes updated = clientesRepository.save(existing);
        
        // Retornar el DTO sin la contraseña
        ClientesDTO res = toDTO(updated);
        res.setPassword(null);
        return res;
    }

    public void delete(Integer id) {
        if (!clientesRepository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado con id: " + id);
        }
        // Eliminar cliente
        clientesRepository.deleteById(id);
    }

    private void validateCliente(ClientesDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
        if (dto.getNombre().length() > 100) {
            throw new IllegalArgumentException("El nombre del cliente no puede exceder 100 caracteres");
        }
        if (dto.getTelefono() != null && dto.getTelefono().length() > 20) {
            throw new IllegalArgumentException("El teléfono no puede exceder 20 caracteres");
        }
        if (dto.getDni() == null || dto.getDni().trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }
        if (dto.getDni().length() > 20) {
            throw new IllegalArgumentException("El DNI no puede exceder 20 caracteres");
        }
        // Contraseña requerida solo al crear cliente
        if (dto.getId() == null) {
            if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("La contraseña es obligatoria al registrar un cliente");
            }
            if (dto.getPassword().length() < 4) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres");
            }
        }
    }

    private ClientesDTO toDTO(Clientes cliente) {
        return new ClientesDTO(cliente.getId(), cliente.getNombre(), cliente.getDni(), cliente.getTelefono(), null, cliente.getCreatedAt());
    }

    private Clientes toEntity(ClientesDTO dto) {
        Clientes cliente = new Clientes();
        cliente.setId(dto.getId());
        cliente.setNombre(dto.getNombre() != null ? dto.getNombre().trim() : null);
        cliente.setDni(dto.getDni() != null ? dto.getDni().trim() : null);
        cliente.setTelefono(dto.getTelefono() != null ? dto.getTelefono().trim() : null);
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            cliente.setPassword(dto.getPassword().trim());
        }
        return cliente;
    }
}
