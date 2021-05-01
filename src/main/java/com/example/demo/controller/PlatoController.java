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
    PlatoRepository platoRepository;

    @GetMapping("/lista")
    public String listaPlatos(Model model) {
        model.addAttribute("listaPlatos", platoRepository.findByDisponible(true));
        return "/AdminRestaurante/listaPlatos";
    }

    @GetMapping("/nuevo")
    public String crearPlato() {
        return "/AdminRestaurante/nuevoPlato";
    }

    @PostMapping("/guardar")
    public String guardarPlato(Plato plato) {
        plato.setIdrestaurante(1); //Jarcodeado
        plato.setIdcategoriaplato(3); //Jarcodeado
        plato.setDisponible(true); //default expresion !!!!
        if(plato.getIdplato()==0){
            platoRepository.save(plato);
        }else{
            Optional<Plato> optPlato = platoRepository.findById(plato.getIdplato());
            if(optPlato.isPresent()){
                platoRepository.save(plato);
            }
        }
        return "redirect:/plato/lista";
    }

    @GetMapping("/editar")
    public String editarPlato(@RequestParam("id") int id,
                             Model model) {
        Optional<Plato> platoOptional = platoRepository.findById(id);
        if (platoOptional.isPresent()) {
            Plato plato= platoOptional.get();
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
            Plato plato= platoOptional.get();
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
