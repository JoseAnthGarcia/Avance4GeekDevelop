package com.example.demo.controller;

import com.example.demo.entities.Distrito;
import com.example.demo.entities.Usuario;
import com.example.demo.entities.Usuario_has_distrito;
import com.example.demo.entities.Usuario_has_distritoKey;
import com.example.demo.repositories.DistritosRepository;
import com.example.demo.repositories.RolRepository;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.repositories.Usuario_has_distritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Controller

public class LoginController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    UsuarioRepository clienteRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    DistritosRepository distritosRepository;

    @Autowired
    Usuario_has_distritoRepository usuario_has_distritoRepository;
    @GetMapping("/ClienteLogin")
    public String loginForm(){
        return "Cliente/login";
    }






    @GetMapping(value = "/redirectByRole")
    public String redirectByRole(Authentication auth, HttpSession session) {
        String rol = "";
        for (GrantedAuthority role : auth.getAuthorities()) {
            rol = role.getAuthority();
            break;
        }

        String correo = auth.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        System.out.println(usuario.getNombres());

        System.out.println(usuario.getApellidos());

        session.setAttribute("usuario",usuario);
        System.out.println(usuario);


        if (rol.equals("cliente")) {
            return "redirect:/cliente/listaRestaurantes";
        } else{
            return "redirect:/plato/";
        }
    }

    //REGISTRO CLIENTE



    @GetMapping("/ClienteNuevo")
    public String nuevoCliente(@ModelAttribute("cliente") Usuario cliente, Model model) {
        // String direccion;
        model.addAttribute("Usuario_has_distrito", new Usuario_has_distrito());
        //distritos
        model.addAttribute("distritosSeleccionados", new ArrayList<>());
        //distritos -->
        model.addAttribute("listaDistritos", distritosRepository.findAll());
        return "Cliente/registro";

    }

    @PostMapping("/ClienteGuardar")
    public String guardarCliente(@ModelAttribute("cliente") @Valid Usuario cliente, BindingResult bindingResult,
                                 @ModelAttribute("Usuario_has_distrito") @Valid Usuario_has_distrito usuario_has_distrito,
                                 BindingResult bindingResult2, Model model, RedirectAttributes attr, @RequestParam("contrasenia2") String contrasenia2) {


        int id = 0;
        if (cliente.getIdusuario() != null) {
            id = cliente.getIdusuario();
        }
        List<Usuario> clientesxcorreo = clienteRepository.findUsuarioByCorreoAndIdusuarioNot(cliente.getCorreo(), id);
        if (!clientesxcorreo.isEmpty()) {
            bindingResult.rejectValue("correo", "error.Usuario", "El correo ingresado ya se encuentra en la base de datos");
        }
        List<Usuario> clientesxdni = clienteRepository.findUsuarioByDniAndIdusuarioNot(cliente.getDni(), id);
        if (!clientesxdni.isEmpty()) {
            bindingResult.rejectValue("dni", "error.Usuario", "El DNI ingresado ya se encuentra en la base de datos");
        }

        List<Usuario> clientesxtelefono = clienteRepository.findUsuarioByTelefonoAndIdusuarioNot(cliente.getTelefono(), id);
        if (!clientesxtelefono.isEmpty()) {
            bindingResult.rejectValue("telefono", "error.Usuario", "El telefono ingresado ya se encuentra en la base de datos");
        }

        Boolean usuario_direccion = usuario_has_distrito.getDireccion().equalsIgnoreCase("") || usuario_has_distrito.getDireccion() == null;
        Boolean dist_u_val =true;
        try {
            Integer u_dist = cliente.getDistritos().get(0).getIddistrito();
            System.out.println(u_dist + "ID DISTRITO");
            int dist_c = distritosRepository.findAll().size();
            System.out.println(dist_c);
            for (int i = 1; i <= dist_c; i++) {
                if (u_dist == i) {
                    dist_u_val = false;
                    System.out.println("ENTRO A LA VAIDACION DE AQUI");
                }
            }
        } catch (NullPointerException n){
            System.out.println("No llegó nada");
            dist_u_val = true;
        }

        String[] parts = cliente.getFechanacimiento().split("-");
        int naci = Integer.parseInt(parts[0]);
        Calendar fecha = new GregorianCalendar();
        int anio = fecha.get(Calendar.YEAR);
        Boolean fecha_naci=false;
        if(anio-naci<18){
            fecha_naci=true;
        }

        if (bindingResult.hasErrors() || !contrasenia2.equals(cliente.getContrasenia()) || usuario_direccion || dist_u_val|| fecha_naci) {
            if (usuario_direccion) {
                model.addAttribute("msg2", "Complete sus datos");
            }
            if (fecha_naci) {
                model.addAttribute("msg7", "Solo pueden registrarse mayores de edad");
            }
            if (dist_u_val) {
                model.addAttribute("msg3", "Seleccione una de las opciones");
                model.addAttribute("msg5", "Complete sus datos");
            }
            if (!contrasenia2.equals(cliente.getContrasenia())) {
                model.addAttribute("msg", "Las contraseñas no coinciden");
            }

            //   String direccion;
            model.addAttribute("Usuario_has_distrito", new Usuario_has_distrito());
            //distritos
            model.addAttribute("distritosSeleccionados", new ArrayList<>());
            //distritos -->
            model.addAttribute("listaDistritos", distritosRepository.findAll());
            model.addAttribute("direccion", usuario_has_distrito.getDireccion());
            return "Cliente/registro";
        } else {
            cliente.setEstado(1);
            cliente.setRol(rolRepository.findById(1).get());
            String fechanacimiento = LocalDate.now().toString();
            cliente.setFecharegistro(fechanacimiento);

            attr.addFlashAttribute("msg", "Cliente creado exitosamente");

            Date date = new Date();
            DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            cliente.setFecharegistro(hourdateFormat.format(date));
            clienteRepository.save(cliente);

            for (Distrito distrito : cliente.getDistritos()) {
                Usuario_has_distritoKey usuario_has_distritoKey = new Usuario_has_distritoKey();
                usuario_has_distritoKey.setIddistrito(distrito.getIddistrito());
                usuario_has_distritoKey.setIdusuario(cliente.getIdusuario());


                usuario_has_distrito.setId(usuario_has_distritoKey);
                usuario_has_distrito.setDistrito(distrito);
                usuario_has_distrito.setUsuario(cliente);

                usuario_has_distritoRepository.save(usuario_has_distrito);
            }

            return "redirect:/cliente/login";

        }

    }

}
