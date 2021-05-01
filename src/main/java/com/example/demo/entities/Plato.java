package com.example.demo.entities;
import javax.persistence.*;

@Entity
@Table(name="plato")
public class Plato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idplato")
    private int idplato;
    @Column(name="nombre", nullable = false)
    private String nombre;
    @Column(name="precio", nullable = false)
    private double precio;
    @Column(name="descripcion", nullable = false)
    private String descripcion;
    @Column(name="idcategoriaplato", nullable = false)
    private int idcategoriaplato;
    @Column(name="idrestaurante", nullable = false)
    private int  idrestaurante;
    @Column(name="disponible", nullable = false)
    private boolean disponible;


    public int getIdplato() {
        return idplato;
    }

    public void setIdplato(int idplato) {
        this.idplato = idplato;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public int getIdcategoriaplato() {
        return idcategoriaplato;
    }

    public void setIdcategoriaplato(int idcategoriaplato) {
        this.idcategoriaplato = idcategoriaplato;
    }

    public int getIdrestaurante() {
        return idrestaurante;
    }

    public void setIdrestaurante(int idrestaurante) {
        this.idrestaurante = idrestaurante;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
