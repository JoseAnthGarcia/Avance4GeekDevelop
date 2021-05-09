package com.example.demo.entities;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "extra")
public class Extra {

    public int getIdextra() {
        return idextra;
    }

    public void setIdextra(int idextra) {
        this.idextra = idextra;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idextra;

    @Column(nullable = false, unique = true)
    @Size(max = 45, message = "Ingrese como máximo 45 caractéres")
    @NotBlank(message = "El nombre del extra no puede estar vacío")
    private String nombre;

    public double getPreciounitario() {
        return preciounitario;
    }

    public void setPreciounitario(double preciounitario) {
        this.preciounitario = preciounitario;
    }

    //Considerando un precio máximo de extra de 200 - superior a esto se considera String <- Se puede cambiar
    @Column(nullable = false, name = "preciounitario")
    @Digits(integer = 200, fraction = 0, message = "Tiene que ingresar un entero")
    @Max(value = 19 , message = "No puede ingresar más de 50 soles")
    @Min(value = 2, message = "No puede ingresar menos de 1 sol")
    @NotNull(message = "Ingrese un número entero")
    private double preciounitario;
    private int idrestaurante;
    private int idcategoriaextra;



    public int getIdrestaurante() {
        return idrestaurante;
    }

    public void setIdrestaurante(int idrestaurante) {
        this.idrestaurante = idrestaurante;
    }

    public int getIdcategoriaextra() { return idcategoriaextra; }

    public void setIdcategoriaextra(int idcategoriaextra) { this.idcategoriaextra = idcategoriaextra; }


    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Column(nullable = false)
    private boolean disponible;



    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


}
