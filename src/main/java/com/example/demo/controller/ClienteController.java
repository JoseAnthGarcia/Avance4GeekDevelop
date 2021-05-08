package com.example.demo.controller;


import com.example.demo.entities.Usuario;
import com.example.demo.repositories.RolRepository;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    UsuarioRepository clienteRepository;

    @Autowired
    RolRepository rolRepository;

    @GetMapping("/login")
    public String loginCliente() {
        return "Cliente/login";
    }

    @GetMapping("/nuevo")
    public String nuevoCliente(@ModelAttribute("cliente") Usuario cliente) {
        return "Cliente/registro";
    }

    @PostMapping("/guardar")
    public String guardarCliente(@ModelAttribute("cliente") Usuario cliente,
                                    Model model, RedirectAttributes attr) {
        //int tamañoListaCredenciales = 0;

            cliente.setEstado(1);
            cliente.setRol(rolRepository.findById(1).get());
            String fechanacimiento = LocalDate.now().toString();
            cliente.setFecharegistro(fechanacimiento);

            clienteRepository.save(cliente);
            attr.addFlashAttribute("msg", "Cliente creado exitosamente");
            return "redirect:/cliente/login";



    }
}
