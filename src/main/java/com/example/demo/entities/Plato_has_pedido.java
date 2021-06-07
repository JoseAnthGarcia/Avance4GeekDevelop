package com.example.demo.entities;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "plato_has_pedido")
public class Plato_has_pedido implements Serializable {

    @EmbeddedId
    private Plato_has_pedidoKey idplatohaspedido;

    @ManyToOne
    @MapsId("idplato")
    @JoinColumn(name = "idplato")
    private Plato plato;

    @ManyToOne
    @MapsId("codigo")
    @JoinColumn(name = "codigo")
    private Pedido pedido;
    //private String codigo;

    // cantidad de un plato
    private int cantidad;

    // creo q deberia ser el subtotal
    private BigDecimal preciounitario;
    private String observacionplatillo;

    public Plato getPlato() { return plato; }

    public void setPlato(Plato plato) { this.plato = plato; }

    public Pedido getPedido() { return pedido; }

    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Plato_has_pedidoKey getIdplatohaspedido() { return idplatohaspedido; }

    public void setIdplatohaspedido(Plato_has_pedidoKey idplatohaspedido) { this.idplatohaspedido = idplatohaspedido; }

    public int getCantidad() { return cantidad; }

    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPreciounitario() { return preciounitario; }

    public void setPreciounitario(BigDecimal preciounitario) { this.preciounitario = preciounitario; }

    public String getObservacionplatillo() { return observacionplatillo; }

    public void setObservacionplatillo(String observacionplatillo) { this.observacionplatillo = observacionplatillo; }
}
