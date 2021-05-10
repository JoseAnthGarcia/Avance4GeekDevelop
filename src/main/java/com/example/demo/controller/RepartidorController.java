package com.example.demo.controller;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/repartidor")
public class RepartidorController {

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
    public String guardarRepartidor(@ModelAttribute("usuario") @Valid Usuario usuario, BindingResult bindingResult, Movilidad movilidad, Model model, @RequestParam("contrasenia2") String contrasenia2) {
        String dni = usuario.getDni();
        String telefono = usuario.getTelefono();
        String correo =  usuario.getCorreo();
        Usuario usuario1 =usuarioRepository.findByDni(dni);
        Usuario usuario2 =usuarioRepository.findByTelefono(telefono);
        Usuario usuario3 =usuarioRepository.findByCorreo(correo);
        Boolean errorMov = false;
        Boolean errorDist=false;

        if (movilidad.getTipoMovilidad().getIdtipomovilidad() == 3 && !movilidad.getLicencia().equals("")&& !movilidad.getPlaca().equals("")) {
            errorMov= true;
        }
        if(movilidad.getTipoMovilidad().getIdtipomovilidad() != 3 && (movilidad.getLicencia().equals("")||movilidad.getPlaca().equals(""))){
            errorMov= true;
        }
        if(usuario.getDistritos().size()>5){
            errorDist=true;
        }
        if(bindingResult.hasErrors() || !contrasenia2.equals(usuario.getContrasenia()) || usuario1!= null || usuario2!= null|| usuario3!= null  || errorMov || errorDist){
            if(!contrasenia2.equals(usuario.getContrasenia())){
                model.addAttribute("msg", "Las contraseñas no coinciden");
            }
            if(usuario1!=null){
                model.addAttribute("msg2", "El DNI ingresado ya se encuentra en la base de datos");
            }
            if(usuario2!=null){
                model.addAttribute("msg3", "El telefono ingresado ya se encuentra en la base de datos");
            }
            if(usuario3!=null){
                model.addAttribute("msg4", "El correo ingresado ya se encuentra en la base de datos");
            }
            if(errorDist){
                model.addAttribute("msg5", "Solo puede seleccionar 5 distritos");
            }
            if(errorMov){
                model.addAttribute("msg6", "Si eligió bicicleta como medio de transporte, no puede ingresar placa ni licencia. En caso contrario, dichos campos son obligatorios.");
            }

            model.addAttribute("usuario", usuario);
            model.addAttribute("movilidad", movilidad);
            model.addAttribute("distritosSeleccionados", new ArrayList<>());

            model.addAttribute("listatipoMovilidad", tipoMovilidadRepository.findAll());
            model.addAttribute("listaDistritos", distritosRepository.findAll());
            return "/Repartidor/registro";
        }else {


            //se agrega rol:
            usuario.setRol(rolRepository.findById(4).get());
            //
            if (movilidad.getTipoMovilidad().getIdtipomovilidad() == 6) {
                movilidad.setLicencia(null);
                movilidad.setPlaca(null);
            }
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

            usuario.setIdusuario(51); //harcodeaoooooooooooooooooooooooooooooooooooo
            usuario = usuarioRepository.save(usuario);

            for (Distrito distrito : usuario.getDistritos()) {
                Usuario_has_distritoKey usuario_has_distritoKey = new Usuario_has_distritoKey();
                usuario_has_distritoKey.setIddistrito(distrito.getIddistrito());
                usuario_has_distritoKey.setIdusuario(usuario.getIdusuario());

                Usuario_has_distrito usuario_has_distrito = new Usuario_has_distrito();
                usuario_has_distrito.setId(usuario_has_distritoKey);
                usuario_has_distrito.setDistrito(distrito);
                usuario_has_distrito.setUsuario(usuario);
                usuario_has_distritoRepository.save(usuario_has_distrito);
            }
            return "redirect:/repartidor/registroRepartidor";
        }


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
