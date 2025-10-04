package com.solvegrades.farma.app.usuarios.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    public enum Role { CLIENTE, EMPLEADO, ADMIN }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150, unique = true)
    private String username;

    // contraseña en claro por ahora (campo simple)
    @Column(length = 300)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    // FK al cliente (opcional, puede ser null)
    private Integer clienteId;

    public Usuario() {}

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
}
