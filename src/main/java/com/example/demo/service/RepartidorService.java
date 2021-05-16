package com.example.demo.service;

import com.example.demo.entities.Usuario;
import com.example.demo.repositories.RolRepository;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RepartidorService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RolRepository rolRepository;

    public Page<Usuario> repartidorPaginacion(int numeroPag, int tamPag){
        Pageable pageable = PageRequest.of(numeroPag - 1, tamPag);
        return usuarioRepository.findByEstadoAndRolOrderByFecharegistroAsc(2, rolRepository.findById(4).get(), pageable);
    }


    public Page<Usuario> repartidorPaginacionBusqueda1(int numeroPag, int tamPag, String nombreUsuario1, String apellidoUsuario1, int fechaRegistro1){
        Pageable pageable = PageRequest.of(numeroPag - 1, tamPag);
        return usuarioRepository.buscarRepartidoresSinMovilidad(nombreUsuario1,apellidoUsuario1, fechaRegistro1, pageable);
    }

    public Page<Usuario> repartidorPaginacionBusqueda2(int numeroPag, int tamPag, String nombreUsuario1, String apellidoUsuario1, int fechaRegistro1, int tipoMovilidad1){
        Pageable pageable = PageRequest.of(numeroPag - 1, tamPag);
        return usuarioRepository.buscarRepartidoresConMovilidad(nombreUsuario1,apellidoUsuario1, fechaRegistro1, tipoMovilidad1, pageable);
    }
}
