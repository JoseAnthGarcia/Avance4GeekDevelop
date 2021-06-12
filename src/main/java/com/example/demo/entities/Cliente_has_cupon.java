package com.example.demo.entities;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "cliente_has_cupon")
public class Cliente_has_cupon implements Serializable {

    @EmbeddedId
    private Cliente_has_cuponKey cliente_has_cuponKey;
    private boolean utilizado;

    public Cliente_has_cuponKey getCliente_has_cuponKey() {
        return cliente_has_cuponKey;
    }

    public void setCliente_has_cuponKey(Cliente_has_cuponKey cliente_has_cuponKey) {
        this.cliente_has_cuponKey = cliente_has_cuponKey;
    }

    public boolean isUtilizado() {
        return utilizado;
    }

    public void setUtilizado(boolean utilizado) {
        this.utilizado = utilizado;
    }
}
