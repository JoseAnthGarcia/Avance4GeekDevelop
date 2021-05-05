package com.example.demo.controller;

import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping("/solicitudes")
    public String listaDeSolicitudes(Model model){
        model.addAttribute("listaUsuariosSolicitudes", usuarioRepository.findByEstado("pendiente"));
        return "/AdminGen/solicitudes";
    }
    @GetMapping("/solicitudesAR")
    public String listaDeSolicitudesAR(Model model){
        model.addAttribute("listaARSolicitudes", usuarioRepository.findByEstado("pendiente"));
        return "/Admin/solicitudes";
    }

}
