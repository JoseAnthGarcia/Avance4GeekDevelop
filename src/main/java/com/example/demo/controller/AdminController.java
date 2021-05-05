package com.example.demo.controller;

import com.example.demo.repositories.MovilidadRepository;
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

    @Autowired
    MovilidadRepository movilidadRepository;

    @GetMapping("/solicitudes")
    public String listaDeSolicitudes(Model model){
        model.addAttribute("listaMovilidades", movilidadRepository.findAll());
        model.addAttribute("listaUsuariosSolicitudes", usuarioRepository.findByEstado("pendiente"));
        return "/AdminGen/solicitudes";
    }

}
