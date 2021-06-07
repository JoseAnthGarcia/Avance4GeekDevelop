package com.example.demo.entities;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Extra_has_pedidoKey implements Serializable {

    private int idextra;
    private String codigo;

    public int getIdextra() { return idextra; }

    public void setIdextra(int idextra) { this.idextra = idextra; }

    public String getCodigo() { return codigo; }

    public void setCodigo(String codigo) { this.codigo = codigo; }
}
