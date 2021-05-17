package com.example.demo.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "restaurante")
public class Restaurante  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idrestaurante;


    @Column(nullable = false)
    @NotBlank(message = "Este campo es obligatorio")
    @Size(max=40, message = "Maximo 40 caracteres")
    private String nombre;

    private String coordenadas;
    @Pattern(regexp ="[0-9]{11},message = Ingrese 11 dígitos")
    @Column(nullable = false)
    private String ruc;
    @Pattern(regexp = "[0-9]{9},message = Ingrese 9 dígitos")
    @Column(nullable = false)
    private String telefono;

    @Column(nullable = false)
    @Pattern(regexp = "[a-zA-Z ]{2,254},message = Solo puede ingresar letras")
    private String direccion;

    private String fecharegistro;

    @OneToOne
    @JoinColumn(name = "idadministrador")
    private Usuario administrador;

    @OneToOne
    @JoinColumn(name="iddistrito", nullable = false)
    //@NotBlank(message = "Este campo es obligatorio")
    private Distrito distrito;

    @ManyToMany
    @JoinTable(name = "restaurante_has_categoriarestaurante",
            joinColumns = @JoinColumn(name = "idrestaurante"),
            inverseJoinColumns = @JoinColumn(name = "idcategoria"))
    private List<Categorias> categoriasRestaurante;


    private String fotonombre;
    private String fotocontenttype;
    @Lob
    private byte[] foto;
    private int estado;

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getFotonombre() {
        return fotonombre;
    }

    public void setFotonombre(String fotonombre) {
        this.fotonombre = fotonombre;
    }

    public String getFotocontenttype() {
        return fotocontenttype;
    }

    public void setFotocontenttype(String fotocontenttype) {
        this.fotocontenttype = fotocontenttype;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public Usuario getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Usuario administrador) {
        this.administrador = administrador;
    }

    public int getIdrestaurante() {
        return idrestaurante;
    }

    public void setIdrestaurante(int idrestaurante) {
        this.idrestaurante = idrestaurante;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Categorias> getCategoriasRestaurante() {
        return categoriasRestaurante;
    }

    public void setCategoriasRestaurante(List<Categorias> categoriasRestaurante) {
        this.categoriasRestaurante = categoriasRestaurante;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Distrito getDistrito() {
        return distrito;
    }

    public void setDistrito(Distrito distrito) {
        this.distrito = distrito;
    }

    public String getFecharegistro() {
        return fecharegistro;
    }

    public void setFecharegistro(String fecharegistro) {
        this.fecharegistro = fecharegistro;
    }
}
