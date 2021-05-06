package com.example.demo.controller;

import com.example.demo.entities.Usuario;
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
    UsuarioRepository usuarioRepository;

    @GetMapping("/nuevo")
    public String nuevoEmployeeForm(@ModelAttribute("cliente") Usuario cliente, Model model) {
        return "Cliente/registro";
    }

    @PostMapping("/save")
    public String guardarNuevoEmployee(@ModelAttribute("cliente") @Valid Usuario cliente, BindingResult bindingResult
                                    , Model model, RedirectAttributes attr) {
        Usuario cli2=cliente;
        System.out.println(cli2.getNombres());
        if(bindingResult.hasErrors()){
            return "Cliente/registro";

        }else{
            usuarioRepository.save(cliente);
            attr.addFlashAttribute("msg", "Cliente creado exitosamente");
            return "redirect:/Cliente/login";

        }


    }
}
