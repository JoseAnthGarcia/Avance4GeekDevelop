package com.example.demo.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "direccion")
public class Direccion {

    private int iddireccion;
    private String ubicacion;
    private String coordenadas;

    @ManyToOne
    @JoinColumn(name = "iddistrito")
    private Distrito distrito;
}
