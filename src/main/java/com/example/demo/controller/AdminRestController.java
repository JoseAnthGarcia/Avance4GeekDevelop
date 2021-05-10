package com.example.demo.controller;

import com.example.demo.entities.*;
import com.example.demo.repositories.DistritosRepository;
import com.example.demo.repositories.RolRepository;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.repositories.Usuario_has_distritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Controller
@RequestMapping("")
public class AdminRestController {
    @Autowired
    UsuarioRepository adminRestRepository;

    @Autowired
    RolRepository rolRepository;


    @GetMapping("/login")
    public String loginAdminRest() {
        return "AdminRestaurante/loginAR";
    }

    @GetMapping("/registro")
    public String nuevoCliente(@ModelAttribute("adminRest") Usuario adminRest, Model model) {
        model.addAttribute("adminRest", new Usuario());
        return "AdminRestaurante/registroAR";
    }


    @PostMapping("/guardarAdminRest")
    public String guardarRepartidor(Usuario adminRest) {

        //se agrega rol:
        adminRest.setRol(rolRepository.findById(3).get());
        //

        //OBS: ------
        adminRest.setEstado(2);

        //Fecha de registro:
        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        adminRest.setFecharegistro(hourdateFormat.format(date));
        //

        //--------

        adminRest.setIdusuario(51); //harcodeaoooooooooooooooooooooooooooooooooooo
        adminRest = adminRestRepository.save(adminRest);

        return "redirect:/login";
    }
}
