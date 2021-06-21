package com.example.demo.service;


import com.example.demo.dtos.UsuarioDtoRepartidor;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UsuarioImplDtoRepartidor implements UsuarioServiceAPIDtoRepartidor{

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public Page<UsuarioDtoRepartidor> listaUsuariosDtoRepartidor(String texto, Integer miFval, Integer maXval, Integer miFestado, Integer maXestado, Integer inFmont, Integer maXmont, Pageable pageable) {
        return usuarioRepository.listaUsuariosDtoRepartidor(texto,miFval,maXval, miFestado, maXestado,inFmont, maXmont, pageable);
    }
}
