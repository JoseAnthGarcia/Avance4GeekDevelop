package com.example.demo.entities;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name="plato")
public class Plato implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idplato")
    private int idplato;
    @Column(name="nombre", nullable = false)
    @NotBlank(message = "Este campo es obligatorio")
    @Size(max=40, message = "Maximo 40 caracteres")
    private String nombre;
    @Column(name="descripcion", nullable = false)
    @NotBlank(message = "Este campo es obligatorio")
    @Size(max=100, message = "Maximo 100 caracteres")
    private String descripcion;
    @Column(name="precio", nullable = false)
    @Positive(message = "Ingrese una cantidad positiva")
    @Digits(integer=10, fraction = 2, message = "Ingrese un precio valido")
    @NotNull(message = "Este campo es obligatorio")
    private double precio;
    @Column(name="idcategoriarestaurante", nullable = false)
    private int idcategoriaplato;
    @Column(name="idrestaurante", nullable = false)
    private int  idrestaurante;
    @Column(name="disponible", nullable = false)
    private boolean disponible;
    @ManyToMany
    @JoinTable(name = "categoriaextra_has_plato",
            joinColumns = @JoinColumn(name = "idplato"),
            inverseJoinColumns = @JoinColumn(name = "idcategoriaextra"))
    private List<CategoriaExtra> categoriaExtraList;

    //FOTO
    private String fotonombre;
    private String fotocontenttype;
    @Lob
    private byte[] foto;

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

    //FIN FOTO
    public List<CategoriaExtra> getCategoriaExtraList() {
        return categoriaExtraList;
    }

    public void setCategoriaExtraList(List<CategoriaExtra> categoriaExtraList) {
        this.categoriaExtraList = categoriaExtraList;
    }

    public int getIdplato() {
        return idplato;
    }

    public void setIdplato(int idplato) {
        this.idplato = idplato;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public int getIdcategoriaplato() {
        return idcategoriaplato;
    }

    public void setIdcategoriaplato(int idcategoriaplato) {
        this.idcategoriaplato = idcategoriaplato;
    }

    public int getIdrestaurante() {
        return idrestaurante;
    }

    public void setIdrestaurante(int idrestaurante) {
        this.idrestaurante = idrestaurante;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

}
