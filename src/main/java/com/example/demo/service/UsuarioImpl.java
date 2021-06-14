package com.example.demo.service;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.entities.Usuario;

    @Service
    public class UsuarioImpl implements UsuarioServiceAPI {

        @Autowired
        private UsuarioRepository usuarioRepository;

        /*@Override
        public Page<Usuario> listaUsuarios(Pageable pageable) {
            return this.usuarioRepository.listaUsuarios(pageable);
        }*/


        @Override
        public Page<Usuario> listaUsuarios(String texto, Integer inFol, Integer maXrol, Integer miFestado, Integer maXestado, Pageable pageable) {
            return usuarioRepository.listaUsuarios(texto, inFol, maXrol, miFestado, maXestado, pageable);
        }

    }



