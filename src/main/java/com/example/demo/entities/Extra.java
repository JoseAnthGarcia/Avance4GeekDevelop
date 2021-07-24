package com.example.demo.entities;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "extra")
public class Extra  implements Serializable {

    public int getIdextra() {
        return idextra;
    }

    public void setIdextra(int idextra) {
        this.idextra = idextra;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idextra;

    @Column(nullable = false, unique = true)
    @Size(max = 45, message = "Ingrese como máximo 45 caractéres")
    @NotBlank(message = "El nombre del extra no puede estar vacío")
    private String nombre;

    //Considerando un precio máximo de extra de 200 - superior a esto se considera String <- Se puede cambiar
    @Column(nullable = false, name = "preciounitario")
    @Digits(integer = 1000, fraction = 1, message = "Ingresar un precio válido")
    @Max(value = 50 , message = "El precio máximo es de 50 soles")
    @Min(value = 1, message = "El precio mínimo es de 1 sol")
    @NotNull(message = "Este campo no puede estar vacío")
    private double preciounitario;

    private int idrestaurante;

    @Column(nullable = false)
    private boolean disponible;

    private int idcategoriaextra;

    //@ManyToOne
    //@JoinColumn(name="idcategoriaextra", nullable = false)
    //private CategoriaExtra categoriaExtra;

    //FOTI
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
//FIN FOTI

    public int getIdrestaurante() {
        return idrestaurante;
    }

    public void setIdrestaurante(int idrestaurante) {
        this.idrestaurante = idrestaurante;
    }

    public int getIdcategoriaextra() { return idcategoriaextra; }

    public void setIdcategoriaextra(int idcategoriaextra) { this.idcategoriaextra = idcategoriaextra; }

    public double getPreciounitario() {
        return preciounitario;
    }

    public void setPreciounitario(double preciounitario) {
        this.preciounitario = preciounitario;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


}
