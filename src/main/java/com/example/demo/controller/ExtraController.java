package com.example.demo.controller;

import com.example.demo.entities.Cupon;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;


@Controller
@RequestMapping("/extra")
public class ExtraController {


    @GetMapping(value = {"/lista", ""})
    public String listarCupones(Model model) {
        model.addAttribute("listaCupon", cuponRepository.findAll());
        return "AdminRestaurante/listaCupones";
    }

    @GetMapping("/nuevo")
    public String nuevoCupon(@ModelAttribute("cupon") Cupon cupon) {
        return "AdminRestaurante/nuevoCupon";
    }

    @PostMapping("/guardar")
    public String guardarCupon(@ModelAttribute("cupon") @Valid Cupon cupon, BindingResult bindingResult,
                               RedirectAttributes attributes,
                               Model model) {
        Cupon cVal = cuponRepository.buscarPorNombre(cupon.getNombre());

        if(cVal == null){
            if(bindingResult.hasErrors()){
                return "AdminRestaurante/nuevoCupon";
            }

            cupon.setIdrestaurante(2);

            if (cupon.getIdcupon() == 0) {
                cupon.setFechainicio(LocalDate.now());
                cupon.setDisponible(true);
                attributes.addFlashAttribute("creado", "Cupon creado exitosamente!");
            } else {
                attributes.addFlashAttribute("editado", "Cupon editado exitosamente!");
            }

            if(cupon.getFechainicio().isEqual(cupon.getFechafin())){
                cupon.setDisponible(false);
            }

            cuponRepository.save(cupon);
            return "redirect:/cupon/lista";
        }else{
            model.addAttribute("val","Este nombre ya está registrado");
            return "AdminRestaurante/nuevoCupon";
        }


    }

    @GetMapping("/editar")
    public String editarCupon(@ModelAttribute("cupon") Cupon cupon,
                              Model model,
                              @RequestParam("id") int id) {
        Optional<Cupon> optionalCupon = cuponRepository.findById(id);
        if (optionalCupon.isPresent()) {
            cupon = optionalCupon.get();
            model.addAttribute("cupon", cupon);
            return "AdminRestaurante/nuevoCupon";
        } else {
            return "redirect:/cupon/lista";
        }
    }
}
