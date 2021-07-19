package com.example.demo.service;

import com.example.demo.dtos.UsuarioDtoReporteIngresos;
import com.example.demo.dtos.UsuarioDtoReporteVentas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioServiceAPIDtoReporteIngresos {

    Page<UsuarioDtoReporteIngresos> listaUsuariosDtoReporteIngresos(String texto, Integer miFestado2, Integer maXestado2, Integer miFestado, Integer maXestado, Integer inFmont, Integer maXmont, Integer minDistrito, Integer maxDistrito, Pageable pageable);

}
