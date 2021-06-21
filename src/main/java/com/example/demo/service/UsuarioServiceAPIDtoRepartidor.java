package com.example.demo.service;

import com.example.demo.dtos.UsuarioDtoCliente;
import com.example.demo.dtos.UsuarioDtoRepartidor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioServiceAPIDtoRepartidor {

    Page<UsuarioDtoRepartidor> listaUsuariosDtoRepartidor(String texto, Integer miFval, Integer maXval, Integer miFestado, Integer maXestado, Integer inFmont, Integer maXmont, Pageable pageable);

}
