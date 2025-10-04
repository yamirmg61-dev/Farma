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
import com.solvegrades.farma.app.usuarios.application.dto.UsuarioDTO;
import com.solvegrades.farma.app.usuarios.application.services.UsuarioService;

@Service
@Transactional
public class ClientesService {

    @Autowired
    private IClientesRepository clientesRepository;

    // servicio de usuarios existente (se usará para crear usuario asociado)
    @Autowired
    private UsuarioService usuarioService;

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
     * Guarda cliente y crea su usuario asociado.
     * Nota: guarda contraseña en texto plano (modo learning). En producción usar hash.
     */
    public ClientesDTO save(ClientesDTO dto) {
        validateCliente(dto);

        // unicidad por DNI y por nombre
        if (dto.getDni() != null && clientesRepository.existsByDni(dto.getDni())) {
            throw new RuntimeException("Ya existe un cliente con el DNI: " + dto.getDni());
        }
        Optional<Clientes> existing = clientesRepository.findByNombre(dto.getNombre());
        if (existing.isPresent()) {
            throw new RuntimeException("Ya existe un cliente con el nombre: " + dto.getNombre());
        }

        Clientes cliente = toEntity(dto);
        // si la contraseña viene en DTO, la guardamos tal cual
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            cliente.setPassword(dto.getPassword().trim());
        }

        Clientes saved;
        try {
            saved = clientesRepository.save(cliente);
        } catch (DataIntegrityViolationException ex) {
            // puede ocurrir si DB tiene unique constraints y hubo condición de carrera
            throw new RuntimeException("Error de integridad en la base de datos al crear cliente: " + ex.getMessage(), ex);
        }

        // crear usuario asociado
        try {
            // SUGERENCIA: usar DNI como username (más estable y único). Si prefieres usar nombre, cambia aquí.
            String usernameCandidate = saved.getDni(); // recomendado: DNI único y sin espacios
            if (usernameCandidate == null || usernameCandidate.trim().isEmpty()) {
                usernameCandidate = saved.getNombre() != null ? saved.getNombre().trim().replaceAll("\\s+"," ") : "user";
            }

            // si ya existe username en usuarios, anexar id
            if (usuarioService.findByUsername(usernameCandidate) != null) {
                usernameCandidate = usernameCandidate + "_" + saved.getId();
            }

            UsuarioDTO userDto = new UsuarioDTO();
            userDto.setId(null);
            userDto.setUsername(usernameCandidate);
            userDto.setPassword(saved.getPassword()); // texto plano por ahora
            userDto.setRole("CLIENTE");
            userDto.setClienteId(saved.getId());

            usuarioService.save(userDto);
        } catch (RuntimeException ex) {
            // si falla la creación del usuario, lanzar excepción para provocar rollback de la transacción
            throw new RuntimeException("Error creando usuario asociado desde DEV: " + ex.getMessage(), ex);
        }

        ClientesDTO res = toDTO(saved);
        res.setPassword(null); // no devolver password en la respuesta
        return res;
    }

    public ClientesDTO update(Integer id, ClientesDTO dto) {
        validateCliente(dto);
        Clientes existing = clientesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con id: " + id));
        Optional<Clientes> clienteConMismoNombre = clientesRepository.findByNombre(dto.getNombre());
        if (clienteConMismoNombre.isPresent() && !clienteConMismoNombre.get().getId().equals(id)) {
            throw new RuntimeException("Ya existe otro cliente con el nombre: " + dto.getNombre());
        }

        // validar dni: si lo cambian, revisar unicidad
        if (dto.getDni() != null && !dto.getDni().equals(existing.getDni())) {
            if (clientesRepository.existsByDni(dto.getDni())) {
                throw new RuntimeException("Ya existe otro cliente con el DNI: " + dto.getDni());
            }
            existing.setDni(dto.getDni().trim());
        }

        existing.setNombre(dto.getNombre());
        existing.setTelefono(dto.getTelefono());

        // actualizar contraseña si viene: actualizar también el usuario vinculado (si existe)
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            existing.setPassword(dto.getPassword().trim()); // texto plano (learning)
            try {
                usuarioService.updatePasswordByClienteId(existing.getId(), dto.getPassword().trim());
            } catch (Exception e) {
                // registrar o ignorar si no existe usuario asociado; no romper la operación
            }
        }

        Clientes updated = clientesRepository.save(existing);
        ClientesDTO res = toDTO(updated);
        res.setPassword(null);
        return res;
    }

    public void delete(Integer id) {
        if (!clientesRepository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado con id: " + id);
        }
        // eliminar usuario asociado si existe
        try {
            usuarioService.deleteByClienteId(id);
        } catch (Exception e) {
            // ignorar si no existe o el método no está implementado
        }
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
        // contraseña requerida únicamente al crear un cliente
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
