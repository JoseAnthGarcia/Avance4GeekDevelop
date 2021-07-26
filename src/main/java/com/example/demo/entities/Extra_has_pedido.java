package com.example.demo.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "extra_has_pedido")
public class Extra_has_pedido implements Serializable {

    @EmbeddedId
    private Extra_has_pedidoKey idextra;

    @ManyToOne
    @MapsId("idextra")
    @JoinColumn(name = "idextra")
    private Extra extra;

    @ManyToOne
    @MapsId("codigo")
    @JoinColumn(name = "codigo")
    private Pedido pedido;

    private int cantidad;
    private BigDecimal preciounitario;

    public Extra getExtra() { return extra; }

    public void setExtra(Extra extra) { this.extra = extra; }

    public Pedido getPedido() { return pedido; }

    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Extra_has_pedidoKey getIdextra() { return idextra; }

    public void setIdextra(Extra_has_pedidoKey idextra) { this.idextra = idextra; }

    public int getCantidad() { return cantidad; }

    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPreciounitario() { return preciounitario; }

    public void setPreciounitario(BigDecimal preciounitario) { this.preciounitario = preciounitario; }
}
