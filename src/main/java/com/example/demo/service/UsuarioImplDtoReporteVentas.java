package com.example.demo.service;


import com.example.demo.dtos.UsuarioDtoRepartidor;
import com.example.demo.dtos.UsuarioDtoReporteVentas;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UsuarioImplDtoReporteVentas implements UsuarioServiceAPIDtoReporteVentas{

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public Page<UsuarioDtoReporteVentas> listaUsuariosDtoReporteVentas(String texto, Integer miFval, Integer maXval, Integer miFestado, Integer maXestado, Integer inFmont, Integer maXmont, Pageable pageable) {
        return usuarioRepository.listaUsuariosDtoReporteVentas(texto,miFval,maXval, miFestado, maXestado,inFmont, maXmont, pageable);
    }
}
