package com.example.demo.entities;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "extra")
public class Extra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idextra;

    @Column(nullable = false, unique = true)
    @Size(max = 45, message = "Ingrese como máximo 45 caractéres")
    @NotBlank(message = "El nombre del extra no puede estar vacío")
    private String nombre;


    //TODO agregar a la BD
    @Column(nullable = false)
    @Size(max = 256, message = "Ingrese como máximo 45 caractéres")
    @NotBlank(message = "El nombre del extra no puede estar vacío")
    private String descripcion;

    //@ManyToOne
    //@JoinColumn(name = "idcategoriaExtra")
   // private CategoriaExtra categoriaExtra;

    //Considerando un precio máximo de extra de 200 - superior a esto se considera String <- Se puede cambiar
    @Column(nullable = false, name = "precioUnitario")
    @Digits(integer = 200, fraction = 0, message = "Tiene que ingresar un entero")
    @Max(value = 19 , message = "No puede ingresar más de 50 soles")
    @Min(value = 2, message = "No puede ingresar menos de 1 sol")
    @NotNull(message = "Ingrese un número entero")
    private int preciounitario;

    @Column(nullable = false)
    private boolean disponible;

   // @ManyToOne
   // @JoinColumn(name="idcategoria_extra")
    //private CategoriaExtra categorias;



    public int getIdextra() {
        return idextra;
    }

    public void setIdextra(int idextra) {
        this.idextra = idextra;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

  //  public CategoriaExtra getCategoriaExtra() {
  //      return categoriaExtra;
   // }

    //public void setCategoriaExtra(CategoriaExtra categoriaExtra) {
     //   this.categoriaExtra = categoriaExtra;
   // }

    public int getPreciounitario() {
        return preciounitario;
    }

    public void setPreciounitario(int preciounitario) {
        this.preciounitario = preciounitario;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
