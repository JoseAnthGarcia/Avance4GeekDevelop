package com.example.demo.entities;

import javax.persistence.*;

@Entity
@Table(name = "urlcorreo")
public class Urlcorreo {

    //cOMMET

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idurlcorreo;
    @Column(nullable = false)
    private String num;
    @OneToOne
    @JoinColumn(name = "idusuario", nullable = false)
    private Usuario usuario;
    @Column(nullable = false)
    private String fecha;

    public int getIdurlcorreo() {
        return idurlcorreo;
    }

    public void setIdurlcorreo(int idurlcorreo) {
        this.idurlcorreo = idurlcorreo;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}