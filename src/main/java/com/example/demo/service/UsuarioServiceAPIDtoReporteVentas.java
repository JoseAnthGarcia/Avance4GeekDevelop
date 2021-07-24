package com.example.demo.service;

import com.example.demo.dtos.UsuarioDtoReporteVentas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioServiceAPIDtoReporteVentas {

    Page<UsuarioDtoReporteVentas> listaUsuariosDtoReporteVentas(String texto, Integer miFval, Integer maXval, Integer miFestado, Integer maXestado, Integer inFmont, Integer maXmont, Pageable pageable);

}
