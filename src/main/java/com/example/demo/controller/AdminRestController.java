package com.example.demo.controller;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/restaurante")
public class AdminRestController {
    @Autowired
    UsuarioRepository adminRestRepository;

    @Autowired
    RolRepository rolRepository;
    @Autowired
    DistritosRepository distritosRepository;

    @Autowired
    RestauranteRepository restauranteRepository;

    @Autowired
    CategoriasRestauranteRepository categoriasRestauranteRepository;


    @GetMapping("/login")
    public String loginAdminRest() {
        return "AdminRestaurante/loginAR";
    }

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
    @PostMapping("/guardarRestaurante")
    public String guardarAdminRest(@ModelAttribute("restaurante") @Valid  Restaurante restaurante, BindingResult bindingResult, HttpSession session, Model model){
        Usuario adminRest=(Usuario)session.getAttribute("usuario");
        restaurante.setAdministrador(adminRest);
        List<Categorias> listaCategorias =restaurante.getCategoriasRestaurante();
        if(bindingResult.hasErrors() || listaCategorias.size()!=4){
            model.addAttribute("listaDistritos", distritosRepository.findAll());
            model.addAttribute("listaCategorias", categoriasRestauranteRepository.findAll());
            if(listaCategorias.size()!=4){
                model.addAttribute("msg", "Se deben seleccionar 4 categorías");
            }
            return "/AdminRestaurante/registroResturante";
        }else {
            restauranteRepository.save(restaurante);
            return "redirect:/plato/";
        }
    }
    @GetMapping("/registroRest")
    public String registrarRestaurante(@ModelAttribute("restaurante") Restaurante restaurante,Model model){
        model.addAttribute("listaDistritos", distritosRepository.findAll());
        model.addAttribute("listaCategorias", categoriasRestauranteRepository.findAll());
        return "AdminRestaurante/registroResturante";
    }
    @GetMapping("/paginabienvenida")
    public String paginaBienvenida(){
        return "AdminRestaurante/adminCreado";
    }

}
