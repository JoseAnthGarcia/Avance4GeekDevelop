package com.example.demo.controller.Cliente;

import com.example.demo.entities.Usuario;
import com.example.demo.repositories.UsuarioRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.constraints.Positive;

@Controller
@RequestMapping("/cliente")
public class RegistroController {
    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping("/registro")
    public String registro(@ModelAttribute("usuario") Usuario usuario ){

        return "/Cliente/registro";
    }


    @PostMapping("/guardar")
    public String guardar(ModelAttribute modelAttribute, User user){



        return "/Cliente/login";
    }


}
