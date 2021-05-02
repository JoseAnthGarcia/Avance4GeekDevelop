package com.example.demo.controller;

import com.example.demo.entities.Plato;
import com.example.demo.repositories.PlatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/plato")
public class PlatoController {

    @Autowired
    PlatoRepository platoRepository;

    @GetMapping("/lista")
    public String listaPlatos(Model model) {
        model.addAttribute("listaPlatos", platoRepository.findByDisponible(true));
        return "/AdminRestaurante/listaPlatos";
    }

    @GetMapping("/nuevo")
    public String crearPlato(@ModelAttribute("plato") Plato plato) {
        return "/AdminRestaurante/nuevoPlato";
    }

    @PostMapping("/guardar")
    public String guardarPlato(@ModelAttribute("plato") @Valid Plato plato,
                               BindingResult bindingResult) {

        if(bindingResult.hasErrors()){
            if (plato.getIdplato() == 0) {
                return "/AdminRestaurante/nuevoPlato";
            } else {
                Optional<Plato> optPlato = platoRepository.findById(plato.getIdplato());
                if (optPlato.isPresent()) {
                    return "/AdminRestaurante/editarPlato";
                }else{
                    return "redirect:/plato/lista";
                }
            }
        }else{
            plato.setIdrestaurante(1); //Jarcodeado
            plato.setIdcategoriaplato(3); //Jarcodeado
            plato.setDisponible(true); //default expresion !!!!

            if (plato.getIdplato() == 0) {
                platoRepository.save(plato);
            } else {
                Optional<Plato> optPlato = platoRepository.findById(plato.getIdplato());
                if (optPlato.isPresent()) {
                    platoRepository.save(plato);
                }
            }
            return "redirect:/plato/lista";
        }

    }

    @GetMapping("/editar")
    public String editarPlato(@RequestParam("id") int id,
                              Model model,
                              @ModelAttribute("plato") Plato plato) {
        Optional<Plato> platoOptional = platoRepository.findById(id);
        if (platoOptional.isPresent()) {
            plato = platoOptional.get();
            model.addAttribute("plato", plato);
            return "/AdminRestaurante/editarPlato";
        } else {
            return "redirect:/plato/lista";
        }
    }

    @GetMapping("/borrar")
    public String borrarPlato(@RequestParam("id") int id) {
        Optional<Plato> platoOptional = platoRepository.findById(id);
        if (platoOptional.isPresent()) {
            Plato plato = platoOptional.get();
            plato.setDisponible(false);
            platoRepository.save(plato);
        }
        return "redirect:/plato/lista";
    }

    @GetMapping("/prueba")
    public String borrarPlato() {
        return "/AdminRestaurante/prueba";
    }


}
