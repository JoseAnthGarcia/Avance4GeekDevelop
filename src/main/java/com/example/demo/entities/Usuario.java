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

    @Column(unique = true)
    @NotBlank(message = "Complete sus datos")
    //@Pattern(regexp = "[0-9]{8}",message = "Ingrese 8 dígitos")
    private String dni;

    @Column(nullable = false)
    //@Pattern(regexp = "[a-zA-Z ]{2,254}",message = "solo letras")
    @NotBlank(message = "Complete sus datos")
    private String nombres;

    @Column(nullable = false)
    @NotBlank(message = "Complete sus datos")
    //@Pattern(regexp = "[a-zA-Z ]{2,254}",message = "solo letras")
    private String apellidos;

    @Column(nullable = false)
    private String sexo;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Complete sus datos")
    //@Pattern(regexp = "[0-9]{9}",message = "Ingrese  dígitos")
    private String telefono;

    private int estado;

    @Column(nullable = false)
    private String fecharegistro;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String fechanacimiento;

    private byte[] foto;

    private String fechaadmitido;

    private String ultimoingreso;

    @ManyToOne
    @JoinColumn(name = "idrol", nullable = false)
    private Rol rol;

    //---credenciales-----
    @Column(nullable = false)
    private String correo;

    @Column(nullable = false)
    private String contrasenia;
    //-------

    @ManyToMany(mappedBy = "usuariosDistrito")
    private List<Distrito> distritos;

    @OneToOne
    @JoinColumn(name = "idmovilidad")
    private Movilidad movilidad;

    @OneToOne(mappedBy = "administrador")
    private Restaurante restaurante;

    /*@OneToOne
    @JoinColumn(name = "credencial", nullable = false)
    private Credenciales credencial;*/

    //private String addresselegido;


    public Restaurante getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(Restaurante restaurante) {
        this.restaurante = restaurante;
    }

    @OneToMany(mappedBy = "cliente")
    private List<Pedido> listaPedidosPorUsuario;

    @OneToMany(mappedBy = "repartidor")
    private List<Pedido> listaPedidosPorRepartidor;


    public List<Pedido> getListaPedidosPorUsuario() { return listaPedidosPorUsuario; }

    public void setListaPedidosPorUsuario(List<Pedido> listaPedidosPorUsuario) { this.listaPedidosPorUsuario = listaPedidosPorUsuario; }

    public List<Pedido> getListaPedidosPorRepartidor() {
        return listaPedidosPorRepartidor;
    }

    public void setListaPedidosPorRepartidor(List<Pedido> listaPedidosPorRepartidor) {
        this.listaPedidosPorRepartidor = listaPedidosPorRepartidor;
    }

    /*public String getAddresselegido() {
        return addresselegido;
    }

    public void setAddresselegido(String addresselegido) {
        this.addresselegido = addresselegido;
    }*/
    /*
    public Credenciales getCredencial() { return credencial; }

    public void setCredencial(Credenciales credenciales) { this.credencial = credenciales; }*/

    public int getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(int idusuario) {
        this.idusuario = idusuario;
    }



    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
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

    public Movilidad getMovilidad() {
        return movilidad;
    }

    public void setMovilidad(Movilidad movilidad) {
        this.movilidad = movilidad;
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

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public List<Distrito> getDistritos() {
        return distritos;
    }

    public void setDistritos(List<Distrito> distritos) {
        this.distritos = distritos;
    }
}
