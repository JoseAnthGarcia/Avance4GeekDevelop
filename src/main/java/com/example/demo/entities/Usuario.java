package com.example.demo.entities;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name="usuario")
public class Usuario {
//tu misma eres mela :3
    //nel mano
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idusuario;

    @Column(nullable = false)
    private String rol;

    private String estado;

    @Column(nullable = false)
    @Pattern(regexp = "[a-zA-Z ]{2,254}",message = "solo letras")
    @NotBlank(message = "Complete sus datos")
    private String nombres;

    @Column(nullable = false)
    @NotBlank(message = "Complete sus datos")
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
    private String fechanacimiento;

    private byte[] foto;
    @Column(nullable = false)
    private String fecharegistro;
    private String fechaadmitido;
    private String ultimoingreso;

    @OneToOne
    @JoinColumn(name = "movilidad")
    private Movilidad movilidad;

    @ManyToOne
    @JoinColumn(name="idusuario")
    private



    //borrar
    @OneToMany(mappedBy = "cliente")
    private List<Pedido> listaPedidosPorUsuario;

    public List<Pedido> getListaPedidosPorUsuario() { return listaPedidosPorUsuario; }

    public void setListaPedidosPorUsuario(List<Pedido> listaPedidosPorUsuario) { this.listaPedidosPorUsuario = listaPedidosPorUsuario; }


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

    public String getFechanacimiento() {
        return fechanacimiento;
    }

    public void setFechanacimiento(String fechanacimiento) {
        this.fechanacimiento = fechanacimiento;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public String getFecharegistro() {
        return fecharegistro;
    }

    public void setFecharegistro(String fecharegistro) {
        this.fecharegistro = fecharegistro;
    }

    public String getFechaadmitido() {
        return fechaadmitido;
    }

    public void setFechaadmitido(String fechaadmitido) {
        this.fechaadmitido = fechaadmitido;
    }

    public String getUltimoingreso() {
        return ultimoingreso;
    }

    public void setUltimoingreso(String ultimoingreso) {
        this.ultimoingreso = ultimoingreso;
    }

    public List<Direccion> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(List<Direccion> direcciones) {
        this.direcciones = direcciones;
    }

    public Movilidad getMovilidad() {
        return movilidad;
    }

    public void setMovilidad(Movilidad movilidad) {
        this.movilidad = movilidad;
    }
}
