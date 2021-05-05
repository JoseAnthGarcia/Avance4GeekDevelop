package com.example.demo.controller;

import com.example.demo.repositories.MovilidadRepository;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    MovilidadRepository movilidadRepository;

    @GetMapping("/solicitudes")
    public String listaDeSolicitudes(@RequestParam("tipo") String tipo, Model model){
        switch (tipo){
            case "restaurante":
                return "";
            case "adminRest":
                model.addAttribute("listaAdminRestSolicitudes",
                        usuarioRepository.findByEstadoAndRolOrderByFecharegistro("pendiente", "administradorR"));
                return "/AdminGen/solicitudAR";
            case "repartidor":
                model.addAttribute("listaMovilidades", movilidadRepository.findAll());
                model.addAttribute("listaRepartidorSolicitudes",
                        usuarioRepository.findByEstadoAndRolOrderByFecharegistro("pendiente", "repartidor"));
                return "/AdminGen/solicitudRepartidor";
            default:
                return "";
                //mandar a la vista principal
        }
    }

}
