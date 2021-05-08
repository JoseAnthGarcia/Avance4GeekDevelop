package com.example.demo.entities;


import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Usuario_has_distritoKey implements Serializable {

    private int idusuario;
    private int iddistrito;

}
