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
    @Digits(integer = 2, fraction = 0, message = "Tiene que ingresar un entero")
    @Max(value = 50 , message = "No puede ingresar más de 50 soles")
    @Min(value = 1, message = "No puede ingresar menos de 1 sol")
    @NotNull(message = "Ingrese un número entero")
    private int descuento;

    @Column(nullable = false, name = "descripcion")
    @NotBlank(message = "La política no puede estar vacío")
    @Size(max = 45, message = "Ingrese como máximo 45 caractéres")
    private String politica;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechainicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Ingrese una fecha")
    @Future(message = "La fecha de caducidad tiene que ser mayor a la fecha actual")
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
