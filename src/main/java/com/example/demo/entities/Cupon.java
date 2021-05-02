package com.example.demo.entities;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "cupon")
public class Cupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idcupon;
    
    @Column(nullable = false, unique = true)
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 8, message = "Ingrese como máximo 8 caractéres")
    private String nombre;

    @Column(nullable = false)
  //  @NotBlank(message = "El descuento no puede estar vacío")
    private int descuento;

    @Column(nullable = false, name = "descripcion")
    @NotBlank(message = "La política no puede estar vacío")
    @Size(max = 45, message = "Ingrese como máximo 45 caractéres")
    private String politica;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechainicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate fechafin;
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

    public void setDescuento(int descuento) { this.descuento = descuento; }

    public void setDescripcion(String descripcion) {
        this.politica = descripcion;
    }

    public int getIdrestaurante() {
        return idrestaurante;
    }

    public void setIdrestaurante(int idrestaurante) {
        this.idrestaurante = idrestaurante;
    }

    public String getPolitica() {
        return politica;
    }

    public void setPolitica(String politica) {
        this.politica = politica;
    }

    public LocalDate getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(LocalDate fechainicio) {
        this.fechainicio = fechainicio;
    }

    public LocalDate getFechafin() {
        return fechafin;
    }

    public void setFechafin(LocalDate fechafin) {
        this.fechafin = fechafin;
    }
}
