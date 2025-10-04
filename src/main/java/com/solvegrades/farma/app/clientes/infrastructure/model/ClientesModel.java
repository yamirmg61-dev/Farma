package com.solvegrades.farma.app.clientes.infrastructure.model;

import java.time.LocalDateTime;

public class ClientesModel {
    private Integer id;
    private String nombre;
    private String dni;
    private String telefono;
    private String password;
    private LocalDateTime createdAt;

    public ClientesModel() {}

    public ClientesModel(Integer id, String nombre, String dni, String telefono, String password, LocalDateTime createdAt) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
        this.password = password;
        this.createdAt = createdAt;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
