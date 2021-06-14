package com.example.demo.entities;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Cliente_has_cuponKey implements Serializable {

    private int idcliente;
    private int idcupon;

    public int getIdcliente() {
        return idcliente;
    }

    public void setIdcliente(int idcliente) {
        this.idcliente = idcliente;
    }

    public int getIdcupon() {
        return idcupon;
    }

    public void setIdcupon(int idcupon) {
        this.idcupon = idcupon;
    }
}
