package com.example.demo.controller;

import com.example.demo.entities.Plato;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping("/solicitudes")
    public String listaDeSolicitudes(Model model){
        model.addAttribute("listaUsuariosSolicitudes", usuarioRepository.findByEstado("pendiente"));
        return "/Admin/solicitudes";
    }




    @GetMapping("/crearAdmin")
    public String nuevoEmployeeForm(@ModelAttribute("usuario") Usuario usuario) {

        return "employee/Frm";
    }


    @PostMapping("/guardar")
    public String guardarPlato(@ModelAttribute("usuario") @Valid Usuario usuario,
                               BindingResult bindingResult, RedirectAttributes attr) {

        if(bindingResult.hasErrors()){
            if (usuario.getIdusuario() == 0) {
                return "/AdminGen/crearAdmin";
            } else {
                Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuario.getIdusuario());
                if (optionalUsuario.isPresent()) {
                    return "/AdminGen/aja";
                }else{

                    return "redirect:/admin/lista";
                }
            }
        }else{
            usuario.setRol("admin");

            if (usuario.getIdusuario() == 0) {

                attr.addFlashAttribute("msg", "Administrador creado exitosamente");
                attr.addFlashAttribute("tipo", "saved");
                usuarioRepository.save(usuario);

            } else {
                Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuario.getIdusuario());
                if (optionalUsuario.isPresent()) {
                    usuarioRepository.save(usuario);
                    attr.addFlashAttribute("tipo", "saved");
                    attr.addFlashAttribute("msg", "Administrador actualizado exitosamente");
                }
            }
            return "redirect:/admin/lista";
        }

    }


    @GetMapping("/editar")
    public String editarPlato(@RequestParam("id") int id,
                              Model model,
                              @ModelAttribute("usuario") Usuario usuario) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            usuario = optionalUsuario.get();
            model.addAttribute("usuario", usuario);
            return "/AdminGen/editarAdmin";
        } else {
            return "redirect:/admin/lista";
        }
    }



}
