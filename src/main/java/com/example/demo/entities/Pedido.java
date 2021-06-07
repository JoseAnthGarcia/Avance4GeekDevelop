package com.example.demo.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Formatter;

@Entity
@Table(name = "pedido")
public class Pedido implements Serializable {

    @Id
    @Column(nullable = false, unique = true)
    private String codigo;

    //TODO SOLO MAPEARÉ LO NECESARIO PARA QUE FUNCIONE LA VISTA DE ADMIN
    @ManyToOne
    @JoinColumn(name = "idcliente", nullable = false)
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name="idcupon")
    private Cupon cupon;

    @ManyToOne
    @JoinColumn(name = "idmetodopago", nullable = false)
    private MetodoDePago metodopago;

    @Column(name = "preciototal", nullable = false)
    private double preciototal;

    //TODO definir - lo defini como int (JOSE)
    private int estado;
    private int tiempoentrega;
    private boolean mismodistrito;
    //private LocalDateTime fechapedido;

    private String fechapedido;

    private String comentariorestaurante;

    private Integer valoracionrestaurante;

    private String comentariorepartidor;
    private String comentrechazorest;

    private Integer valoracionrepartidor;


    @ManyToOne
    @JoinColumn(name = "idrepartidor")
    private Usuario repartidor;

    @ManyToOne
    @JoinColumn(name = "idrestaurante")
    private Restaurante restaurante;

    @ManyToOne
    @JoinColumn(name = "idubicacion", nullable = false)
    private Ubicacion ubicacion;

    @Column(nullable = true)
    private Float cantidadapagar;

    public Float getCantidadapagar() {
        return cantidadapagar;
    }

    public void setCantidadapagar(Float cantidadapagar) {
        this.cantidadapagar = cantidadapagar;
    }

    public Cupon getCupon() {
        return cupon;
    }

    public void setCupon(Cupon cupon) {
        this.cupon = cupon;
    }


    public String getComentariorestaurante() {
        return comentariorestaurante;
    }

    public void setComentariorestaurante(String comentariorestaurante) {
        this.comentariorestaurante = comentariorestaurante;
    }

    public MetodoDePago getMetodopago() {
        return metodopago;
    }

    public void setMetodopago(MetodoDePago metodopago) {
        this.metodopago = metodopago;
    }

    public Integer getValoracionrestaurante() {
        return valoracionrestaurante;
    }

    public void setValoracionrestaurante(Integer valoracionrestaurante) {
        this.valoracionrestaurante = valoracionrestaurante;
    }

    public String getComentariorepartidor() {
        return comentariorepartidor;
    }

    public void setComentariorepartidor(String comentariorepartidor) {
        this.comentariorepartidor = comentariorepartidor;
    }

    public Integer getValoracionrepartidor() {
        return valoracionrepartidor;
    }

    public void setValoracionrepartidor(Integer valoracionrepartidor) {
        this.valoracionrepartidor = valoracionrepartidor;
    }

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

    /*public LocalDateTime getFechapedido() { return fechapedido; }

    public void setFechapedido(LocalDateTime fechapedido) { this.fechapedido = fechapedido; }*/

    public String getFechapedido() {
        return fechapedido;
    }

    public void setFechapedido(String fechapedido) {
        this.fechapedido = fechapedido;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

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

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
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


    public String getComentrechazorest() {
        return comentrechazorest;
    }

    public void setComentrechazorest(String comentrechazorest) {
        this.comentrechazorest = comentrechazorest;
    }
}
