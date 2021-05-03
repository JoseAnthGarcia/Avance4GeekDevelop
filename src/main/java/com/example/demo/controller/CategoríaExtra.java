package com.example.demo.controller;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "categoriaextra")
public class CategoríaExtra {

    @Id
    @Column(nullable = false)
    private int idcategoriaextra;
    @Column(nullable = false)
    private String tipo;

    public int getIdcategoriaextra() {
        return idcategoriaextra;
    }

    public void setIdcategoriaextra(int idcategoriaextra) {
        this.idcategoriaextra = idcategoriaextra;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
