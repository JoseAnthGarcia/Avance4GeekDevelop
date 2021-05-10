package com.example.demo.controller;

import com.example.demo.entities.Pedido;
import com.example.demo.entities.Plato;
import com.example.demo.entities.Rol;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.*;
import com.example.demo.service.RepartidorService;
import com.example.demo.service.RepartidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.validation.Valid;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController  {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    MovilidadRepository movilidadRepository;

    @Autowired
    TipoMovilidadRepository tipoMovilidadRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired  //////------------importante para enviar correo
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    Usuario_has_distritoRepository usuario_has_distritoRepository;

    @Autowired
    RepartidorService repartidorService;

    @Autowired
    PedidoRepository pedidoRepository;

    @GetMapping("/solicitudes")
    public String listaDeSolicitudes(@RequestParam(value = "tipo", required = false) String tipo,
                                     @RequestParam(value = "numPag", required = false) Integer numPag, Model model){
        if(tipo == null){
            tipo = "repartidor";//TODO: cambiar a "tipo = "adminRest";"
        }

        //paginacion --------
        if(numPag==null){
            numPag= 1;
        }

        int tamPag = 3;

        //-------

        model.addAttribute("listaTipoMovilidad", tipoMovilidadRepository.findAll());
        switch (tipo){
            case "restaurante":
                return "1";
            case "adminRest":
                //model.addAttribute("listaAdminRestSolicitudes",
                //        usuarioRepository.findByEstadoAndRolOrderByFecharegistroAsc(2, rolRepository.findById(3).get()));
                return "/AdminGen/solicitudAR";
            case "repartidor":
                Page<Usuario> pagina = repartidorService.repartidorPaginacion(numPag, tamPag);
                List<Usuario> listaRepartidores = pagina.getContent();
                model.addAttribute("tamPag",tamPag);
                model.addAttribute("currentPage",numPag);
                model.addAttribute("totalPages", pagina.getTotalPages());
                model.addAttribute("totalItems", pagina.getTotalElements());

                model.addAttribute("listaRepartidorSolicitudes", listaRepartidores);
                //model.addAttribute("listaRepartidorSolicitudes",
                //        usuarioRepository.findByEstadoAndRolOrderByFecharegistroAsc(2, rolRepository.findById(4).get()));
                return "/AdminGen/solicitudRepartidor";
            default:
                return "";
                //mandar a la vista principal
        }
    }

    @PostMapping("/buscadorSR")
    public String aceptarSolitud(@RequestParam(value = "nombreUsuario", required = false) String nombreUsuario1,
                                 @RequestParam(value = "tipoMovilidad", required = false) Integer tipoMovilidad1,
                                 @RequestParam(value = "fechaRegistro", required = false) Integer fechaRegistro1,
                                 Model model){
        System.out.println(nombreUsuario1+", "+tipoMovilidad1+", "+fechaRegistro1);
        model.addAttribute("nombreUsuario1", nombreUsuario1);
        model.addAttribute("tipoMovilidad1", tipoMovilidad1);
        model.addAttribute("fechaRegistro1", fechaRegistro1);

        if(fechaRegistro1==null){
            fechaRegistro1 = usuarioRepository.buscarFechaMinimaRepartidor()+1;
        }


        model.addAttribute("listaTipoMovilidad", tipoMovilidadRepository.findAll());
        if(tipoMovilidad1==null){
            model.addAttribute("listaRepartidorSolicitudes", usuarioRepository.buscarRepartidoresSinMovilidad(nombreUsuario1,nombreUsuario1, fechaRegistro1*-1));
        }else{
            model.addAttribute("listaRepartidorSolicitudes", usuarioRepository.buscarRepartidoresConMovilidad(nombreUsuario1,nombreUsuario1, fechaRegistro1*-1, tipoMovilidad1));
        }

        //BORRAR
        model.addAttribute("currentPage",1);
        model.addAttribute("tamPag",0);
        model.addAttribute("totalPages", 3);
        model.addAttribute("totalItems", 4);

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
                usuario.setEstado(1);
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
                usuario.setEstado(3);
                usuarioRepository.save(usuario);
                return "redirect:/admin/solicitudes?tipo=repartidor";
            }else{
                return ""; //Retornar pagina principal
            }
        }

    }

    @GetMapping("/usuarios")
    public String listaDeUsuarios(Model model) {
        model.addAttribute("listaUsuarios", usuarioRepository.listaUsuarios());
        return "/AdminGen/lista";
    }

    @GetMapping("/buscador")
    public String buscadorUsuario(@RequestParam(value = "texto",required = false) String texto,
                                  @RequestParam(value = "fechaRegsitro",required = false) Integer fechaRegsitro,
                                  @RequestParam(value = "idRol",required = false) Integer idRol,
                                  @RequestParam(value = "estado",required = false) Integer estado,
                                  Model model){

        //busca por nombre y apellido - no hay problema si es nulo
        model.addAttribute("textoBuscador", texto);
        model.addAttribute("fechaBuscador", fechaRegsitro);
        model.addAttribute("rolBuscador", idRol);
        model.addAttribute("estadoBuscador", estado);

        //si es nulo se manda la fecha minima de la lista de USUARIOS
        if(fechaRegsitro==null){
            fechaRegsitro = usuarioRepository.buscarFechaMinimaRepartidor()+1;
        }
        if(estado==null && idRol == null){
            model.addAttribute("listaUsuarios",usuarioRepository.buscadorUsuarioSinEstadoNiRol(texto,-1*fechaRegsitro));
        }else if(idRol==null){
            model.addAttribute("listaUsuarios",usuarioRepository.buscadorUsuarioSinRol(texto,-1*fechaRegsitro,estado));
        }else if(estado==null){
            model.addAttribute("listaUsuarios",usuarioRepository.buscadorUsuarioSinEstado(texto,-1*fechaRegsitro,idRol));
        }else{
            model.addAttribute("listaUsuarios",usuarioRepository.buscadorUsuario(texto,-1*fechaRegsitro,idRol,estado));
        }

        return "/AdminGen/lista";
    }

    @GetMapping("/buscadorCliente")
    public String buscadorUsuario(@RequestParam(value = "idUsuario",required = false) Integer idUsuario,
                                  @RequestParam(value = "textoPedido",required = false) String texto,
                                  @RequestParam(value = "fechaPedido",required = false) Integer fechaPedido,
                                  @RequestParam(value = "valoracion",required = false) Integer valoracion,
                                  Model model){
        System.out.println(idUsuario + "sfasdfasdfasfasfd");
        System.out.println(texto + "sfasdfasdfasfasfd");
        System.out.println(fechaPedido + "sfasdfasdfasfasfd");
        System.out.println(valoracion + "sfasdfasdfasfasfd");

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(idUsuario);
        if(usuarioOptional.isPresent()){
            Usuario cliente = usuarioOptional.get();
            model.addAttribute("cliente", cliente);
        }
        model.addAttribute("textoBuscador", texto);
        model.addAttribute("fechaPedidoBuscador", fechaPedido);
        model.addAttribute("valoracionBuscador", valoracion);
        model.addAttribute("idUsuario", idUsuario);

        if(fechaPedido == null){
            fechaPedido = usuarioRepository.buscarFechaMinimaRepartidor();
        }

        model.addAttribute("listaPedidos",pedidoRepository.pedidosPorCliente(idUsuario,texto,-1*fechaPedido,valoracion));
        return "/AdminGen/visualizarCliente";
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

            switch (usuario.getRol().getTipo()) {
                case "administrador":
                    model.addAttribute("administrador",usuario);
                    return "/AdminGen/visualizarAdministrador";
                case "repartidor":
                    model.addAttribute("repartidor",usuario);
                    model.addAttribute("ganancia",usuarioRepository.gananciaRepartidor(idUsuario));
                    model.addAttribute("valoracion",usuarioRepository.valoracionRepartidor(idUsuario));
                    model.addAttribute("direcciones",usuario_has_distritoRepository.findAll());
               //     model.addAttribute("totalIngresos", totalIngresos);
                    return "/AdminGen/visualizarRepartidor";
                case "cliente":
                    //TODO ver que solo sean los pedidos entregados
                    model.addAttribute("cliente",usuario);
                    model.addAttribute("totalIngresos", totalIngresos);
                    model.addAttribute("direcciones",usuario_has_distritoRepository.direccionesPorUsuario(idUsuario));
                    return "/AdminGen/visualizarCliente";
                case "administradorR":
                    model.addAttribute("administradorRestaurante",usuario);
                   return "/AdminGen/visualizarAdministradorRestaurante";
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
            usuario.setEstado(1);
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
            usuario.setEstado(0);
            usuarioRepository.save(usuario);

        }
        return "redirect:/admin/solicitudes";

    }


    @GetMapping("/crear")
    public String crearAdministrador(@ModelAttribute("usuario") Usuario usuario) {
        return "/AdminGen/crearAdmin";
    }


    @PostMapping("/guardar")
    public String guardarAdmin(@ModelAttribute("usuario") @Valid Usuario usuario,
                               BindingResult bindingResult, RedirectAttributes attr) {

        // TODO: 8/05/2021 Falta validar que no se repita el correo y dni

        if(bindingResult.hasErrors()){
            return "AdminGen" +
                    "/crearAdmin";
        }else {

            if (usuario.getIdusuario() == 0) {
                attr.addFlashAttribute("msg", "Administrador creado exitosamente");
                usuario.setRol(rolRepository.findById(5).get());
                usuario.setFecharegistro(String.valueOf(LocalDateTime.now()));
                usuario.setContrasenia("123456");////contraseña por default
                usuarioRepository.save(usuario);

                /////----------------Envio Correo--------------------/////

                String contenido = "Hola "+ usuario.getNombres()+" administrador esta es tu cuenta creada";
                sendEmail(usuario.getCorreo(), "Cuenta Administrador creado", contenido);

                //sendEmailHtml(usuario.getCorreo(), "Cuenta Administrador creado html", usuario);



                /////-----------------------------------------------/////




                return "redirect:/admin/solicitudes";
            } else {
                usuarioRepository.save(usuario);
                attr.addFlashAttribute("msg", "Administrador actualizado exitosamente");
                return "redirect:/admin/solicitudes";
            }
        }

    }


    //Pasamos por parametro: destinatario, asunto y el mensaje
    public void sendEmail(String to, String subject, String content) {

        SimpleMailMessage email = new SimpleMailMessage();

        email.setTo(to);
        email.setSubject(subject);
        email.setText(content);

        mailSender.send(email);
    }

    public void sendEmailHtml(String to, String subject, Usuario usuario) {

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        // Crear cuerpo del mensaje
        Context context = new Context();
        context.setVariable("user", usuario.getNombres());
        context.setVariable("id", usuario.getDni());
        String emailContent = templateEngine.process("/AdminGen/mailTemplate", context);
        email.setText(emailContent);
        mailSender.send(email);



    }


}
