package com.example.demo.controller;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.service.AdminRestService;
import com.example.demo.service.RepartidorService;
import com.example.demo.service.RepartidorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    UbicacionRepository ubicacionRepository;

    @Autowired
    RepartidorService repartidorService;

    @Autowired
    AdminRestService adminRestService;

    @Autowired
    PedidoRepository pedidoRepository;

    @GetMapping("tipoSolicitud")
    public String tipoSolicitud(){
        return "AdminGen/tipoSolicitudes";
    }

    @GetMapping("/solicitudes")
    public String listaDeSolicitudes(@RequestParam(value = "tipo", required = false) String tipo,
                                     @RequestParam(value = "numPag", required = false) Integer numPag,
                                     Model model,
                                     @RequestParam(value = "nombreUsuario", required = false) String nombreUsuario1,
                                     @RequestParam(value = "tipoMovilidad", required = false) Integer tipoMovilidad1,
                                     @RequestParam(value = "fechaRegistro", required = false) Integer fechaRegistro1){


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
                Page<Usuario> pagina1 = adminRestService.adminRestPaginacion(numPag, tamPag);
                List<Usuario> listaAdminRest = pagina1.getContent();
                model.addAttribute("tamPag",tamPag);
                model.addAttribute("currentPage",numPag);
                model.addAttribute("totalPages", pagina1.getTotalPages());
                model.addAttribute("totalItems", pagina1.getTotalElements());
                model.addAttribute("listaAdminRestSolicitudes", listaAdminRest);


                return "/AdminGen/solicitudAR";
            case "repartidor":

                Page<Usuario> pagina;

                if((nombreUsuario1==null || nombreUsuario1.equals(""))
                        && tipoMovilidad1==null && fechaRegistro1==null){
                    pagina = repartidorService.repartidorPaginacion(numPag, tamPag);
                }else{
                    model.addAttribute("nombreUsuario1", nombreUsuario1);
                    model.addAttribute("tipoMovilidad1", tipoMovilidad1);
                    model.addAttribute("fechaRegistro1", fechaRegistro1);

                    if(fechaRegistro1==null){
                        fechaRegistro1 = usuarioRepository.buscarFechaMinimaRepartidor()+1;
                    }


                    model.addAttribute("listaTipoMovilidad", tipoMovilidadRepository.findAll());
                    if(tipoMovilidad1==null){
                        pagina = repartidorService.repartidorPaginacionBusqueda1(numPag, tamPag, nombreUsuario1,nombreUsuario1, fechaRegistro1*-1);
                    }else{
                        pagina = repartidorService.repartidorPaginacionBusqueda2(numPag, tamPag, nombreUsuario1,nombreUsuario1, fechaRegistro1*-1, tipoMovilidad1);
                    }
                }

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

    @PostMapping("/buscadorSAdminRest")
    public String buscarAdminRest(@RequestParam(value = "nombreUsuario", required = false) String nombreUsuario1,
                              @RequestParam(value = "fechaRegistro", required = false) Integer fechaRegistro1,
                              @RequestParam(value = "dni", required = false) Integer dni1,
                              Model model){
        model.addAttribute("nombreUsuario1", nombreUsuario1);

        model.addAttribute("fechaRegistro1", fechaRegistro1);

        model.addAttribute("dni1",dni1);
        if(fechaRegistro1==null){
            fechaRegistro1 = usuarioRepository.buscarFechaMinimaRepartidor()+1;
        }


        model.addAttribute("listaTipoMovilidad", tipoMovilidadRepository.findAll());


        //BORRAR
        model.addAttribute("currentPage",1);
        model.addAttribute("tamPag",0);
        model.addAttribute("totalPages", 3);
        model.addAttribute("totalItems", 4);


        return "/AdminGen/solicitudAR";

    }

    @GetMapping("/aceptarSolicitud")
    public String aceptarSolitud(@RequestParam(value = "id", required = false) Integer id,
                                 @RequestParam(value = "tipo", required = false) String tipo){

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
                return "redirect:/admin/solicitudes?tipo="+tipo;
            }else{
                return ""; //Retornar pagina principal
            }
        }

    }

    @GetMapping("/rechazarSolicitud")
    public String rechazarSolicitud(@RequestParam(value = "id", required = false) Integer id,
                                    @RequestParam(value = "tipo", required = false) String tipo){

        if(id == null){
            return ""; //Retornar pagina principal
        }else {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

            if(usuarioOpt.isPresent()){
                Usuario usuario = usuarioOpt.get();
                usuario.setEstado(3);
                usuarioRepository.save(usuario);
                return "redirect:/admin/solicitudes?tipo="+tipo;
            }else{
                return ""; //Retornar pagina principal
            }
        }

    }
    @GetMapping("/detalleAdminSoli")
    public String detalleAdminSOli(@RequestParam("idUsuario") int idUsuario,
                                   Model model){
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            model.addAttribute("administradorRestaurante", usuario);
            return "/AdminGen/detalleAdminR";

        }else {
            return "redirect:/admin/solicitudes?tipo=adminRest";
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
                    model.addAttribute("direcciones", ubicacionRepository.findByUsuario(usuario));
               //     model.addAttribute("totalIngresos", totalIngresos);
                    return "/AdminGen/visualizarRepartidor";
                case "cliente":
                    //TODO ver que solo sean los pedidos entregados
                    model.addAttribute("cliente",usuario);
                    model.addAttribute("totalIngresos", totalIngresos);
                    model.addAttribute("direcciones", ubicacionRepository.findByUsuario(usuario));
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

    /*

    @GetMapping("/actualizar")
    public String actualizarEstado(@RequestParam("idUsuario") int id, RedirectAttributes attr){

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if(usuarioOpt.isPresent()){
            Usuario usuario = usuarioOpt.get();
            // ESTADOS
            /*
                0 - BLOQUEDO
                1 - ACTIVO
                2 - PENDIENTE
                DESBLOQUEAR: DE 0 - 1

            switch (usuario.getEstado()){
        case 0:
            // de bloqueado a aceptado
            usuario.setEstado(1);
            usuarioRepository.save(usuario);
            attr.addFlashAttribute("msgAceptado", "Usuario Desbloqueado exitosamente");
            return "redirect:/admin/usuarios";
        case 1:
            // de activo a bloquado
            usuario.setEstado(0);
            usuarioRepository.save(usuario);
            attr.addFlashAttribute("msgBloqueado", "Usuario Bloqueado exitosamente");
            return "redirect:/admin/usuarios";
        default:
            return "redirect:/admin/ususarios";
    }
}

        return "";
                }
     */

    @GetMapping("/crear")
    public String crearAdministrador(@ModelAttribute("usuario") Usuario usuario,HttpSession httpSession,Model model) {

        Usuario usuario2 = (Usuario) httpSession.getAttribute("usuario");

        if (usuario2.getRol().getIdrol()!=2){
            model.addAttribute("listaUsuarios", usuarioRepository.listaUsuarios());
            return "/AdminGen/lista";

        }else{
            return "/AdminGen/crearAdmin";

        }




    }


    @GetMapping("/editarPerfil")
    public String editarAdministrador(HttpSession httpSession, Model model) {

        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        model.addAttribute("usuario", usuario);

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



    @PostMapping("/guardarAdmin")
    public String guardarAdministrador(@ModelAttribute("usuario") @Valid Usuario usuario,
                                 BindingResult bindingResult2, Model model, RedirectAttributes attr) throws MessagingException {

        List<Usuario> usuarioxcorreo = usuarioRepository.findUsuarioByCorreo(usuario.getCorreo());
        if (!usuarioxcorreo.isEmpty()) {
            bindingResult2.rejectValue("correo", "error.Usuario", "El correo ingresado ya se encuentra en la base de datos");
        }
        List<Usuario> usuarioxdni = usuarioRepository.findUsuarioByDni(usuario.getDni());
        if (!usuarioxdni.isEmpty()) {
            bindingResult2.rejectValue("dni", "error.Usuario", "El DNI ingresado ya se encuentra en la base de datos");
        }

        List<Usuario> usuarioxtelefono = usuarioRepository.findUsuarioByTelefono(usuario.getTelefono());
        if (!usuarioxtelefono.isEmpty()) {
            bindingResult2.rejectValue("telefono", "error.Usuario", "El telefono ingresado ya se encuentra en la base de datos");
        }


        Boolean fecha_naci = true;
        try {
            String[] parts = usuario.getFechanacimiento().split("-");
            int naci = Integer.parseInt(parts[0]);
            Calendar fecha = new GregorianCalendar();
            int anio = fecha.get(Calendar.YEAR);

            if (anio - naci >= 18) {
                fecha_naci = false;
            }
        } catch (NumberFormatException n) {
        }

        if (bindingResult2.hasErrors() || fecha_naci
        ) {
            System.out.println("siguen errores");

            //----------------------------------------


            if (fecha_naci) {
                model.addAttribute("msg7", "Solo pueden registrarse m   ayores de edad");
            }

            return "/AdminGen" +
                    "/crearAdmin";
        } else {
            usuario.setEstado(1);
            usuario.setRol(rolRepository.findById(5).get());
            String fechanacimiento = LocalDate.now().toString();
            usuario.setFecharegistro(fechanacimiento);

            attr.addFlashAttribute("msg", "Usuario creado exitosamente");

            Date date = new Date();
            DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            usuario.setFecharegistro(hourdateFormat.format(date));


            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(usuario.getDni());
            System.out.println(hashedPassword);
            usuario.setContrasenia(hashedPassword);

            /////----------------Envio Correo--------------------/////

            String contenido = "Hola "+ usuario.getNombres()+" administrador esta es tu cuenta creada";
            sendEmail(usuario.getCorreo(), "Cuenta Administrador creado", contenido);
            sendHtmlMail(usuario.getCorreo(), "Cuenta Administrador creado html", usuario);


            /////-----------------------------------------  ------/////



            usuarioRepository.save(usuario);

            return "redirect:/admin/usuarios";

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

    public void sendHtmlMail(String to, String subject, Usuario usuario) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        Context context = new Context();
        context.setVariable("user", usuario.getNombres());
        context.setVariable("id", usuario.getDni());
        String emailContent = templateEngine.process("/AdminGen/mailTemplate", context);
        helper.setText(emailContent, true);
        mailSender.send(message);
    }

    public void sendHtmlMailREgistrado(String to, String subject, Usuario usuario) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        Context context = new Context();
        context.setVariable("user", usuario.getNombres());
        context.setVariable("id", usuario.getDni());
        String emailContent = templateEngine.process("/Correo/clienteREgistrado", context);
        helper.setText(emailContent, true);
        mailSender.send(message);
    }

}
