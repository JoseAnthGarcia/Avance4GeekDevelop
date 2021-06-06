package com.example.demo.entities;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Plato_has_pedidoKey implements Serializable {

    private String codigo;
    private int idplato;

    public String getCodigo() { return codigo; }

    public void setCodigo(String codigo) { this.codigo = codigo; }

    public int getIdplato() { return idplato; }

    public void setIdplato(int idplato) { this.idplato = idplato; }
}
