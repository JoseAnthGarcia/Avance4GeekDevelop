package com.example.demo.controller;


import com.example.demo.entities.Cliente;
import com.example.demo.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Date;


@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    ClienteRepository clienteRepository;
    @GetMapping("/login")
    public String loginCliente() {
        return "Cliente/login";
    }

    @GetMapping("/nuevo")
    public String nuevoEmployeeForm(@ModelAttribute("cliente") Cliente cliente, Model model) {
        return "Cliente/registro";
    }

    @PostMapping("/save")
    public String guardarNuevoEmployee(@ModelAttribute("cliente") @Valid Cliente cliente, BindingResult bindingResult
                                    , Model model, RedirectAttributes attr) {


        if(bindingResult.hasErrors()){
            return "Cliente/registro";
       }else{
            cliente.getCredencial().setIdcredenciales(1);
            cliente.setRol("CLIENTE");
            cliente.setFecharegistro(LocalDate.now());
            clienteRepository.save(cliente);
            attr.addFlashAttribute("msg", "Cliente creado exitosamente");
            return "redirect:/cliente/login";
       }


    }
}
