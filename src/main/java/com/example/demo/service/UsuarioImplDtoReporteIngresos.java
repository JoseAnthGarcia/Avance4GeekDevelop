package com.example.demo.service;


import com.example.demo.dtos.UsuarioDtoReporteIngresos;
import com.example.demo.dtos.UsuarioDtoReporteVentas;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UsuarioImplDtoReporteIngresos implements UsuarioServiceAPIDtoReporteIngresos{

    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    public Page<UsuarioDtoReporteIngresos> listaUsuariosDtoReporteIngresos(String texto, Integer miFestado2, Integer maXestado2, Integer cantidad, Integer maXestado, Integer inFmont, Integer maXmont, Integer minDistrito, Integer maxDistrito, Pageable pageable) {
        return usuarioRepository.listaUsuariosDtoReporteIngresos(texto,miFestado2,maXestado2, cantidad, maXestado,inFmont, maXmont,minDistrito,maxDistrito, pageable);
    }
}
