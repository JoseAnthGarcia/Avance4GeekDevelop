package com.example.demo.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "distrito")
public class Distrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int iddistrito;
    @Column(nullable = false)
    private String nombre;

    @ManyToMany
    @JoinTable(name="usuario_has_distrito",
            joinColumns = @JoinColumn(name="iddistrito"),
            inverseJoinColumns = @JoinColumn(name="idusuario"))
    private List<Usuario> usuariosDistrito;

    public int getIddistrito() {
        return iddistrito;
    }

    public void setIddistrito(int iddistrito) {
        this.iddistrito = iddistrito;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Usuario> getUsuariosDistrito() {
        return usuariosDistrito;
    }

    public void setUsuariosDistrito(List<Usuario> usuariosDistrito) {
        this.usuariosDistrito = usuariosDistrito;
    }
}
