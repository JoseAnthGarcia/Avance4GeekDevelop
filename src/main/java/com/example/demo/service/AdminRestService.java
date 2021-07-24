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
public class AdminRestService {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RolRepository rolRepository;
    public Page<Usuario> adminRestPaginacion(int numeroPag, int tamPag){
        Pageable pageable = PageRequest.of(numeroPag - 1, tamPag);
        return usuarioRepository.findByEstadoAndRolOrderByFecharegistroAsc(2, rolRepository.findById(3).get(), pageable);
    }
    public Page<Usuario> administradorRestBusqueda(int numeroPag, int tamPag, String nombreUsuario1, String apellidoUsuario1, String dni, int fechaRegistro1){
        Pageable pageable = PageRequest.of(numeroPag - 1, tamPag);
        return usuarioRepository.buscarAdministradorR(nombreUsuario1,apellidoUsuario1, dni, fechaRegistro1, pageable);
    }
}
