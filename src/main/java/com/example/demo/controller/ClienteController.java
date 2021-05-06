package com.example.demo.controller;


import com.example.demo.entities.Cliente;
import com.example.demo.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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
        return "Cliente/registroCliente";
    }

    @PostMapping("/guardar")
    public String guardarNuevoEmployee(@ModelAttribute("cliente") Cliente cliente, BindingResult bindingResult
                                    , Model model, RedirectAttributes attr) {
        //Usuario cli2=cliente;

        //if(bindingResult.hasErrors()){
       //     return "Cliente/registro";

       // }else{
            cliente.setRol("cliente");
            clienteRepository.save(cliente);
            attr.addFlashAttribute("msg", "Cliente creado exitosamente");
            return "redirect:/cliente/login";

       // }


    }
}
