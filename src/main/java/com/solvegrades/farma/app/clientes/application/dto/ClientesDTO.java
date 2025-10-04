package com.solvegrades.farma.app.clientes.application.dto;

import java.time.LocalDateTime;

public class ClientesDTO {
    private Integer id;
    private String nombre;
    private String dni;
    private String telefono;
    private String password;        // contraseña proporcionada por el frontend (texto plano en tu entorno de aprendizaje)
    private LocalDateTime createdAt;

    public ClientesDTO() {}

    public ClientesDTO(Integer id, String nombre, String dni, String telefono, String password, LocalDateTime createdAt) {
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
