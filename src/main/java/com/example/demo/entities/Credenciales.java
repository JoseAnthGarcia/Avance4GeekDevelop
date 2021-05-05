package com.example.demo.entities;

import javax.persistence.*;

@Entity
@Table
public class Credenciales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idcredenciales;
    private int idUsuario;
    private String correo;
    private String contrasenia;

    public int getIdcredenciales() {
        return idcredenciales;
    }

    public void setIdcredenciales(int idcredenciales) {
        this.idcredenciales = idcredenciales;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }
}
