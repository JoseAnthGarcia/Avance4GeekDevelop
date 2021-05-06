package com.example.demo.controller;

import com.example.demo.entities.Cliente;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.ClienteRepository;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;


@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    ClienteRepository clienteRepository;

    @GetMapping("/login")
    public String nuevoEmployeeForm() {
        return "Cliente/login";
    }

    @GetMapping("/nuevo")
    public String nuevoEmployeeForm(@ModelAttribute("cliente") Cliente cliente, Model model) {
        return "Cliente/registro";
    }

    @PostMapping("/save")
    public String guardarNuevoEmployee(@ModelAttribute("cliente") Cliente cliente, Model model, RedirectAttributes attr) {

        cliente.setRol("cliente");
        cliente.setEstado("ACTIVO");
        clienteRepository.save(cliente);
        return "redirect:/cliente/login";
    }
}
