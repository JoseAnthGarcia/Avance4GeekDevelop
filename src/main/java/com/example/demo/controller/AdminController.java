package com.example.demo.controller;

import com.example.demo.dtos.*;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.service.*;
import com.example.demo.service.RepartidorService;
import com.example.demo.service.RestauranteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    @Autowired
    RestauranteService restauranteService;

    @Autowired
    RestauranteRepository restauranteRepository;

    @Autowired
    private UsuarioServiceAPI usuarioServiceAPI;

    @Autowired
    private UsuarioServiceAPIDtoCliente usuarioServiceAPIDtoCliente;
    @Autowired
    private UsuarioServiceAPIDtoRepartidor usuarioServiceAPIDtoRepartidor;
    // TODO: 26/06/2021
    @Autowired
    private UsuarioServiceAPIDtoReporteVentas usuarioServiceAPIDtoReporteVentas;

    @GetMapping("/listaReportes")
    public String listaReportes(Model model, HttpSession session) {

        Usuario usuario1 = (Usuario) session.getAttribute("usuario");
        model.addAttribute("notificaciones", usuarioRepository.notificacionCliente(usuario1.getIdusuario()));
        return "AdminGen/listaReportes";
    }

    @GetMapping("tipoSolicitud")
    public String tipoSolicitud(){
        return "AdminGen/tipoSolicitudes";
    }

    @GetMapping("/solicitudes")
    public String listaDeSolicitudes(@RequestParam(value = "tipo", required = false) String tipo,
                                     @RequestParam(value = "numPag", required = false) Integer numPag,
                                     Model model,
                                     @RequestParam(value = "nombreUsuario", required = false) String nombreUsuario1,
                                     @RequestParam(value = "tipoMovilidad", required = false) String tipoMovilidad1,
                                     @RequestParam(value = "fechaRegistro", required = false) String fechaRegistro1,
                                     @RequestParam(value = "dni", required = false) String dni1,
                                     @RequestParam(value = "nombreRest", required = false) String nombreRest1,
                                     @RequestParam(value = "ruc", required = false) String ruc1){


        if(tipo == null){
            tipo = "repartidor";//TODO: cambiar a "tipo = "adminRest";"
        }

        //paginacion --------
        if(numPag==null){
            numPag= 1;
        }

        int tamPag = 3;

        //-------
        Integer fechaRegistro = 1;
        model.addAttribute("listaTipoMovilidad", tipoMovilidadRepository.findAll());
        switch (tipo){
            case "restaurante":
                Page<Restaurante> pagina2;

                if((nombreRest1==null || nombreRest1.equals(""))
                        && (ruc1==null || ruc1.equals(""))&& fechaRegistro1==null){
                    pagina2 = restauranteService.restaurantePaginacion(numPag, tamPag);
                }else{
                    model.addAttribute("nombreRest1", nombreRest1);

                    model.addAttribute("ruc1", ruc1);
                    model.addAttribute("fechaRegistro1", fechaRegistro1);
                    if(fechaRegistro1==null){
                        fechaRegistro = restauranteRepository.buscarFechaMinimaRestaurante()+1;

                    }else{
                        try{

                            fechaRegistro = Integer.parseInt(fechaRegistro1);
                            model.addAttribute("fechaRegistro1", fechaRegistro);

                        }catch (NumberFormatException e){
                            fechaRegistro = restauranteRepository.buscarFechaMinimaRestaurante()+1;
                        }
                    }

                    System.out.println(nombreRest1 +" "+ ruc1+" " +fechaRegistro1+"AAAAA");

                    pagina2=restauranteService.restBusqueda(numPag,tamPag,nombreRest1,ruc1, fechaRegistro*-1);
                }

                List<Restaurante> listaRestaurantes = pagina2.getContent();
                model.addAttribute("tamPag",tamPag);
                model.addAttribute("currentPage",numPag);
                model.addAttribute("totalPages", pagina2.getTotalPages());
                model.addAttribute("totalItems", pagina2.getTotalElements());

                model.addAttribute("listaRestaurantes", listaRestaurantes);

                return "AdminGen/solicitudRestaurante";
            case "adminRest":

                Page<Usuario> pagina1 ;
                if((nombreUsuario1==null || nombreUsuario1.equals(""))
                        && (dni1==null || dni1.equals("") && fechaRegistro1==null)){
                    pagina1 = adminRestService.adminRestPaginacion(numPag, tamPag);
                }else{
                    model.addAttribute("nombreUsuario1", nombreUsuario1);
                    model.addAttribute("dni1", dni1);
                    model.addAttribute("fechaRegistro1", fechaRegistro1);

                    if(fechaRegistro1==null){
                        fechaRegistro = usuarioRepository.buscarFechaMinimaRepartidor()+1;
                    }else{
                        try{

                            fechaRegistro = Integer.parseInt(fechaRegistro1);
                            model.addAttribute("fechaRegistro1", fechaRegistro);
                        }catch (NumberFormatException e){
                            fechaRegistro = usuarioRepository.buscarFechaMinimaRepartidor()+1;
                        }
                    }
                    System.out.println(fechaRegistro1);

                    pagina1=adminRestService.administradorRestBusqueda(numPag,tamPag,nombreUsuario1,nombreUsuario1,dni1,fechaRegistro*-1);

                }

                List<Usuario> listaAdminRest = pagina1.getContent();
                model.addAttribute("tamPag",tamPag);
                model.addAttribute("currentPage",numPag);
                model.addAttribute("totalPages", pagina1.getTotalPages());
                model.addAttribute("totalItems", pagina1.getTotalElements());
                model.addAttribute("listaAdminRestSolicitudes", listaAdminRest);


                return "AdminGen/solicitudAR";
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
                        fechaRegistro = usuarioRepository.buscarFechaMinimaRepartidor()+1;
                    }else{
                        try{

                            fechaRegistro = Integer.parseInt(fechaRegistro1);
                            model.addAttribute("fechaRegistro1", fechaRegistro);
                        }catch (NumberFormatException e){
                            fechaRegistro = usuarioRepository.buscarFechaMinimaRepartidor()+1;
                        }
                    }


                    model.addAttribute("listaTipoMovilidad", tipoMovilidadRepository.findAll());

                    Integer tipoMovilidad = 5;
                    if(tipoMovilidad1!=null){
                        try {
                            tipoMovilidad = Integer.parseInt(tipoMovilidad1);
                        }catch (NumberFormatException e){
                            tipoMovilidad1= null;
                        }
                    }
                    if(tipoMovilidad1==null){
                        pagina = repartidorService.repartidorPaginacionBusqueda1(numPag, tamPag, nombreUsuario1,nombreUsuario1, fechaRegistro*-1);
                    }else{
                        pagina = repartidorService.repartidorPaginacionBusqueda2(numPag, tamPag, nombreUsuario1,nombreUsuario1, fechaRegistro*-1, tipoMovilidad);
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
                return "AdminGen/solicitudRepartidor";
            default:
                return "";
                //mandar a la vista principal
        }
    }


    @GetMapping("/aceptarSolicitudRest")
    public String aceptarSolitudRest(@RequestParam(value = "id", required = false) Integer id, RedirectAttributes attr){

        if(id == null){
            return "redirect:/admin/solicitudes?tipo=restaurante"; //Retornar pagina principal
        }else {
            Optional<Restaurante> restauranteOpt =restauranteRepository.findById(id);

            if(restauranteOpt.isPresent()){
                Restaurante restaurante = restauranteOpt.get();
                if(restaurante.getEstado()==2) {

                    restaurante.setEstado(1);
                    //Fecha de registro:
                    //Date date = new Date();
                    //DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    //restaurante.setFechaadmitido(hourdateFormat.format(date));
                    //
                    restauranteRepository.save(restaurante);

                    String contenido = "Hola " + restaurante.getNombre() + " tu cuenta fue creada exitosamente";
                    sendEmail(restaurante.getAdministrador().getCorreo(), "Restaurante aceptado", contenido);
                    attr.addFlashAttribute("msg1", "Restaurante aceptado exitosamente");
                    return "redirect:/admin/solicitudes?tipo=restaurante";
                }else {
                    attr.addFlashAttribute("msg2", "No se pudo aceptar el restaurante indicado.");
                    return "redirect:/admin/solicitudes?tipo=restaurante";
                }
            }else{
                attr.addFlashAttribute("msg3", "El restaurante ingresado no existe.");
                return "redirect:/admin/solicitudes?tipo=restaurante"; //Retornar pagina principal
            }
        }

    }
    @GetMapping("/images")
    public ResponseEntity<byte[]> mostrarUsuario(@RequestParam("id") int id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuario= usuarioOptional.get();
            byte[] image = usuario.getFoto();

            // HttpHeaders permiten al cliente y al servidor enviar información adicional junto a una petición o respuesta.
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(usuario.getFotocontenttype()));

            return new ResponseEntity<>(image, httpHeaders, HttpStatus.OK);

        } else {
            return null;
        }
    }
    @GetMapping("/imagesRest")
    public ResponseEntity<byte[]> mostrarRestaurante(@RequestParam("id") int id) {
        Optional<Restaurante> restauranteOpt = restauranteRepository.findById(id);
        if (restauranteOpt.isPresent()) {
            Restaurante restaurante = restauranteOpt.get();
            byte[] image = restaurante.getFoto();

            // HttpHeaders permiten al cliente y al servidor enviar información adicional junto a una petición o respuesta.
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(restaurante.getFotocontenttype()));

            return new ResponseEntity<>(image, httpHeaders, HttpStatus.OK);

        } else {
            return null;
        }
    }
    @GetMapping("/rechazarSolicitudRest")
    public String rechazarSolicitudRest(@RequestParam(value = "id", required = false) Integer id, RedirectAttributes attr){

        if(id == null){
            return "redirect:/admin/solicitudes?tipo=restaurante";
        }else {
            Optional<Restaurante> restauranteOpt =restauranteRepository.findById(id);

            if(restauranteOpt.isPresent()){
                Restaurante restaurante = restauranteOpt.get();
                if(restaurante.getEstado()==2) {
                    restaurante.setEstado(3);
                    restauranteRepository.save(restaurante);
                    String contenido = "Hola " + restaurante.getAdministrador().getNombres() + " administrador esta es tu cuenta creada";
                    sendEmail(restaurante.getAdministrador().getCorreo(), "Cuenta Administrador creado", contenido);
                    attr.addFlashAttribute("msg2", "Restaurante rechazado exitosamente");

                    return "redirect:/admin/solicitudes?tipo=restaurante";
                }else{
                    return "redirect:/admin/solicitudes?tipo=restaurante";
                }
            }else{
                return "redirect:/admin/solicitudes?tipo=restaurante";
            }
        }

    }
    @GetMapping("/detalleRestSoli")
    public String detalleRestSoli(@RequestParam("idRest") int idRest,
                                   Model model){
        Optional<Restaurante> restauranteOpt = restauranteRepository.findById(idRest);
        if (restauranteOpt.isPresent()) {
            Restaurante restaurante = restauranteOpt.get();
            model.addAttribute("restaurante", restaurante);
            return "AdminGen/detalleRest";

        }else {
            return "redirect:/admin/solicitudes?tipo=restaurante";
        }
    }
// HOLA
    @GetMapping("/aceptarSolicitud")////error al direccionar - administrador rest
    public String aceptarSolitud(@RequestParam(value = "id", required = false) Integer id,
                                 @RequestParam(value = "tipo", required = false) String tipo,
                                 RedirectAttributes attr) throws MessagingException {

        if(id == null){
            return "redirect:/admin/solicitudes?tipo=" + tipo; //Retornar pagina principal
        }else {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

            if(usuarioOpt.isPresent()){
                Usuario usuario = usuarioOpt.get();
                if(usuario.getEstado()==2) {
                    usuario.setEstado(1);
                    //Fecha de registro:
                    Date date = new Date();
                    DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    usuario.setFechaadmitido(hourdateFormat.format(date));
                    //
                    usuarioRepository.save(usuario);
                    /////----------------Envio Correo--------------------/////

                    String contenido = "Hola " + usuario.getNombres() + " tu solicitud fue aceptada exitosamente";
                    sendEmail(usuario.getCorreo(), "Cuenta fue aceptada", contenido);
                    //sendHtmlMailAceptado(usuario.getCorreo(), "Cuenta Fue ACeptada html", usuario);


                    /////-----------------------------------------  ------/////
                    attr.addFlashAttribute("msg1", "Usuario aceptado exitosamente");

                    return "redirect:/admin/solicitudes?tipo=" + tipo;
                }else {
                    return "redirect:/admin/solicitudes?tipo=" + tipo;
                }
            }else{
                return "redirect:/admin/solicitudes?tipo=" + tipo; //Retornar pagina principal
            }
        }

    }

    @GetMapping("/rechazarSolicitud")////Error al redireccionar - administrador rest
    public String rechazarSolicitud(@RequestParam(value = "id", required = false) Integer id,
                                    @RequestParam(value = "tipo", required = false) String tipo,
                                    RedirectAttributes attr) throws MessagingException {

        if(id == null){
            return ""; //Retornar pagina principal
        }else {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

            if(usuarioOpt.isPresent()){
                Usuario usuario = usuarioOpt.get();
                if(usuario.getEstado()==2) {
                    usuario.setEstado(3);
                    usuarioRepository.save(usuario);
                    /////----------------Envio Correo--------------------/////

                    String contenido = "Hola " + usuario.getNombres() + " solicitud fue rechazada";
                    sendEmail(usuario.getCorreo(), "Cuenta fue rechazada", contenido);
                    //sendHtmlMailRechazado(usuario.getCorreo(), "Cuenta Fue Rechazada html", usuario);

                    attr.addFlashAttribute("msg2", "Usuario rechazado exitosamente");
                    /////-----------------------------------------  ------/////
                    return "redirect:/admin/solicitudes?tipo=" + tipo;
                }else{
                    return "redirect:/admin/solicitudes?tipo=" + tipo;
                }
            }else{
                return "redirect:/admin/solicitudes?tipo=" + tipo; //Retornar pagina principal
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
            return "AdminGen/detalleAdminR";

        }else {
            return "redirect:/admin/solicitudes?tipo=adminRest";
        }
    }

    //@GetMapping("/usuarios1") ///Pagina principal
    //public String listaDeUsuarios(Model model,HttpSession session) {
        //Integer numerop=1;//(10*pag)-10))
        //model.addAttribute("listaUsuarios", usuarioRepository.listaUsuarios());
        //session.setAttribute("pag", numerop);
        //session.setAttribute("total",usuarioRepository.listaUsuarios(1).size());
      //  return "AdminGen/lista";
    //}

    /*@GetMapping(value ="/usuarios2")//funciona
    public String findAll(@RequestParam Map<String, Object> params, Model model) {
        int page = params.get("page") != null ? (Integer.valueOf(params.get("page").toString()) - 1) : 0;

        PageRequest pageRequest = PageRequest.of(page, 10);

        Page<Usuario> pagePersona = usuarioServiceAPI.listaUsuarios(pageRequest);

        int totalPage = pagePersona.getTotalPages();
        if(totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
        model.addAttribute("listaUsuarios", pagePersona.getContent());
        model.addAttribute("current", page + 1);
        model.addAttribute("next", page + 2);
        model.addAttribute("prev", page);
        model.addAttribute("last", totalPage);
        return "AdminGen/lista";
    }*/

    @GetMapping(value ="/usuarios")//lista de usuarios principal
    public String listaUsuarios(@RequestParam Map<String, Object> params, Model model,
                                @RequestParam(value = "texto", required = false) String texto,
                                @RequestParam(value = "estado", required = false) String estado,
                                @RequestParam(value = "idrol", required = false) String idrol,
                                HttpSession session) {

        int page;
        int estadoBuscador = 0;

        try{
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        }catch(NumberFormatException nfe){
            page =0;
        }

        PageRequest pageRequest = PageRequest.of(page, 10);

        if(texto==null){
            texto="";
            session.removeAttribute("texto");

        }else{
            session.setAttribute("texto",texto);
            estadoBuscador=1;
        }


        if(estado==null){
            estado="3";
            session.removeAttribute("estado");
        }else{
            session.setAttribute("estado",estado);
            estadoBuscador=1;
        }


        if(idrol==null){
            idrol="6";
            session.removeAttribute("idrol");
        }else{
            session.setAttribute("idrol",idrol);
            estadoBuscador=1;
        }

        texto= session.getAttribute("texto") == null ? "" :  (String) session.getAttribute("texto");
        estado= session.getAttribute("estado") == null ? "3" :  (String) session.getAttribute("estado");
        idrol= session.getAttribute("idrol") == null ? "6" :  (String) session.getAttribute("idrol");
        Integer inFrol ;
        Integer maXrol ;
        Integer miFestado ;
        Integer maXestado ;
        switch (estado){
            case "3":
                miFestado=-1;
                maXestado=1;
                break;
            case "0":
                miFestado=-1;
                maXestado=0;
                break;
            case "1":
                miFestado=0;
                maXestado=1;
                break;
            default:
                miFestado=-1;
                maXestado=1;


        }


        switch (idrol){
            case "1":
                inFrol = 0;
                maXrol = 1;
                break;
            case "3":
                inFrol = 2;
                maXrol = 3;
                break;
            case "4":
                inFrol = 3;
                maXrol = 4;
                break;

            case "5":
                inFrol = 4;
                maXrol = 5;
                break;
            default:
                inFrol = 0;
                maXrol = 5;
        }




        Page<Usuario> pagePersona = usuarioServiceAPI.listaUsuarios(texto, inFrol, maXrol,  miFestado,  maXestado,  pageRequest);
        int totalPage = pagePersona.getTotalPages();

        System.out.println(totalPage+"----------------------------ddd-ddd");

        if(totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }

// TODO: 27/06/2021
        model.addAttribute("estadoBuscador",estadoBuscador);


        model.addAttribute("texto",texto);
        model.addAttribute("estado",estado);
        model.addAttribute("idrol",idrol);
        model.addAttribute("listaUsuarios", pagePersona.getContent());

        model.addAttribute("current", page + 1);
        model.addAttribute("next", page + 2);
        model.addAttribute("prev", page);
        model.addAttribute("last", totalPage);
        return "AdminGen/lista";
    }
    @GetMapping(value ="/pagina")//lista de usuarios principal
    public String listaUsuariosPagina(@RequestParam Map<String, Object> params, Model model,
                                @RequestParam(value = "texto", required = false) String texto,
                                @RequestParam(value = "estado", required = false) String estado,
                                @RequestParam(value = "idrol", required = false) String idrol,
                                HttpSession session) {
        int page;
        int estadoBuscador = session.getAttribute("texto") != null || session.getAttribute("estado") != null || session.getAttribute("idrol") != null ? 1 :0;
        try{
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        }catch(NumberFormatException nfe){
            page =0;
        }

        PageRequest pageRequest = PageRequest.of(page, 10);
        texto= session.getAttribute("texto") == null ? "" :  (String) session.getAttribute("texto");
        estado= session.getAttribute("estado") == null ? "3" :  (String) session.getAttribute("estado");
        idrol= session.getAttribute("idrol") == null ? "6" :  (String) session.getAttribute("idrol");




        Integer inFrol ;
        Integer maXrol ;
        Integer miFestado ;
        Integer maXestado ;
        switch (estado){
            case "3":
                miFestado=-1;
                maXestado=1;
                break;
            case "0":
                miFestado=-1;
                maXestado=0;
                break;
            case "1":
                miFestado=0;
                maXestado=1;
                break;
            default:
                miFestado=-1;
                maXestado=1;


        }


        switch (idrol){
            case "1":
                inFrol = 0;
                maXrol = 1;
                break;
            case "3":
                inFrol = 2;
                maXrol = 3;
                break;
            case "4":
                inFrol = 3;
                maXrol = 4;
                break;

            case "5":
                inFrol = 4;
                maXrol = 5;
                break;
            default:
                inFrol = 0;
                maXrol = 5;
        }

      Page<Usuario> pagePersona = usuarioServiceAPI.listaUsuarios(texto, inFrol, maXrol,  miFestado,  maXestado,  pageRequest);
        int totalPage = pagePersona.getTotalPages();

        System.out.println(totalPage+"----------------------------ddd-ddd");

        if(totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
// TODO: 27/06/2021
        model.addAttribute("estadoBuscador",estadoBuscador);

        model.addAttribute("texto",texto);
        model.addAttribute("estado",estado);
        model.addAttribute("idrol",idrol);
        model.addAttribute("listaUsuarios", pagePersona.getContent());

        model.addAttribute("current", page + 1);
        model.addAttribute("next", page + 2);
        model.addAttribute("prev", page);
        model.addAttribute("last", totalPage);
        return "AdminGen/lista";
    }

    @GetMapping(value ="/reporteClientesPedido")//lista de reporte de cliente pedido
    public String listaReporteClientesPedido(@RequestParam Map<String, Object> params, Model model,
                                @RequestParam(value = "texto", required = false) String texto,
                                @RequestParam(value = "estado", required = false) String estado,
                                @RequestParam(value = "monto", required = false) String monto,
                                @RequestParam(value = "valoracion", required = false) String valoracion,
                                             HttpSession session) {
        int page;
        int estadoBuscador = 0;

        try{
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        }catch(NumberFormatException nfe){
            page =0;
        }
        PageRequest pageRequest = PageRequest.of(page, 10);
        if(monto==null){
            monto="0";
            session.removeAttribute("monto");
        }else{
            session.setAttribute("monto",monto);
            estadoBuscador=1;
        }
        if(valoracion==null){
            valoracion="0";
            session.removeAttribute("valoracion");

        }else{
            session.setAttribute("valoracion",valoracion);
            estadoBuscador=1;
        }


        if(texto==null){
            texto="";
            session.removeAttribute("texto");

        }else{
            session.setAttribute("texto",texto);
            estadoBuscador=1;
        }


        if(estado==null){
            estado="7";
            session.removeAttribute("estado");
        }else{
            session.setAttribute("estado",estado);
            estadoBuscador=1;
        }

        texto= session.getAttribute("texto") == null ? "" :  (String) session.getAttribute("texto");
        estado= session.getAttribute("estado") == null ? "7" :  (String) session.getAttribute("estado");
        monto= session.getAttribute("monto") == null ? "0" :  (String) session.getAttribute("monto");
        valoracion= session.getAttribute("valoracion") == null ? "0" :  (String) session.getAttribute("valoracion");

        Integer inFmont ;
        Integer maXmont ;
        Integer miFval ;
        Integer maXval ;
        Integer miFestado ;
        Integer maXestado ;

        switch (estado){
            case "7":
                miFestado=-1;
                maXestado=7;
                break;
            case "0":
                miFestado=-1;
                maXestado=0;
                break;
            case "1":
                miFestado=0;
                maXestado=1;
                break;
            case "2":
                miFestado=1;
                maXestado=2;
                break;
            case "3":
                miFestado=2;
                maXestado=3;
                break;
            case "4":
                miFestado=3;
                maXestado=4;
                break;
            case "5":
                miFestado=4;
                maXestado=5;
                break;
            case "6":
                miFestado=5;
                maXestado=6;
                break;
            default:
                miFestado=-1;
                maXestado=7;


        }

        switch (monto){
            case "0":
                inFmont = 0;
                maXmont = 10000;
                break;
            case "1":
                inFmont = 0;
                maXmont = 20;
                break;
            case "2":
                inFmont = 20;
                maXmont = 40;
                break;
            case "3":
                inFmont = 40;
                maXmont = 60;
                break;
            case "4":
                inFmont = 60;
                maXmont = 80;
                break;
            case "5":
                inFmont = 80;
                maXmont = 100;
                break;
            case "6":
                inFmont = 100;
                maXmont = 10000;
                break;
            default:
                inFmont = 0;
                maXmont = 10000;
        }

        switch (valoracion){
            case "0":
                miFval = -1;
                maXval = 7;
                break;
            case "1":
                miFval = 0;
                maXval = 1;
                break;
            case "2":
                miFval = 1;
                maXval = 2;
                break;
            case "3":
                miFval = 2;
                maXval = 3;
                break;
            case "4":
                miFval = 3;
                maXval = 4;
                break;
            case "5":
                miFval = 4;
                maXval = 5;
                break;
            case "6":
                miFval = -1;
                maXval = 0;
                break;
            default:
                miFval = -1;
                maXval = 7;
        }

        Page<UsuarioDtoCliente> pagePersona = usuarioServiceAPIDtoCliente.listaUsuariosDtoCliente(texto,miFval,maXval, inFmont, maXmont,  miFestado,  maXestado,  pageRequest);
        int totalPage = pagePersona.getTotalPages();
        System.out.println(totalPage+"----------------------------ddd-ddd");
        if(totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
        /*List<ClienteConMasPedidosDto> listaClienteConMasPedidos = usuarioRepository.listaClienteConMasPedidos();
        model.addAttribute("listaClienteConMasPedidos", listaClienteConMasPedidos.get(0));
        System.out.println(listaClienteConMasPedidos.get(0));
        model.addAttribute("listaClienteConMenosPedidos",listaClienteConMasPedidos.get(listaClienteConMasPedidos.size()-1));
        System.out.println(listaClienteConMasPedidos.get(listaClienteConMasPedidos.size()-1));*/

        // TODO: 27/06/2021
        model.addAttribute("estadoBuscador",estadoBuscador);

        model.addAttribute("texto",texto);
        model.addAttribute("estado",estado);
        model.addAttribute("monto",monto);
        model.addAttribute("valoracion",valoracion);
        model.addAttribute("listaUsuarios", pagePersona.getContent());

        model.addAttribute("current", page + 1);
        model.addAttribute("next", page + 2);
        model.addAttribute("prev", page);
        model.addAttribute("last", totalPage);
        return "AdminGen/reportePedidoCliente";
    }
    @GetMapping(value ="/reporteClientesPedidoPagina")//lista de reporte de cliente pedido Pagina
    public String listaReporteClientesPedidoPagina(@RequestParam Map<String, Object> params, Model model,
                                             @RequestParam(value = "texto", required = false) String texto,
                                             @RequestParam(value = "estado", required = false) String estado,
                                             @RequestParam(value = "monto", required = false) String monto,
                                             @RequestParam(value = "valoracion", required = false) String valoracion,
                                             HttpSession session) {
        int page;
        int estadoBuscador = session.getAttribute("texto") != null || session.getAttribute("estado") != null || session.getAttribute("idrol") != null ? 1 :0;

        try{
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        }catch(NumberFormatException nfe){
            page =0;
        }

        PageRequest pageRequest = PageRequest.of(page, 10);

        texto= session.getAttribute("texto") == null ? "" :  (String) session.getAttribute("texto");
        estado= session.getAttribute("estado") == null ? "7" :  (String) session.getAttribute("estado");
        monto= session.getAttribute("monto") == null ? "0" :  (String) session.getAttribute("monto");
        valoracion= session.getAttribute("valoracion") == null ? "0" :  (String) session.getAttribute("valoracion");

        Integer inFmont ;
        Integer maXmont ;
        Integer miFval ;
        Integer maXval ;
        Integer miFestado ;
        Integer maXestado ;

        switch (estado){
            case "7":
                miFestado=-1;
                maXestado=7;
                break;
            case "0":
                miFestado=-1;
                maXestado=0;
                break;
            case "1":
                miFestado=0;
                maXestado=1;
                break;
            case "2":
                miFestado=1;
                maXestado=2;
                break;
            default:
                miFestado=-1;
                maXestado=7;


        }

        switch (monto){
            case "0":
                inFmont = 0;
                maXmont = 10000;
                break;
            case "1":
                inFmont = 0;
                maXmont = 20;
                break;
            case "2":
                inFmont = 20;
                maXmont = 40;
                break;
            case "3":
                inFmont = 40;
                maXmont = 60;
                break;
            case "4":
                inFmont = 60;
                maXmont = 80;
                break;
            case "5":
                inFmont = 80;
                maXmont = 100;
                break;
            case "6":
                inFmont = 100;
                maXmont = 10000;
                break;
            default:
                inFmont = 0;
                maXmont = 10000;
        }

        switch (valoracion){
            case "0":
                miFval = -1;
                maXval = 7;
                break;
            case "1":
                miFval = 0;
                maXval = 1;
                break;
            case "2":
                miFval = 1;
                maXval = 2;
                break;
            case "3":
                miFval = 2;
                maXval = 3;
                break;
            case "4":
                miFval = 3;
                maXval = 4;
                break;
            case "5":
                miFval = 4;
                maXval = 5;
                break;
            case "6":
                miFval = -1;
                maXval = 0;
                break;
            default:
                miFval = -1;
                maXval = 7;
        }

       Page<UsuarioDtoCliente> pagePersona = usuarioServiceAPIDtoCliente.listaUsuariosDtoCliente(texto,miFval,maXval, inFmont, maXmont,  miFestado,  maXestado,  pageRequest);
        int totalPage = pagePersona.getTotalPages();
        System.out.println(totalPage+"----------------------------ddd-ddd");
        if(totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
        /*List<ClienteConMasPedidosDto> listaClienteConMasPedidos = usuarioRepository.listaClienteConMasPedidos();
        model.addAttribute("listaClienteConMasPedidos", listaClienteConMasPedidos.get(0));
        model.addAttribute("listaClienteConMenosPedidos",listaClienteConMasPedidos.get(listaClienteConMasPedidos.size()-1));
*/
        // TODO: 27/06/2021
        model.addAttribute("estadoBuscador",estadoBuscador);
        model.addAttribute("texto",texto);
        model.addAttribute("estado",estado);
        model.addAttribute("monto",monto);
        model.addAttribute("valoracion",valoracion);
        model.addAttribute("listaUsuarios", pagePersona.getContent());

        model.addAttribute("current", page + 1);
        model.addAttribute("next", page + 2);
        model.addAttribute("prev", page);
        model.addAttribute("last", totalPage);
        return "AdminGen/reportePedidoCliente";
    }

    @GetMapping(value ="/usuariosR")//Reporte de usuarios
    public String listaUsuariosR(@RequestParam Map<String, Object> params, Model model,
                                @RequestParam(value = "texto", required = false) String texto,
                                @RequestParam(value = "estado", required = false) String estado,
                                @RequestParam(value = "idrol", required = false) String idrol,
                                HttpSession session) {

        int page;
        int estadoBuscador = 0;
        try{
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        }catch(NumberFormatException nfe){
            page =0;
        }

        PageRequest pageRequest = PageRequest.of(page, 10);
        if(texto==null){
            texto="";
            session.removeAttribute("texto");

        }else{
            session.setAttribute("texto",texto);
            estadoBuscador=1;
        }


        if(estado==null){
            estado="3";
            session.removeAttribute("estado");
        }else{
            session.setAttribute("estado",estado);
            estadoBuscador=1;
        }


        if(idrol==null){
            idrol="6";
            session.removeAttribute("idrol");
        }else{
            session.setAttribute("idrol",idrol);
            estadoBuscador=1;
        }

        texto= session.getAttribute("texto") == null ? "" :  (String) session.getAttribute("texto");
        estado= session.getAttribute("estado") == null ? "3" :  (String) session.getAttribute("estado");
        idrol= session.getAttribute("idrol") == null ? "6" :  (String) session.getAttribute("idrol");
        Integer inFrol ;
        Integer maXrol ;
        Integer miFestado ;
        Integer maXestado ;
        switch (estado){
            case "3":
                miFestado=-1;
                maXestado=1;
                break;
            case "0":
                miFestado=-1;
                maXestado=0;
                break;
            case "1":
                miFestado=0;
                maXestado=1;
                break;
            default:
                miFestado=-1;
                maXestado=1;


        }


        switch (idrol){
            case "1":
                inFrol = 0;
                maXrol = 1;
                break;
            case "3":
                inFrol = 2;
                maXrol = 3;
                break;
            case "4":
                inFrol = 3;
                maXrol = 4;
                break;

            case "5":
                inFrol = 4;
                maXrol = 5;
                break;
            default:
                inFrol = 0;
                maXrol = 5;
        }




        Page<Usuario> pagePersona = usuarioServiceAPI.listaUsuarios(texto, inFrol, maXrol,  miFestado,  maXestado,  pageRequest);
        int totalPage = pagePersona.getTotalPages();

        System.out.println(totalPage+"----------------------------ddd-ddd");

        if(totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
// TODO: 27/06/2021
        model.addAttribute("estadoBuscador",estadoBuscador);

        model.addAttribute("texto",texto);
        model.addAttribute("estado",estado);
        model.addAttribute("idrol",idrol);
        model.addAttribute("listaUsuarios", pagePersona.getContent());

        model.addAttribute("current", page + 1);
        model.addAttribute("next", page + 2);
        model.addAttribute("prev", page);
        model.addAttribute("last", totalPage);
        return "AdminGen/reporteUsuarios";
    }

    @GetMapping(value ="/usuariosRPagina")//Reporte de usuarios
    public String listaUsuariosRPagina(@RequestParam Map<String, Object> params, Model model,
                                 @RequestParam(value = "texto", required = false) String texto,
                                 @RequestParam(value = "estado", required = false) String estado,
                                 @RequestParam(value = "idrol", required = false) String idrol,
                                 HttpSession session) {

        int page;
        int estadoBuscador = session.getAttribute("texto") != null || session.getAttribute("estado") != null || session.getAttribute("idrol") != null ? 1 :0;

        try{
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        }catch(NumberFormatException nfe){
            page =0;
        }

        PageRequest pageRequest = PageRequest.of(page, 10);
        texto= session.getAttribute("texto") == null ? "" :  (String) session.getAttribute("texto");
        estado= session.getAttribute("estado") == null ? "3" :  (String) session.getAttribute("estado");
        idrol= session.getAttribute("idrol") == null ? "6" :  (String) session.getAttribute("idrol");
        Integer inFrol ;
        Integer maXrol ;
        Integer miFestado ;
        Integer maXestado ;
        switch (estado){
            case "3":
                miFestado=-1;
                maXestado=1;
                break;
            case "0":
                miFestado=-1;
                maXestado=0;
                break;
            case "1":
                miFestado=0;
                maXestado=1;
                break;
            default:
                miFestado=-1;
                maXestado=1;


        }


        switch (idrol){
            case "1":
                inFrol = 0;
                maXrol = 1;
                break;
            case "3":
                inFrol = 2;
                maXrol = 3;
                break;
            case "4":
                inFrol = 3;
                maXrol = 4;
                break;

            case "5":
                inFrol = 4;
                maXrol = 5;
                break;
            default:
                inFrol = 0;
                maXrol = 5;
        }



       Page<Usuario> pagePersona = usuarioServiceAPI.listaUsuarios(texto, inFrol, maXrol,  miFestado,  maXestado,  pageRequest);
        int totalPage = pagePersona.getTotalPages();

        System.out.println(totalPage+"----------------------------ddd-ddd");

        if(totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
// TODO: 27/06/2021
        model.addAttribute("estadoBuscador",estadoBuscador);

        model.addAttribute("texto",texto);
        model.addAttribute("estado",estado);
        model.addAttribute("idrol",idrol);
        model.addAttribute("listaUsuarios", pagePersona.getContent());

        model.addAttribute("current", page + 1);
        model.addAttribute("next", page + 2);
        model.addAttribute("prev", page);
        model.addAttribute("last", totalPage);
        return "AdminGen/reporteUsuarios";
    }

    @GetMapping(value ="/reporteRepartidorPedido")//lista de reporte de Repartidor pedido
    public String listaReporteRepartidorPedido(@RequestParam Map<String, Object> params, Model model,
                                             @RequestParam(value = "texto", required = false) String texto,
                                             @RequestParam(value = "cantidad", required = false) String cantidad,
                                             @RequestParam(value = "monto", required = false) String monto,
                                             @RequestParam(value = "valoracion", required = false) String valoracion,
                                             HttpSession session) {

        int page;
        int estadoBuscador = 0;
        try{
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        }catch(NumberFormatException nfe){
            page =0;
        }

        PageRequest pageRequest = PageRequest.of(page, 10);
        if(monto==null){
            monto="0";
            session.removeAttribute("monto");
        }else{
            session.setAttribute("monto",monto);
            estadoBuscador=1;
        }
        if(valoracion==null){
            valoracion="0";
            session.removeAttribute("valoracion");

        }else{
            session.setAttribute("valoracion",valoracion);
            estadoBuscador=1;
        }


        if(texto==null){
            texto="";
            session.removeAttribute("texto");

        }else{
            session.setAttribute("texto",texto);
            estadoBuscador=1;
        }


        if(cantidad==null){
            cantidad="7";
            session.removeAttribute("cantidad");
        }else{
            session.setAttribute("cantidad",cantidad);
            estadoBuscador=1;
        }

        texto= session.getAttribute("texto") == null ? "" :  (String) session.getAttribute("texto");
        cantidad= session.getAttribute("cantidad") == null ? "7" :  (String) session.getAttribute("cantidad");
        monto= session.getAttribute("monto") == null ? "0" :  (String) session.getAttribute("monto");
        valoracion= session.getAttribute("valoracion") == null ? "0" :  (String) session.getAttribute("valoracion");

        Integer inFmont ;
        Integer maXmont ;
        Integer miFval ;
        Integer maXval ;
        Integer miFestado ;
        Integer maXestado ;

        switch (cantidad){
            case "7":
                miFestado=-1;
                maXestado=100000;
                break;
            case "0":
                miFestado=-1;
                maXestado=50;
                break;
            case "1":
                miFestado=50;
                maXestado=100;
                break;
            case "2":
                miFestado=100;
                maXestado=150;
                break;
            case "3":
                miFestado=150;
                maXestado=200;
                break;
            case "4":
                miFestado=200;
                maXestado=250;
                break;
            case "5":
                miFestado=250;
                maXestado=300;
                break;
            case "6":
                miFestado=300;
                maXestado=100000;
                break;
            default:
                miFestado=-1;
                maXestado=100000;
        }

        switch (monto){
            case "0":
                inFmont = 0;
                maXmont = 10000;
                break;
            case "1":
                inFmont = 0;
                maXmont = 200;
                break;
            case "2":
                inFmont = 200;
                maXmont = 400;
                break;
            case "3":
                inFmont = 400;
                maXmont = 600;
                break;
            case "4":
                inFmont = 600;
                maXmont = 800;
                break;
            case "5":
                inFmont = 800;
                maXmont = 1000;
                break;
            case "6":
                inFmont = 1000;
                maXmont = 100000;
                break;
            default:
                inFmont = 0;
                maXmont = 100000;
        }

        switch (valoracion){
            case "0":
                miFval = -1;
                maXval = 7;
                break;
            case "1":
                miFval = 0;
                maXval = 1;
                break;
            case "2":
                miFval = 1;
                maXval = 2;
                break;
            case "3":
                miFval = 2;
                maXval = 3;
                break;
            case "4":
                miFval = 3;
                maXval = 4;
                break;
            case "5":
                miFval = 4;
                maXval = 5;
                break;
            case "6":
                miFval = -1;
                maXval = 0;
                break;
            default:
                miFval = -1;
                maXval = 7;
        }



        Page<UsuarioDtoRepartidor> pagePersona = usuarioServiceAPIDtoRepartidor.listaUsuariosDtoRepartidor(texto,miFval,maXval,  miFestado,  maXestado, inFmont, maXmont,  pageRequest);
        int totalPage = pagePersona.getTotalPages();
        System.out.println(totalPage+"----------------------------ddd-ddd");
        if(totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
       /* List<ClienteConMasPedidosDto> listaClienteConMasPedidos = usuarioRepository.listaClienteConMasPedidos();
        model.addAttribute("listaClienteConMasPedidos", listaClienteConMasPedidos.get(0));
        System.out.println(listaClienteConMasPedidos.get(0));
        model.addAttribute("listaClienteConMenosPedidos",listaClienteConMasPedidos.get(listaClienteConMasPedidos.size()-1));
        System.out.println(listaClienteConMasPedidos.get(listaClienteConMasPedidos.size()-1));*/

        // TODO: 27/06/2021
        model.addAttribute("estadoBuscador",estadoBuscador);
        model.addAttribute("texto",texto);
        model.addAttribute("cantidad",cantidad);
        model.addAttribute("monto",monto);
        model.addAttribute("valoracion",valoracion);
        model.addAttribute("listaUsuarios", pagePersona.getContent());

        model.addAttribute("current", page + 1);
        model.addAttribute("next", page + 2);
        model.addAttribute("prev", page);
        model.addAttribute("last", totalPage);
        return "AdminGen/reportePedidoRepartidor";
    }
    @GetMapping(value ="/reporteRepartidorPedidoPagina")//lista de reporte de Repartidor pedido
    public String listaReporteRepartidorPedidoPagina(@RequestParam Map<String, Object> params, Model model,
                                               @RequestParam(value = "texto", required = false) String texto,
                                               @RequestParam(value = "cantidad", required = false) String cantidad,
                                               @RequestParam(value = "monto", required = false) String monto,
                                               @RequestParam(value = "valoracion", required = false) String valoracion,
                                               HttpSession session) {

        int page;
        int estadoBuscador = session.getAttribute("texto") != null || session.getAttribute("valoracion") != null || session.getAttribute("cantidad") != null || session.getAttribute("valoracion") != null  ? 1 :0;

        try{
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        }catch(NumberFormatException nfe){
            page =0;
        }

        PageRequest pageRequest = PageRequest.of(page, 10);
        texto= session.getAttribute("texto") == null ? "" :  (String) session.getAttribute("texto");
        cantidad= session.getAttribute("cantidad") == null ? "7" :  (String) session.getAttribute("cantidad");
        monto= session.getAttribute("monto") == null ? "0" :  (String) session.getAttribute("monto");
        valoracion= session.getAttribute("valoracion") == null ? "0" :  (String) session.getAttribute("valoracion");

        Integer inFmont ;
        Integer maXmont ;
        Integer miFval ;
        Integer maXval ;
        Integer miFestado ;
        Integer maXestado ;

        switch (cantidad){
            case "7":
                miFestado=-1;
                maXestado=100000;
                break;
            case "0":
                miFestado=-1;
                maXestado=50;
                break;
            case "1":
                miFestado=50;
                maXestado=100;
                break;
            case "2":
                miFestado=100;
                maXestado=150;
                break;
            case "3":
                miFestado=150;
                maXestado=200;
                break;
            case "4":
                miFestado=200;
                maXestado=250;
                break;
            case "5":
                miFestado=250;
                maXestado=300;
                break;
            case "6":
                miFestado=300;
                maXestado=100000;
                break;
            default:
                miFestado=-1;
                maXestado=100000;
        }

        switch (monto){
            case "0":
                inFmont = 0;
                maXmont = 10000;
                break;
            case "1":
                inFmont = 0;
                maXmont = 200;
                break;
            case "2":
                inFmont = 200;
                maXmont = 400;
                break;
            case "3":
                inFmont = 400;
                maXmont = 600;
                break;
            case "4":
                inFmont = 600;
                maXmont = 800;
                break;
            case "5":
                inFmont = 800;
                maXmont = 1000;
                break;
            case "6":
                inFmont = 1000;
                maXmont = 100000;
                break;
            default:
                inFmont = 0;
                maXmont = 100000;
        }

        switch (valoracion){
            case "0":
                miFval = -1;
                maXval = 7;
                break;
            case "1":
                miFval = 0;
                maXval = 1;
                break;
            case "2":
                miFval = 1;
                maXval = 2;
                break;
            case "3":
                miFval = 2;
                maXval = 3;
                break;
            case "4":
                miFval = 3;
                maXval = 4;
                break;
            case "5":
                miFval = 4;
                maXval = 5;
                break;
            case "6":
                miFval = -1;
                maXval = 0;
                break;
            default:
                miFval = -1;
                maXval = 7;
        }

        Page<UsuarioDtoRepartidor> pagePersona = usuarioServiceAPIDtoRepartidor.listaUsuariosDtoRepartidor(texto,miFval,maXval,  miFestado,  maXestado, inFmont, maXmont,  pageRequest);
        int totalPage = pagePersona.getTotalPages();
        System.out.println(totalPage+"----------------------------ddd-ddd");
        if(totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
      /*  List<ClienteConMasPedidosDto> listaClienteConMasPedidos = usuarioRepository.listaClienteConMasPedidos();
        model.addAttribute("listaClienteConMasPedidos", listaClienteConMasPedidos.get(0));
        System.out.println(listaClienteConMasPedidos.get(0));
        model.addAttribute("listaClienteConMenosPedidos",listaClienteConMasPedidos.get(listaClienteConMasPedidos.size()-1));
        System.out.println(listaClienteConMasPedidos.get(listaClienteConMasPedidos.size()-1));*/

        // TODO: 27/06/2021
        model.addAttribute("estadoBuscador",estadoBuscador);

        model.addAttribute("texto",texto);
        model.addAttribute("cantidad",cantidad);
        model.addAttribute("monto",monto);
        model.addAttribute("valoracion",valoracion);
        model.addAttribute("listaUsuarios", pagePersona.getContent());
        model.addAttribute("current", page + 1);
        model.addAttribute("next", page + 2);
        model.addAttribute("prev", page);
        model.addAttribute("last", totalPage);
        return "AdminGen/reportePedidoRepartidor";
    }
// TODO: 26/06/2021
@GetMapping(value ="/ReporteVentas")//lista de reporte de Repartidor pedido
public String listaReporteVentas(@RequestParam Map<String, Object> params, Model model,
                                           @RequestParam(value = "texto", required = false) String texto,
                                           @RequestParam(value = "cantidad", required = false) String cantidad,
                                           @RequestParam(value = "monto", required = false) String monto,
                                           @RequestParam(value = "valoracion", required = false) String valoracion,
                                           HttpSession session) {

    int page;
    int estadoBuscador = 0;
    try{
        page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
    }catch(NumberFormatException nfe){
        page =0;
    }

    PageRequest pageRequest = PageRequest.of(page, 10);
    if(monto==null){
        monto="0";
        session.removeAttribute("monto");
    }else{
        session.setAttribute("monto",monto);
        estadoBuscador=1;
    }
    if(valoracion==null){
        valoracion="0";
        session.removeAttribute("valoracion");

    }else{
        session.setAttribute("valoracion",valoracion);
        estadoBuscador=1;
    }


    if(texto==null){
        texto="";
        session.removeAttribute("texto");

    }else{
        session.setAttribute("texto",texto);
        estadoBuscador=1;
    }


    if(cantidad==null){
        cantidad="7";
        session.removeAttribute("cantidad");
    }else{
        session.setAttribute("cantidad",cantidad);
        estadoBuscador=1;
    }

    texto= session.getAttribute("texto") == null ? "" :  (String) session.getAttribute("texto");
    cantidad= session.getAttribute("cantidad") == null ? "7" :  (String) session.getAttribute("cantidad");
    monto= session.getAttribute("monto") == null ? "0" :  (String) session.getAttribute("monto");
    valoracion= session.getAttribute("valoracion") == null ? "0" :  (String) session.getAttribute("valoracion");

    Integer inFmont ;
    Integer maXmont ;
    Integer miFval ;
    Integer maXval ;
    Integer miFestado ;
    Integer maXestado ;

    switch (cantidad){
        case "7":
            miFestado=-1;
            maXestado=100000;
            break;
        case "0":
            miFestado=-1;
            maXestado=100;
            break;
        case "1":
            miFestado=100;
            maXestado=200;
            break;
        case "2":
            miFestado=200;
            maXestado=300;
            break;
        case "3":
            miFestado=300;
            maXestado=400;
            break;
        case "4":
            miFestado=400;
            maXestado=500;
            break;
        case "5":
            miFestado=500;
            maXestado=600;
            break;
        case "6":
            miFestado=600;
            maXestado=100000;
            break;
        default:
            miFestado=-1;
            maXestado=100000;
    }

    switch (monto){
        case "0":
            inFmont = 0;
            maXmont = 10000;
            break;
        case "1":
            inFmont = 0;
            maXmont = 2000;
            break;
        case "2":
            inFmont = 2000;
            maXmont = 4000;
            break;
        case "3":
            inFmont = 4000;
            maXmont = 6000;
            break;
        case "4":
            inFmont = 6000;
            maXmont = 8000;
            break;
        case "5":
            inFmont = 8000;
            maXmont = 10000;
            break;
        case "6":
            inFmont = 10000;
            maXmont = 1000000;
            break;
        default:
            inFmont = 0;
            maXmont = 100000;
    }

    switch (valoracion){
        case "0":
            miFval = -1;
            maXval = 7;
            break;
        case "1":
            miFval = 0;
            maXval = 1;
            break;
        case "2":
            miFval = 1;
            maXval = 2;
            break;
        case "3":
            miFval = 2;
            maXval = 3;
            break;
        case "4":
            miFval = 3;
            maXval = 4;
            break;
        case "5":
            miFval = 4;
            maXval = 5;
            break;
        case "6":
            miFval = -1;
            maXval = 0;
            break;
        default:
            miFval = -1;
            maXval = 7;
    }



    Page<UsuarioDtoReporteVentas> pagePersona = usuarioServiceAPIDtoReporteVentas.listaUsuariosDtoReporteVentas(texto,miFval,maXval,  miFestado,  maXestado, inFmont, maXmont,  pageRequest);
    int totalPage = pagePersona.getTotalPages();
    System.out.println(totalPage+"----------------------------ddd-ddd");
    if(totalPage > 0) {
        List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
        model.addAttribute("pages", pages);
    }
    /*List<ClienteConMasPedidosDto> listaClienteConMasPedidos = usuarioRepository.listaClienteConMasPedidos();
    model.addAttribute("listaClienteConMasPedidos", listaClienteConMasPedidos.get(0));
    System.out.println(listaClienteConMasPedidos.get(0));
    model.addAttribute("listaClienteConMenosPedidos",listaClienteConMasPedidos.get(listaClienteConMasPedidos.size()-1));
    System.out.println(listaClienteConMasPedidos.get(listaClienteConMasPedidos.size()-1));*/

    // TODO: 27/06/2021
    model.addAttribute("estadoBuscador",estadoBuscador);

    model.addAttribute("texto",texto);
    model.addAttribute("cantidad",cantidad);
    model.addAttribute("monto",monto);
    model.addAttribute("valoracion",valoracion);
    model.addAttribute("listaUsuarios", pagePersona.getContent());

    model.addAttribute("current", page + 1);
    model.addAttribute("next", page + 2);
    model.addAttribute("prev", page);
    model.addAttribute("last", totalPage);
    return "AdminGen/reporteVentas";
}
    @GetMapping(value ="/ReporteVentasPagina")//lista de reporte de Repartidor pedido
    public String listaReporteVentasPagina(@RequestParam Map<String, Object> params, Model model,
                                     @RequestParam(value = "texto", required = false) String texto,
                                     @RequestParam(value = "cantidad", required = false) String cantidad,
                                     @RequestParam(value = "monto", required = false) String monto,
                                     @RequestParam(value = "valoracion", required = false) String valoracion,
                                     HttpSession session) {

        int page;
        int estadoBuscador = session.getAttribute("texto") != null || session.getAttribute("cantidad") != null || session.getAttribute("monto") != null || session.getAttribute("valoracion") != null ? 1 :0;

        try{
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        }catch(NumberFormatException nfe){
            page =0;
        }

        PageRequest pageRequest = PageRequest.of(page, 10);

        texto= session.getAttribute("texto") == null ? "" :  (String) session.getAttribute("texto");
        cantidad= session.getAttribute("cantidad") == null ? "7" :  (String) session.getAttribute("cantidad");
        monto= session.getAttribute("monto") == null ? "0" :  (String) session.getAttribute("monto");
        valoracion= session.getAttribute("valoracion") == null ? "0" :  (String) session.getAttribute("valoracion");

        Integer inFmont ;
        Integer maXmont ;
        Integer miFval ;
        Integer maXval ;
        Integer miFestado ;
        Integer maXestado ;

        switch (cantidad){
            case "7":
                miFestado=-1;
                maXestado=100000;
                break;
            case "0":
                miFestado=-1;
                maXestado=100;
                break;
            case "1":
                miFestado=100;
                maXestado=200;
                break;
            case "2":
                miFestado=200;
                maXestado=300;
                break;
            case "3":
                miFestado=300;
                maXestado=400;
                break;
            case "4":
                miFestado=400;
                maXestado=500;
                break;
            case "5":
                miFestado=500;
                maXestado=600;
                break;
            case "6":
                miFestado=600;
                maXestado=100000;
                break;
            default:
                miFestado=-1;
                maXestado=100000;
        }

        switch (monto){
            case "0":
                inFmont = 0;
                maXmont = 10000;
                break;
            case "1":
                inFmont = 0;
                maXmont = 2000;
                break;
            case "2":
                inFmont = 2000;
                maXmont = 4000;
                break;
            case "3":
                inFmont = 4000;
                maXmont = 6000;
                break;
            case "4":
                inFmont = 6000;
                maXmont = 8000;
                break;
            case "5":
                inFmont = 8000;
                maXmont = 10000;
                break;
            case "6":
                inFmont = 10000;
                maXmont = 1000000;
                break;
            default:
                inFmont = 0;
                maXmont = 100000;
        }

        switch (valoracion){
            case "0":
                miFval = -1;
                maXval = 7;
                break;
            case "1":
                miFval = 0;
                maXval = 1;
                break;
            case "2":
                miFval = 1;
                maXval = 2;
                break;
            case "3":
                miFval = 2;
                maXval = 3;
                break;
            case "4":
                miFval = 3;
                maXval = 4;
                break;
            case "5":
                miFval = 4;
                maXval = 5;
                break;
            case "6":
                miFval = -1;
                maXval = 0;
                break;
            default:
                miFval = -1;
                maXval = 7;
        }



        Page<UsuarioDtoReporteVentas> pagePersona = usuarioServiceAPIDtoReporteVentas.listaUsuariosDtoReporteVentas(texto,miFval,maXval,  miFestado,  maXestado, inFmont, maXmont,  pageRequest);
        int totalPage = pagePersona.getTotalPages();
        System.out.println(totalPage+"----------------------------ddd-ddd");
        if(totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
        /*List<ClienteConMasPedidosDto> listaClienteConMasPedidos = usuarioRepository.listaClienteConMasPedidos();
        model.addAttribute("listaClienteConMasPedidos", listaClienteConMasPedidos.get(0));
        System.out.println(listaClienteConMasPedidos.get(0));
        model.addAttribute("listaClienteConMenosPedidos",listaClienteConMasPedidos.get(listaClienteConMasPedidos.size()-1));
        System.out.println(listaClienteConMasPedidos.get(listaClienteConMasPedidos.size()-1));*/

        // TODO: 27/06/2021
        model.addAttribute("estadoBuscador",estadoBuscador);

        model.addAttribute("texto",texto);
        model.addAttribute("cantidad",cantidad);
        model.addAttribute("monto",monto);
        model.addAttribute("valoracion",valoracion);
        model.addAttribute("listaUsuarios", pagePersona.getContent());

        model.addAttribute("current", page + 1);
        model.addAttribute("next", page + 2);
        model.addAttribute("prev", page);
        model.addAttribute("last", totalPage);
        return "AdminGen/reporteVentas";
    }

    @GetMapping("/buscador2")
    public String buscadorUsuario2(@RequestParam Map<String, Object> params, Model model){

        String texto = (String) params.get("texto");
        Integer fechaRegsitro = (Integer) params.get("fechaRegsitro");
        Integer idRol = (Integer) params.get("idRol");
        Integer estado = (Integer) params.get("estado");
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
        return "AdminGen/lista";
    }

    @GetMapping("/buscador1")
    public String buscadorUsuario1(@RequestParam(value = "texto",required = false) String texto,
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

        return "AdminGen/lista";
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
        return "AdminGen/visualizarCliente";
    }

    @GetMapping("/detalle")
    public String detalleUsuario(@RequestParam("idUsuario") String idUsuario,
                                 Model model) {
        try{
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(Integer.parseInt(idUsuario));
            double totalIngresos = 0.0;


            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();

                for(int i = 0; i < usuario.getListaPedidosPorUsuario().size(); i++) {
                    totalIngresos += usuario.getListaPedidosPorUsuario().get(i).getPreciototal();
                }

                switch (usuario.getRol().getTipo()) {
                    case "administrador":
                        model.addAttribute("administrador",usuario);
                        return "AdminGen/visualizarAdministrador";

                    case "repartidor":
                        model.addAttribute("repartidor",usuario);
                        model.addAttribute("ganancia",usuarioRepository.gananciaRepartidor(Integer.parseInt(idUsuario)));
                        model.addAttribute("valoracion",usuarioRepository.valoracionRepartidor(Integer.parseInt(idUsuario)));
                        model.addAttribute("direcciones", ubicacionRepository.findByUsuarioVal(usuario));
                   //     model.addAttribute("totalIngresos", totalIngresos);
                        return "AdminGen/visualizarRepartidor";
                    case "cliente":
                        //TODO ver que solo sean los pedidos entregados
                        model.addAttribute("cliente",usuario);
                        model.addAttribute("totalIngresos", totalIngresos);
                        model.addAttribute("direcciones", ubicacionRepository.findByUsuarioVal(usuario));
                        return "AdminGen/visualizarCliente";
                    case "administradorR":
                        model.addAttribute("administradorRestaurante",usuario);
                       return "AdminGen/visualizarAdministradorRestaurante";
                    default:
                        //TODO ver si enviar con mensaje de alerta
                        return "redirect:/admin/usuarios";
                }
            }else {
                //TODO ver si enviar con mensaje de alerta
                return "redirect:/admin/usuarios";
            }

        }catch (NullPointerException e){

            return "redirect:/admin/usuarios";

        }catch (NumberFormatException ee){

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
            String contenido = "Hola "+ usuario.getNombres()+" tu cuenta aceptada";
            sendEmail(usuario.getCorreo(), "Cuenta Aceptada", contenido);

        }
        return "redirect:/admin/solicitudes";

    }

    /* @GetMapping("/bloqueado")
    public String bloquearUsuario(Model model,
                                  @RequestParam("id") int id) {

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);

        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            usuario.setEstado(0);
            usuarioRepository.save(usuario);

        }
        return "redirect:/admin/solicitudes";

    }*/

    @GetMapping("/actualizar")
    public String actualizarEstado(@RequestParam("idUsuario") String  id, RedirectAttributes attr){

        try {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(Integer.parseInt(id));

        if(usuarioOpt.isPresent()){
            Usuario usuario = usuarioOpt.get();
            // ESTADOS
            /*
                0 - BLOQUEDO
                1 - ACTIVO
                2 - PENDIENTE
                DESBLOQUEAR: DE 0 - 1
            */
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
                    return "redirect:/admin/usuarios";

                     }
        }else{
            return "redirect:/admin/usuarios";
        }

        }catch (NullPointerException e){

            return "redirect:/admin/usuarios";

        }catch (NumberFormatException ee){

            return "redirect:/admin/usuarios";

        }finally{

            return "redirect:/admin/usuarios";

        }

    }


    @GetMapping("/crear")
    public String crearAdministrador(@ModelAttribute("usuario") Usuario usuario,HttpSession httpSession,Model model) {

        Usuario usuario2 = (Usuario) httpSession.getAttribute("usuario");

        if (usuario2.getRol().getIdrol()!=2){
           model.addAttribute("listaUsuarios", usuarioRepository.findAll());
            return "redirect:/admin/usuarios";

        }else{
            return "AdminGen/crearAdmin";

        }



    }


    @GetMapping("/editarPerfil")
    public String editarAdministrador(HttpSession httpSession, Model model) {

        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        model.addAttribute("usuario", usuario);

        return "AdminGen/crearAdmin";
    }



    @PostMapping("/guardar")
    public String guardarAdmin(@ModelAttribute("usuario") @Valid Usuario usuario,
                               BindingResult bindingResult, RedirectAttributes attr) throws MessagingException {

        // TODO: 8/05/2021 Falta validar que no se repita el correo y dni
        if(bindingResult.hasErrors()){
            return "AdminGen/crearAdmin";
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
                //sendHtmlMailREgistrado(usuario.getCorreo(), "Cuenta Administrador creado html", usuario);

                /////-----------------------------------------------/////



                return "redirect:/admin/solicitudes";
            } else {
                usuarioRepository.save(usuario);
                attr.addFlashAttribute("msg", "Administrador actualizado exitosamente");
                return "redirect:/admin/solicitudes";
            }
        }
    }
    @GetMapping("/fotoAdmin")
    public ResponseEntity<byte[]> mostrarPerfilAdmin(@RequestParam("id") int id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            byte[] image = usuario.getFoto();

            // HttpHeaders permiten al cliente y al servidor enviar información adicional junto a una petición o respuesta.
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(usuario.getFotocontenttype()));

            return new ResponseEntity<>(image, httpHeaders, HttpStatus.OK);

        } else {
            return null;
        }
    }


    @PostMapping("/guardarAdmin")//error para guardar
    public String guardarAdministrador(@ModelAttribute("usuario") @Valid Usuario usuario,
                                 BindingResult bindingResult2,
                                       @RequestParam("photo") MultipartFile file,
                                       Model model, RedirectAttributes attr) throws MessagingException {

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
        Boolean validarFoto = true;
        String fileName = "";
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
        if (file != null) {

            System.out.println(file);
            if (file.isEmpty()) {
                model.addAttribute("mensajefoto", "Debe subir una imagen");
                validarFoto = false;
            } else if (!file.getContentType().contains("jpeg") && !file.getContentType().contains("png") && !file.getContentType().contains("web")) {

                model.addAttribute("mensajefoto", "Ingrese un formato de imagen válido (p.e. JPEG,PNG o WEBP)");
                validarFoto = false;
            }
            fileName = file.getOriginalFilename();
            if (fileName.contains("..")) {
                model.addAttribute("mensajefoto", "No se premite '..' een el archivo");
                return "Repartidor/registro";
            }
        }


        UsuarioDao ud = new UsuarioDao();
        ValidarDniDTO udto = ud.validarDni(usuario.getDni());
        boolean dni_val = true;
        boolean usuario_val = true;
        boolean usuario_null = true;

        boolean apellido_val = true;
        boolean nombre_val = true;

        if(udto.getSuccess().equals("true")){
            if(cleanString(usuario.getDni()).equals(udto.getRuc())){
                dni_val = false;
                // se uso contains para validar 3 nombres
                if(udto.getApellido_materno() != null && udto.getApellido_paterno() != null && udto.getNombres() != null){
                    usuario_null = false;
                    if((cleanString(usuario.getNombres()) + " " + cleanString(usuario.getApellidos())).equalsIgnoreCase(udto.getNombres() + " " + udto.getApellido_paterno() + " " + udto.getApellido_materno())){
                        usuario_val = false;
                        nombre_val = false;
                        apellido_val = false;
                    }else{
                        if (cleanString(udto.getNombres().toUpperCase()).contains(cleanString(usuario.getNombres().toUpperCase()))){
                            usuario_val = false;
                            nombre_val = false;
                        }
                        if(cleanString(usuario.getApellidos()).equalsIgnoreCase(cleanString(udto.getApellido_paterno())) ||
                                cleanString(usuario.getApellidos()).equalsIgnoreCase(cleanString(udto.getApellido_materno()))  ||
                                cleanString(usuario.getApellidos()).equalsIgnoreCase((cleanString(udto.getApellido_paterno()) + " " + cleanString(udto.getApellido_materno())))){
                            usuario_val = false;
                            apellido_val = false;
                        }
                    }
                }
            }
        }else{
            System.out.println("No encontro nada, sea xq no había nadie o xq ingreso cualquier ocsa");
        }

        boolean validarSexo= true;

        switch (usuario.getSexo()){
            case "FEMENINO":
                // FEMENINO
                usuario.setSexo("FEMENINO");
                break;

            case "MASCULINO":
                // MASCULINO
                usuario.setSexo("MASCULINO");
                break;

            default:
                validarSexo=false;
                break;

        }

        if (bindingResult2.hasErrors() || !validarSexo || fecha_naci || !validarFoto  || dni_val || usuario_val || usuario_null || apellido_val || nombre_val) {
            System.out.println("siguen errores");

            //----------------------------------------
            if(dni_val) {
                model.addAttribute("msg8","El DNI ingresado no es válido");
            }
            if(usuario_null){
                model.addAttribute("msg10","No hay persona registrado para este DNI");
            }
            if(usuario_val){
                model.addAttribute("msg9","El usuario no coincide con el propietario del DNI");
            }
            if(nombre_val){
                model.addAttribute("msg11","El nombre del usuario no coincide con el propietario del DNI");
            }
            if(apellido_val){
                model.addAttribute("msg12","El apellido del usuario no coincide con el propietario del DNI");
            }

            if (fecha_naci) {
                model.addAttribute("msg7", "Solo pueden registrarse m   ayores de edad");
            }

            return "AdminGen/crearAdmin";
        } else {

            try {
                usuario.setFoto(file.getBytes());
                usuario.setFotonombre(fileName);
                usuario.setFotocontenttype(file.getContentType());
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("mensajefoto", "Ocurrió un error al subir el archivo");
                return "AdminGen/crearAdmin";
            }

            usuario.setEstado(1);
            usuario.setRol(rolRepository.findById(5).get());
            String fechanacimiento = LocalDate.now().toString();
            //usuario.setFecharegistro(fechanacimiento);

            attr.addFlashAttribute("msg", "Usuario creado exitosamente");

            Date date = new Date();
            DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            usuario.setFecharegistro(hourdateFormat.format(date));
            usuario.setFechaadmitido(hourdateFormat.format(date));


            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(usuario.getDni());
            System.out.println(hashedPassword);
            usuario.setContrasenia(hashedPassword);
            usuarioRepository.save(usuario);

            /////----------------Envio Correo--------------------/////

            String contenido = "Hola "+ usuario.getNombres()+" tu cuenta de administrador fue creada exitosamente, recuerda resetear tu contraseña.";
            sendEmail(usuario.getCorreo(), "Cuenta Administrador creado", contenido);

            //sendHtmlMailREgistrado(usuario.getCorreo(), "Cuenta Administrador creado html", usuario);

            /////-----------------------------------------  ------/////


            attr.addFlashAttribute("msg", "Administrador creado exitosamente.");
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

    public void sendHtmlMailAceptado(String to, String subject, Usuario usuario) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        Context context = new Context();
        context.setVariable("user", usuario.getNombres());
        context.setVariable("id", usuario.getDni());
        String emailContent = templateEngine.process("/Correo/Aceptado", context);
        helper.setText(emailContent, true);
        mailSender.send(message);
    }
    public void sendHtmlMailRechazado(String to, String subject, Usuario usuario) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        Context context = new Context();
        context.setVariable("user", usuario.getNombres());
        context.setVariable("id", usuario.getDni());
        String emailContent = templateEngine.process("/Correo/Rechazado", context);
        helper.setText(emailContent, true);
        mailSender.send(message);
    }
    public String cleanString(String texto) {
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return texto;
    }
}
