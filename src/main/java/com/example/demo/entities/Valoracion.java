package com.example.demo.entities;

import com.fasterxml.jackson.datatype.jsr310.deser.JSR310DateTimeDeserializerBase;

import javax.persistence.*;

@Entity
@Table(name = "valoracion")
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int idvaloracion;

    @Column(name = "valoracionRestaurante")
    private int valoracionrestaurante;

    @Column(name = "comentarioRestaurante")
    private String comentariorestaurante;

    @Column(name = "valoracionRepartidor")
    private int valoracionrepartidor;

    @Column(name = "comentarioRestaurante")
    private String comentariorepartidor;

    public int getIdvaloracion() {
        return idvaloracion;
    }

    public void setIdvaloracion(int idvaloracion) {
        this.idvaloracion = idvaloracion;
    }

    public int getValoracionrestaurante() {
        return valoracionrestaurante;
    }

    public void setValoracionrestaurante(int valoracionrestaurante) {
        this.valoracionrestaurante = valoracionrestaurante;
    }

    public String getComentariorestaurante() {
        return comentariorestaurante;
    }

    public void setComentariorestaurante(String comentariorestaurante) {
        this.comentariorestaurante = comentariorestaurante;
    }

    public int getValoracionrepartidor() {
        return valoracionrepartidor;
    }

    public void setValoracionrepartidor(int valoracionrepartidor) {
        this.valoracionrepartidor = valoracionrepartidor;
    }

    public String getComentariorepartidor() {
        return comentariorepartidor;
    }

    public void setComentariorepartidor(String comentariorepartidor) {
        this.comentariorepartidor = comentariorepartidor;
    }
}
