package com.example.demo.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "categoriaextra")
public class CategoriaExtra  implements Serializable {

    @Id
    @Column(nullable = false)
    private int idcategoriaextra;
    @Column(nullable = false)
    private String tipo;

   @ManyToMany(mappedBy = "categoriaExtraList")
    private List<Plato> platos;


    public int getIdcategoriaextra() {
        return idcategoriaextra;
    }

    public void setIdcategoriaextra(int idcategoriaextra) {
        this.idcategoriaextra = idcategoriaextra;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
