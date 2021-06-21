package com.example.demo.service;

import com.example.demo.dtos.UsuarioDtoCliente;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UsuarioImplDtoCliente implements UsuarioServiceAPIDtoCliente {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override //texto,miFval,maXval, inFmont, maXmont,  miFestado,  maXestado,  pageRequest
    public Page<UsuarioDtoCliente> listaUsuariosDtoCliente(String texto, Integer miFval, Integer maXval, Integer inFmont, Integer maXmont, Integer miFestado, Integer maXestado, Pageable pageable) {
        return usuarioRepository.listaUsuariosDtoCliente(texto,miFval,maXval,inFmont, maXmont, miFestado, maXestado, pageable);
    }

}



