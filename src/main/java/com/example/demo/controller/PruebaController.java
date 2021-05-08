package com.example.demo.controller;

import com.example.demo.entities.Movilidad;
import com.example.demo.entities.TipoMovilidad;
import com.example.demo.repositories.MovilidadRepository;
import com.example.demo.repositories.TipoMovilidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/prueba")
public class PruebaController {

    @Autowired
    MovilidadRepository movilidadRepository;

    @Autowired
    TipoMovilidadRepository tipoMovilidadRepository;

    @GetMapping("/form")
    public String movilidadForm(Model model){
        model.addAttribute("movilidad", new Movilidad());
        model.addAttribute("tipoMovilidad", new TipoMovilidad());
        return "movilidadForm";
    }

    @PostMapping("/save")
    public String saveForm(Movilidad movilidad, TipoMovilidad tipoMovilidad){
        System.out.println(movilidad.getLicencia() +" | "+ movilidad.getPlaca() + " | "+tipoMovilidad.getTipo());
        tipoMovilidad = tipoMovilidadRepository.save(tipoMovilidad);
        movilidad.setTipoMovilidad(tipoMovilidad);
        movilidadRepository.save(movilidad);
        return "redirect:/prueba/form";
    }
}
