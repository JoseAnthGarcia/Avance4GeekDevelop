package com.example.demo.controller;


import com.example.demo.entities.Usuario;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.example.demo.repositories.DistritosRepository;
import com.example.demo.repositories.RolRepository;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.repositories.UbicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller

@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    UsuarioRepository clienteRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    DistritosRepository distritosRepository;

    @Autowired
    UbicacionRepository ubicacionRepository;



    @GetMapping("/editarPerfil")
    public String editarPerfil(HttpSession httpSession, Model model) {

        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        model.addAttribute("usuario", usuario);

        return "Cliente/editarPerfil";

    }
    @PostMapping("/guardarEditar")
    public String guardarEdicion(@RequestParam("contraseniaConf") String contraseniaConf,
                                 @RequestParam("telefonoNuevo") String telefonoNuevo,
                                 HttpSession httpSession,
                                 Model model) {

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");
        boolean valContra = true;
        /*boolean telfValid = true;

        if( telefonoNuevo.equals('') || telefonoNuevo.length()!=9){
            telfValid =false;
        }*/

        if (BCrypt.checkpw(contraseniaConf,usuario1.getContrasenia())) {
            valContra = false;
        }

        if (valContra) {
            System.out.println("ENTRO AEA");
            if(valContra){
            model.addAttribute("msg", "Contraseña incorrecta");
            }
            return "Cliente/editarPerfil";

        } else {
            usuario1.setTelefono(telefonoNuevo); //usar save para actualizar
            httpSession.setAttribute("usuario",usuario1); //sesion -> actualizar la sesion
            clienteRepository.save(usuario1); //actualizar la base datos
            return "Cliente/listaRestaurantes";
        }


    }

    @GetMapping("/listaRestaurantes")
    public String listaRestaurantes(){

        return "Cliente/listaRestaurantes";
    }

    @GetMapping("/listaDirecciones")
    public String listaDirecciones(Model model,HttpSession httpSession){
        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        model.addAttribute("listaDirecciones", ubicacionRepository.findByUsuario(usuario));

        return "Cliente/listaDirecciones";
    }


    @PostMapping("/guardarDireccion")
    public String guardarDirecciones(){

        return "redirect:/cliente/listaDirecciones";
    }
    @PostMapping("/agregarDireccion")
    public  String registrarNewDireccion(@RequestParam("direccion") String direccion, HttpSession httpSession ){
        boolean valNul=true;
        if(direccion.isEmpty()){
            valNul=false;
            return "/cliente/listaDirecciones";
        }else{
            Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

            httpSession.setAttribute("usuario",usuario1);
            clienteRepository.save(usuario1);

            return "redirect:/cliente/listaDirecciones";
        }

    }




    @GetMapping("/listaCupones")
    public String listacupones(){

        return "Cliente/listaCupones";
    }

    @GetMapping("/listaReportes")
    public String listaReportes(){
        return "Cliente/listaReportes";
    }









}
