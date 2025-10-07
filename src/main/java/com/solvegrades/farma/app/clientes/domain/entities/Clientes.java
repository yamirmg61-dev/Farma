package com.solvegrades.farma.app.clientes.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.solvegrades.farma.app.usuarios.domain.entities.Usuario;

@Entity
@Table(name = "clientes")
public class Clientes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 20, unique = true)
    private String dni;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false)
    private Usuario usuario;

    public Clientes() {}

    // Constructor que coincide con el mapping (sin usuario)
    public Clientes(Integer id, String nombre, String dni, String telefono, String password, LocalDateTime createdAt) {
        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
        this.password = password;
        this.createdAt = createdAt;
    }

    // Constructor con Usuario
    public Clientes(String nombre, String dni, String telefono, String password, Usuario usuario) {
        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
        this.password = password;
        this.usuario = usuario;
    }

    // Getters / Setters
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

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
