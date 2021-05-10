package com.example.demo.entities;

import javax.persistence.*;

@Entity
@Table(name = "usuario_has_distrito")
public class Usuario_has_distrito {

    @EmbeddedId
    Usuario_has_distritoKey id;

    @ManyToOne
    @MapsId("idusuario")
    @JoinColumn(name = "idusuario")
    private Usuario usuario;

    @ManyToOne
    @MapsId("iddistrito")
    @JoinColumn(name = "iddistrito")
    private Distrito distrito;

    @Column(nullable = false)
    private String direccion;
    @Column(nullable = false)
    private String coordenadas;

    public Usuario_has_distritoKey getId() {
        return id;
    }

    public void setId(Usuario_has_distritoKey id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Distrito getDistrito() {
        return distrito;
    }

    public void setDistrito(Distrito distrito) {
        this.distrito = distrito;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }
}
