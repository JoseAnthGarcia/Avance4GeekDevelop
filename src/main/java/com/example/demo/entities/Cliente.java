package com.example.demo.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Entity
@Table(name="usuario")

public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idusuario;
    @Column(nullable = false)
    private String rol;
    private String estado;
    @Column(nullable = false)
    //@NotBlank(message = "Complete sus datos")
   // @Pattern(regexp = "[^@]+[^\\.]+\\..+",message = "Solo Ingrese letras")
    private String nombres;
    @Column(nullable = false)
    //@NotBlank(message = "Complete sus datos")
    private String apellidos;
    @Column(nullable = false)
    private String sexo;
    @Column(nullable = false, unique = true)
    //@NotBlank(message = "Complete sus datos")
    private String telefono;
    @Column(unique = true)
    //@NotBlank(message = "Complete sus datos")
    //@Pattern(regexp = "[^[a-zA-Z0-9.\\-\\/+=@_ ]*$]{8}",message = "Ingrese 8 dígitos")
    private String dni;

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
}
