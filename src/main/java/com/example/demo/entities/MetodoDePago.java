package com.example.demo.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "metodopago")
public class MetodoDePago implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idmetodopago;
    private String tipo;

    public int getIdmetodopago() {
        return idmetodopago;
    }

    public void setIdmetodopago(int idmetodopago) {
        this.idmetodopago = idmetodopago;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
