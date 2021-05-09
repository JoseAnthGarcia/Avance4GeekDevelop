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

    @GetMapping("/nuevo")
    public String nuevoCliente(@ModelAttribute("cliente")  Usuario cliente, Model model) {
        // String direccion;
        model.addAttribute("Usuario_has_distrito", new Usuario_has_distrito());
        //distritos
        model.addAttribute("distritosSeleccionados", new ArrayList<>());
        //distritos -->
        model.addAttribute("listaDistritos", distritosRepository.findAll());
        return "Cliente/registro";

    }

    @PostMapping("/guardar")
    public String guardarCliente(@ModelAttribute("cliente") @Valid Usuario cliente,BindingResult bindingResult,
                                 @ModelAttribute("Usuario_has_distrito") @Valid Usuario_has_distrito usuario_has_distrito,
                                 BindingResult bindingResult2,Model model, RedirectAttributes attr) {

        /*int id = 0;
        if (cliente.getIdusuario() != null) {
            id = cliente.getIdusuario();
        }
        List<Usuario> clientesxcorreo = clienteRepository.findUsuarioByCorreoAndIdusuarioNot(cliente.getCorreo(),id);
        if (!clientesxcorreo.isEmpty()) {
            bindingResult.rejectValue("correo", "error.Usuario", "No puedes colocar un correo existente");
        }
        List<Usuario> clientesxdni = clienteRepository.findUsuarioByDniAndIdusuarioNot(cliente.getDni(),id);
        if (!clientesxdni.isEmpty()) {
            bindingResult.rejectValue("dni", "error.Usuario", "DNI ya registrado anteriormente");
        }*/



            if (bindingResult.hasErrors() || bindingResult2.hasErrors() ) {
                System.out.println("Entro hasErrors");
             //   String direccion;
                model.addAttribute("Usuario_has_distrito", new Usuario_has_distrito());
                //distritos
                model.addAttribute("distritosSeleccionados", new ArrayList<>());
                //distritos -->
                model.addAttribute("listaDistritos", distritosRepository.findAll());
                return "Cliente/registro";
            } else {
                cliente.setEstado(1);
                cliente.setRol(rolRepository.findById(1).get());
                String fechanacimiento = LocalDate.now().toString();
                cliente.setFecharegistro(fechanacimiento);
                clienteRepository.save(cliente);
                attr.addFlashAttribute("msg", "Cliente creado exitosamente");


                clienteRepository.save(cliente);

                for (Distrito distrito : cliente.getDistritos()) {
                    Usuario_has_distritoKey usuario_has_distritoKey = new Usuario_has_distritoKey();
                    usuario_has_distritoKey.setIddistrito(distrito.getIddistrito());
                    usuario_has_distritoKey.setIdusuario(cliente.getIdusuario());


                    usuario_has_distrito.setId(usuario_has_distritoKey);
                    usuario_has_distrito.setDistrito(distrito);
                    usuario_has_distrito.setUsuario(cliente);

                    usuario_has_distritoRepository.save(usuario_has_distrito);
                }

                return "redirect:/cliente/login";

            }
        }
    }
