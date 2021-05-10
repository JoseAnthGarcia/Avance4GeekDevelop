package com.example.demo.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Formatter;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @Column(nullable = false, unique = true)
    private String codigo;

    //TODO SOLO MAPEARÉ LO NECESARIO PARA QUE FUNCIONE LA VISTA DE ADMIN
    @ManyToOne
    @JoinColumn(name = "idcliente")
    private Usuario cliente;

    /*
    private int idcupon;
    private int idmetodopago;
*/
    @Column(name = "preciototal", nullable = false)
    private double preciototal;

    //TODO definir
    private String estado;
    private int tiempoentrega;
    private boolean mismodistrito;
    private LocalDateTime fechapedido;

    @OneToOne
    @JoinColumn(name = "idvaloracion")
    private Valoracion valoracion;

    @ManyToOne
    @JoinColumn(name = "idrepartidor")
    private Usuario repartidor;

    @ManyToOne
    @JoinColumn(name = "idrestaurante")
    private Restaurante restaurante;

    public Usuario getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(Usuario repartidor) {
        this.repartidor = repartidor;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }

    public LocalDateTime getFechapedido() { return fechapedido; }

    public void setFechapedido(LocalDateTime fechapedido) { this.fechapedido = fechapedido; }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public double getPreciototal() {
        return preciototal;
    }

    public void setPreciototal(double preciototal) {
        this.preciototal = preciototal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getTiempoentrega() {
        return tiempoentrega;
    }

    public void setTiempoentrega(int tiempoentrega) {
        this.tiempoentrega = tiempoentrega;
    }

    public boolean isMismodistrito() {
        return mismodistrito;
    }

    public void setMismodistrito(boolean mismodistrito) {
        this.mismodistrito = mismodistrito;
    }

    public Valoracion getValoracion() {
        return valoracion;
    }

    public void setValoracion(Valoracion valoracion) {
        this.valoracion = valoracion;
    }
}
