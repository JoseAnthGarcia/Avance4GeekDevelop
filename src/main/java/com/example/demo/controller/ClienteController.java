package com.example.demo.controller;


import com.example.demo.dtos.*;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.service.ExtrasClienteService;
import com.example.demo.service.PedidoActualService;
import com.example.demo.service.PlatoClienteService;
import com.example.demo.service.RestauranteClienteService;
import com.example.demo.service.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import javax.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.aspectj.runtime.internal.Conversions.doubleValue;

@Controller

@RequestMapping("/cliente")
public class ClienteController {

    //todo change in presentation public String ip = "54.175.37.128.nip.io";
    public String ip = "34.227.30.44.nip.io";//hector
    //public String ip = "54.87.150.35.nip.io";//diego
    //public String ip = "localhost";
    public String puerto = "8080";

    @Autowired
    TarjetaRepository tarjetaRepository;

    @Autowired
    RestauranteClienteService restauranteClienteService;

    @Autowired
    RestauranteRepository restauranteRepository;

    @Autowired
    UsuarioRepository clienteRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    DistritosRepository distritosRepository;

    @Autowired
    UbicacionRepository ubicacionRepository;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    PlatoRepository platoRepository;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ExtraRepository extraRepository;

    @Autowired
    PlatoHasPedidoRepository platoHasPedidoRepository;

    @Autowired
    ExtraHasPedidoRepository extraHasPedidoRepository;

    @Autowired
    PedidoActualService pedidoActualService;

    @Autowired
    PlatoClienteService platoClienteService;

    @Autowired
    ExtrasClienteService extrasClienteService;

    @Autowired
    CategoriasRestauranteRepository categoriasRestauranteRepository;

    @Autowired
    CuponRepository cuponRepository;


    @Autowired
    HistorialPedidoService historialPedidoService;

    @Autowired
    ReportePedidoCService reportePedidoCService;

    @Autowired
    ClienteHasCuponRepository clienteHasCuponRepository;

    @Autowired
    ReporteTiempoService reporteTiempoService;

    @Autowired
    ReporteDineroService reporteDineroService;


    @Autowired
    CuponClienteService cuponClienteService;

    @Autowired
    MetodoPagoRepository metodoPagoRepository;

    @Autowired
    ExtraDetalleService extraDetalleService;

    @Autowired
    RestauranteClienteService2 restauranteClienteService2;

    @GetMapping("/fotoPerfil")
    public ResponseEntity<byte[]> mostrarPerfil(@RequestParam("id") int id) {
        Optional<Usuario> usuarioOptional = clienteRepository.findById(id);
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

    @GetMapping("/editarPerfil")
    public String editarPerfil(HttpSession httpSession, Model model) {

        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        model.addAttribute("usuario", usuario);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario.getIdusuario()));
        return "Cliente/editarPerfil";

    }

    @PostMapping("/guardarEditar")
    public String guardarEdicion(@RequestParam("contraseniaConf") String contraseniaConf,
                                 @RequestParam("telefonoNuevo") String telefonoNuevo,
                                 HttpSession httpSession,
                                 Model model) {

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");
        boolean valContra = true;
        boolean telfValid = false;
        boolean telfUnico = true;

        List<Usuario> clientesxtelefono = clienteRepository.findUsuarioByTelefonoAndIdusuarioNot(telefonoNuevo, usuario1.getIdusuario());
        if (clientesxtelefono.isEmpty()) {
            telfUnico = false;
        }
        int telfInt;
        try {
            telfInt = Integer.parseInt(telefonoNuevo);
        } catch (NumberFormatException e) {
            telfInt = -1;
        }

        if (telfInt == -1 || telefonoNuevo.trim().equals("") || telefonoNuevo.length() != 9) {
            telfValid = true;
        }

        if (BCrypt.checkpw(contraseniaConf, usuario1.getContrasenia())) {
            valContra = false;
        }

        if (valContra || telfValid || telfUnico) {

            if (telfUnico) {
                model.addAttribute("msg1", "El telefono ingresado ya está registrado");
            }
            if (valContra) {
                model.addAttribute("msg", "Contraseña incorrecta");
            }
            if (telfValid) {
                model.addAttribute("msg2", "Coloque 9 dígitos si desea actualizar");
            }
            model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
            return "Cliente/editarPerfil";


        } else {
            usuario1.setTelefono(telefonoNuevo); //usar save para actualizar
            httpSession.setAttribute("usuario", usuario1); //TODO: preguntar profe
            clienteRepository.save(usuario1);
            model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
            return "redirect:/cliente/listaRestaurantes";
        }


    }
/*********************************************************  LISTA RESTAURANTE***************************************************************************************/
    @GetMapping("/listaRestaurantes")
    public String listaRestaurantes(Model model, HttpSession httpSession,
                                    @RequestParam Map<String, Object> params,
                                    @RequestParam(value = "texto",required = false) String texto,
                                    @RequestParam(value = "idPrecio",required = false) String idPrecio,
                                    @RequestParam(value = "idCategoria",required = false) String idCategoria,
                                    @RequestParam(value = "val",required = false) String val) {

        if(httpSession.getAttribute("carrito") != null){
            httpSession.removeAttribute("carrito");
        }
        if(httpSession.getAttribute("extrasCarrito") != null){
            httpSession.removeAttribute("extrasCarrito");
        }
        System.out.println("IDCATEGORIA: "+ idCategoria);


        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        System.out.println("USUARIO ID: "+ usuario.getIdusuario());
        int page;
        try{
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        }catch(NumberFormatException nfe){
            page =0;
        }
        Pageable pageRequest = PageRequest.of(page, 5);
        String direccionactual = usuario.getDireccionactual();




        int iddistritoactual = 1;
        Integer limitInfP = 0;
        Integer limitSupP = 5000;
        Integer limitInfVal = 0;
        Integer limitSupVal = 6; //TODO 5

        List<ClienteDTO> listadirecc = clienteRepository.listaParaCompararDirecciones(usuario.getIdusuario());

        for (ClienteDTO cl : listadirecc) {
            if (cl.getDireccion().equalsIgnoreCase(direccionactual)) {
                iddistritoactual = cl.getIddistrito();
                break;
            }
        }


        if(val == null || val.equals("")){
            val = "7";
            httpSession.removeAttribute("val");
        }else{
            httpSession.setAttribute("val",val);
        }

        if(idPrecio == null || idPrecio.equals("")){
            idPrecio = "6";
            httpSession.removeAttribute("idPrecio");
        }else{
            httpSession.setAttribute("idPrecio",idPrecio);
        }

        if (texto == null) {
            texto = "";
            httpSession.removeAttribute("texto");

        }else{
            httpSession.setAttribute("texto",texto);
        }

        String id1="";
        String id2="-";
        String id3="-";
        if(idCategoria == null){
            idCategoria="";
            httpSession.removeAttribute("idCategoria");
        }else {
            try {
                int idcat = Integer.parseInt(idCategoria);
                if( idcat>0 && idcat<28) {
                    id1 = "-"+idCategoria+"-";
                    id2 = "-"+idCategoria;
                    id3 = idCategoria+"-";
                    httpSession.setAttribute("idCategoria",idCategoria);
                }

            }catch (NumberFormatException e){
                idCategoria="";
                httpSession.removeAttribute("idCategoria");
            }

        }


        System.out.println("id1 :"+id1);
        System.out.println("id12 :"+id2);
        System.out.println("id3 :"+id3   );
        System.out.println("valoracion :"+val );

        texto= httpSession.getAttribute("texto") == null ? texto :  (String) httpSession.getAttribute("texto");
        val= httpSession.getAttribute("val") == null ? val :  (String) httpSession.getAttribute("val");
        idCategoria= httpSession.getAttribute("idCategoria") == null ? idCategoria :  (String) httpSession.getAttribute("idCategoria");
        idPrecio= httpSession.getAttribute("idPrecio") == null ? idPrecio :  (String) httpSession.getAttribute("idPrecio");

        switch (idPrecio){
            case "1":
                limitInfP = 0;
                limitSupP = 15;
                break;
            case "2":
                limitInfP = 15;
                limitSupP = 25;
                break;
            case "3":
                limitInfP = 25;
                limitSupP = 40;
                break;
            case "4":
                limitInfP = 40;
                limitSupP = 5000;
                break;

            default:
                limitInfP = 0;
                limitSupP = 5000;
        }

        switch (val){
            case "0":
                limitInfVal = -1;
                limitSupVal = 0;
                break;
            case "1":
                limitInfVal = 0;
                limitSupVal = 1;
                break;
            case "2":
                limitInfVal = 1;
                limitSupVal = 2;
                break;
            case "3":
                limitInfVal = 2;
                limitSupVal = 3;
                break;
            case "4":
                limitInfVal = 3;
                limitSupVal = 4;
                break;
            case "5":
                limitInfVal = 4;
                limitSupVal = 5;
                break;
            default:
                limitInfVal = -1;
                limitSupVal = 5;
        }
        System.out.println("lmmiteinf: "+limitInfVal);
        System.out.println("lmmitesup: "+limitSupVal);
        System.out.println("preciol1: "+limitSupP);
        System.out.println("preciol2: "+limitInfP);
        System.out.println("IDCATEGORIA2: "+idCategoria);





        if(val.equals("1") || val.equals("2") || val.equals("3") || val.equals("4") || val.equals("5") ){
            System.out.println(" ENTRO AL QUERY 1"  );
            Page<RestauranteDTO> listaRestaurante2 = restauranteClienteService2.listaRestaurantePaginada2(limitInfP,limitSupP,limitInfVal,limitSupVal,texto,id1,id2,id3,iddistritoactual,pageRequest);
            int totalPage = listaRestaurante2.getTotalPages();
            if(totalPage > 0){
                List<Integer> pages = IntStream.rangeClosed(1,totalPage).boxed().collect(Collectors.toList());
                model.addAttribute("pages",pages);
            }

            model.addAttribute("listaRestaurante", listaRestaurante2.getContent());
            model.addAttribute("total", totalPage);
        }else{
            System.out.println(" ENTRO AL QUERY 2"  );
            Page<RestauranteDTO> listaRestaurante = restauranteClienteService.listaRestaurantePaginada(limitInfP,limitSupP,limitInfVal,limitSupVal,texto,id1,id2,id3,iddistritoactual,pageRequest);
            int totalPage = listaRestaurante.getTotalPages();
            if(totalPage > 0){
                List<Integer> pages = IntStream.rangeClosed(1,totalPage).boxed().collect(Collectors.toList());
                model.addAttribute("pages",pages);
            }
            model.addAttribute("total", totalPage);
            model.addAttribute("listaRestaurante", listaRestaurante.getContent());
        }

        model.addAttribute("current", page + 1);
        model.addAttribute("categorias",categoriasRestauranteRepository.findAll());
        model.addAttribute("idPrecio", idPrecio);
        model.addAttribute("idCategoria", idCategoria);
        System.out.println("IDCATEGORIA3:  "+ idCategoria);
        model.addAttribute("texto", texto);
        model.addAttribute("val", val);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario.getIdusuario()));
        return "Cliente/listaRestaurantes";
    }


    @GetMapping("/listaRestaurantespag")
    public String listaRestaurantespag(Model model, HttpSession httpSession,
                                    @RequestParam Map<String, Object> params,
                                    @RequestParam(value = "texto",required = false) String texto,
                                    @RequestParam(value = "idPrecio",required = false) String idPrecio,
                                    @RequestParam(value = "idCategoria",required = false) String idCategoria,
                                    @RequestParam(value = "val",required = false) String val) {

        if(httpSession.getAttribute("carrito") != null){
            httpSession.removeAttribute("carrito");
        }
        if(httpSession.getAttribute("extrasCarrito") != null){
            httpSession.removeAttribute("extrasCarrito");
        }
        System.out.println("IDCATEGORIA: "+ idCategoria);


        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        System.out.println("USUARIO ID: "+ usuario.getIdusuario());
        int page;
        try{
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        }catch(NumberFormatException nfe){
            page =0;
        }
        Pageable pageRequest = PageRequest.of(page, 5);
        String direccionactual = usuario.getDireccionactual();


        //coment

        int iddistritoactual = 1;
        Integer limitInfP = 0;
        Integer limitSupP = 5000;
        Integer limitInfVal = 0;
        Integer limitSupVal = 6; //TODO 5




        List<ClienteDTO> listadirecc = clienteRepository.listaParaCompararDirecciones(usuario.getIdusuario());

        for (ClienteDTO cl : listadirecc) {
            if (cl.getDireccion().equalsIgnoreCase(direccionactual)) {
                iddistritoactual = cl.getIddistrito();
                break;
            }
        }


        String id1="";
        String id2="-";
        String id3="-";

        System.out.println("id1 :"+id1);
        System.out.println("id12 :"+id2);
        System.out.println("id3 :"+id3   );
        System.out.println("valoracion :"+val   );

        texto= httpSession.getAttribute("texto") == null ? "" :  (String) httpSession.getAttribute("texto");
        val= httpSession.getAttribute("val") == null ? "7" :  (String) httpSession.getAttribute("val");
        idCategoria= httpSession.getAttribute("idCategoria") == null ? "" : (String) httpSession.getAttribute("idCategoria");
        idPrecio= httpSession.getAttribute("idPrecio") == null ? "6" :  (String) httpSession.getAttribute("idPrecio");

        if(idCategoria == null){
            idCategoria="";
            httpSession.removeAttribute("idCategoria");
        }else {
            try {
                int idcat = Integer.parseInt(idCategoria);
                if( idcat>0 && idcat<28) {
                    id1 = "-"+idCategoria+"-";
                    id2 = "-"+idCategoria;
                    id3 = idCategoria+"-";
                }
                httpSession.setAttribute("idCategoria",idCategoria);
            }catch (NumberFormatException e){
                idCategoria="";
                httpSession.removeAttribute("idCategoria");
            }

        }

        switch (idPrecio){
            case "1":
                limitInfP = 0;
                limitSupP = 15;
                break;
            case "2":
                limitInfP = 15;
                limitSupP = 25;
                break;
            case "3":
                limitInfP = 25;
                limitSupP = 40;
                break;
            case "4":
                limitInfP = 40;
                limitSupP = 5000;
                break;

            default:
                limitInfP = 0;
                limitSupP = 5000;
        }

        switch (val){
            case "0":
                limitInfVal = -1;
                limitSupVal = 0;
                break;
            case "1":
                limitInfVal = 0;
                limitSupVal = 1;
                break;
            case "2":
                limitInfVal = 1;
                limitSupVal = 2;
                break;
            case "3":
                limitInfVal = 2;
                limitSupVal = 3;
                break;
            case "4":
                limitInfVal = 3;
                limitSupVal = 4;
                break;
            case "5":
                limitInfVal = 4;
                limitSupVal = 5;
                break;
            default:
                limitInfVal = -1;
                limitSupVal = 5;
        }
        System.out.println("lmmiteinf: "+limitInfVal);
        System.out.println("lmmitesup: "+limitSupVal);
        System.out.println("preciol1: "+limitSupP);
        System.out.println("preciol2: "+limitInfP);
        System.out.println("IDCATEGORIA2: "+idCategoria);


        if(val.equals("1") || val.equals("2") || val.equals("3") || val.equals("4") || val.equals("5") ){
            System.out.println(" ENTRO AL QUERY 1"  );
            Page<RestauranteDTO> listaRestaurante2 = restauranteClienteService2.listaRestaurantePaginada2(limitInfP,limitSupP,limitInfVal,limitSupVal,texto,id1,id2,id3,iddistritoactual,pageRequest);
            int totalPage = listaRestaurante2.getTotalPages();
            if(totalPage > 0){
                List<Integer> pages = IntStream.rangeClosed(1,totalPage).boxed().collect(Collectors.toList());
                model.addAttribute("pages",pages);

            }

            model.addAttribute("listaRestaurante", listaRestaurante2.getContent());
            model.addAttribute("total", totalPage);

        }else{
            System.out.println(" ENTRO AL QUERY 2"  );
            Page<RestauranteDTO> listaRestaurante = restauranteClienteService.listaRestaurantePaginada(limitInfP,limitSupP,limitInfVal,limitSupVal,texto,id1,id2,id3,iddistritoactual,pageRequest);
            int totalPage = listaRestaurante.getTotalPages();
            if(totalPage > 0){
                List<Integer> pages = IntStream.rangeClosed(1,totalPage).boxed().collect(Collectors.toList());
                model.addAttribute("pages",pages);
                //model.addAttribute("total", totalPage);
            }
            model.addAttribute("total", totalPage);
            model.addAttribute("listaRestaurante", listaRestaurante.getContent());
        }
        //model.addAttribute();
        model.addAttribute("current", page + 1);
        model.addAttribute("categorias",categoriasRestauranteRepository.findAll());
        model.addAttribute("idPrecio", idPrecio);
        model.addAttribute("idCategoria", idCategoria);
        System.out.println("IDCATEGORIA3:  "+ idCategoria);
        model.addAttribute("texto", texto);
        model.addAttribute("val", val);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario.getIdusuario()));
        return "Cliente/listaRestaurantes";

    }


/************************************************************************************************************************************************************/
    @GetMapping("/listaDirecciones")
    public String listaDirecciones(Model model, HttpSession httpSession) {
        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");

        List<Ubicacion> listaDirecciones = ubicacionRepository.findByUsuarioVal(usuario);
        model.addAttribute("listaDirecciones", listaDirecciones);

        ArrayList<Ubicacion> listaUbicacionesSinActual = new ArrayList<>();

        for (Ubicacion ubicacion : listaDirecciones) {
            if ((!ubicacion.getDireccion().equals(usuario.getDireccionactual())) && ubicacion.getBorrado() == 0) {
                listaUbicacionesSinActual.add(ubicacion);
            }
        }
        model.addAttribute("listaDistritos", distritosRepository.findAll());
        model.addAttribute("direccionesSinActual", listaUbicacionesSinActual);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario.getIdusuario()));
        return "Cliente/listaDirecciones";
    }


    @PostMapping("/guardarDireccion")
    public String guardarDirecciones(HttpSession httpSession, @RequestParam("direccionactual") String direccionActual, Model model) {
        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        List<Ubicacion> listaDirecciones = ubicacionRepository.findByUsuarioVal(usuario);
        for (Ubicacion direc : listaDirecciones) {
            if (direccionActual.trim().equalsIgnoreCase(direc.getDireccion())) {
                usuario.setDireccionactual(direccionActual);
                httpSession.setAttribute("usuario", usuario);
                model.addAttribute("listaDistritos", distritosRepository.findAll());
                clienteRepository.save(usuario);
                break;
            }
        }

        httpSession.setAttribute("distritoActual", distritosRepository.findByUsuarioAndDireccion(usuario.getIdusuario(), usuario.getDireccionactual()));
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario.getIdusuario()));
        return "redirect:/cliente/listaDirecciones";
    }

    @PostMapping("/eliminarDireccion")
    public String eliminarDirecciones(@RequestParam("listaIdDireccionesAeliminar") List<String> listaIdDireccionesAeliminar, HttpSession session, Model model) {
        Usuario usuarioS = (Usuario) session.getAttribute("usuario");
        try {
            for (String idUbicacion : listaIdDireccionesAeliminar) {
                //validad int idUbicacion:
                int idUb = Integer.parseInt(idUbicacion);
                Ubicacion ubicacion = (ubicacionRepository.findById(idUb)).get();

                model.addAttribute("listaDistritos", distritosRepository.findAll());
                if (!ubicacion.getDireccion().equalsIgnoreCase(usuarioS.getDireccionactual())) {
                    ubicacion.setBorrado(1);
                    ubicacionRepository.save(ubicacion);
                }

            }
            model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuarioS.getIdusuario()));
        } catch (Exception e) {
            model.addAttribute("msg90", "La dirección seleccionada no existe");
        } finally {
            return "redirect:/cliente/listaDirecciones";
        }

    }

    @PostMapping("/agregarDireccion")
    public String registrarNewDireccion(@RequestParam("direccion") String direccion, @RequestParam("distrito") String distrito, HttpSession httpSession, Model model) {
        boolean valNul = false;
        boolean valNew = false;
        boolean valLong = false;


        if (direccion.isEmpty()) {
            valNul = true;
        }

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");
        List<Ubicacion> listaDir = ubicacionRepository.findByUsuarioVal(usuario1);
        Boolean dist_u_val = true;
        try {

            Integer u_dist = Integer.parseInt(distrito);
            System.out.println(u_dist + "ID DISTRITO");
            int dist_c = distritosRepository.findAll().size();
            System.out.println(dist_c);
            for (int i = 1; i <= dist_c; i++) {
                if (u_dist == i) {
                    dist_u_val = false;

                    System.out.println("ENTRO A LA VAIDACION DE AQUI");
                }
            }

        } catch (Exception n) {
            System.out.println("No llegó nada");
            dist_u_val = true;
        }

        for (Ubicacion u : listaDir) {
            if (u.getDireccion().equalsIgnoreCase(direccion.trim())) {
                valNew = true;
            }
        }
        if (listaDir.size() > 5) {
            valLong = true;
        }

        if (valNul || valNew || valLong || dist_u_val) {
            if (valNul) {
                model.addAttribute("msg", "No ingresó dirección");
            }
            if (valNew) {
                model.addAttribute("msg1", "La dirección ingresda ya está registrada");
            }

            if (valLong) {
                model.addAttribute("msg2", "Solo puede registrar 6 direcciones");
            }
            if (dist_u_val) {

                model.addAttribute("msg3", "Seleccione una de las opciones");
            }

            Usuario usuario = (Usuario) httpSession.getAttribute("usuario");

            List<Ubicacion> listaDirecciones = ubicacionRepository.findByUsuarioVal(usuario);
            model.addAttribute("listaDirecciones", listaDirecciones);

            ArrayList<Ubicacion> listaUbicacionesSinActual = new ArrayList<>();

            for (Ubicacion ubicacion : listaDirecciones) {
                if (!ubicacion.getDireccion().equals(usuario.getDireccionactual()) && ubicacion.getBorrado() == 0) {
                    listaUbicacionesSinActual.add(ubicacion);
                }
            }

            model.addAttribute("direccionesSinActual", listaUbicacionesSinActual);
            model.addAttribute("listaDistritos", distritosRepository.findAll());
            model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario.getIdusuario()));
            return "Cliente/listaDirecciones";

        } else {
            Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
            List<Ubicacion> listaDirecciones = (List) httpSession.getAttribute("poolDirecciones");
            Ubicacion ubicacion = new Ubicacion();
            ubicacion.setUsuario(usuario);
            ubicacion.setDireccion(direccion);
            //TODO: @JOHAM QUE PEDOS
            Distrito distritoEnviar = distritosRepository.getOne(Integer.parseInt(distrito));
            ubicacion.setDistrito(distritoEnviar);
            listaDirecciones.add(ubicacion);
            ubicacionRepository.save(ubicacion);
            httpSession.setAttribute("listaDirecciones", listaDirecciones);
            model.addAttribute("listaDistritos", distritosRepository.findAll());
            model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario.getIdusuario()));
            return "redirect:/cliente/listaDirecciones";
        }

    }

    @GetMapping("/images")
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

    @GetMapping("/imagesPlato")
    public ResponseEntity<byte[]> mostrarPlatos(@RequestParam("id") int id) {
        Optional<Plato> platoOpt = platoRepository.findById(id);
        if (platoOpt.isPresent()) {
            Plato plato = platoOpt.get();
            byte[] image = plato.getFoto();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(plato.getFotocontenttype()));
            return new ResponseEntity<>(image, httpHeaders, HttpStatus.OK);
        } else {
            return null;
        }
    }

    @GetMapping("/imagenExtra")
    public ResponseEntity<byte[]> mostrarExtras(@RequestParam("id") int id) {
        Optional<Extra> extraOpt = extraRepository.findById(id);
        if (extraOpt.isPresent()) {
            Extra extra = extraOpt.get();
            byte[] image = extra.getFoto();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(extra.getFotocontenttype()));
            return new ResponseEntity<>(image, httpHeaders, HttpStatus.OK);
        } else {
            return null;
        }
    }


    @GetMapping("/listaPlatos")
    public String listaplatos(@RequestParam Map<String, Object> params,
                              @RequestParam(value = "idRest",required = false) String idRestS, //solo es necesario recibirla de restaurante a platos
                              @RequestParam(value = "texto",required = false) String texto,
                              @RequestParam(value = "idPrecio",required = false) String idPrecio,
                              @RequestParam(value = "idCategoria",required = false) String idCategoriaS,
                              Model model, HttpSession session) {

        Integer limitInf = 0;
        Integer limitSup = 5000;
        Integer limitInfC = 0;
        Integer limitSupC = 28;
        Integer idRest;


        if (idRestS == null) {
            try {
                idRest = (Integer) session.getAttribute("idRest");
            } catch (Exception e){
                return "redirect:/cliente/listaRestaurantes"    ;   
            }
        } else {
            try {
                idRest = Integer.parseInt(idRestS);
            } catch (NumberFormatException e) {
                return "redirect:/cliente/listaRestaurantes";
            }
            //PROBAR
            if (session.getAttribute("idRest") != null) {
                session.removeAttribute("idRest");
            }
            session.setAttribute("idRest", idRest);
        }

        if (session.getAttribute("idPlato") != null) {
            session.removeAttribute("idPlato");
        }

        Optional<Restaurante> restauranteOpt = restauranteRepository.findById(idRest);
        int page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        Pageable pageRequest = PageRequest.of(page, 6);

        if(idPrecio == null || idPrecio.equals("")){
            idPrecio = "6";
        }

        if(idCategoriaS == null || idCategoriaS.equals("")){
            idCategoriaS = "0-28";
        }

        if(texto == null){
            texto = "";
        }

        if (restauranteOpt.isPresent()) {
            Restaurante restaurante = restauranteOpt.get();
            model.addAttribute("nombreRest", restaurante.getNombre());
        }

        switch (idPrecio) {
            case "1":
                limitInf = 0;
                limitSup = 10;
                break;
            case "2":
                limitInf = 10;
                limitSup = 20;
                break;
            case "3":
                limitInf = 20;
                limitSup = 30;
                break;
            case "4":
                limitInf = 30;
                limitSup = 40;
                break;
            case "5":
                limitInf = 40;
                limitSup = 5000;
                break;
            default:
                limitInf = 0;
                limitSup = 5000;
        }

        List<Categorias> categoriasList = categoriasRestauranteRepository.findCategoriasByIdrestaurante(idRest);
        try {
            limitInfC = Integer.valueOf(idCategoriaS.split("-")[0]);
            limitSupC = Integer.valueOf(idCategoriaS.split("-")[1]);
            // 7 9 11 12 - (9 - 7 - 11 - 12)
            if(categoriasList.get(0).getIdcategoria() != limitInfC &&
                    categoriasList.get(1).getIdcategoria() != limitInfC &&
                    categoriasList.get(2).getIdcategoria() != limitInfC &&
                    categoriasList.get(3).getIdcategoria() != limitInfC ) {
                limitInfC = 0;
                limitSupC = 28;
            }
            
        }catch (NumberFormatException e){
            limitInfC = 0;
            limitSupC = 28;
        }
        Page<PlatosDTO> listaPlato = platoClienteService.listaPlatoPaginada(idRest, texto, limitInf, limitSup, limitInfC, limitSupC,pageRequest);

        int totalPage = listaPlato.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
        Usuario usuario1 = (Usuario) session.getAttribute("usuario");
        //a

        model.addAttribute("current", page + 1);
        model.addAttribute("listaPlato",listaPlato.getContent());
        model.addAttribute("texto",texto);
        model.addAttribute("idPrecio",idPrecio);
        model.addAttribute("idCategoria",idCategoriaS);
        model.addAttribute("categorias",categoriasList);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/listaProductos";
    }

    @GetMapping("/detallePlato")
    public String detallePlato(@RequestParam Map<String, Object> params,
                               @RequestParam(value = "texto", required = false) String texto,
                               @RequestParam(value = "idPrecio", required = false) String idPrecio,
                               @RequestParam(value = "idCategoria", required = false) String idCategoria,
                               @RequestParam(value = "idPlato", required = false) String idPlatoS, HttpSession session,
                               Model model) {
        Integer idRest = (Integer) session.getAttribute("idRest");
        Usuario usuario1 = (Usuario) session.getAttribute("usuario");
        Integer idPlato;
        try {
            idPlato = Integer.parseInt(idPlatoS);
        } catch (NumberFormatException e) {
            return "redirect:/cliente/listaPlatos";
        }

        if (idPlato == null) {
            idPlato = (Integer) session.getAttribute("idPlato");
        }

        if (session.getAttribute("idPlato") == null) {
            //session.removeAttribute("idPlato");
            session.setAttribute("idPlato", idPlato);
        }



        Plato platoObs = platoRepository.findByIdplatoAndIdrestaurante(idPlato, idRest);
        if (platoObs == null) {
            return "redirect:/cliente/listaPlatos";
        }


        // validar que el plato pertenezaca alretaurantes
        Optional<Restaurante> restauranteOpt = restauranteRepository.findById(idRest);
        Optional<Plato> platoOpt = platoRepository.findById(idPlato);


        int limitInfPe = 0;
        int limitSupPe = 0;
        int limitInfCa = 0;
        int limitSupCa = 0;
        int page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        Pageable pageRequest = PageRequest.of(page, 5);

        if (platoOpt.isPresent() && restauranteOpt.isPresent()) {
            Plato plato = platoOpt.get();
            Restaurante restaurante = restauranteOpt.get();

            if (idPrecio == null || idPrecio.equals("")) {
                idPrecio = "6";
            }

            if (idCategoria == null || idCategoria.equals("")) {
                idCategoria = "5";
            }

            if (texto == null) {
                texto = "";
            }

            switch (idCategoria) {
                case "1":
                    limitInfCa = 0;
                    limitSupCa = 1;
                    break;
                case "2":
                    limitInfCa = 1;
                    limitSupCa = 2;
                    break;
                case "3":
                    limitInfCa = 2;
                    limitSupCa = 3;
                    break;
                case "4":
                    limitInfCa = 3;
                    limitSupCa = 4;
                    break;
                default:
                    limitInfCa = 0;
                    limitSupCa = 5;

            }

            switch (idPrecio) {
                case "1":
                    limitInfPe = 0;
                    limitSupPe = 5;
                    break;
                case "2":
                    limitInfPe = 5;
                    limitSupPe = 10;
                    break;
                case "3":
                    limitInfPe = 10;
                    limitSupPe = 15;
                    break;
                case "4":
                    limitInfPe = 15;
                    limitSupPe = 20;
                    break;
                default:
                    limitInfPe = 0;
                    limitSupPe = 5000;
            }


            Page<ExtraDTO> listaExtras = extrasClienteService.listaExtrasDisponiblesPaginada(idRest, idPlato, texto, limitInfCa, limitSupCa, limitInfPe, limitSupPe, 1, pageRequest);
            int totalPage = listaExtras.getTotalPages();
            if (totalPage > 0) {
                List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
                model.addAttribute("pages", pages);
            }

            model.addAttribute("plato", plato);
            model.addAttribute("listaExtras", listaExtras.getContent());
            //  model.addAttribute("idRest",idRest);
            model.addAttribute("idPrecio", idPrecio);
            model.addAttribute("idCategoria", idCategoria);
            model.addAttribute("texto", texto);
            model.addAttribute("current", page + 1);
            model.addAttribute("nombreRest", restaurante.getNombre());
            model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
            return "Cliente/detallePlato";
        } else {
            //model.addAttribute("idRest",idRest);
            //model.addAttribute("idPlato",idPlato);
            model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
            return "redirect: cliente/listaPlatos?idRest=" + idRest + "&idPlato=" + idPlato;
        }
    }

    @GetMapping("/mostrarCarrito")
    public String mostrarCarrito(@RequestParam(value = "idPlato", required = false) Integer idPlato,
                                 @RequestParam(value = "idPage", required = false) String idPage,
                                 HttpSession session, RedirectAttributes attr,
                                 Model model) {
        //ArrayList<Plato_has_pedido> carrito = (ArrayList<Plato_has_pedido>) session.getAttribute("carrito");
        //List<Plato_has_pedido> carritoL = (List<Plato_has_pedido>) session.getAttribute("carrito");

        Integer idRest = (Integer) session.getAttribute("idRest");

        //en caso le cambie el html el disbled lo redireccionará al mismo sitio si no hay sesión de carrito
        if (session.getAttribute("carrito") == null) {
            attr.addFlashAttribute("msgCarritoNull", "Añada un plato al carrito para continuar.");
            return "redirect:/cliente/listaPlatos?idRest=" + idRest;
        }

        Usuario usuario1 = (Usuario) session.getAttribute("usuario");
        //model.addAttribute("carrito",carritoL);
        //model.addAttribute("idRest",idRest);
        model.addAttribute("idPlato", idPlato);
        model.addAttribute("idPage", idPage);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        model.addAttribute("idRest", idRest);
        return "Cliente/carritoCompras";
    }

    @PostMapping("/aniadirCarrito")
    public String aniadirCarrito(@RequestParam("idPlato") int idPlato,
                                 @RequestParam(value = "idPage", required = false) String idPage,
                                 @RequestParam("cantidadPlato") String cantidadPlato,
                                 HttpSession session,
                                 RedirectAttributes attr, Model model) {

        String url = "";
        String params = "";
        ArrayList<Plato_has_pedido> carrito = null;
        Usuario usuario1 = (Usuario) session.getAttribute("usuario");
        if (session.getAttribute("carrito") == null) {
            carrito = new ArrayList<>();
        } else {
            carrito = (ArrayList<Plato_has_pedido>) session.getAttribute("carrito");
        }

        Integer idRest = (Integer) session.getAttribute("idRest");

        Plato_has_pedido php = new Plato_has_pedido();
        Plato platoOther = platoRepository.findByIdplatoAndIdrestaurante(idPlato, idRest);
        if (platoOther == null) {
            return "redirect:/cliente/listaPlatos/";
        }

        Optional<Plato> platoOptional = platoRepository.findById(idPlato);
        int cantint = 0;
        try {
            cantint = Integer.parseInt(cantidadPlato);
            //TODO creo q se puede elimnar - evaluarlo después de la presentación
            if (cantint <= 0 || cantint > 20) {
                if (idPage.equals("1")) {
                    url = "detallePlato";
                    params = "?idRest=" + idRest + "&idPlato=" + idPlato;
                } else if (idPage.equals("0")) {
                    url = "listaPlatos";
                    params = "?idRest=" + idRest;
                } else {
                    url = "listaRestaurantes";
                }
                attr.addFlashAttribute("msgVal", "Ingrese un número mayor a 0 y menor a 20");
                return "redirect:/cliente/" + url + params;
            }

        } catch (NumberFormatException e) {
            if (idPage.equals("1")) {
                url = "detallePlato";
                params = "?idRest=" + idRest + "&idPlato=" + idPlato;
            } else if (idPage.equals("0")) {
                url = "listaPlatos";
                params = "?idRest=" + idRest;
            } else {
                url = "listaRestaurantes";
            }
            attr.addFlashAttribute("msgValCant", "Ingrese un número");
            return "redirect:/cliente/" + url + params;
        }

        if (platoOptional.isPresent()) {
            //GUARDANDO TODOS LOS ATRIBUTOS NECESARIOS A CARRITO
            Plato plato = platoOptional.get();
            Plato_has_pedidoKey idComPlato = new Plato_has_pedidoKey();
            //SE GUARDARÁ TEMPORALMENTE EN SESIÓN CON UN CÓDIGO TEMPORAL QUE SE ACTUALIZARÁ
            String codigo = "CODIGOTEMPORAL";
            int puntero = 0;
            if (carrito.size() > 0) {
                for (int i = 0; i < carrito.size(); i++) {
                    if (idPlato == carrito.get(i).getIdplatohaspedido().getIdplato()) {
                        puntero = i;
                        break;
                    }
                }
                if (idPlato == carrito.get(puntero).getIdplatohaspedido().getIdplato()) {
                    carrito.get(puntero).setCantidad(carrito.get(puntero).getCantidad() + cantint);
                } else {
                    idComPlato.setIdplato(idPlato);
                    idComPlato.setCodigo(codigo);
                    php.setPlato(plato);
                    php.setCantidad(cantint);
                    php.setPreciounitario(BigDecimal.valueOf(plato.getPrecio()));
                    php.setIdplatohaspedido(idComPlato);
                    carrito.add(php);
                }

            } else {
                //TODO HAY QUE VALIDAR DE QUE VISTA SE ESTÁ AÑADIENDO AL CARRITO - XQ DE ESO DEPENDE EL COMENTARIO
                idComPlato.setIdplato(idPlato);
                idComPlato.setCodigo(codigo);

                php.setPlato(plato);
                php.setCantidad(cantint);
                php.setPreciounitario(BigDecimal.valueOf(plato.getPrecio()));
                php.setIdplatohaspedido(idComPlato);
                carrito.add(php);
            }
            session.setAttribute("carrito", carrito);
            attr.addFlashAttribute("msgAdd", "Se agregó un plato al carrito");
        } else {
            attr.addFlashAttribute("msgNotFound", "No se encontro el plato");
        }
        //TODO por ahora solo funcionará si el flujo es LISTA DE PLATOS - DETALLE - VER CARRITO
        if (idPage.equals("1")) {
            url = "detallePlato";
            params = "?idRest=" + idRest + "&idPlato=" + idPlato;
        } else if (idPage.equals("0")) {
            url = "listaPlatos";
            params = "?idRest=" + idRest;
        } else {
            session.removeAttribute("carrito");
            url = "listaRestaurantes";
        }
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "redirect:/cliente/" + url + params;
    }


    @GetMapping("/vaciarCarrito")
    public String vaciarCarrito(RedirectAttributes attr, HttpSession session) {
        session.removeAttribute("carrito");
        return "redirect:/cliente/listaPlatos";
    }

    @GetMapping("/vaciarExtras")
    public String vaciarExtras(RedirectAttributes attr, HttpSession session) {
        session.removeAttribute("extrasCarrito");
        return "redirect:/cliente/mostrarCarrito?idPage=0";
    }

    @PostMapping("/eliminar")
    public String eliminarPlatos(@RequestParam(value = "platoEliminar", required = false) List<Integer> platoEliminar, HttpSession session,
                                 Model model, @RequestParam(value = "idPage", required = false) String idPage,
                                 RedirectAttributes attr) {
        System.out.println(platoEliminar);
        // 3 elementos
        // 1 elemento - idPlato
        //en caso no seleccione nada


        if (platoEliminar == null) {
            attr.addFlashAttribute("msgNotNull", "Tiene que seleccionar un plato para borrar");
            return "redirect:/cliente/mostrarCarrito?idPage=0";
        }

        List<Plato_has_pedido> carrito = (List<Plato_has_pedido>) session.getAttribute("carrito");
        Integer idRest = (Integer) session.getAttribute("idRest");

        for (Integer idPlato : platoEliminar) {
            for (int i = 0; i < carrito.size(); i++) {
                if (idPlato == carrito.get(i).getIdplatohaspedido().getIdplato()) {
                    carrito.remove(i);
                    break;
                }
            }
        }


        //en caso elimine el carrito quitar la sesión se debe
        if (carrito.size() == 0) {
            session.removeAttribute("carrito");
            return "redirect:/cliente/listaPlatos";
        }
        //Actualizando el carrito
        session.setAttribute("carrito", carrito);
        return "redirect:/cliente/mostrarCarrito?idPage=0";
    }

    @PostMapping("/eliminarExtras")
    public String eliminarExtras(@RequestParam(value = "extraEliminar", required = false) List<Integer> extraEliminar, HttpSession session,
                                 Model model) {
        System.out.println(extraEliminar);
        // 3 elementos
        // 1 elemento - idPlato
        //en caso no seleccione nada

        Integer idRest = (Integer) session.getAttribute("idRest");

        if (extraEliminar == null) {
            return "redirect:/cliente/mostrarExtrasCarrito";
        }

        List<Extra_has_pedido> carritoExtra = (List<Extra_has_pedido>) session.getAttribute("extrasCarrito");

        for (Integer idExtra : extraEliminar) {
            for (int i = 0; i < carritoExtra.size(); i++) {
                if (idExtra == carritoExtra.get(i).getIdextra().getIdextra()) {
                    carritoExtra.remove(i);
                    break;
                }
            }
        }


        //en caso elimine el carrito quitar la sesión se debe
        if (carritoExtra.size() == 0) {
            session.removeAttribute("extrasCarrito");
            return "redirect:/cliente/listaPlatos?idRest=" + idRest;
        }
        //Actualizando el carrito
        session.setAttribute("extrasCarrito", carritoExtra);
        return "redirect:/cliente/mostrarExtrasCarrito?idRest=" + idRest;
    }

    @GetMapping("/mostrarExtrasCarrito")
    public String mostrarExtrasCarrito(HttpSession session,
                                       Model model) {
        Usuario usuario1 = (Usuario) session.getAttribute("usuario");
        if (session.getAttribute("extrasCarrito") == null) {
            return "redirect:/cliente/listaPlatos";
        }
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        // model.addAttribute("idRest",idRest);
        return "Cliente/carritoExtras";
    }

    @PostMapping("/aniadirExtras")
    public String aniadirExtras(@RequestParam("idExtra") Integer idExtra,
                                @RequestParam(value = "idPlato", required = false) Integer idPlato,
                                @RequestParam(value = "cantidadExtra") String cantidadExtra,
                                HttpSession session,
                                RedirectAttributes attr, Model model) {
        //extras de carrito
        ArrayList<Extra_has_pedido> extrasCarrito = null;
        String urlDetalle = "";
        String params = "";
        // todo verificar noti
        Usuario usuario1 = (Usuario) session.getAttribute("usuario");
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));

        if (idPlato == null) {
            idPlato = (Integer) session.getAttribute("idPlato");
        }

        Integer idRest = (Integer) session.getAttribute("idRest");

        Extra extraOther = extraRepository.findByIdextraAndIdrestaurante(idExtra, idRest);
        if (extraOther == null) {
            return "redirect:/cliente/listaPlatos";
        }

        if (session.getAttribute("extrasCarrito") == null) {
            extrasCarrito = new ArrayList<>();
        } else {
            extrasCarrito = (ArrayList<Extra_has_pedido>) session.getAttribute("extrasCarrito");
        }

        int cantint = 0;
        try {
            cantint = Integer.parseInt(cantidadExtra);

            if (cantint <= 0 || cantint > 20) {
                urlDetalle = "detallePlato";
                params = "?idRest=" + idRest + "&idPlato=" + idPlato;

                attr.addFlashAttribute("msgValExt", "Ingrese un número mayor a 0 y menor a 20");
                return "redirect:/cliente/" + urlDetalle + params;
            }
        } catch (NumberFormatException e) {
            urlDetalle = "detallePlato";
            params = "?idRest=" + idRest + "&idPlato=" + idPlato;

            attr.addFlashAttribute("msgValCantExt", "Ingrese un número");
            return "redirect:/cliente/" + urlDetalle + params;
        }


        Extra_has_pedido ehp = new Extra_has_pedido();
        Optional<Extra> extraOptional = extraRepository.findById(idExtra);

        if (extraOptional.isPresent()) {
            Extra extra = extraOptional.get();
            Extra_has_pedidoKey idComExtra = new Extra_has_pedidoKey();
            String codigo = "CODIGOTEMPORAL";

            int puntero = 0;
            if (extrasCarrito.size() > 0) {
                for (int i = 0; i < extrasCarrito.size(); i++) {
                    if (idExtra == extrasCarrito.get(i).getIdextra().getIdextra()) {
                        puntero = i;
                        break;
                    }
                }
                if (idExtra == extrasCarrito.get(puntero).getIdextra().getIdextra()) {
                    extrasCarrito.get(puntero).setCantidad(extrasCarrito.get(puntero).getCantidad() + cantint);
                } else {
                    idComExtra.setIdextra(idExtra);
                    idComExtra.setCodigo(codigo);
                    ehp.setExtra(extra);
                    ehp.setCantidad(cantint);
                    ehp.setPreciounitario(BigDecimal.valueOf(extra.getPreciounitario()));
                    ehp.setIdextra(idComExtra);
                    extrasCarrito.add(ehp);
                }

            } else {
                //TODO HAY QUE VALIDAR DE QUE VISTA SE ESTÁ AÑADIENDO AL CARRITO - XQ DE ESO DEPENDE EL COMENTARIO
                idComExtra.setIdextra(idExtra);
                idComExtra.setCodigo(codigo);

                //RECORDAR VOLVERLO NULL Y AÑADIR EL PEDIDO AL FINAL
                ehp.setExtra(extra);
                ehp.setCantidad(cantint);

                //TODO SI FUERA EL SUBTOTAL EN EL CARRITO SE GUARDARÍA PRECIO UNITARIO X CANTIDAD PLATO
                ehp.setPreciounitario(BigDecimal.valueOf(extra.getPreciounitario()));
                ehp.setIdextra(idComExtra);
                extrasCarrito.add(ehp);
            }
            session.setAttribute("extrasCarrito", extrasCarrito);
            attr.addFlashAttribute("msgAddExtra", "Se agregó un extra al carrito");
        } else {
            attr.addFlashAttribute("msgNotFound", "No se encontró el extra");
        }

        urlDetalle = "detallePlato";
        params = "?idRest=" + idRest + "&idPlato=" + idPlato;
        return "redirect:/cliente/" + urlDetalle + params;
    }

    @PostMapping("/modificarExtra")
    public String modificarExtra(@RequestParam(value = "cantidad", required = false) List<String> cantidad,
                                 @RequestParam(value = "extraGuardar", required = false) List<Integer> extraGuardar,
                                 RedirectAttributes attr, Model model,
                                 HttpSession session) {
        Extra extra = new Extra();
        List<Extra_has_pedido> carritoExtra = (List<Extra_has_pedido>) session.getAttribute("extrasCarrito");

        int cantVal = 0;

        // LOS TAMAÑOS DE LOS ARREGLOS DEBEN SER IGUALES - INCLUSO SI NO INGRESA UNO ESTE SERÁ ""
        if (cantidad.size() != extraGuardar.size() ||
                extraGuardar.size() != carritoExtra.size()) {
            return "redirect:/cliente/mostrarExtrasCarrito"; //TODO redireccionar al mismo sitio
        }

        for (int i = 0; i < cantidad.size(); i++) {
            try {
                cantVal = Integer.parseInt(cantidad.get(i));
                if (cantVal <= 0 || cantVal > 20) {
                    attr.addFlashAttribute("msgIntMayExt", "Ingrese una cantidad entre 0 y 20");
                    return "redirect:/cliente/mostrarExtrasCarrito";
                }
            } catch (NumberFormatException e) {
                attr.addFlashAttribute("msgIntExt", "Ingrese un número entero");
                return "redirect:/cliente/mostrarExtrasCarrito";
            }
        }

        for (int i = 0; i < carritoExtra.size(); i++) {
            carritoExtra.get(i).getIdextra().setIdextra(extraGuardar.get(i));
            carritoExtra.get(i).setCantidad(Integer.parseInt(cantidad.get(i)));
        }
        session.setAttribute("extrasCarrito", carritoExtra);
        attr.addFlashAttribute("msgExtra", "Se actualizaron los datos correctamente");
        return "redirect:/cliente/mostrarCarrito?idPage=0";
    }


    @PostMapping("/terminarCompra")
    public String terminarCompra(@RequestParam(value = "cantidad", required = false) List<String> cantidad,
                                 @RequestParam(value = "platoGuardar", required = false) List<Integer> platoGuardar,
                                 @RequestParam(value = "observacion", required = false) List<String> observacion,
                                 RedirectAttributes attr, Model model,
                                 HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        //  List<Ubicacion> listaDirecciones = (List) session.getAttribute("poolDirecciones");
        List<Ubicacion> listaDirecciones = ubicacionRepository.findByUsuarioVal(usuario);
        //List<Ubicacion> direcciones_distritos = clienteRepository.findUbicacionActual(usuario.getIdusuario());
        //List <Cupon> listaCupones = (List<Cupon>) session.getAttribute("listaCupones");
        int idRest = (Integer) session.getAttribute("idRest");
        //CUPONES
        List<Cupon> listaCupones1 = cuponRepository.findCuponesbyIdclienteAndRestId(usuario.getIdusuario(), idRest);

        ArrayList<Plato_has_pedido> carrito = (ArrayList<Plato_has_pedido>) session.getAttribute("carrito");
        ArrayList<Extra_has_pedido> carritoExtra = (ArrayList<Extra_has_pedido>) session.getAttribute("extrasCarrito");

        Distrito distritoRestaurante = distritosRepository.findDistritoById(idRest);

        double subTotalCarrito = 0.00;
        double subTotalExtras = 0.00;
        double delivery = 0.00;
        Ubicacion distritoActual = null;
        int cantVal = 0;
        boolean obsVal = false;
        System.out.println(observacion);
        System.out.println(platoGuardar);
        System.out.println(cantidad);
        System.out.println(carrito);

        if (observacion.size() != 0) {
            obsVal = true;
        }

        if (cantidad.size() != 0 && platoGuardar.size() != 0) {
            System.out.println("ENTRO DESDE MODIFICAR CARRITO A TERMINAR COMPRA");
            if (carrito.size() == 0) {
                return "redirect:/cliente/listaPlatos";
            }
            if (obsVal) {
                if (cantidad.size() != observacion.size() ||
                        observacion.size() != platoGuardar.size() ||
                        platoGuardar.size() != carrito.size()) {
                    System.out.println("LOS TAMAÑOS NO SON DIFERENTES");
                    return "redirect:/cliente/mostrarCarrito?idPage=0";
                }
            } else {
                if (cantidad.size() != platoGuardar.size() ||
                        platoGuardar.size() != carrito.size()) {
                    System.out.println("LOS TAMAÑOS NO SON DIFERENTES");
                    return "redirect:/cliente/mostrarCarrito?idPage=0";
                }
            }

            for (int i = 0; i < cantidad.size(); i++) {
                try {
                    cantVal = Integer.parseInt(cantidad.get(i));
                    if (cantVal <= 0 || cantVal > 20) {
                        System.out.println("LAS CANTIDADES NO SON LAS MISMAS");
                        attr.addFlashAttribute("msgIntMay", "Ingrese una cantidad entre 0 y 20");
                        return "redirect:/cliente/mostrarCarrito?idPage=0";
                    }
                } catch (NumberFormatException e) {
                    System.out.println("UNA DE LAS CANTIDADES ES UNA LETRA");
                    attr.addFlashAttribute("msgInt", "Ingrese un número entero");
                    return "redirect:/cliente/mostrarCarrito?idPage=0";
                }
            }

            if (obsVal) {
                for (int i = 0; i < observacion.size(); i++) {
                    if (observacion.get(i).length() > 256) {
                        attr.addFlashAttribute("msgLen", "Ingrese un comentario menor a 256 carácteres");
                        return "redirect:/cliente/mostrarCarrito?idPage=0";
                    }
                }
            }

            for (int i = 0; i < carrito.size(); i++) {

                if (obsVal) {
                    carrito.get(i).setObservacionplatillo(observacion.get(i));
                } else {
                    carrito.get(i).setObservacionplatillo(null);
                }
                carrito.get(i).getIdplatohaspedido().setIdplato(platoGuardar.get(i));
                carrito.get(i).setCantidad(Integer.parseInt(cantidad.get(i)));
                subTotalCarrito = subTotalCarrito + carrito.get(i).getCantidad() * doubleValue(carrito.get(i).getPreciounitario());
            }
            if (carritoExtra != null) {
                for (int i = 0; i < carritoExtra.size(); i++) {
                    subTotalExtras = subTotalExtras + carritoExtra.get(i).getCantidad() * doubleValue(carritoExtra.get(i).getPreciounitario());
                }
            } else {
                subTotalExtras = 0.00;
            }
            for (Ubicacion u : listaDirecciones) {
                if (u.getDireccion().equalsIgnoreCase(usuario.getDireccionactual())) {
                    distritoActual = u;
                    if (u.getDistrito().getIddistrito() == distritoRestaurante.getIddistrito()) {
                        delivery = 5.00;
                        break;
                    }
                }
            }
            if (delivery == 0.00) {
                delivery = 8.00;
            }

            session.setAttribute("carrito", carrito);
            session.setAttribute("delivery", delivery);
        } else {
            System.out.println("SOY LA REDIRECCICIÓN POR VALIDACIÓN");

            // SI ENTRO EN ESTA CONDICIONAL QUIERE DECIR QUE VIENE DE LA SEGUNDA VISTA
            // SI GENERA ERROR AL MOMENTO DE MODIFICAR EL CARRITO
            // ENVIAR DESDE EL CONTROLLER ANTERIOR UN IDENTIFICADOR DE LA PAGINA
            for (int i = 0; i < carrito.size(); i++) {
                subTotalCarrito = subTotalCarrito + carrito.get(i).getCantidad() * doubleValue(carrito.get(i).getPreciounitario());
            }
            for (int i = 0; i < carritoExtra.size(); i++) {
                subTotalExtras = subTotalExtras + carritoExtra.get(i).getCantidad() * doubleValue(carritoExtra.get(i).getPreciounitario());
            }
        }

      /*  int monto = (int) (subTotalCarrito + subTotalExtras + delivery);
        int montoVal = (int) (subTotalCarrito + subTotalExtras);

        for (Cupon c: listaCupones1){
            if(c.getDescuento() > monto){
                c.setDescuento(montoVal);
            }
        }
*/
        model.addAttribute("montoCarrito", subTotalCarrito);
        model.addAttribute("listaCupones", listaCupones1);
        model.addAttribute("montoExtras", subTotalExtras);
//        model.addAttribute("delivery",delivery);

        model.addAttribute("listaTarjetas", tarjetaRepository.findByUsuario(usuario));
        model.addAttribute("listaDirecciones", listaDirecciones);
        // model.addAttribute("distritoActual",distritoActual);

        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario.getIdusuario()));
        return "Cliente/terminarCompra";
    }

    @PostMapping("/generarPedido")
    public String generarPedido(@RequestParam(value = "cupon", required = false) String idCupon,
                                @RequestParam(value = "ubicacion", required = false) String idUbicacion,
                               // PARAMETROS DE LA TARJETA A RECOGER - OJO : SOLO VALIDARLOS NO GUARDALRLOS
                                @RequestParam(value = "tarjeta", required = false) String tarjeta,
                                @RequestParam(value = "newTarjeta", required = false) String tipoTarjeta,
                                @RequestParam(value = "numeroTarjeta", required = false) String numeroTarjeta,
                                @RequestParam(value = "mes", required = false) String mes,
                                @RequestParam(value = "year", required = false) String year,
                                @RequestParam(value = "cvv", required = false) String cvv,
                                // @RequestParam(value = "delivery", required = false) String precioDelivery,
                                @RequestParam(value = "efectivoPagar", required = false) String efectivoPagar,
                                @RequestParam(value = "metodoPago", required = false) String idmp,
                                Model model, RedirectAttributes attr,
                                HttpSession session){
        /*
        * THINGS TODO:
        * ENVIAR CORREO
        * */
        Usuario cliente = (Usuario) session.getAttribute("usuario");
        //   List<Ubicacion> listaDirecciones = (List) session.getAttribute("poolDirecciones");
        List<Ubicacion> listaDirecciones = ubicacionRepository.findByUsuarioVal(cliente);
        int idRest = (int) session.getAttribute("idRest");
        Optional<Restaurante> restOpt = restauranteRepository.findById(idRest);
        Restaurante restaurante = restOpt.get();

        //Obteniendo el cupon
        Cupon cupon = null;
        boolean idCuponVal = false;
     /*     if(idCupon == null || idCupon.equals("")){
            idCupon = "";
            idCuponVal = true;
        }*/

        if (efectivoPagar == null) {
            efectivoPagar = "";
        }

        // por defecto si no selecciona nada entonces debería dejar pasa
        try {
/*
            if(!idCupon.equals("")) {
*/
            int idCuponInt = Integer.parseInt(idCupon);
            Optional<Cupon> cuponOpt = cuponRepository.findById(idCuponInt);
            if (cuponOpt.isPresent()) {
                cupon = cuponOpt.get();
                idCuponVal = true;
            }
            /*}else{
                idCuponVal = false;
            }*/
        } catch (NumberFormatException e) {
            if (idCupon == null || idCupon.trim().equals("")) {
                idCuponVal = true;
            }
        }




        //Obteniendo Ubicacion
        Ubicacion ubicacion = null;
        boolean idUbicVal = false;
        try {
            int idUbicInt = Integer.parseInt(idUbicacion);
            Optional<Ubicacion> ubiOpt = ubicacionRepository.findByIdusuarioAndIdubicacionVal(idUbicInt, cliente.getIdusuario());
            if (ubiOpt.isPresent()) {
                ubicacion = ubiOpt.get();
                idUbicVal = true;
            }
        } catch (NumberFormatException e) {
        }

        MetodoDePago metodoDePago = null;
        boolean idMetPaVal = false;
        boolean idTarjetaVal = false;
        boolean cantidadNullVal = false;
        boolean cvvVal = false;
        boolean numTarjetaVal = false;
        boolean mesVal = false;
        boolean anioVal = false;
        boolean tipoVal = false;
        boolean cvvValNull = false;
        boolean numTarjetaValNull = false;
        boolean mesValNull = false;
        boolean anioValNull = false;
        try {
            int idMePaInt = Integer.parseInt(idmp);
            Optional<MetodoDePago> metPagOpt = metodoPagoRepository.findById(idMePaInt);
            if (metPagOpt.isPresent()) {
                metodoDePago = metPagOpt.get();
                idMetPaVal = true;

                if (metodoDePago.getIdmetodopago() == 2) {
                    //en caso seleccione una
                    if (tarjeta != null) {
                        if (!tarjeta.equals("")) {
                            try {
                                int idTarjeta = Integer.parseInt(tarjeta);
                                Optional<Tarjeta> tarjetaOpt = tarjetaRepository.findById(String.valueOf(idTarjeta));
                                if (tarjetaOpt.isPresent()) {
                                    idTarjetaVal = true;
                                    cvvVal = true;
                                    numTarjetaVal = true;
                                    mesVal = true;
                                    anioVal = true;
                                    tipoVal = true;

                                    cvvValNull = true;
                                    numTarjetaValNull = true;
                                    mesValNull = true;
                                    anioValNull = true;
                                    cantidadNullVal = true;

                                }
                            } catch (NumberFormatException e) {
                            }
                        } else {
                            //VALIDANDO EL CVV
                            cantidadNullVal = true;
                            if(cvv != null){
                                cvvValNull = true;
                            }
                            if(cvvValNull){
                                if (cvv.length() == 3) {
                                    int cvvInt = Integer.parseInt(cvv);
                                    cvvVal = true;
                                }
                            }
                            //validando el mes
                            if(mes != null){
                                mesValNull = true;
                            }
                            if(mesValNull){
                                int mesInt = Integer.parseInt(mes);
                                if(mesInt >= 1 && mesInt <= 12) {//nodeberiaseralreves? - mmm? TODO checkar
                                    mesVal = true;
                                }
                            }
                            //validando el año
                            //validando el mes
                            if (year != null) {
                                anioValNull = true;
                            }
                            if (anioValNull) {
                                if (year.length() == 4) {
                                    int anioInt = Integer.parseInt(year);
                                    anioVal = true;
                                }
                            }
                            //validando el numero de tarjeta
                            if (numeroTarjeta != null) {
                                numTarjetaValNull = true;
                            }
                            if (numTarjetaValNull) {
                                if (numeroTarjeta.length() == 16) {
                                    numTarjetaVal = true;
                                }
                            }
                            //validacion del tipo de tarjeta
                            // 1 - visa
                            // 2 - mastercard
                            int tipoInt = Integer.parseInt(tipoTarjeta);
                            if (tipoInt == 1 || tipoInt == 2) {
                                tipoVal = true;
                            }
                            idTarjetaVal = true;
                        }
                        //caso contrario estará registrando una negistrando una nueva - pero no guardandola
                    } else {
                        cantidadNullVal = true;
                        //VALIDANDO EL CVV
                        if (cvv != null) {
                            cvvValNull = true;
                        }
                        if (cvvValNull) {
                            if (cvv.length() == 3) {
                                int cvvInt = Integer.parseInt(cvv);
                                cvvVal = true;
                            }
                        }
                        //validando el mes
                        if (mes != null) {
                            mesValNull = true;
                        }
                        if (mesValNull) {
                            int mesInt = Integer.parseInt(mes);
                            if (mesInt >= 1 && mesInt <= 12) {//nodeberiaseralreves? - mmm? TODO checkar
                                mesVal = true;
                            }
                        }
                        //validando el año
                        //validando el mes
                        if (year != null) {
                            anioValNull = true;
                        }
                        if (anioValNull) {
                            if (year.length() == 4) {
                                int anioInt = Integer.parseInt(year);
                                anioVal = true;
                            }
                        }
                        //validando el numero de tarjeta
                        if (numeroTarjeta != null) {
                            numTarjetaValNull = true;
                        }
                        if (numTarjetaValNull) {
                            if (numeroTarjeta.length() == 16) {
                                numTarjetaVal = true;
                            }
                        }
                        //validacion del tipo de tarjeta
                        // 1 - visa
                        // 2 - mastercard
                        int tipoInt = Integer.parseInt(tipoTarjeta);
                        if (tipoInt == 1 || tipoInt == 2) {
                            tipoVal = true;
                        }
                        idTarjetaVal = true;
                    }
                } else if (metodoDePago.getIdmetodopago() == 1 || metodoDePago.getIdmetodopago() == 3) {
                    cvvVal = true;
                    numTarjetaVal = true;
                    mesVal = true;
                    anioVal = true;
                    tipoVal = true;
                    cvvValNull = true;
                    numTarjetaValNull = true;
                    mesValNull = true;
                    anioValNull = true;
                    idTarjetaVal = true;

                    if(metodoDePago.getIdmetodopago() == 1){
                        if(!efectivoPagar.equals("")){
                            // aquí hago la validación de que ingrese una cantidad a pagar
                            // si el metodo de pago es efectivo, más adelante se validará que
                            // la cantidad a pagar será mayor a la del monto Total
                            cantidadNullVal = true;
                        }
                    }else{
                        cantidadNullVal = true;
                    }
                }
            }
        } catch (NumberFormatException e) {
        }
        Double delivery = (Double) session.getAttribute("delivery");
        // chancando la sesion
        try {
            if (ubicacion.getDistrito().getIddistrito() != restaurante.getDistrito().getIddistrito()) {
                delivery = 8.0;
            } else {
                delivery = 5.0;
            }
            session.setAttribute("delivery", delivery);
        } catch (NullPointerException e) {
        }
        // si el distrito es el mismo al que pertenezco esto pasos - si no debo cambair
        BigDecimal deliveryBig = new BigDecimal(delivery);
        /*
        Double precioDel = null;
        boolean precioDelVal = false;
        try{
            precioDel = Double.parseDouble(precioDelivery);
            precioDelVal = true;
        }catch (NumberFormatException e){
        }*/

        // se calcula los montos antes de entrar a guardar el pedido
        List<Plato_has_pedido> listaPlatos = (List<Plato_has_pedido>) session.getAttribute("carrito");
        List<Extra_has_pedido> listaExtra = (List<Extra_has_pedido>) session.getAttribute("extrasCarrito");

        BigDecimal precioTotalPlatos = new BigDecimal(0);
        for (int i = 0; i < listaPlatos.size(); i++) {
            BigDecimal subtotal = listaPlatos.get(i).getPreciounitario().multiply(new BigDecimal(listaPlatos.get(i).getCantidad()));
            precioTotalPlatos = precioTotalPlatos.add(subtotal);
        }
        System.out.println(listaExtra);
        BigDecimal precioTotalExtras = new BigDecimal(0);
        if (listaExtra != null) {
            for (int i = 0; i < listaExtra.size(); i++) {
                BigDecimal subTotal1 = listaExtra.get(i).getPreciounitario().multiply(new BigDecimal(listaExtra.get(i).getCantidad()));
                precioTotalExtras = precioTotalExtras.add(subTotal1);
            }
        }

        //!precioDelVal ||
        if (!idCuponVal || !idUbicVal || !idMetPaVal || !cantidadNullVal ||
                !numTarjetaVal || !mesVal || !anioVal || !cvvVal || !tipoVal ||
                !cvvValNull || !numTarjetaValNull || !mesValNull || !anioValNull || !idTarjetaVal) {
            // si algunos de estos datos esta mal debería redireccionarte a la misma vista
            if (!idCuponVal) {
                model.addAttribute("msgCuponVal", "Cupón ingresado inválido.");
            }
            if (!idUbicVal) {
                model.addAttribute("msgUbicVal", "Ubicación ingresada inválido.");
            }
            if (!idMetPaVal) {
                model.addAttribute("msgMetPagoVal", "Metodo de Pago ingresado inválido.");
            }

            if (!cvvValNull || !numTarjetaValNull || !mesValNull || !anioValNull) {
                model.addAttribute("msgNullMdp", "Ingrese un dato válido.");
            }

            if(!cantidadNullVal){
                model.addAttribute("msgCantidadNotNull", "Ingrese una cantidad a pagar mayor al monto total.");
            }

            if (!numTarjetaVal) {
                model.addAttribute("msgNumTarjetaVal", "El número de la tarjeta tiene que ser entero y de 16 dígitos. ");
                model.addAttribute("numTarjeta", numeroTarjeta);
            }
            if (!anioVal || !mesVal) {
                model.addAttribute("msgFechaVal", "Fecha ingresada inválido.");
                if (!anioVal) {
                    model.addAttribute("anio", year);
                }
                if (!mesVal) {
                    model.addAttribute("mes", mes);
                }
            }
            if (!cvvVal) {
                model.addAttribute("msgCvvVal", "El CVV tiene que ser entero y de 3 dígitos.");
                model.addAttribute("cvv", cvv);
            }
            if (!tipoVal) {
                model.addAttribute("msgTipoVal", "Tipo de tarjeta ingresado inválido.");
                model.addAttribute("tipo", tipoTarjeta);
            }

            model.addAttribute("montoCarrito", precioTotalPlatos);
            model.addAttribute("listaCupones", cuponRepository.findCuponesbyIdclienteAndRestId(cliente.getIdusuario(), idRest));
            model.addAttribute("montoExtras", precioTotalExtras);
            model.addAttribute("listaTarjetas", tarjetaRepository.findByUsuario(cliente));
            model.addAttribute("listaDirecciones", listaDirecciones);
            model.addAttribute("notificaciones", clienteRepository.notificacionCliente(cliente.getIdusuario()));
            return "Cliente/terminarCompra";
        } else {


            BigDecimal desc = new BigDecimal(0);
            if (idCupon != null) {
                if (!idCupon.trim().equals("")) {
                    desc = BigDecimal.valueOf(cupon.getDescuento());
                }
            }

            BigDecimal precioTotal = new BigDecimal(0);
            precioTotal = precioTotal.add(precioTotalPlatos);
            if (listaExtra != null) {
                precioTotal = precioTotal.add(precioTotalExtras);
            }
            if (idCupon != null) {
                if (!idCupon.trim().equals("")) {

                    if(desc.compareTo(precioTotal) > 0){
                        precioTotal = desc;
                        System.out.println("CUPON MAYOR AL PRECIO TOTAL" + precioTotal.toString() + " - " + desc.toString());
                        // si esto pasa el precio total se restaria y quedaría cero
                        // para que después se añada el efectivo
                    }
                    precioTotal = precioTotal.subtract(desc);

                }
            }

            precioTotal = precioTotal.add(deliveryBig);

            BigDecimal efectivoPagarF = BigDecimal.ZERO;
            if (!efectivoPagar.equals("")) {
                try {
                    efectivoPagarF = new BigDecimal(efectivoPagar);
                    if (precioTotal.compareTo(efectivoPagarF) == 1) {
                        model.addAttribute("msgEfect", "La cantidad a pagar debe ser mayor al precio total");
                        model.addAttribute("efectivo", efectivoPagar);

                        model.addAttribute("montoCarrito", precioTotalPlatos);
                        model.addAttribute("listaCupones", cuponRepository.findCuponesbyIdclienteAndRestId(cliente.getIdusuario(), idRest));
                        model.addAttribute("montoExtras", precioTotalExtras);
                        model.addAttribute("listaTarjetas", tarjetaRepository.findByUsuario(cliente));
                        model.addAttribute("listaDirecciones", listaDirecciones);
                        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(cliente.getIdusuario()));
                        return "Cliente/terminarCompra";
                    }
                } catch (NumberFormatException e) {
                    model.addAttribute("msgNotString", "Ingrese un número válido.");

                    model.addAttribute("montoCarrito", precioTotalPlatos);
                    model.addAttribute("listaCupones", cuponRepository.findCuponesbyIdclienteAndRestId(cliente.getIdusuario(), idRest));
                    model.addAttribute("montoExtras", precioTotalExtras);
                    model.addAttribute("listaTarjetas", tarjetaRepository.findByUsuario(cliente));
                    model.addAttribute("listaDirecciones", listaDirecciones);
                    model.addAttribute("notificaciones", clienteRepository.notificacionCliente(cliente.getIdusuario()));
                    return "Cliente/terminarCompra";
                }
            }
            BigDecimal precioTotal_1d = precioTotal.setScale(1, RoundingMode.HALF_UP);
            Pedido pedido = new Pedido();
            pedido.setPreciototal(precioTotal_1d.floatValue());


            if (!efectivoPagar.equals("")) {
                pedido.setCantidadapagar(Float.valueOf(efectivoPagar));
            }
            pedido.setEstado(0);

            //seteo mismoDistrito
            if (delivery.equals(Double.parseDouble("5"))) {
                pedido.setTiempoentrega(45);
                pedido.setMismodistrito(true);
            } else {
                pedido.setTiempoentrega(60);
                pedido.setMismodistrito(false);
            }
            pedido.setRestaurante(restauranteRepository.findById(listaPlatos.get(0).getPlato().getIdrestaurante()).get());
            if (idCupon != null) {
                if (!idCupon.trim().equals("")) {
                    pedido.setCupon(cupon);
               }
            }
            pedido.setCliente(cliente);
            pedido.setMetodopago(metodoDePago);
            pedido.setUbicacion(ubicacion);

            //seteo fecha
            Date date = new Date();
            DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            pedido.setFechapedido(hourdateFormat.format(date));

            //obtengo codigo del Pedido
            String codigoAleatorio = generarCodigoPedido();
            System.out.println(codigoAleatorio);
            pedido.setCodigo(codigoAleatorio);

            pedido = pedidoRepository.save(pedido);

            if (idCupon != null) {
                if (!idCupon.trim().equals("")) {
                    Cliente_has_cuponKey cliente_has_cuponKey = new Cliente_has_cuponKey();
                    Cliente_has_cupon cliente_has_cupon = new Cliente_has_cupon();
                    cliente_has_cuponKey.setIdcliente(cliente.getIdusuario());
                    cliente_has_cuponKey.setIdcupon(cupon.getIdcupon());
                    cliente_has_cupon.setCliente_has_cuponKey(cliente_has_cuponKey);
                    cliente_has_cupon.setUtilizado(true);
                    clienteHasCuponRepository.save(cliente_has_cupon);
                }
            }
            for (Plato_has_pedido plato_has_pedido : listaPlatos) {
                Plato_has_pedidoKey plato_has_pedidoKey = new Plato_has_pedidoKey();
                plato_has_pedidoKey.setCodigo(pedido.getCodigo());
                plato_has_pedidoKey.setIdplato(plato_has_pedido.getPlato().getIdplato());
                plato_has_pedido.setIdplatohaspedido(plato_has_pedidoKey);
                plato_has_pedido.setPedido(pedido);
                platoHasPedidoRepository.save(plato_has_pedido);
            }
            if (listaExtra != null) {
                for (Extra_has_pedido extra_has_pedido : listaExtra) {
                    Extra_has_pedidoKey extra_has_pedidoKey = new Extra_has_pedidoKey();
                    extra_has_pedidoKey.setCodigo(pedido.getCodigo());
                    extra_has_pedidoKey.setIdextra(extra_has_pedido.getExtra().getIdextra());
                    extra_has_pedido.setIdextra(extra_has_pedidoKey);
                    extra_has_pedido.setPedido(pedido);
                    extraHasPedidoRepository.save(extra_has_pedido);
                }
            }
            try {
                sendHtmlMailPedidoGen(cliente.getCorreo(),"Pedido Generado Exitosamente",pedido, cliente.getNombres());
            } catch (MessagingException e) { }
            session.removeAttribute("delivery");
            session.removeAttribute("idRest");
            session.removeAttribute("idPlato");
            session.removeAttribute("carrito");
            session.removeAttribute("extrasCarrito");
            session.removeAttribute("delivery");
            attr.addFlashAttribute("msgPedGen", "Se generó exitosamente un pedido con código: " + pedido.getCodigo());
            // todo falta reenviar notis
            model.addAttribute("notificaciones", clienteRepository.notificacionCliente(cliente.getIdusuario()));
            return "redirect:/cliente/pedidoActual";
        }

    }

    @GetMapping("/listaReportes")
    public String listaReportes(Model model, HttpSession session) {

        Usuario usuario1 = (Usuario) session.getAttribute("usuario");
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/listaReportes1";
    }


    /*********************************PEDIDO ACTUAL******************************************************************************++*/
    @GetMapping("/pedidoActual")
    public String pedidoActual23(@RequestParam Map<String, Object> params, Model model, HttpSession httpSession,
                                 @RequestParam(value = "texto", required = false) String texto,
                                 @RequestParam(value = "estado", required = false) String estado) {
        if (httpSession.getAttribute("carrito") != null) {
            httpSession.removeAttribute("carrito");
        }

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        int page;
        try {
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        } catch (NumberFormatException nfe) {
            page = 0;
        }
        Pageable pageRequest = PageRequest.of(page, 5);


        if (texto == null ) {
            texto = "";

        }
        if (estado == null) {
            estado = "7";

        }

        //texto = httpSession.getAttribute("texto") == null ? texto : (String) httpSession.getAttribute("texto");
        //estado = httpSession.getAttribute("estado") == null ? estado : (String) httpSession.getAttribute("estado");

        Integer limitSup;
        Integer limitInf;
        switch (estado) {
            case "0":
                limitSup = 0;
                limitInf = -1;
                break;
            case "1":
                limitSup = 1;
                limitInf = 0;
                break;

            case "3":
                limitSup = 3;
                limitInf = 2;
                break;

            case "4":
                limitSup = 4;
                limitInf = 3;
                break;
            case "5":
                limitSup = 5;
                limitInf = 4;
                break;

            default:
                limitSup = 6;
                limitInf = -1;
        }

        Page<PedidoDTO> listaPedidos = pedidoActualService.findPaginated(usuario1.getIdusuario(), texto, limitInf, limitSup, pageRequest);
        int totalPage = listaPedidos.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
        model.addAttribute("current", page + 1);

        model.addAttribute("listaPedidos", listaPedidos.getContent());
        //mandar valores
        model.addAttribute("texto", texto);
        model.addAttribute("estado", estado);
        model.addAttribute("total", totalPage);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/listaPedidoActual";

    }

    //PEDIDO ACTUAL
    @GetMapping("/pedidoActualPagina")
    public String pedidoActualPagina(@RequestParam Map<String, Object> params, Model model, HttpSession httpSession,
                                     @RequestParam(value = "texto", required = false) String texto,
                                     @RequestParam(value = "estado", required = false) String estado) {
        if (httpSession.getAttribute("carrito") != null) {
            httpSession.removeAttribute("carrito");
        }

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        int page;
        try {
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        } catch (NumberFormatException nfe) {
            page = 0;
        }
        Pageable pageRequest = PageRequest.of(page, 5);

        texto = httpSession.getAttribute("texto") == null ? "" : (String) httpSession.getAttribute("texto");
        estado = httpSession.getAttribute("estado") == null ? "7" : (String) httpSession.getAttribute("estado");

        Integer limitSup;
        Integer limitInf;
        switch (estado) {
            case "0":
                limitSup = 0;
                limitInf = -1;
                break;
            case "1":
                limitSup = 1;
                limitInf = 0;
                break;

            case "3":
                limitSup = 3;
                limitInf = 2;
                break;

            case "4":
                limitSup = 4;
                limitInf = 3;
                break;
            case "5":
                limitSup = 5;
                limitInf = 4;
                break;

            default:
                limitSup = 6;
                limitInf = -1;
        }

        Page<PedidoDTO> listaPedidos = pedidoActualService.findPaginated(usuario1.getIdusuario(), texto, limitInf, limitSup, pageRequest);
        int totalPage = listaPedidos.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
        model.addAttribute("total", totalPage);
        model.addAttribute("current", page + 1);
        model.addAttribute("listaPedidos", listaPedidos.getContent());
        //mandar valores
        model.addAttribute("texto", texto);
        model.addAttribute("estado", estado);

        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/listaPedidoActual";
    }

    /*********************************
     * ****************************************************************************************************************/
    @GetMapping("/cancelarPedido")
    public String cancelarPedido(@RequestParam("id") String id,
                                 RedirectAttributes attr,
                                 Model model, HttpSession session) {
        Usuario cliente = (Usuario) session.getAttribute("usuario");

        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(cliente.getIdusuario()));

        int idr = cliente.getIdusuario();
        try {
        Pedido pedido = pedidoRepository.encontrarporId(id);


            if (pedido != null && pedido.getCliente().getIdusuario() == idr) {
                if (pedido.getEstado() == 0) {
                    pedido.setEstado(2);
                    pedidoRepository.save(pedido);

                    try {
                        if (String.valueOf(pedido.getCupon().getIdcupon()) != null) {
                            Cliente_has_cupon chp = new Cliente_has_cupon();
                            Cliente_has_cuponKey chk = new Cliente_has_cuponKey();
                            chk.setIdcupon(pedido.getCupon().getIdcupon());
                            chk.setIdcliente(pedido.getCliente().getIdusuario());
                            chp.setCliente_has_cuponKey(chk);
                            chp.setUtilizado(false);
                            clienteHasCuponRepository.save(chp);
                        }
                    }catch (NullPointerException e){
                        //si es nulo quiere decir que no se uso cupon
                    }

                }
            }
        } catch (Exception e) {
            return "redirect:/cliente/pedidoActual";
        }
        return "redirect:/cliente/historialPedidos";
    }


    @GetMapping("/detallePedidoActual")
    public String detallePedidoActual(@RequestParam("codigo") String codigo, Model model, HttpSession session) {

        List<Pedido1DTO> pedido1DTOS = pedidoRepository.detalle1(codigo);
        if (pedido1DTOS.isEmpty()) {
            return "redirect:/cliente/pedidoActual";
        }

        Usuario usuario1 = (Usuario) session.getAttribute("usuario");
        if (codigo == null) {
            codigo = "";
        }

        //List<Pedido> codigos=pedidoRepository.listacodigos(usuario1.getIdusuario());
        List<CodigosEstados> codigos = pedidoRepository.listacod(usuario1.getIdusuario());
        System.out.println(codigo);
        for (CodigosEstados pedido : codigos) {
            System.out.println(pedido.getCodigo());
            if (pedido.getCodigo().equals(codigo)) {
                int estadop = pedido.getEstado();
                if (estadop == 0 || estadop == 1 || estadop == 3 || estadop == 4 || estadop == 5) {
                    model.addAttribute("listapedido1", pedidoRepository.detalle1(codigo));


                    List<Plato_has_PedidoDTO> listaPedidos = pedidoRepository.detalle2(codigo);

                    model.addAttribute("listapedido2", listaPedidos);

                    model.addAttribute("listaRepartidor", pedidoRepository.detalleRepartidor(codigo));


                    model.addAttribute("listaExtra", pedidoRepository.extrasPorPedido(codigo));
                    System.out.println(pedidoRepository.extrasPorPedido(codigo).size());


                    model.addAttribute("codigo", codigo);

                    model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
                    return "Cliente/detallePedidoActual";
                } else {
                    return "redirect:/cliente/pedidoActual";
                }


            }
        }

        return "redirect:/cliente/pedidoActual";


    }

    /********************************* HISTORIAL DE PEDIDO *******************************************************************************************************************++*/
    @GetMapping("/historialPedidos")
    public String historialPedidos(@RequestParam Map<String, Object> params, Model model, HttpSession httpSession,
                                   @RequestParam(value = "texto", required = false) String texto,
                                   @RequestParam(value = "estado", required = false) String estado) {
        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        int page;
        try {
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        } catch (NumberFormatException nfe) {
            page = 0;
        }
        Pageable pageRequest = PageRequest.of(page, 5);


        if (texto == null) {
            texto = "";

        }
        if (estado == null) {
            estado = "7";

        }

       // texto = httpSession.getAttribute("texto") == null ? "" : (String) httpSession.getAttribute("texto");
        //estado = httpSession.getAttribute("estado") == null ? "7" : (String) httpSession.getAttribute("estado");

        Integer limitSup = 6;
        Integer limitInf = 0;

        switch (estado) {
            case "2":
                limitSup = 2;
                limitInf = 1;
                break;

            case "6":
                limitSup = 6;
                limitInf = 5;
                break;

            default:
                limitSup = 6;
                limitInf = 0;
        }

        Page<PedidoValoracionDTO> listaPedidos = historialPedidoService.findPaginated2(usuario1.getIdusuario(), texto, limitInf, limitSup, pageRequest);
        int totalPage = listaPedidos.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }

        model.addAttribute("current", page + 1);
        model.addAttribute("listaPedidos", listaPedidos);
        //mandar valores
        model.addAttribute("texto", texto);
        model.addAttribute("estado", estado);
        model.addAttribute("total", totalPage);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/listaHistorialPedidos";
    }


    //HISTORIAL DE PEDIDO
    @GetMapping("/paginacionHistPed")
    public String paginacionHistPed(@RequestParam Map<String, Object> params, Model model, HttpSession httpSession,
                                    @RequestParam(value = "texto", required = false) String texto,
                                    @RequestParam(value = "estado", required = false) String estado) {

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        int page;
        try {
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        } catch (NumberFormatException nfe) {
            page = 0;
        }
        Pageable pageRequest = PageRequest.of(page, 5);

        texto = httpSession.getAttribute("texto") == null ? "" : (String) httpSession.getAttribute("texto");
        estado = httpSession.getAttribute("estado") == null ? "7" : (String) httpSession.getAttribute("estado");

        Integer limitSup = 6;
        Integer limitInf = 0;
        switch (estado) {
            case "2":
                limitSup = 2;
                limitInf = 1;
                break;

            case "6":
                limitSup = 6;
                limitInf = 5;
                break;

            default:
                limitSup = 6;
                limitInf = 0;
        }


        Page<PedidoValoracionDTO> listaPedidos = historialPedidoService.findPaginated2(usuario1.getIdusuario(), texto, limitInf, limitSup, pageRequest);
        int totalPage = listaPedidos.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
        model.addAttribute("current", page + 1);
        model.addAttribute("listaPedidos", listaPedidos);
        //mandar valores
        model.addAttribute("total", totalPage);
        model.addAttribute("texto", texto);
        model.addAttribute("estado", estado);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/listaHistorialPedidos";
    }

    /************************************************************************************************************************************************************************************************************/

    @GetMapping("/detalleHistorialPedido")
    public String detalleHistorialPedido(
            @RequestParam("codigo") String codigo, Model model, HttpSession session) {

        Usuario usuario1 = (Usuario) session.getAttribute("usuario");
        List<CodigosEstados> codigos = pedidoRepository.listacod(usuario1.getIdusuario());

        List<Pedido1DTO> pedido1DTOS = pedidoRepository.detalle1(codigo);
        if (pedido1DTOS.isEmpty()) {
            return "redirect:/cliente/historialPedidos";
        }


        if (codigo == null) {
            codigo = "";
        }


        for (CodigosEstados pedido : codigos) {
            if (pedido.getCodigo().equals(codigo)) {
                int estadop = pedido.getEstado();
                if (estadop == 2 || estadop == 6) {
                    model.addAttribute("listapedido1", pedidoRepository.detalle1(codigo));


                    List<Plato_has_PedidoDTO> listaPedidos = pedidoRepository.detalle2(codigo);


                    model.addAttribute("listapedido2", listaPedidos);
                    model.addAttribute("codigo", codigo);
                    model.addAttribute("listaRepartidor", pedidoRepository.detalleRepartidor(codigo));


                    model.addAttribute("listaExtra", pedidoRepository.extrasPorPedido(codigo));
                    model.addAttribute("codigo", codigo);
                    model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
                    return "Cliente/detallePedido";

                }

                return "redirect:/cliente/historialPedidos";
            }

        }


        return "redirect:/cliente/historialPedidos";

    }


    @PostMapping("/valorarRest")
    public String valorarRest(Model model, HttpSession httpSession, @RequestParam("id") String id,
                              @RequestParam(value = "val", required = false) String valoraRest,
                              @RequestParam(value = "comentRest", required = false) String comentRest) {
        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");
        Pedido pedido = pedidoRepository.encontrarporId(id);
        int idr = usuario1.getIdusuario();
        try {
            Integer valoraR = Integer.parseInt(valoraRest);
            if (valoraR == null) {
                model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
                return "redirect:/cliente/historialPedidos";
            }


            if (pedido != null && pedido.getCliente().getIdusuario() == idr && pedido.getEstado()==6) {
                Pedido pedido1 = pedido;
                pedido1.setValoracionrestaurante(valoraR);
                pedido1.setComentariorestaurante(comentRest);
                pedidoRepository.save(pedido1);
            }

        } catch (Exception w) {

        }
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "redirect:/cliente/historialPedidos";
    }

    @PostMapping("/valorarRep")
    public String valorarRep(Model model, HttpSession httpSession, @RequestParam("id") String id,
                             @RequestParam(value = "val", required = false) String valoraRest,
                             @RequestParam(value = "comentRep", required = false) String comentRest) {
        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");
        System.out.println(valoraRest);
        System.out.println(comentRest);
        System.out.println(id);
        Pedido pedido = pedidoRepository.encontrarporId(id);
        int idr = usuario1.getIdusuario();
        Integer valoraR;
        try {
            valoraR = Integer.parseInt(valoraRest);
            if (valoraRest == null) {
                model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
                return "redirect:/cliente/historialPedidos";
            }
            if (pedido != null  && pedido.getCliente().getIdusuario() == idr && pedido.getEstado()==6) {
                Pedido pedido1 = pedido;
                pedido1.setValoracionrepartidor(valoraR);
                pedido1.setComentariorepartidor(comentRest);
                pedidoRepository.save(pedido1);
            }
        } catch (Exception e) {
        }
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "redirect:/cliente/historialPedidos";
    }


    /********************************* REPORTEDINERO *******************************************************************************************************************++*/
    @GetMapping("/reporteDinero")
    public String pedidoActual3(@RequestParam Map<String, Object> params, Model model, HttpSession httpSession,
                                @RequestParam(value = "texto", required = false) String texto,
                                @RequestParam(value = "nombrec", required = false) String nombrec
            , @RequestParam(value = "mes", required = false) String mes,
                                @RequestParam(value = "anio", required = false) String anio) {


        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        int page;
        try {
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        } catch (NumberFormatException nfe) {
            page = 0;
        }
        Pageable pageRequest = PageRequest.of(page, 5);


        if (texto == null) {
            texto = "";

        }

        /******************************/
        if (nombrec == null) {
            nombrec = "";
        }

        /*********************************************++AÑO *************************************/
        List<YearDTO> listanios = pedidoRepository.listanios();
        List<YearDTO> listanios3 = pedidoRepository.listanios();

        Calendar c2 = GregorianCalendar.getInstance();
        int a = c2.get(Calendar.YEAR);
        String anio1 = Integer.toString(a);


        try {
            if (anio == null) {
                anio = anio1;

            } else if (mes != null) {
                int an = Integer.parseInt(anio);

            } else {
                anio = anio1;
            }
        } catch (NumberFormatException e) {
            anio = anio1;

        }
        /*******************************************************************/
        /******************************/
        Calendar c1 = GregorianCalendar.getInstance();
        int m = c1.get(Calendar.MONTH) + 1;
        int limitSup = 0;
        int limitInf = 12;


        try {
            if (mes == null) {
                mes = Integer.toString(m);
                limitSup = m;
                limitInf = m - 1;

            } else if (Integer.parseInt(mes) > 0 && Integer.parseInt(mes) <= 13) {
                if (mes.equalsIgnoreCase("13")) {
                    limitSup = 12;
                    limitInf = 0;
                } else {
                    limitSup = Integer.parseInt(mes);
                    limitInf = limitSup - 1;
                }

            } else {
                limitSup = 12;
                limitInf = 0;
            }
        } catch (NumberFormatException e) {
            limitSup = 12;
            limitInf = 0;
        }


        /*************************************************************************/


        //texto = httpSession.getAttribute("texto") == null ? texto : (String) httpSession.getAttribute("texto");

        //nombrec = httpSession.getAttribute("nombrec") == null ? nombrec : (String) httpSession.getAttribute("nombrec");


        //mes = httpSession.getAttribute("mes") == null ? mes : (String) httpSession.getAttribute("mes");

        //anio = httpSession.getAttribute("anio") == null ? anio : (String) httpSession.getAttribute("anio");


        Page<ReporteDineroDTO> listapedidos = reporteDineroService.findpage(usuario1.getIdusuario(), limitInf, limitSup, anio, texto, nombrec, pageRequest);
        int totalPage = listapedidos.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }


        List<ReporteDineroDTO> listapedidos2 = pedidoRepository.reportedinero2(usuario1.getIdusuario(), limitInf, limitSup, anio, texto, nombrec);
        BigDecimal totalsuma1 = new BigDecimal(0);
        if (listapedidos2.size() > 0) {
            for (ReporteDineroDTO rep : pedidoRepository.reportedinerocant(usuario1.getIdusuario(), limitInf, limitSup, anio, texto, nombrec)) {
                System.out.println(rep.getDescuento());
                totalsuma1 = totalsuma1.add(rep.getDescuento());
            }
        }
        model.addAttribute("current", page + 1);

        model.addAttribute("total", totalPage);
        System.out.println(totalsuma1);
        model.addAttribute("listapedidos", listapedidos);
        model.addAttribute("totalsuma", totalsuma1);
        model.addAttribute("texto", texto);
        model.addAttribute("mes", mes);


        /*************************************/
        if (listanios.isEmpty()) {
            ArrayList<String> lista = new ArrayList<>();
            lista.add(anio1);

            model.addAttribute("listanios", lista);

        } else {
            model.addAttribute("listanios", listanios);
        }
        String tam;
        if (listanios3.isEmpty()) {
            tam = "1";
        } else {
            tam = "0";
        }
        model.addAttribute("tam", tam);

        model.addAttribute("anio", anio);
        /*************************************/


        model.addAttribute("nombrec", nombrec);

        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/reporteDineroCliente";
    }


    //Reporte Dinero
    @GetMapping("/reportedineropage")
    public String pedidoActualPagina3(@RequestParam Map<String, Object> params, Model model, HttpSession httpSession,
                                      @RequestParam(value = "texto", required = false) String texto,
                                      @RequestParam(value = "nombrec", required = false) String nombrec
            ,@RequestParam(value = "mes", required = false) String mes,
                                      @RequestParam(value = "anio", required = false) String anio) {


        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        int page;
        try {
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        } catch (NumberFormatException nfe) {
            page = 0;
        }
        Pageable pageRequest = PageRequest.of(page, 10);


        texto = httpSession.getAttribute("texto") == null ? "" : (String) httpSession.getAttribute("texto");

        nombrec = httpSession.getAttribute("nombrec") == null ? "" : (String) httpSession.getAttribute("nombrec");

        Calendar c1 = GregorianCalendar.getInstance();
        int m = c1.get(Calendar.MONTH) + 1;
        mes = httpSession.getAttribute("mes") == null ? Integer.toString(m) : (String) httpSession.getAttribute("mes");


        Calendar c2 = GregorianCalendar.getInstance();
        int a = c2.get(Calendar.YEAR);
        String anio1 = Integer.toString(a);
        anio = httpSession.getAttribute("anio") == null ? Integer.toString(a) : (String) httpSession.getAttribute("anio");

        List<YearDTO> listanios = pedidoRepository.listanios();
        List<YearDTO> listanios3 = pedidoRepository.listanios();
        try {
            if (anio == null) {
                anio = anio1;
                httpSession.removeAttribute("anio");
            } else if (mes != null) {
                int an = Integer.parseInt(anio);
                httpSession.setAttribute("anio", anio);
            } else {
                anio = anio1;
            }
        } catch (NumberFormatException e) {
            anio = anio1;

        }


        int limitSup = 0;
        int limitInf = 12;


        try {
            if (mes == null) {
                mes = Integer.toString(m);
                limitSup = m;
                limitInf = m - 1;
                httpSession.removeAttribute("mes");
            } else if (Integer.parseInt(mes) > 0 && Integer.parseInt(mes) <= 13) {
                if (mes.equalsIgnoreCase("13")) {
                    limitSup = 12;
                    limitInf = 0;
                } else {
                    limitSup = Integer.parseInt(mes);
                    limitInf = limitSup - 1;
                }
                httpSession.setAttribute("mes", mes);
            } else {
                limitSup = 12;
                limitInf = 0;
            }
        } catch (NumberFormatException e) {
            limitSup = 12;
            limitInf = 0;
        }


        Page<ReporteDineroDTO> listapedidos = reporteDineroService.findpage(usuario1.getIdusuario(), limitInf, limitSup, anio, texto, nombrec, pageRequest);
        int totalPage = listapedidos.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
        List<ReporteDineroDTO> listapedidos2 = pedidoRepository.reportedinero2(usuario1.getIdusuario(), limitInf, limitSup, anio, texto, nombrec);
        BigDecimal totalsuma1 = new BigDecimal(0);
        if (listapedidos2.size() > 0) {
            for (ReporteDineroDTO rep : listapedidos2) {
                System.out.println(rep.getDescuento());
                totalsuma1 = totalsuma1.add(rep.getDescuento());
            }
        }
        model.addAttribute("current", page + 1);

        model.addAttribute("total", totalPage);
        System.out.println(totalsuma1);
        model.addAttribute("listapedidos", listapedidos);
        model.addAttribute("totalsuma", totalsuma1);
        model.addAttribute("texto", texto);
        model.addAttribute("mes", mes);
        model.addAttribute("nombrec", nombrec);
        if (listanios.isEmpty()) {
            ArrayList<String> lista = new ArrayList<>();
            lista.add(anio1);

            model.addAttribute("listanios", lista);

        } else {
            model.addAttribute("listanios", listanios);
        }
        String tam;
        if (listanios3.isEmpty()) {
            tam = "1";
        } else {
            tam = "0";
        }
        model.addAttribute("tam", tam);

        model.addAttribute("anio", anio);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/reporteDineroCliente";
    }

/****************************************************************REPORTE PEDIDO****************************************************************************/
    @GetMapping("/reportePedido")
    public String pedidoActual5(@RequestParam Map<String, Object> params, Model model, HttpSession httpSession,
                                @RequestParam(value = "texto", required = false) String texto,
                                @RequestParam(value = "numpedidos", required = false) String numpedidos
                                ,@RequestParam(value = "mes", required = false) String mes,
                                @RequestParam(value = "anio", required = false) String anio) {

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        int page;
        try {
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        } catch (NumberFormatException nfe) {
            page = 0;
        }
        //int page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        Pageable pageRequest = PageRequest.of(page, 10);

        if (texto == null) {
            texto = "";
        }

        if (numpedidos == null) {
            numpedidos = "7";

        }

        List<YearDTO> listanios = pedidoRepository.listanios();
        List<YearDTO> listanios3 = pedidoRepository.listanios();

        Calendar c2 = GregorianCalendar.getInstance();
        int a = c2.get(Calendar.YEAR);
        String anio1 = Integer.toString(a);

        try {
            if (anio == null) {
                anio = anio1;

            } else if (mes != null) {
                int an = Integer.parseInt(anio);

            } else {
                anio = anio1;
            }
        } catch (Exception e) {
            anio = anio1;

        }


        int limitSup = 0;
        int limitInf = 12;

        Calendar c1 = GregorianCalendar.getInstance();
        int m = c1.get(Calendar.MONTH) + 1;

        try {
            if (mes == null) {
                mes = Integer.toString(m);
                limitSup = m;
                limitInf = m - 1;

            } else if (Integer.parseInt(mes) > 0 && Integer.parseInt(mes) <= 13) {
                if (mes.equalsIgnoreCase("13")) {
                    limitSup = 12;
                    limitInf = 0;
                } else {
                    limitSup = Integer.parseInt(mes);
                    limitInf = limitSup - 1;
                }
            } else {
                limitSup = 12;
                limitInf = 0;
            }
        } catch (Exception e) {
            limitSup = 12;
            limitInf = 0;
        }

        //texto = httpSession.getAttribute("texto") == null ? texto : (String) httpSession.getAttribute("texto");

        //numpedidos = httpSession.getAttribute("numpedidos") == null ? numpedidos : (String) httpSession.getAttribute("numpedidos");

        //mes = httpSession.getAttribute("mes") == null ? mes : (String) httpSession.getAttribute("mes");
        //anio = httpSession.getAttribute("anio") == null ? anio : (String) httpSession.getAttribute("anio");

        int limit1cant;
        int limit2cant;
        switch (numpedidos) {
            case "1":
                limit1cant = 0;
                limit2cant = 10;
                break;

            case "2":
                limit1cant = 10;
                limit2cant = 20;
                break;

            case "3":
                limit1cant = 20;
                limit2cant = 30;
                break;

            case "4":
                limit1cant = 30;
                limit2cant = 40;
                break;

            default:
                limit1cant = 0;
                limit2cant = 50;
        }

        System.out.println("limiteInf: "+ limitInf);
        System.out.println("limiteSup: "+ limitSup);
        System.out.println("limit1cat: "+ limit1cant);
        System.out.println("limit2cat: "+ limit2cant);
        Page<ReportePedido> listapedidos = reportePedidoCService.findPaginated3(usuario1.getIdusuario(), limitInf, limitSup, texto,anio,limit1cant,limit2cant, pageRequest);
        List<ReporteTop3> listarestTop = pedidoRepository.reporteTop3Rest(usuario1.getIdusuario(), limitInf, limitSup, anio);
        List<ReporteTop3P> listaPl = pedidoRepository.reporteTop3Pl(usuario1.getIdusuario(), limitInf, limitSup, anio);
        int totalPage = listapedidos.getTotalPages();

        if (totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);

        }

        List<ReportePedido> listapedidos2 = pedidoRepository.reportexmes2(usuario1.getIdusuario(), limitInf, limitSup, texto, anio, limit1cant, limit2cant);
        BigDecimal totalsuma = new BigDecimal(0);
        if (listapedidos2.size() > 0) {
            for (ReportePedido rep : pedidoRepository.reportexmescant(usuario1.getIdusuario(), limitInf, limitSup, texto,anio,limit1cant,limit2cant)) {
                System.out.println(rep.getTotal());
                totalsuma = totalsuma.add(rep.getTotal());
            }
        }


        System.out.println(totalsuma);
        System.out.println("limiteInf(reportePedido): "+ limitInf);
        System.out.println("limiteSup(reportePedido): "+ limitSup);
        System.out.println("limit1cant(reportePedido): "+ limit1cant);
        System.out.println("limit2cant(reportePedido): "+ limit2cant);
        System.out.println("anio(reportePedido): "+ anio);
        System.out.println("usuario(reportePedido): "+ usuario1.getIdusuario());

        model.addAttribute("current", page + 1);
        model.addAttribute("totalsuma", totalsuma);
        model.addAttribute("listapedidos", listapedidos);
        model.addAttribute("listarestTop", listarestTop);
        model.addAttribute("listarestPl", listaPl);
        model.addAttribute("texto", texto);
        model.addAttribute("mes", mes);
        model.addAttribute("total", totalPage);
        model.addAttribute("numpedidos", numpedidos);
        System.out.println("IMPRIMIEDNO");
        if (listanios.isEmpty()) {
            ArrayList<String> lista = new ArrayList<>();
            lista.add(anio1);

            model.addAttribute("listanios", lista);

        } else {
            model.addAttribute("listanios", listanios);
        }
        String tam;
        if (listanios3.isEmpty()) {
            tam = "1";
        } else {
            tam = "0";
        }
        model.addAttribute("tam", tam);

        model.addAttribute("anio", anio);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/reportePedidoCliente";
    }


    //Reporte Pedido
    @GetMapping("/reportepedidopage")
    public String pedidoActualPagina5(@RequestParam Map<String, Object> params, Model model, HttpSession httpSession,
                                      @RequestParam(value = "texto", required = false) String texto,
                                      @RequestParam(value = "numpedidos", required = false) String numpedidos
            ,@RequestParam(value = "mes", required = false) String mes,
                                      @RequestParam(value = "anio", required = false) String anio) {


        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");


        int page;
        try {
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        } catch (NumberFormatException nfe) {
            page = 0;
        }
        Pageable pageRequest = PageRequest.of(page, 10);


        texto = httpSession.getAttribute("texto") == null ? "" : (String) httpSession.getAttribute("texto");

        numpedidos = httpSession.getAttribute("numpedidos") == null ? "7" : (String) httpSession.getAttribute("numpedidos");

        Calendar c1 = GregorianCalendar.getInstance();
        int m = c1.get(Calendar.MONTH) + 1;
        mes = httpSession.getAttribute("mes") == null ? Integer.toString(m) : (String) httpSession.getAttribute("mes");
        System.out.println(m);

        List<YearDTO> listanios = pedidoRepository.listanios();
        List<YearDTO> listanios3 = pedidoRepository.listanios();

        Calendar c2 = GregorianCalendar.getInstance();
        int a = c2.get(Calendar.YEAR);
        String anio1 = Integer.toString(a);
        anio = httpSession.getAttribute("anio") == null ? Integer.toString(a) : (String) httpSession.getAttribute("anio");

        try {
            if (anio == null) {
                anio = anio1;
                httpSession.removeAttribute("anio");
            } else if (mes != null) {
                int an = Integer.parseInt(anio);
                httpSession.setAttribute("anio", anio);
            } else {
                anio = anio1;
            }
        } catch (NumberFormatException e) {
            anio = anio1;

        }


        int limitSup = 0;
        int limitInf = 12;

        try {
            if (mes == null) {
                mes = Integer.toString(m);
                limitSup = m;
                limitInf = m - 1;
                httpSession.removeAttribute("mes");
            } else if (Integer.parseInt(mes) > 0 && Integer.parseInt(mes) <= 13) {
                if (mes.equalsIgnoreCase("13")) {
                    limitSup = 12;
                    limitInf = 0;
                } else {
                    limitSup = Integer.parseInt(mes);
                    limitInf = limitSup - 1;
                }
                httpSession.setAttribute("mes", mes);
            } else {
                limitSup = 12;
                limitInf = 0;
            }
        } catch (NumberFormatException e) {
            limitSup = 12;
            limitInf = 0;
        }



        int limit1cant;
        int limit2cant;
        switch (numpedidos) {
            case "1":
                limit1cant = 0;
                limit2cant = 10;
                break;

            case "2":
                limit1cant = 10;
                limit2cant = 20;
                break;

            case "3":
                limit1cant = 20;
                limit2cant = 30;
                break;

            case "4":
                limit1cant = 30;
                limit2cant = 40;
                break;

            default:
                limit1cant = 0;
                limit2cant = 50;
        }


        System.out.println("limiteInf(reportePedido): "+ limitInf);
        System.out.println("limiteSup(reportePedido): "+ limitSup);
        System.out.println("limit1cant(reportePedido): "+ limit1cant);
        System.out.println("limit2cant(reportePedido): "+ limit2cant);
        System.out.println("anio(reportePedido): "+ anio);
        System.out.println("usuario(reportePedido): "+ usuario1.getIdusuario());

        Page<ReportePedido> listapedidos = reportePedidoCService.findPaginated3(usuario1.getIdusuario(), limitInf, limitSup, texto, anio,limit1cant,limit2cant, pageRequest);

        List<ReporteTop3> listarestTop = pedidoRepository.reporteTop3Rest(usuario1.getIdusuario(),limitInf, limitSup,anio);
        List<ReporteTop3P> listaPl = pedidoRepository.reporteTop3Pl(usuario1.getIdusuario(), limitInf,limitSup,anio);
        int totalPage = listapedidos.getTotalPages();

        if (totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);

        }

        List<ReportePedido> listapedidos2 = pedidoRepository.reportexmes2(usuario1.getIdusuario(), limitInf, limitSup, texto,anio,limit1cant,limit2cant);
        BigDecimal totalsuma = new BigDecimal(0);
        if(listapedidos2.size()>0) {
            for (ReportePedido rep : listapedidos) {
                System.out.println(rep.getTotal());
                totalsuma = totalsuma.add(rep.getTotal());
            }
        }

        System.out.println(totalsuma);
        model.addAttribute("current", page + 1);
        model.addAttribute("totalsuma", totalsuma);
        model.addAttribute("listapedidos", listapedidos);
        model.addAttribute("listarestTop", listarestTop);
        model.addAttribute("listarestPl", listaPl);
        model.addAttribute("texto", texto);
        model.addAttribute("mes", mes);
        model.addAttribute("total", totalPage);
        model.addAttribute("numpedidos", numpedidos);
        if (listanios.isEmpty()) {
            ArrayList<String> lista = new ArrayList<>();
            lista.add(anio1);
            model.addAttribute("listanios", lista);
        } else {
            model.addAttribute("listanios", listanios);
        }
        String tam;
        if (listanios3.isEmpty()) {
            tam = "1";
        } else {
            tam = "0";
        }
        model.addAttribute("tam", tam);

        model.addAttribute("anio", anio);


        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/reportePedidoCliente";
    }


    /************************************************************************************************************************************************************************************************************/


    /*******************************************REPORTE TIEMPO**************************************************/
    @GetMapping("/reporteTiempo")
    public String pedidoActual4(@RequestParam Map<String, Object> params, Model model, HttpSession httpSession,
                                @RequestParam(value = "texto", required = false) String texto,
                                @RequestParam(value = "numpedidos", required = false) String numpedidos
            , @RequestParam(value = "mes", required = false) String mes,
                                @RequestParam(value = "anio", required = false) String anio) {

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        int page;
        try {
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        } catch (NumberFormatException nfe) {
            page = 0;
        }
        Pageable pageRequest = PageRequest.of(page, 10);


        if (texto == null) {
            texto = "";

        }


        if (numpedidos == null) {
            numpedidos = "";

        }


        /*********************************************++AÑO *************************************/
        List<YearDTO> listanios = pedidoRepository.listanios();
        List<YearDTO> listanios3 = pedidoRepository.listanios();

        Calendar c2 = GregorianCalendar.getInstance();
        int a = c2.get(Calendar.YEAR);
        String anio1 = Integer.toString(a);


        try {
            if (anio == null) {
                anio = anio1;
            } else if (mes != null) {
                int an = Integer.parseInt(anio);
            } else {
                anio = anio1;
            }
        } catch (NumberFormatException e) {
            anio = anio1;

        }
        /*******************************************************************/


        Calendar c1 = GregorianCalendar.getInstance();
        int m = c1.get(Calendar.MONTH) + 1;
        int limitSup = 0;
        int limitInf = 12;
        try {
            if (mes == null) {
                mes = Integer.toString(m);
                limitSup = m;
                limitInf = m - 1;
            } else if (Integer.parseInt(mes) > 0 && Integer.parseInt(mes) <= 13) {
                if (mes.equalsIgnoreCase("13")) {
                    limitSup = 12;
                    limitInf = 0;
                } else {
                    limitSup = Integer.parseInt(mes);
                    limitInf = limitSup - 1;
                }
            } else {
                limitSup = 12;
                limitInf = 0;
            }
        } catch (NumberFormatException e) {
            limitSup = 12;
            limitInf = 0;
        }


        //texto = httpSession.getAttribute("texto") == null ? texto : (String) httpSession.getAttribute("texto");

        //numpedidos = httpSession.getAttribute("numpedidos") == null ? numpedidos : (String) httpSession.getAttribute("numpedidos");


        //mes = httpSession.getAttribute("mes") == null ? mes : (String) httpSession.getAttribute("mes");
        //anio = httpSession.getAttribute("anio") == null ? anio : (String) httpSession.getAttribute("anio");
        int limit1cant;
        int limit2cant;
        switch (numpedidos) {
            case "1":
                limit1cant = 0;
                limit2cant = 10;
                break;

            case "2":
                limit1cant = 10;
                limit2cant = 20;
                break;

            case "3":
                limit1cant = 20;
                limit2cant = 30;
                break;

            case "4":
                limit1cant = 30;
                limit2cant = 40;
                break;

            default:
                limit1cant = 0;
                limit2cant = 40;
        }


        Page<ReportePedidoCDTO> listapedidos = reporteTiempoService.findPaginated3(usuario1.getIdusuario(), limitInf, limitSup, anio, texto, limit1cant, limit2cant, pageRequest);
        int totalPage = listapedidos.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
        int totalsuma1 = 0;
        int i = 0;
        if (totalPage > 0) {
            for (ReportePedidoCDTO rep : pedidoRepository.reportetiempocant(usuario1.getIdusuario(), limitInf, limitSup, anio, texto, limit1cant, limit2cant)) {
                // System.out.println(rep.getTiempoEntrega());
                totalsuma1 = totalsuma1 + rep.getTiempoentrega();
                i = i + 1;
            }
            totalsuma1 = totalsuma1 / i;
        }
        System.out.println("numoeidos:" + numpedidos);
        System.out.println(totalsuma1);
        model.addAttribute("current", page + 1);
        model.addAttribute("listapedidos", listapedidos);
        model.addAttribute("totalsuma1", totalsuma1);
        model.addAttribute("texto", texto);
        model.addAttribute("mes", mes);
        model.addAttribute("numpedidos", numpedidos);
        /*************************************/
        if (listanios.isEmpty()) {
            ArrayList<String> lista = new ArrayList<>();
            lista.add(anio1);

            model.addAttribute("listanios", lista);

        } else {
            model.addAttribute("listanios", listanios);
        }
        String tam;
        if (listanios3.isEmpty()) {
            tam = "1";
        } else {
            tam = "0";
        }
        model.addAttribute("tam", tam);

        model.addAttribute("anio", anio);

        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/reporteTiempoCliente";
    }

    //Reporte Dinero
    @GetMapping("/reportetiempopage")
    public String pedidoActualPagina4(@RequestParam Map<String, Object> params, Model model, HttpSession httpSession,
                                      @RequestParam(value = "texto", required = false) String texto,
                                      @RequestParam(value = "numpedidos", required = false) String numpedidos
                                      ,@RequestParam(value = "mes", required = false) String mes,
                                      @RequestParam(value = "anio", required = false) String anio) {


        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");


        int page;
        try {
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        } catch (NumberFormatException nfe) {
            page = 0;
        }
        Pageable pageRequest = PageRequest.of(page, 10);


        texto = httpSession.getAttribute("texto") == null ? "" : (String) httpSession.getAttribute("texto");

        numpedidos = httpSession.getAttribute("numpedidos") == null ? "" : (String) httpSession.getAttribute("numpedidos");

        Calendar c1 = GregorianCalendar.getInstance();
        int m = c1.get(Calendar.MONTH) + 1;
        mes = httpSession.getAttribute("mes") == null ? Integer.toString(m) : (String) httpSession.getAttribute("mes");


        List<YearDTO> listanios = pedidoRepository.listanios();
        List<YearDTO> listanios3 = pedidoRepository.listanios();
        Calendar c2 = GregorianCalendar.getInstance();
        int a = c2.get(Calendar.YEAR);
        String anio1 = Integer.toString(a);
        anio = httpSession.getAttribute("anio") == null ? Integer.toString(a) : (String) httpSession.getAttribute("anio");
        try {
            if (anio == null) {
                anio = anio1;
                httpSession.removeAttribute("anio");
            } else if (mes != null) {
                int an = Integer.parseInt(anio);
                httpSession.setAttribute("anio", anio);
            } else {
                anio = anio1;
            }
        } catch (NumberFormatException e) {
            anio = anio1;

        }






        int limitSup = 0;
        int limitInf = 12;


        try {
            if (mes == null) {
                mes = Integer.toString(m);
                limitSup = m;
                limitInf = m - 1;
                httpSession.removeAttribute("mes");
            } else if (Integer.parseInt(mes) > 0 && Integer.parseInt(mes) <= 13) {
                if (mes.equalsIgnoreCase("13")) {
                    limitSup = 12;
                    limitInf = 0;
                } else {
                    limitSup = Integer.parseInt(mes);
                    limitInf = limitSup - 1;
                }
                httpSession.setAttribute("mes", mes);
            } else {
                limitSup = 12;
                limitInf = 0;
            }
        } catch (NumberFormatException e) {
            limitSup = 12;
            limitInf = 0;
        }

        int limit1cant;
        int limit2cant;
        switch (numpedidos) {
            case "1":
                limit1cant = 0;
                limit2cant = 10;
                break;

            case "2":
                limit1cant = 10;
                limit2cant = 20;
                break;

            case "3":
                limit1cant = 20;
                limit2cant = 30;
                break;

            case "4":
                limit1cant = 30;
                limit2cant = 40;
                break;

            default:
                limit1cant = 0;
                limit2cant = 40;
        }


        Page<ReportePedidoCDTO> listapedidos = reporteTiempoService.findPaginated3(usuario1.getIdusuario(), limitInf, limitSup, anio, texto, limit1cant, limit2cant, pageRequest);
        int totalPage = listapedidos.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
        int totalsuma1 = 0;
        int i = 0;
        if (totalPage > 0) {
            for (ReportePedidoCDTO rep : listapedidos) {
                // System.out.println(rep.getTiempoEntrega());
                totalsuma1 = totalsuma1 + rep.getTiempoentrega();
                i = i + 1;
            }
            totalsuma1 = totalsuma1 / i;
        }

        System.out.println(totalsuma1);
        model.addAttribute("current", page + 1);
        model.addAttribute("listapedidos", listapedidos);
        model.addAttribute("totalsuma1", totalsuma1);
        model.addAttribute("texto", texto);
        model.addAttribute("mes", mes);
        model.addAttribute("numpedidos", numpedidos);
        if (listanios.isEmpty()) {
            ArrayList<String> lista = new ArrayList<>();
            lista.add(anio1);

            model.addAttribute("listanios", lista);

        } else {
            model.addAttribute("listanios", listanios);
        }
        model.addAttribute("tam", listanios3.size());
        model.addAttribute("anio", anio);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/reporteTiempoCliente";
    }

    /************************************************************************************************************************************************************************************************************/

    /********************************* CUPONES *******************************************************************************************************************++*/
    @GetMapping("/listaCupones")
    public String pedidoActual6(@RequestParam Map<String, Object> params, Model model, HttpSession httpSession,
                                @RequestParam(value = "texto", required = false) String texto,
                                @RequestParam(value = "descuento", required = false) String descuento) {


        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        int page;
        try {
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        } catch (NumberFormatException nfe) {
            page = 0;
        }
        Pageable pageRequest = PageRequest.of(page, 5);
        int limitSup;
        int limitInf;


        if (texto == null) {
            texto = "";
        }
        if (descuento == null) {
            descuento = "7";
        }

        //texto = httpSession.getAttribute("texto") == null ? texto : (String) httpSession.getAttribute("texto");
        //descuento = httpSession.getAttribute("descuento") == null ? descuento : (String) httpSession.getAttribute("descuento");

        //int limitSup ;
        //int limitInf ;

        switch (descuento) {
            case "1":
                limitSup = 5;
                limitInf = 0;
                break;

            case "2":
                limitSup = 10;
                limitInf = 5;
                break;
            case "3":
                limitSup = 15;
                limitInf = 10;
                break;

            case "4":
                limitSup = 20;
                limitInf = 15;
                break;
            default:
                limitSup = 20;
                limitInf = 0;
        }

        //List<CuponClienteDTO> listaCupones1=pedidoRepository.listaCupones1(usuario.getIdusuario());

        Page<CuponClienteDTO> cuponClienteDTOS = cuponClienteService.findPaginated2(usuario1.getIdusuario(), texto, limitInf, limitSup, pageRequest);
        int totalPage = cuponClienteDTOS.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
        model.addAttribute("total", totalPage);
        model.addAttribute("current", page + 1);

        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        model.addAttribute("listaCuponesenviar", cuponClienteDTOS);
        model.addAttribute("texto", texto);
        model.addAttribute("descuento", descuento);

        return "Cliente/listaCupones";
    }


    //HISTORIAL DE PEDIDO
    @GetMapping("/cuponespage")
    public String pedidoActualPagina6(@RequestParam Map<String, Object> params, Model model, HttpSession httpSession,
                                      @RequestParam(value = "texto", required = false) String texto,
                                      @RequestParam(value = "descuento", required = false) String descuento) {


        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        int page;
        try {
            page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        } catch (NumberFormatException nfe) {
            page = 0;
        }
        Pageable pageRequest = PageRequest.of(page, 5);

        texto = httpSession.getAttribute("texto") == null ? "" : (String) httpSession.getAttribute("texto");
        descuento = httpSession.getAttribute("descuento") == null ? "7" : (String) httpSession.getAttribute("descuento");

        int limitSup;
        int limitInf;
        switch (descuento) {
            case "1":
                limitSup = 5;
                limitInf = 0;
                break;

            case "2":
                limitSup = 10;
                limitInf = 5;
                break;

            case "3":
                limitSup = 15;
                limitInf = 10;
                break;
            case "4":
                limitSup = 20;
                limitInf = 15;
                break;
            default:
                limitSup = 20;
                limitInf = 0;
        }

        Page<CuponClienteDTO> cuponClienteDTOS = cuponClienteService.findPaginated2( usuario1.getIdusuario(),texto, limitInf, limitSup, pageRequest);
        int totalPage = cuponClienteDTOS.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
        model.addAttribute("total", totalPage);
        model.addAttribute("current", page + 1);

        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        model.addAttribute("listaCuponesenviar", cuponClienteDTOS);
        model.addAttribute("texto", texto);
        model.addAttribute("descuento", descuento);

        return "Cliente/listaCupones";
    }

    /************************************************************************************************************************************************************************************************************/

    public String generarCodigoPedido() {
        String codigo_mayor = pedidoRepository.maxCodigo();
        Date ahora = new Date();
        SimpleDateFormat formateador = new SimpleDateFormat("dd-MM-yyyy");

        String fecha = formateador.format(ahora);

        String[] parte = fecha.split("-");

        int anioHoy;
        int codigo2 = 0;
        anioHoy = Integer.parseInt(parte[2]) % 100;
        int mesHoy = Integer.parseInt(parte[1]);
        int diaHoy = Integer.parseInt(parte[0]);
        String codigoAc = anioHoy + parte[1] + parte[0];
        if (codigo_mayor == null) {
            codigo_mayor = codigoAc + "1000";
        }
        String codigo1 = "0";
        String codigoPedido = "";
        if (Integer.parseInt(codigoAc) >= Integer.parseInt(codigo1)) {
            int codigoAnio = (int) (Double.parseDouble(codigo_mayor) / 100000000);
            int codigoMes = (int) (Double.parseDouble(codigo_mayor) / 1000000);
            codigoMes = codigoMes % 100;
            int codigoDia = (int) (Double.parseDouble(codigo_mayor) / 10000);
            codigoDia = codigoDia % 100;
            codigo1 = codigoAc;
            if (codigoAnio == anioHoy && codigoMes == mesHoy && codigoDia == diaHoy) {
                codigo2 = (int) ((Double.parseDouble(codigo_mayor)) % 10000);//1001
                codigo2++;
                codigoPedido = (codigo1 + codigo2);
            } else {
                codigo2 = 1000;
                codigo2++;
                codigoPedido = (codigo1 + codigo2);
            }
        }

        return codigoPedido;
    }

    public void sendHtmlMailPedidoGen(String to, String subject, Pedido pedido, String cliente) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        Context context = new Context();
        context.setVariable("pedidoCodigo", pedido.getCodigo());
        context.setVariable("restaurante", pedido.getRestaurante().getNombre());
        context.setVariable("user", cliente);
        context.setVariable("ip", ip);
        context.setVariable("puerto", puerto);
        String emailContent = templateEngine.process("Correo/pedidoGenerado", context);
        helper.setText(emailContent, true);
        javaMailSender.send(message);
    }

}
