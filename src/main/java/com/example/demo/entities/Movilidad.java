package com.example.demo.entities;

import javax.persistence.*;

@Entity
public class Movilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idmovilidad;
    private String placa;
    private String licencia;
    @OneToOne
    @JoinColumn(name = "idtipomovilidad", nullable = false)
    private TipoMovilidad tipoMovilidad;
    @OneToOne
    @JoinColumn(name = "idrepartidor", nullable = false)
    private Usuario usuario;

    public int getIdmovilidad() {
        return idmovilidad;
    }

    public void setIdmovilidad(int idmovilidad) {
        this.idmovilidad = idmovilidad;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    public TipoMovilidad getTipoMovilidad() {
        return tipoMovilidad;
    }

    public void setTipoMovilidad(TipoMovilidad tipoMovilidad) {
        this.tipoMovilidad = tipoMovilidad;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
