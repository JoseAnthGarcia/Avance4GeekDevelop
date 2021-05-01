package com.example.demo.controller;

import com.example.demo.entities.Plato;
import com.example.demo.repositories.PlatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/plato")
public class PlatoController {

    @Autowired
    PlatoRepository adminRestRepository;

    @GetMapping("/lista")
    public String listaPlatos(Model model) {
        model.addAttribute("listaPlatos", adminRestRepository.findAll());
        return "/AdminRestaurante/listaPlatos";
    }

    @GetMapping("/formulario")
    public String crearPlato() {
        return "/AdminRestaurante/nuevoPlato";
    }

    @PostMapping("/guardar")
    public String guardarPlato(Plato plato) {
        adminRestRepository.save(plato);
        return "redirect:/plato/lista";
    }

    @GetMapping("/editar")
    public String editarPlato(@RequestParam("id") int id,
                             Model model) {
        Optional<Plato> platoOptional = adminRestRepository.findById(id);
        if (platoOptional.isPresent()) {
            Plato plato= platoOptional.get();
            model.addAttribute("plato", plato);
            return "/AdminRestaurante/editarPlato";
        } else {
            return "redirect:/plato/lista";
        }
    }
    @GetMapping("/borrar")
    public String borrarPlato(@RequestParam("id") int id,
                              Model model) {
        Optional<Plato> platoOptional = adminRestRepository.findById(id);
        if (platoOptional.isPresent()) {
            Plato plato= platoOptional.get();
            plato.setDisponible(false);
            adminRestRepository.save(plato);
            return "/AdminRestaurante/editarPlato";
        } else {
            return "redirect:/plato/lista";
        }
    }


}
