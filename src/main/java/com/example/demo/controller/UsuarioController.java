package com.example.demo.controller;

import com.example.demo.entities.Movilidad;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.MovilidadRepository;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {
    @Autowired
    MovilidadRepository movilidadRepository;

    @Autowired
    UsuarioRepository usuarioRepository;
    @GetMapping("/registroRepartidor")
    public String registroRepartidor(Model model, @ModelAttribute("usuario")Usuario usuario) {
        model.addAttribute("listaMovilidad", movilidadRepository.findAll());

        return "/Repartidor/registro";
    }
    @PostMapping("/save")
    public String guardarRepartidor(@ModelAttribute("usuario") @Valid Usuario usuario, BindingResult bindingResult,
                                    RedirectAttributes attr, Model model, @ModelAttribute Movilidad movilidad) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("listaMovilidad", movilidadRepository.findAll());
            return "Repartidor/registro";
        } else {

            usuarioRepository.save(usuario);
            return "redirect:/x";
        }
    }

}
