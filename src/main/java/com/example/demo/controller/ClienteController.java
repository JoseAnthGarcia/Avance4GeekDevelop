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
    public String guardarEdicion(@RequestParam("contraseniaConf") String contraseniaConf,
                                 @RequestParam("telefonoNuevo") String telefonoNuevo,
                                 HttpSession httpSession,
                                 Model model) {

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");
        boolean valContra = true;
        boolean telfValid = true;

        int telfInt;
        try{
            telfInt = Integer.parseInt(telefonoNuevo);
        }catch (NullPointerException e){
            telfInt = -1;
        }

        if(telfInt==-1 || telefonoNuevo.trim().equals("") || telefonoNuevo.length()!=9){
            telfValid =false;
        }

        if (BCrypt.checkpw(contraseniaConf,usuario1.getContrasenia())) {
            valContra = false;
        }

        if (valContra || !telfValid){
            System.out.println("ENTRO AEA");
            if(valContra){
            model.addAttribute("msg", "Contraseña incorrecta");
            }
            return "Cliente/editarPerfil";

        } else {
            usuario1.setTelefono(telefonoNuevo); //usar save para actualizar
            httpSession.setAttribute("usuario",usuario1); //TODO: preguntar profe
            clienteRepository.save(usuario1);
            return "Cliente/listaRestaurantes";
        }


    }

    @GetMapping("/listaRestaurantes")
    public String listaRestaurantes(){

        return "Cliente/listaRestaurantes";
    }




}
