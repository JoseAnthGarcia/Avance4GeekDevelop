package com.example.demo.entities;


import javax.persistence.*;

@Entity
@Table(name = "validarcorreo")
public class Validarcorreo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idvalidarcorreo;
    @OneToOne
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario usuario;
    @Column(nullable = false, name="hash")
    private String hash;
    @Column(name="fecha")
    private String fecha;

    public int getIdvalidarcorreo() {
        return idvalidarcorreo;
    }

    public void setIdvalidarcorreo(int idvalidarcorreo) {
        this.idvalidarcorreo = idvalidarcorreo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
