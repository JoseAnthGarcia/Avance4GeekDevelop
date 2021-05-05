package com.example.demo.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idusuario;
    @Column(nullable = false)
    private String rol;
    private String estado;
    @Column(nullable = false)
    private String nombres;
    @Column(nullable = false)
    private String apellidos;
    @Column(nullable = false)
    private String sexo;
    @Column(nullable = false, unique = true)
    private String telefono;
    @Column(unique = true)
    private String dni;
    @Column(nullable = false)
    private String fechanacimiento;
    private byte[] foto;
    @Column(nullable = false)
    private String fecharegistro;
    private String fechaadmitido;
    private String ultimoingreso;

 /*   @OneToOne(mappedBy = "credenciales")
    private Credenciales credenciales;
*/
    @ManyToMany(mappedBy = "usuarioPorDireccion")
    private List<Direccion> direcciones;

    @OneToMany(mappedBy = "cliente")
    private List<Pedido> listaPedidosPorUsuario;

    public List<Pedido> getListaPedidosPorUsuario() { return listaPedidosPorUsuario; }

    public void setListaPedidosPorUsuario(List<Pedido> listaPedidosPorUsuario) { this.listaPedidosPorUsuario = listaPedidosPorUsuario; }

 /*   public Credenciales getCredenciales() { return credenciales; }

    public void setCredenciales(Credenciales credenciales) { this.credenciales = credenciales; }
*/
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
}
