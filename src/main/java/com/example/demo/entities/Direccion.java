package com.example.demo.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "direccion")
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int iddireccion;
    private String ubicacion;
    private String coordenadas;

    @ManyToOne
    @JoinColumn(name = "iddistrito")
    private Distrito distrito;

    @ManyToMany
    @JoinTable(name = "direccion_has_usuario",
                joinColumns = @JoinColumn(name = "iddireccion"),
                inverseJoinColumns = @JoinColumn(name = "idusuario"))
    private List<Usuario> usuarioPorDireccion;

    public int getIddireccion() {
        return iddireccion;
    }

    public void setIddireccion(int iddireccion) {
        this.iddireccion = iddireccion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public Distrito getDistrito() {
        return distrito;
    }

    public void setDistrito(Distrito distrito) {
        this.distrito = distrito;
    }

    public List<Usuario> getUsuarioPorDireccion() {
        return usuarioPorDireccion;
    }

    public void setUsuarioPorDireccion(List<Usuario> usuarioPorDireccion) {
        this.usuarioPorDireccion = usuarioPorDireccion;
    }
}
