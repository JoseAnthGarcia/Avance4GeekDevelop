package com.example.demo.controller;


import com.example.demo.entities.Usuario;
import com.example.demo.repositories.CredencialesRepository;
import com.example.demo.repositories.RolRepository;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;


@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    UsuarioRepository clienteRepository;

    @Autowired
    CredencialesRepository credencialesRepository;

    @Autowired
    RolRepository rolRepository;

    @GetMapping("/login")
    public String loginCliente() {
        return "Cliente/login";
    }

    @GetMapping("/nuevo")
    public String nuevoEmployeeForm(@ModelAttribute("cliente") Usuario cliente, Model model) {
        return "Cliente/registro";
    }

    @PostMapping("/save")
    public String guardarNuevoEmployee(@ModelAttribute("cliente") @Valid Usuario cliente, BindingResult bindingResult
                                    , Model model, RedirectAttributes attr) {
        //int tamañoListaCredenciales = 0;

        if(bindingResult.hasErrors()){
            return "Cliente/registro";
       }else{

            //tamañoListaCredenciales = clienteRepository.findAll().size();
            //int idCredencial = tamañoListaCredenciales + 1;
            //cliente.getCredencial().setIdcredenciales(5);
            cliente.setRol(rolRepository.findById(1).get());
            String fechanacimiento = LocalDate.now().toString();
            cliente.setFecharegistro(fechanacimiento);

            clienteRepository.save(cliente);
            attr.addFlashAttribute("msg", "Cliente creado exitosamente");
            return "redirect:/cliente/login";
       }


    }
}
