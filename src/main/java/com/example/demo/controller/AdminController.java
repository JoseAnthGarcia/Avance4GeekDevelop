package com.example.demo.controller;

import com.example.demo.entities.Plato;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.MovilidadRepository;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    MovilidadRepository movilidadRepository;

    @GetMapping("/solicitudes")
    public String listaDeSolicitudes(Model model){
        model.addAttribute("listaMovilidades", movilidadRepository.findAll());
        model.addAttribute("listaUsuariosSolicitudes", usuarioRepository.findByEstado("pendiente"));
        return "/AdminGen/solicitudes";
    }



    @GetMapping("/aceptado")
    public String aceptarUsuario(Model model,
                                 @RequestParam("id") int id) {

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);

        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            usuario.setEstado("ACTIVO");
            usuario.setFechaadmitido(String.valueOf(new Date()));
            usuarioRepository.save(usuario);

        }
        return "redirect:/admin/solicitudes";

    }

    @GetMapping("/bloqueado")
    public String bloquearUsuario(Model model,
                                 @RequestParam("id") int id) {

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);

        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            usuario.setEstado("BLOQUEADO");
            usuarioRepository.save(usuario);

        }
        return "redirect:/admin/solicitudes";

    }


    @GetMapping("/crear")
    public String crearAdministrador(@ModelAttribute("usuario") Usuario usuario) {
        return "/AdminGen/crearAdmin";
    }


    @PostMapping("/guardar")
    public String guardarPlato(@ModelAttribute("usuario") @Valid Usuario usuario,
                               BindingResult bindingResult, RedirectAttributes attr) {

        if(bindingResult.hasErrors()){
            return "AdminGen/crearAdmin";
        }else {

            if (usuario.getIdusuario() == 0) {
                attr.addFlashAttribute("msg", "Administrador creado exitosamente");
                usuario.setRol("ADMINISTRADOR");
                usuario.setFecharegistro(String.valueOf(new Date()));
                usuarioRepository.save(usuario);
                return "redirect:/admin";
            } else {
                usuarioRepository.save(usuario);
                attr.addFlashAttribute("msg", "Administrador actualizado exitosamente");
                return "redirect:/admin";
            }
        }

    }


}
