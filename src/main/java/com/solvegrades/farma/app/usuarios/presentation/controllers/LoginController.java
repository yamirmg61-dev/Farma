package com.solvegrades.farma.app.usuarios.presentation.controllers;

import com.solvegrades.farma.app.usuarios.application.dto.UsuarioDTO;
import com.solvegrades.farma.app.usuarios.application.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Auth", description = "Endpoints de autenticación")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Iniciar sesión (dev - texto plano)")
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            if (req == null) return ResponseEntity.badRequest().body(new ErrorResponse("Validación", "Request body es null"));

            String username = req.getUsername() != null ? req.getUsername().trim() : "";
            String password = req.getPassword() != null ? req.getPassword() : "";

            if (username.isEmpty() || password.isEmpty()) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Validación", "username o password faltan"));
            }

            logger.debug("Intento de login para usuario='{}'", username);

            UsuarioDTO u = usuarioService.findByUsername(username);
            if (u == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Credenciales", "Usuario o contraseña incorrectos"));
            }

            // comparación simple (texto plano; en producción usar hash/BCrypt)
            if (!password.equals(u.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Credenciales", "Usuario o contraseña incorrectos"));
            }

            // Construimos una respuesta segura sin password
            UserResponse userResponse = new UserResponse(u.getId(), u.getUsername(), u.getRole(), u.getClienteId());
            LoginResponse resp = new LoginResponse(null, userResponse); // token null en dev
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            logger.error("Error en POST /api/v1/auth/login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error interno", "Ha ocurrido un error inesperado en el servidor"));
        }
    }

    // Request / Response DTOs
    public static class LoginRequest {
        private String username;
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String token;
        private UserResponse user;
        public LoginResponse(String token, UserResponse user) { this.token = token; this.user = user; }
        public String getToken() { return token; }
        public UserResponse getUser() { return user; }
    }

    public static class UserResponse {
        private Long id;
        private String username;
        private String role;
        private Integer clienteId;
        public UserResponse(Long id, String username, String role, Integer clienteId) {
            this.id = id; this.username = username; this.role = role; this.clienteId = clienteId;
        }
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
        public Integer getClienteId() { return clienteId; }
    }

    public static class ErrorResponse {
        private String tipo, mensaje;
        public ErrorResponse(String tipo, String mensaje) { this.tipo = tipo; this.mensaje = mensaje; }
        public String getTipo() { return tipo; } public String getMensaje() { return mensaje; }
    }
}
