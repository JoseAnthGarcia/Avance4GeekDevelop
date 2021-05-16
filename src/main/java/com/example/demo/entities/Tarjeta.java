package com.example.demo.entities;

import javax.persistence.*;

@Entity
@Table(name="tarjeta")
public class Tarjeta {

    @Id
    private String idtarjeta;

    @Column(nullable = false)
    private String numerotarjeta;

    @ManyToOne
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario usuario;

    public String getIdtarjeta() {
        return idtarjeta;
    }

    public void setIdtarjeta(String idtarjeta) {
        this.idtarjeta = idtarjeta;
    }

    public String getNumerotarjeta() {
        return numerotarjeta;
    }

    public void setNumerotarjeta(String numerotarjeta) {
        this.numerotarjeta = numerotarjeta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
