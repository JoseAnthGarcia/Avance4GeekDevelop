package com.example.demo.controller;

import com.example.demo.entities.Movilidad;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.MovilidadRepository;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
@Controller
@RequestMapping("/movilidad")
public class MovilidadController {


    @Autowired
    MovilidadRepository movilidadRepository;

    @Autowired
    UsuarioRepository usuarioRepository;
    @GetMapping("/registroRepartidor")
    public String registroRepartidor(Model model, @ModelAttribute("usuario") Usuario usuario) {
        model.addAttribute("listaMovilidad", movilidadRepository.findAll());

        return "/Repartidor/registro";
    }
    @PostMapping("/save")
    public String guardarRepartidor(@ModelAttribute("movilidad") Movilidad movilidad, BindingResult bindingResult,
                                    RedirectAttributes attr, Model model, @RequestAttribute("idmovilidad") int idmovilidad) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("listaMovilidad", movilidadRepository.findAll());
            return "Repartidor/registro";
        } else {

            movilidadRepository.save(movilidad);
            return "redirect:/x";
        }
    }
}
