package com.example.demo.controller;


import com.example.demo.entities.Usuario;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.example.demo.repositories.DistritosRepository;
import com.example.demo.repositories.RolRepository;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.repositories.UbicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller

@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    UsuarioRepository clienteRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    DistritosRepository distritosRepository;

    @Autowired
    UbicacionRepository ubicacionRepository;



    @GetMapping("/editarPerfil")
    public String editarPerfil(HttpSession httpSession, Model model) {

        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        model.addAttribute("usuario", usuario);

        return "Cliente/editarPerfil";

    }
    @PostMapping("/guardarEditar")
    public String guardarEdicion(@ModelAttribute("usuario") @Valid Usuario usuario , BindingResult bindingResult, HttpSession httpSession
                    , Model model) {

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");
        boolean valContra = true;
        if (BCrypt.checkpw(usuario.getContrasenia(), usuario1.getContrasenia())) {
            valContra = false;
        }

        if (valContra || bindingResult.hasErrors()) {
            System.out.println("ENTRO AEA");
            if(valContra){
            model.addAttribute("msg", "Contraseña incorrecta");
            }
            return "Cliente/editarPerfil";

        } else {
            usuario1.setTelefono(usuario.getTelefono()); //usar save para actualizar
            clienteRepository.save(usuario1);
            return "Cliente/listaRestaurantes";
        }


    }

    @GetMapping("/listaRestaurantes")
    public String listaRestaurantes(){

        return "Cliente/listaRestaurantes";
    }




}
