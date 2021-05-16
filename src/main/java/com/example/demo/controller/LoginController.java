package com.example.demo.controller;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    UbicacionRepository ubicacionRepository;
    @Autowired
    RestauranteRepository restauranteRepository;

    @GetMapping("/login")
    public String loginForm() {
        return "Cliente/login";
    }


    @GetMapping("/accessDenied")
    public String acces() {
        return "/accessDenied";
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

        session.setAttribute("usuario", usuario);


        //redirect:
        switch (rol){
            case "cliente":
                List<Ubicacion> listaDirecciones = ubicacionRepository.findByUsuario(usuario);
                session.setAttribute("poolDirecciones", listaDirecciones);
                return "redirect:/cliente/listaRestaurantes";
            case "administradorG":
                return "redirect:/admin/usuarios";
            case "administrador":
                return "redirect:/admin/usuarios";
            case "administradorR":
                Restaurante restaurante=null;
                try {
                    restaurante = restauranteRepository.encontrarRest(usuario.getIdusuario());
                }catch(NullPointerException e){
                    System.out.println("Fallo");
                }
                if(restaurante==null){
                    return "redirect:/restaurante/paginabienvenida";
                }else{
                    return "redirect:/plato/";
                }
            case "repartidor":
                List<Ubicacion> listaDirecciones1 = ubicacionRepository.findByUsuario(usuario);
                session.setAttribute("poolDirecciones", listaDirecciones1);
                //TODO: agregar redireccion a repartidor
                return "somewhere";
            default:
                return "somewhere"; //no tener en cuenta
        }
    }

    //REGISTRO CLIENTE


    @GetMapping("/ClienteNuevo")
    public String nuevoCliente(@ModelAttribute("cliente") Usuario cliente, Model model) {
        // String direccion;
        model.addAttribute("ubicacion", new Ubicacion());
        model.addAttribute("listaDistritos", distritosRepository.findAll());
        return "Cliente/registro";

    }


    @PostMapping("/ClienteGuardar")
    public String guardarCliente(@ModelAttribute("cliente") @Valid Usuario cliente, BindingResult bindingResult,
                                 @ModelAttribute("ubicacion") @Valid Ubicacion ubicacion,
                                 BindingResult bindingResult2, Model model, RedirectAttributes attr, @RequestParam("contrasenia2") String contrasenia2) {


        List<Usuario> clientesxcorreo = clienteRepository.findUsuarioByCorreo(cliente.getCorreo());
        if (!clientesxcorreo.isEmpty()) {
            bindingResult.rejectValue("correo", "error.Usuario", "El correo ingresado ya se encuentra en la base de datos");
        }
        List<Usuario> clientesxdni = clienteRepository.findUsuarioByDni(cliente.getDni());
        if (!clientesxdni.isEmpty()) {
            bindingResult.rejectValue("dni", "error.Usuario", "El DNI ingresado ya se encuentra en la base de datos");
        }

        List<Usuario> clientesxtelefono = clienteRepository.findUsuarioByTelefono(cliente.getTelefono());
        if (!clientesxtelefono.isEmpty()) {
            bindingResult.rejectValue("telefono", "error.Usuario", "El telefono ingresado ya se encuentra en la base de datos");
        }

        Boolean usuario_direccion = ubicacion.getDireccion().equalsIgnoreCase("") || ubicacion.getDireccion() == null;
        Boolean dist_u_val = true;


        try {
            Integer u_dist = ubicacion.getDistrito().getIddistrito();
            System.out.println(u_dist + "ID DISTRITO");
            int dist_c = distritosRepository.findAll().size();
            System.out.println(dist_c);
            for (int i = 1; i <= dist_c; i++) {
                if (u_dist == i) {
                    dist_u_val = false;
                    System.out.println("ENTRO A LA VAIDACION DE AQUI");
                }
            }
        } catch (NullPointerException n) {
            System.out.println("No llegó nada");
            dist_u_val = true;
        }
        Boolean fecha_naci = true;
        try {
            String[] parts = cliente.getFechanacimiento().split("-");
            int naci = Integer.parseInt(parts[0]);
            Calendar fecha = new GregorianCalendar();
            int anio = fecha.get(Calendar.YEAR);

            if (anio - naci >= 18) {
                fecha_naci = false;
            }
        } catch (NumberFormatException n) {
        }

        if (bindingResult.hasErrors() || !contrasenia2.equals(cliente.getContrasenia()) || usuario_direccion || dist_u_val || fecha_naci
        ) {

            //----------------------------------------

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
            model.addAttribute("Usuario_has_distrito", new Ubicacion());
            //distritos
            model.addAttribute("distritosSeleccionados", new ArrayList<>());
            //distritos -->
            model.addAttribute("listaDistritos", distritosRepository.findAll());
            model.addAttribute("direccion", ubicacion.getDireccion());
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


            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(cliente.getContrasenia());
            System.out.println(hashedPassword);
            cliente.setContrasenia(hashedPassword);

            //guardamos direccion actual:
            cliente.setDireccionactual(ubicacion.getDireccion());

            clienteRepository.save(cliente);

            ubicacion.setUsuario(cliente);
            ubicacionRepository.save(ubicacion);

            return "redirect:/cliente/login";

        }

    }

}
