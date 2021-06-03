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
    private float cantidadapagar;


    public int getIdmetodopago() {
        return idmetodopago;
    }

    public void setIdmetodopago(int idmetodopago) {
        this.idmetodopago = idmetodopago;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public float getCantidadapagar() {
        return cantidadapagar;
    }

    public void setCantidadapagar(float cantidadapagar) {
        this.cantidadapagar = cantidadapagar;
    }
}
