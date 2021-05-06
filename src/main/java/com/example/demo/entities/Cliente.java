package com.example.demo.entities;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Entity
@Table(name="usuario")

public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idusuario;
    @Column(nullable = false)
    private String rol;

    @Column(nullable = false)
    //@NotBlank(message = "Complete sus datos")
    @Pattern(regexp = "[a-zA-Z ]{2,254}",message = "solo letras")
    private String nombres;
    @Column(nullable = false)
    //@NotBlank(message = "Complete sus datos")
    @Pattern(regexp = "[a-zA-Z ]{2,254}",message = "solo letras")
    private String apellidos;
    @Column(nullable = false)
    private String sexo;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Complete sus datos")
    @Pattern(regexp = "[0-9]{9}",message = "Ingrese  dígitos")
    private String telefono;

    @Column(unique = true)
    @NotBlank(message = "Complete sus datos")
    @Pattern(regexp = "[0-9]{8}",message = "Ingrese 8 dígitos")
    private String dni;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechanacimiento;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecharegistro;


    @OneToOne
    @JoinColumn(name = "credencial", nullable = false)
    private Credenciales credencial;

    public Credenciales getCredencial() {
        return credencial;
    }

    public void setCredencial(Credenciales credencial) {
        this.credencial = credencial;
    }

    public LocalDate getFecharegistro() {
        return fecharegistro;
    }

    public void setFecharegistro(LocalDate fecharegistro) {
        this.fecharegistro = fecharegistro;
    }

    public LocalDate getFechanacimiento() {
        return fechanacimiento;
    }

    public void setFechanacimiento(LocalDate fechanacimiento) {
        this.fechanacimiento = fechanacimiento;
    }

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
