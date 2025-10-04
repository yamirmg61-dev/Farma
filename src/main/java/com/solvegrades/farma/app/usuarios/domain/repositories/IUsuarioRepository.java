// src/main/java/com/solvegrades/farma/usuarios/domain/repositories/IUsuarioRepository.java
package com.solvegrades.farma.app.usuarios.domain.repositories;

import com.solvegrades.farma.app.usuarios.domain.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {
    // devuelve lista (no Optional) para clienteId — así el servicio puede borrar/recorrer varios usuarios
    List<Usuario> findByClienteId(Integer clienteId);

    Optional<Usuario> findByUsername(String username);

    List<Usuario> findByUsernameContainingIgnoreCase(String q);
}
