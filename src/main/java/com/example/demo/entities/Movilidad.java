package com.example.demo.entities;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Entity
@Table(name="movilidad")
public class Movilidad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idmovilidad;
    //@Pattern(regexp = "^[A-Z]{3}-[1-9]{3}|^[A-Z]{1}[1-9]{1}[A-Z]{1}-[1-9]{3}|^[1-9]{4}-[A-Z,1-9]{2}|^[A-Z]{2}-[1-9]{3}$",message = "Ingrese una placa válida")
    private String placa;
    private String licencia;

    @OneToOne
    @JoinColumn(name = "idtipomovilidad", nullable = false)
    private TipoMovilidad tipoMovilidad;

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

}
