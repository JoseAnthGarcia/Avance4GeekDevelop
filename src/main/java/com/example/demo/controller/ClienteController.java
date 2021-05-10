package com.example.demo.controller;


import com.example.demo.entities.Distrito;
import com.example.demo.entities.Usuario;
import com.example.demo.entities.Usuario_has_distrito;
import com.example.demo.entities.Usuario_has_distritoKey;
import com.example.demo.repositories.DistritosRepository;
import com.example.demo.repositories.RolRepository;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.repositories.Usuario_has_distritoRepository;
import org.apache.tomcat.util.modeler.BaseAttributeFilter;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller

@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    UsuarioRepository clienteRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    DistritosRepository distritosRepository;

    @Autowired
    Usuario_has_distritoRepository usuario_has_distritoRepository;


    @GetMapping("/login")
    public String loginCliente() {
        return "Cliente/login";
    }



    @GetMapping("/listaRestaurantes")
    public String listaRestaurantes(){


        return "Cliente/listaRestaurantes";
    }




}
