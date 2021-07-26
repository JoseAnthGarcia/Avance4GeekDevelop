package com.example.demo.service;
import com.example.demo.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UsuarioServiceAPI {

    //Page<Usuario> listaUsuarios(Pageable pageable);
    Page<Usuario> listaUsuarios(String texto, Integer inFrol, Integer maXrol, Integer miFestado, Integer maXestado, Pageable pageable);

}