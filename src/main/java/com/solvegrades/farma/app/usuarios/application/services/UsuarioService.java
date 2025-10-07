// src/main/java/com/solvegrades/farma/usuarios/application/services/UsuarioService.java
package com.solvegrades.farma.app.usuarios.application.services;

import com.solvegrades.farma.app.usuarios.application.dto.UsuarioDTO;
import com.solvegrades.farma.app.usuarios.domain.entities.Usuario;
import com.solvegrades.farma.app.usuarios.domain.repositories.IUsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioService {

    private final IUsuarioRepository usuarioRepository;

    public UsuarioService(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO findById(Long id) {
        return usuarioRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> searchByUsername(String q) {
        return usuarioRepository.findByUsernameContainingIgnoreCase(q)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public long countUsuarios() { return usuarioRepository.count(); }

    @Transactional(readOnly = true)
    public UsuarioDTO findByUsername(String username) {
        return usuarioRepository.findByUsername(username).map(this::toDTO).orElse(null);
    }

    public UsuarioDTO save(UsuarioDTO dto) {
        validate(dto);
        usuarioRepository.findByUsername(dto.getUsername())
                .ifPresent(u -> { throw new RuntimeException("Ya existe un usuario con username: " + dto.getUsername()); });

        Usuario u = toEntity(dto);
        Usuario saved = usuarioRepository.save(u);
        return toDTO(saved);
    }

    public UsuarioDTO update(Long id, UsuarioDTO dto) {
        validate(dto);
        Usuario existing = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        usuarioRepository.findByUsername(dto.getUsername()).ifPresent(u -> {
            if (!u.getId().equals(id)) throw new RuntimeException("Ya existe otro usuario con username: " + dto.getUsername());
        });

        existing.setUsername(dto.getUsername());
        existing.setPassword(dto.getPassword());
        if (dto.getRole() != null) existing.setRole(Usuario.Role.valueOf(dto.getRole()));
        existing.setClienteId(dto.getClienteId());

        Usuario updated = usuarioRepository.save(existing);
        return toDTO(updated);
    }

    public void delete(Long id) {
        if (!usuarioRepository.existsById(id)) throw new RuntimeException("Usuario no encontrado con id: " + id);
        usuarioRepository.deleteById(id);
    }

    // métodos para cliente-service usando List<Usuario>
    public void deleteByClienteId(Integer clienteId) {
        List<Usuario> list = usuarioRepository.findByClienteId(clienteId);
        if (list != null && !list.isEmpty()) {
            usuarioRepository.deleteAll(list);
        }
    }

    public void updatePasswordByClienteId(Integer clienteId, String newPassword) {
        List<Usuario> list = usuarioRepository.findByClienteId(clienteId);
        if (list != null && !list.isEmpty()) {
            for (Usuario u : list) {
                u.setPassword(newPassword);
            }
            usuarioRepository.saveAll(list);
        }
    }

    // helpers
    private void validate(UsuarioDTO dto) {
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) throw new IllegalArgumentException("El username es obligatorio");
        if (dto.getUsername().length() > 150) throw new IllegalArgumentException("El username no puede exceder 150 caracteres");
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) throw new IllegalArgumentException("La contraseña es obligatoria");
        if (dto.getPassword().length() > 300) throw new IllegalArgumentException("La contraseña no puede exceder 300 caracteres");
        if (dto.getRole() != null) {
            try { Usuario.Role.valueOf(dto.getRole()); }
            catch (IllegalArgumentException ex) { throw new IllegalArgumentException("Role inválido. Valores: CLIENTE, EMPLEADO, ADMIN"); }
        }
    }

    private UsuarioDTO toDTO(Usuario u) {
        String roleName = (u.getRole() != null) ? u.getRole().name() : null;
        return new UsuarioDTO(u.getId(), u.getUsername(), u.getPassword(), roleName, u.getClienteId());
    }

    private Usuario toEntity(UsuarioDTO dto) {
        Usuario u = new Usuario();
        u.setId(dto.getId());
        u.setUsername(dto.getUsername() != null ? dto.getUsername().trim() : null);
        u.setPassword(dto.getPassword() != null ? dto.getPassword().trim() : null);
        u.setClienteId(dto.getClienteId());
        if (dto.getRole() != null) u.setRole(Usuario.Role.valueOf(dto.getRole()));
        return u;
    }
}