package com.example.demo.controller;

import com.example.demo.entities.Tarjeta;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.TarjetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/tarjeta")
public class TarjetaController {

    @Autowired
    TarjetaRepository tarjetaRepository;

    @GetMapping("/lista")
    public String mostrarTarjetas(HttpSession httpSession, Model model, @ModelAttribute("tarjeta") Tarjeta tarjeta){
        Usuario usuario = (Usuario)httpSession.getAttribute("usuario");
        List<Tarjeta> listaTarejeta = tarjetaRepository.findByUsuario(usuario);
        model.addAttribute("listaTarjetas", listaTarejeta);
        return "Cliente/tarjetas";
    }

    @PostMapping("/guardar")
    public String guardarTarjeta(@RequestParam("numeroTarjeta") String numeroTarjeta,
                                 HttpSession httpSession){

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setNumerotarjeta(numeroTarjeta.substring(0,6)+numeroTarjeta.substring(12,16));
        tarjeta.setIdtarjeta(numeroTarjeta.substring(6,12)); //Por ahora, seran los numero restantes
        tarjeta.setUsuario((Usuario)httpSession.getAttribute("usuario"));
        tarjetaRepository.save(tarjeta);
        return "redirect:/tarjeta/lista";

    }

    @PostMapping("/eliminar")
    public String eliminarTarjeta(@RequestParam("listaTarjetasSelecciones") List<Tarjeta> listaTarjetasSeleccionadas){
        //validar existencia
        for(Tarjeta tarjeta: listaTarjetasSeleccionadas){
            tarjetaRepository.delete(tarjeta);
        }
        return "redirect:/tarjeta/lista";
    }


}
