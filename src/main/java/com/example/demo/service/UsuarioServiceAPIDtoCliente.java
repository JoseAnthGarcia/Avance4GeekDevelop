package com.example.demo.service;
import com.example.demo.dtos.UsuarioDtoCliente;
import com.example.demo.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UsuarioServiceAPIDtoCliente {

    //Page<Usuario> listaUsuarios(Pageable pageable);
    Page<UsuarioDtoCliente> listaUsuariosDtoCliente(String texto, Integer miFval, Integer maXval, Integer inFmont, Integer maXmont, Integer miFestado, Integer maXestado, Pageable pageable);
}