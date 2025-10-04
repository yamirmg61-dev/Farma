package com.solvegrades.farma.app.categorias.infrastructure.model;

public class CategoriasModel {
    private Integer id;
    private String nombre;

    public CategoriasModel() {}

    public CategoriasModel(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
