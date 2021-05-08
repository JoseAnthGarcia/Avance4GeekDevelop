package com.example.demo.controller;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    TipoMovilidadRepository tipoMovilidadRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    DistritosRepository distritosRepository;

    @Autowired
    MovilidadRepository movilidadRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    Usuario_has_distritoRepository usuario_has_distritoRepository;

    @GetMapping("/registroRepartidor")
    public String registroRepartidor(Model model, @ModelAttribute("usuario") Usuario usuario) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("movilidad", new Movilidad());
        model.addAttribute("distritosSeleccionados", new ArrayList<>());

        model.addAttribute("listatipoMovilidad", tipoMovilidadRepository.findAll());
        model.addAttribute("listaDistritos", distritosRepository.findAll());


        return "/Repartidor/registro";
    }

    @PostMapping("/guardarRepartidor")
    public String guardarRepartidor(Usuario usuario, Movilidad movilidad) {

        //se agrega rol:
        usuario.setRol(rolRepository.findById(4).get());
        //

        movilidad = movilidadRepository.save(movilidad);
        usuario.setMovilidad(movilidad);

        //OBS: ------
        usuario.setEstado(2);

        //Fecha de registro:
        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        usuario.setFecharegistro(hourdateFormat.format(date));
        //

        //--------

        usuario.setIdusuario(50); //harcodeaoooooooooooooooooooooooooooooooooooo
        usuario = usuarioRepository.save(usuario);

        for(Distrito distrito : usuario.getDistritos()){
            Usuario_has_distritoKey usuario_has_distritoKey = new Usuario_has_distritoKey();
            usuario_has_distritoKey.setIddistrito(distrito.getIddistrito());
            usuario_has_distritoKey.setIdusuario(usuario.getIdusuario());

            Usuario_has_distrito usuario_has_distrito = new Usuario_has_distrito();
            usuario_has_distrito.setId(usuario_has_distritoKey);
            usuario_has_distrito.setDistrito(distrito);
            usuario_has_distrito.setUsuario(usuario);
            usuario_has_distritoRepository.save(usuario_has_distrito);
        }

        return "redirect:/usuario/registroRepartidor";
    }

    /*
    @PostMapping("/guardarRepartidor")
    public String guardarRepartidor(@ModelAttribute("usuario") Usuario usuario, BindingResult bindingResult,
                                    RedirectAttributes attr, Model model,
                                    @RequestAttribute("contrasenia1") String contrasenia1, @RequestAttribute("contrasenia2") String contrasenia2) {

        if (bindingResult.hasErrors()) {


            model.addAttribute("listatipoMovilidad", tipoMovilidadRepository.findAll());
            model.addAttribute("listaDistritos", distritosRepository.findAll());

            return "Repartidor/registro";
        }else if (contrasenia1!=contrasenia2){
            attr.addFlashAttribute("msg", "Las contraseñas no coinciden");
            model.addAttribute("listatipoMovilidad", tipoMovilidadRepository.findAll());
            model.addAttribute("listaDistritos", distritosRepository.findAll());

            return "Repartidor/registro";

        }else {

            usuarioRepository.save(usuario);
            return "redirect:/x";
        }*/

}
