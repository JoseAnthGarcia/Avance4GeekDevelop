package com.example.demo.controller;

import com.example.demo.entities.Movilidad;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.DistritosRepository;
import com.example.demo.repositories.MovilidadRepository;
import com.example.demo.repositories.TipoMovilidadRepository;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    TipoMovilidadRepository tipoMovilidadRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    DistritosRepository distritosRepository;
    @GetMapping("/registroRepartidor")
    public String registroRepartidor(Model model, @ModelAttribute("usuario") Usuario usuario) {
        model.addAttribute("listatipoMovilidad", tipoMovilidadRepository.findAll());
        model.addAttribute("listaDistritos", distritosRepository.findAll());


        return "/Repartidor/registro";
    }
    @PostMapping("/guardarRepartidor")
    public String guardarRepartidor(@ModelAttribute("usuario") Usuario usuario, BindingResult bindingResult,
                                    RedirectAttributes attr, Model model, @RequestAttribute("contrasenia1") String contrasenia1, @RequestAttribute("contrasenia2") String contrasenia2) {

        if (bindingResult.hasErrors() ) {


            model.addAttribute("listatipoMovilidad", tipoMovilidadRepository.findAll());
            model.addAttribute("listaDistritos", distritosRepository.findAll());

            return "Repartidor/registro";
        }else if (contrasenia1!=contrasenia2){
            attr.addFlashAttribute("msg", "Las contraseñas no coinciden");
            model.addAttribute("listatipoMovilidad", tipoMovilidadRepository.findAll());
            model.addAttribute("listaDistritos", distritosRepository.findAll());

            return "Repartidor/registro";

        }else {

            usuarioRepository.save(usuario);
            return "redirect:/x";
        }




    }


}
