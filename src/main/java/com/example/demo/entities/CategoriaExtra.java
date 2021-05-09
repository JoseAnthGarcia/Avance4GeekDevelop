package com.example.demo.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "categoriaextra")
public class CategoriaExtra {
    @Id
    private int idcategoriaextra;
    private String tipo;

    @ManyToMany(mappedBy = "categoriaExtraList")
    private List<Plato> extraslist;

    public List<Plato> getExtraslist() {
        return extraslist;
    }

    public void setExtraslist(List<Plato> extraslist) {
        this.extraslist = extraslist;
    }

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
