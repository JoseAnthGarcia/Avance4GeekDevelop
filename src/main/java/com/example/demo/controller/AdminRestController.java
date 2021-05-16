package com.example.demo.controller;

import com.example.demo.entities.*;
import com.example.demo.repositories.RolRepository;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@Controller
@RequestMapping("")
public class AdminRestController {
    @Autowired
    UsuarioRepository adminRestRepository;

    @Autowired
    RolRepository rolRepository;

    //oe ya pes ><
    /*@GetMapping("/login")
    public String loginAdminRest() {
        return "AdminRestaurante/loginAR";
    }*/

    @GetMapping("/registro")
    public String nuevoAdminRest(@ModelAttribute("adminRest") Usuario adminRest, Model model) {
        model.addAttribute("adminRest", new Usuario());
        return "AdminRestaurante/registroAR";
    }


    @PostMapping("/guardarAdminR")
    public String guardarAdminRest(@ModelAttribute("adminRest") @Valid Usuario adminRest, BindingResult bindingResult,
                                   @RequestParam("confcontra") String contra2) {

        System.out.println(contra2);
        System.out.println(adminRest.getContrasenia());
        //se agrega rol:
        adminRest.setRol(rolRepository.findById(3).get());
        adminRest.setEstado(2);
        String fechanacimiento = LocalDate.now().toString();
        adminRest.setFecharegistro(fechanacimiento);
        if(bindingResult.hasErrors()||!contra2.equalsIgnoreCase(adminRest.getContrasenia())){
            return "/AdminRestaurante/registroAR";
        }else {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(adminRest.getContrasenia());
            adminRest.setContrasenia(hashedPassword);
            adminRestRepository.save(adminRest);
            return "redirect:/login";
        }
    }
}
