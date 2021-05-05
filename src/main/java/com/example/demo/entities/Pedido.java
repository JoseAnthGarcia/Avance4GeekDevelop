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

    private int idrepartidor;
    private int idrestaurante;
    private int idcupon;
    private int idmetodopago;

    @Column(name = "precioTotal", nullable = false)
    private BigDecimal preciototal;

    //TODO definir
    private String estado;
    private int tiempoentrega;
    private boolean mismodistrito;
    private LocalDateTime fechapedido;

    @ManyToOne
    @JoinColumn(name = "idvaloracion")
    private Valoracion valoracion;

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

    public int getIdrepartidor() {
        return idrepartidor;
    }

    public void setIdrepartidor(int idrepartidor) {
        this.idrepartidor = idrepartidor;
    }

    public int getIdrestaurante() {
        return idrestaurante;
    }

    public void setIdrestaurante(int idrestaurante) {
        this.idrestaurante = idrestaurante;
    }

    public int getIdcupon() {
        return idcupon;
    }

    public void setIdcupon(int idcupon) {
        this.idcupon = idcupon;
    }

    public int getIdmetodopago() {
        return idmetodopago;
    }

    public void setIdmetodopago(int idmetodopago) {
        this.idmetodopago = idmetodopago;
    }

    public BigDecimal getPreciototal() {
        return preciototal;
    }

    public void setPreciototal(BigDecimal preciototal) {
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
