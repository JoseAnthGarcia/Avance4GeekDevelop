package com.example.demo.entities;

import javax.persistence.*;

@Entity
@Table(name = "restaurante")
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idrestaurante;

    @Column(nullable = false)
    private String nombre;

    @OneToOne
    @JoinColumn(name = "idadministrador")
    private Usuario administrador;

    public int getIdrestaurante() {
        return idrestaurante;
    }

    public void setIdrestaurante(int idrestaurante) {
        this.idrestaurante = idrestaurante;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
