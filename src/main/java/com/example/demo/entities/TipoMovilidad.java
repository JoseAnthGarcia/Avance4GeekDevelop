package com.example.demo.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tipomovilidad")
public class TipoMovilidad implements Serializable  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idtipomovilidad;
    @Column(nullable = false)
    private String tipo;

    public int getIdtipomovilidad() {
        return idtipomovilidad;
    }

    public void setIdtipomovilidad(int idtipomovilidad) {
        this.idtipomovilidad = idtipomovilidad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
