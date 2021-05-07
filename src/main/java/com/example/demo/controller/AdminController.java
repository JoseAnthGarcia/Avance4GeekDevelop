package com.example.demo.controller;

import com.example.demo.entities.Pedido;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.MovilidadRepository;
import com.example.demo.repositories.TipoMovilidadRepository;
import com.example.demo.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.text.DateFormat;
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

    @Autowired
    TipoMovilidadRepository tipoMovilidadRepository;

    @GetMapping("/solicitudes")
    public String listaDeSolicitudes(@RequestParam(value = "tipo", required = false) String tipo, Model model){
        if(tipo == null){
            tipo = "adminRest";
        }
        model.addAttribute("listaTipoMovilidad", tipoMovilidadRepository.findAll());
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

    @PostMapping("/buscadorSoli")
    public String aceptarSolitud(@RequestParam(value = "nombreUsuario", required = false) String nombreUsuario1,
                                 @RequestParam(value = "tipoMovilidad", required = false) Integer tipoMovilidad1,
                                 @RequestParam(value = "fechaRegistro", required = false) Integer fechaRegistro1,
                                 Model model){
        System.out.println(nombreUsuario1+", "+tipoMovilidad1+", "+fechaRegistro1);
        model.addAttribute("nombreUsuario1", nombreUsuario1);
        model.addAttribute("tipoMovilidad1", tipoMovilidad1);
        model.addAttribute("fechaRegistro1", fechaRegistro1);

        if(fechaRegistro1==null){
            fechaRegistro1 = usuarioRepository.buscarFechaMinimaRepartidor();
        }


        model.addAttribute("listaTipoMovilidad", tipoMovilidadRepository.findAll());
        if(tipoMovilidad1==null){
            model.addAttribute("listaRepartidorSolicitudes", usuarioRepository.buscarRepartidoresSinMovilidad(nombreUsuario1, (fechaRegistro1*-1)-1));
        }else{
            model.addAttribute("listaRepartidorSolicitudes", usuarioRepository.buscarRepartidoresConMovilidad(nombreUsuario1, (fechaRegistro1*-1)-1, tipoMovilidad1));
        }

        System.out.println("------------");
        System.out.println(nombreUsuario1+", "+tipoMovilidad1+", "+fechaRegistro1);
        System.out.println("------------");

        return "/AdminGen/solicitudRepartidor";
    }

    @GetMapping("/aceptarSolicitud")
    public String aceptarSolitud(@RequestParam(value = "id", required = false) Integer id){

        if(id == null){
            return ""; //Retornar pagina principal
        }else {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

            if(usuarioOpt.isPresent()){
                Usuario usuario = usuarioOpt.get();
                usuario.setEstado("ACTIVO");
                //Fecha de registro:
                Date date = new Date();
                DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                usuario.setFechaadmitido(hourdateFormat.format(date));
                //
                usuarioRepository.save(usuario);
                return "redirect:/admin/solicitudes?tipo=repartidor";
            }else{
                return ""; //Retornar pagina principal
            }
        }

    }

    @GetMapping("/rechazarSolicitud")
    public String rechazarSolicitud(@RequestParam(value = "id", required = false) Integer id){

        if(id == null){
            return ""; //Retornar pagina principal
        }else {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

            if(usuarioOpt.isPresent()){
                Usuario usuario = usuarioOpt.get();
                usuario.setEstado("REHAZADO");
                usuarioRepository.save(usuario);
                return "redirect:/admin/solicitudes?tipo=repartidor";
            }else{
                return ""; //Retornar pagina principal
            }
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
        double totalIngresos = 0.0;


        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            for(int i = 0; i < usuario.getListaPedidosPorUsuario().size(); i++) {
                totalIngresos += usuario.getListaPedidosPorUsuario().get(i).getPreciototal();
            }

            switch (usuario.getRol()) {
                case "administrador":

                   // return "/AdminGen/visualizarCliente";
                case "repartidor":

                   // return "/AdminGen/visualizarCliente";
                case "cliente":
                    //TODO ver que solo sean los pedidos entregados
                    model.addAttribute("cliente",usuario);
                    model.addAttribute("totalIngresos", totalIngresos);
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
