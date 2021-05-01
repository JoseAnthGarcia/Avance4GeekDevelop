package com.example.demo.controller;

import com.example.demo.entities.Cupon;
import com.example.demo.repositories.CuponRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cupon")
public class CuponController {
    @Autowired
    CuponRepository cuponRepository;

    @GetMapping(value = {"/lista",""})
    public String listarCupones(Model model){
    List<Cupon> list = cuponRepository.findAll();
    model.addAttribute("listaCupon",list);
    return "AdminRestaurante/listaCupones";

    }

    @GetMapping("/nuevo")
    public String nuevoCupon(){
        return "AdminRestaurante/nuevoCupon";
    }

    @PostMapping("/guardar")
    public String guardarCupon(Cupon cupon, RedirectAttributes attributes){
        Cupon cupon1=cupon;
        System.out.println(cupon1.getIdcupon());
        if (cupon.getIdcupon()==0){
            attributes.addFlashAttribute("creado","Cupon creado exitosamente!");
        }else {
            attributes.addFlashAttribute("editado","Cupon editado exitosamente!");
        }
        cuponRepository.save(cupon);
        return "redirect:/cupon/lista";
    }

    @GetMapping("/editar")
    public String editarCupon( Cupon cupon, Model model, @RequestParam("id") int id){
        Optional<Cupon> optionalCupon=cuponRepository.findById(id);
        if (optionalCupon.isPresent()){
            cupon = optionalCupon.get();
            model.addAttribute("cupon",cupon);
            return "AdminRestaurante/editarCupon";
        } else {
            return "redirect:/cupon/lista";
        }
    }

    /*@GetMapping("/eliminar")
    public  String eliminarCupon(@RequestParam("id") int id, RedirectAttributes attributes){
        Optional<Cupon> cuponOptional = cuponRepository.findById(id);
        if (cuponOptional.isPresent()){
            cuponRepository.deleteById(id);
            attributes.addFlashAttribute("eliminado", "Cupon eliminado exitosamente!");
        }
        return "redirect:/cupon/listar";
    }*/
}
