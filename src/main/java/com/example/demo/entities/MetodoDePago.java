package com.example.demo.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "metodopago")
public class MetodoDePago implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idmetodopago;
    private int tipo;

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

}
