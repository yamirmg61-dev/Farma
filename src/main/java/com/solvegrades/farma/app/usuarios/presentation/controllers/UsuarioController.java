package com.solvegrades.farma.app.usuarios.presentation.controllers;

import com.solvegrades.farma.app.usuarios.application.dto.UsuarioDTO;
import com.solvegrades.farma.app.usuarios.application.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Usuarios", description = "API para gestión de usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Obtener todos los usuarios")
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> getAll() {
        try { return ResponseEntity.ok(usuarioService.findAll()); }
        catch (Exception e) { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); }
    }

    @Operation(summary = "Obtener usuario por ID")
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<UsuarioDTO> getById(@Parameter(description = "ID del usuario", required = true) @PathVariable Long id) {
        try { return ResponseEntity.ok(usuarioService.findById(id)); }
        catch (RuntimeException e) { return ResponseEntity.notFound().build(); }
        catch (Exception e) { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); }
    }

    @Operation(summary = "Buscar usuarios por username")
    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioDTO>> buscar(@RequestParam String q) {
        try {
            if (q == null || q.trim().isEmpty()) return ResponseEntity.badRequest().build();
            return ResponseEntity.ok(usuarioService.searchByUsername(q));
        } catch (Exception e) { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); }
    }

    @Operation(summary = "Conteo de usuarios")
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        try { return ResponseEntity.ok(usuarioService.countUsuarios()); }
        catch (Exception e) { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); }
    }

    @Operation(summary = "Crear usuario (admin/propósito general)")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody UsuarioDTO dto) {
        try {
            dto.setId(null);
            UsuarioDTO created = usuarioService.save(dto);
            // No devolver password
            created.setPassword(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Validación", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("Ya existe"))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Conflicto", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error interno", "Ha ocurrido un error en el servidor"));
        }
    }

    @Operation(summary = "Actualizar usuario")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        try {
            UsuarioDTO updated = usuarioService.update(id, dto);
            updated.setPassword(null);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Validación", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("no encontrado")) return ResponseEntity.notFound().build();
            else if (e.getMessage() != null && e.getMessage().contains("Ya existe"))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Conflicto", e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error", e.getMessage()));
        } catch (Exception e) { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error interno", "Ha ocurrido un error en el servidor")); }
    }

    @Operation(summary = "Eliminar usuario")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            usuarioService.delete(id);
            return ResponseEntity.ok(new SuccessResponse("Eliminación exitosa", "Usuario eliminado correctamente"));
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("no encontrado")) return ResponseEntity.notFound().build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error", e.getMessage()));
        } catch (Exception e) { return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error interno", "Ha ocurrido un error en el servidor")); }
    }

    // response helpers
    public static class ErrorResponse {
        private String tipo, mensaje;
        public ErrorResponse(String tipo, String mensaje) { this.tipo = tipo; this.mensaje = mensaje; }
        public String getTipo() { return tipo; } public String getMensaje() { return mensaje; }
    }
    public static class SuccessResponse {
        private String tipo, mensaje;
        public SuccessResponse(String tipo, String mensaje) { this.tipo = tipo; this.mensaje = mensaje; }
        public String getTipo() { return tipo; } public String getMensaje() { return mensaje; }
    }
}
