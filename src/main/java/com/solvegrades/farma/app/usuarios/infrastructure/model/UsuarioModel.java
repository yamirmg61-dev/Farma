package com.solvegrades.farma.app.usuarios.infrastructure.model;

public class UsuarioModel {
    private Long id;
    private String username;
    private String password;
    private String role;
    private Integer clienteId;

    public UsuarioModel() {}

    public UsuarioModel(Long id, String username, String password, String role, Integer clienteId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.clienteId = clienteId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
}
