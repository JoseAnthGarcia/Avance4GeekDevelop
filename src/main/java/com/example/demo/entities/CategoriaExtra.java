package com.example.demo.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "categoriaextra")
public class CategoriaExtra {

    @Id
    @Column(nullable = false)
    private int idcategoriaextra;
    @Column(nullable = false)
    private String tipo;

  /*  @ManyToMany(mappedBy = "categoriaExtraPorPlato")
    private List<Plato> platos;


    public int getIdcategoriaextra() {
        return idcategoriaextra;
    }

    public void setIdcategoriaextra(int idcategoriaextra) {
        this.idcategoriaextra = idcategoriaextra;
    }
*/
    @ManyToMany
    @JoinTable(name = "categoriaextra_has_plato",
    joinColumns = @JoinColumn(name = "idcategoria_extra"),
    inverseJoinColumns = @JoinColumn(name = "idplato"))
    private List<Plato> categoriaextra_has_plato;

    public int getIdcategoriaextra() {
        return idcategoriaextra;
    }

    public void setIdcategoriaextra(int idcategoriaextra) {
        this.idcategoriaextra = idcategoriaextra;
    }

    public List<Plato> getCategoriaextra_has_plato() {
        return categoriaextra_has_plato;
    }

    public void setCategoriaextra_has_plato(List<Plato> categoriaextra_has_plato) {
        this.categoriaextra_has_plato = categoriaextra_has_plato;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
