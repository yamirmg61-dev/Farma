package com.solvegrades.farma.app.usuarios.application.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UsuarioDTO {
    private Long id;

    @JsonProperty("username")
    @JsonAlias({ "usuario", "nombre" })
    private String username;

    @JsonProperty("password")
    @JsonAlias({ "contrasena", "passwd", "pwd" })
    private String password;

    private String role;

    @JsonProperty("clienteId")
    @JsonAlias({ "cliente_id", "cliente" })
    private Integer clienteId;

    // campo transitorio usado por la API
    @JsonProperty("login")
    @JsonAlias({ "isLogin", "loginUsuario" })
    private Boolean login;

    public UsuarioDTO() {}

    // Constructor que utiliza UsuarioService (id, username, password, role, clienteId)
    public UsuarioDTO(Long id, String username, String password, String role, Integer clienteId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.clienteId = clienteId;
    }

    // getters / setters
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

    public Boolean getLogin() { return login; }
    public void setLogin(Boolean login) { this.login = login; }
}
