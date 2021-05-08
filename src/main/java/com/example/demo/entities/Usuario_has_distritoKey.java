package com.example.demo.entities;


import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Usuario_has_distritoKey implements Serializable {

    private int idusuario;
    private int iddistrito;

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }

    public int getIddistrito() {
        return iddistrito;
    }

    public void setIddistrito(int iddistrito) {
        this.iddistrito = iddistrito;
    }
}
