package com.example.demo.controller;

import com.example.demo.entities.Usuario;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller

public class LoginController {

    @Autowired
    UsuarioRepository usuarioRepository;
    @GetMapping("/ClienteLogin")
    public String loginForm(){
        return "Cliente/login";
    }

    @GetMapping(value = "/redirectByRole")
    public String redirectByRole(Authentication auth, HttpSession session) {
        String rol = "";
        for (GrantedAuthority role : auth.getAuthorities()) {
            rol = role.getAuthority();
            break;
        }

        String correo = auth.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        System.out.println(usuario.getNombres());

        System.out.println(usuario.getApellidos());

        session.setAttribute("usuario",usuario);
        System.out.println(usuario);


        if (rol.equals("cliente")) {
            return "redirect:/cliente/listaRestaurantes";
        } else{
            return "redirect:/plato/";
        }
    }


}
