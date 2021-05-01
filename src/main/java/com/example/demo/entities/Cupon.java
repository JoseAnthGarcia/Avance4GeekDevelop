package com.example.demo.entities;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.util.Date;

@Entity
@Table(name = "cupon")
public class Cupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idcupon;
    
    @Column(nullable = false, unique = true)
    @NotBlank
    @Size(max = 8, message = "Ingrese como máximo 8 caractéres")
    private String nombre;

    @Column(nullable = false)
    @NotBlank
    private int descuento;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 45, message = "Ingrese como máximo 45 caractéres")
    private String descripcion;

    @Column(nullable = false)
    private String fechainicio;

    @Column(nullable = false)
    private String fechafin;

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

    public String getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(String fechainicio) {
        this.fechainicio = fechainicio;
    }

    public String getFechafin() {
        return fechafin;
    }

    public void setFechafin(String fechafin) {
        this.fechafin = fechafin;
    }
}
