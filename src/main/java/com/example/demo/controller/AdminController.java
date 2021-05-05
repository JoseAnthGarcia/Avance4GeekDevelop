package com.example.demo.controller;

import com.example.demo.entities.Usuario;
import com.example.demo.repositories.MovilidadRepository;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
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
    public String listaDeSolicitudes(@RequestParam(value = "tipo", required = false) String tipo, Model model){
        if(tipo == null){
            tipo = "adminRest";
        }
        switch (tipo){
            case "restaurante":
                return "";
            case "adminRest":
                model.addAttribute("listaAdminRestSolicitudes",
                        usuarioRepository.findByEstadoAndRolOrderByFecharegistroAsc("pendiente", "administradorR"));
                return "/AdminGen/solicitudAR";
            case "repartidor":
                model.addAttribute("listaRepartidorSolicitudes",
                        usuarioRepository.findByEstadoAndRolOrderByFecharegistroAsc("pendiente", "repartidor"));
                return "/AdminGen/solicitudRepartidor";
            default:
                return "";
                //mandar a la vista principal
        }
    }

    @GetMapping("/usuarios")
    public String listaDeUsuarios(Model model) {
        model.addAttribute("listaUsuarios", usuarioRepository.listaUsuariosAceptados());
        return "/AdminGen/lista";
    }

    @GetMapping("/detalle")
    public String detalleUsuario(@RequestParam("idUsuario") int idUsuario,
                                 Model model) {

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            switch (usuario.getRol()) {
                case "administrador":

                   // return "/AdminGen/visualizarCliente";
                case "repartidor":

                   // return "/AdminGen/visualizarCliente";
                case "cliente":
                    model.addAttribute("cliente",usuario);
                    return "/AdminGen/visualizarCliente";
                case "administradorRestaurante":

                   // return "/AdminGen/visualizarCliente";
                default:
                    //TODO ver si enviar con mensaje de alerta
                    return "redirect:/admin/usuarios";
            }
        }else {
            //TODO ver si enviar con mensaje de alerta
            return "redirect:/admin/usuarios";
        }
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
