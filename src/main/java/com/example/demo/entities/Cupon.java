package com.example.demo.entities;

import javax.persistence.*;

import java.util.Date;

@Entity
@Table(name = "cupon")
public class Cupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idcupon;
    @Column(nullable = false)
    @NotBlank
    @Size(max = 45, message = "El nombre no puede tener más de 45 caracteres")
    private String nombre;
    @Column(nullable = false)
    @Positive
    @Max(value = 90)
    @Min(value = 0)
    private int descuento;
    @Column(nullable = false)
    @NotBlank
    @Size(max = 300, message = "La descripción no puede tener más de 300 caracteres")
    private String descripcion;

    @Column(nullable = false)
    private Date fechainicio;
    @Column(nullable = false)
    private Date fechafin;
    @Column(nullable = false)
    private int idrestaurante;

    public int getIdcupon() {
        return idcupon;
    }

    public void setIdcupon(int idcupon) {
        this.idcupon = idcupon;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDescuento() {
        return descuento;
    }

    public void setDescuento(int descuento) {
        this.descuento = descuento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getIdrestaurante() {
        return idrestaurante;
    }

    public void setIdrestaurante(int idrestaurante) {
        this.idrestaurante = idrestaurante;
    }

    public Date getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(Date fechainicio) {
        this.fechainicio = fechainicio;
    }

    public Date getFechafin() {
        return fechafin;
    }

    public void setFechafin(Date fechafin) {
        this.fechafin = fechafin;
    }
}
