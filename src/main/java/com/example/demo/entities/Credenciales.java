package com.example.demo.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table
public class Credenciales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idcredenciales;

    @Pattern(regexp = "[^@]+@[^\\.]+\\..+",message = "Debe tener el formato nombre@correo.com")
    private String correo;
    @NotBlank(message = "Complete sus datos")
    @Size(min=8, message = "Ingrese 8 caracteres")
    private String contrasenia;

    public int getIdcredenciales() {
        return idcredenciales;
    }

    public void setIdcredenciales(int idcredenciales) {
        this.idcredenciales = idcredenciales;
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
