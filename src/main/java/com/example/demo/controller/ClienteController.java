package com.example.demo.controller;


import com.example.demo.dtos.*;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.service.ExtrasClienteService;
import com.example.demo.service.PedidoActualService;
import com.example.demo.service.PlatoClienteService;
import com.example.demo.service.RestauranteClienteService;
import com.example.demo.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.security.crypto.bcrypt.BCrypt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.model.IModel;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpSession;
import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.aspectj.runtime.internal.Conversions.doubleValue;

@Controller

@RequestMapping("/cliente")
public class ClienteController {

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
    ReporteTiempoService reporteTiempoService;

    @Autowired
    ReporteDineroService reporteDineroService;

    @Autowired
    Detalle2Service detalle2Service;

    @Autowired
    CuponClienteService cuponClienteService;

    @Autowired
    MetodoPagoRepository metodoPagoRepository;

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

        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        String direccionactual = usuario.getDireccionactual();
        int iddistritoactual = 1;
        Integer limitInfP = 0;
        Integer limitSupP = 5000;
        Integer limitInfVal = 0;
        Integer limitSupVal = 6;
        Integer limitInfCat = 0;
        Integer limitSupCat = 5000;

        //buscar que direccion de milista de direcciones coincide con mi direccion actual

        List<ClienteDTO> listadirecc = clienteRepository.listaParaCompararDirecciones(usuario.getIdusuario());

        for (ClienteDTO cl : listadirecc) {
            if (cl.getDireccion().equalsIgnoreCase(direccionactual)) {
                iddistritoactual = cl.getIddistrito();
                break;
            }
        }

        if(val == null || val.equals("")){
            val = "6";
        }

        if(idPrecio == null || idPrecio.equals("")){
            idPrecio = "6";
        }

        if(texto == null){
            texto = "";
        }

        if(idCategoria == null){
            idCategoria="6";
        }else {
            String[] chain = idCategoria.split("-");
            limitInfCat = Integer.parseInt(chain[0]);
            limitSupCat = Integer.parseInt(chain[1]);
        }

        switch (idPrecio){
            case "1":
                limitInfP = 0;
                limitSupP = 10;
                break;
            case "2":
                limitInfP = 10;
                limitSupP = 20;
                break;
            case "3":
                limitInfP = 20;
                limitSupP = 30;
                break;
            case "4":
                limitInfP = 30;
                limitSupP = 40;
                break;
            case "5":
                limitInfP = 50;
                limitSupP = 5000;
                break;
            default:
                limitInfP = 0;
                limitSupP = 5000;
        }

        switch (val){
            case "1":
                limitInfVal = 1;
                limitSupVal = 2;
                break;
            case "2":
                limitInfVal = 2;
                limitSupVal = 3;
                break;
            case "3":
                limitInfVal = 3;
                limitSupVal = 4;
                break;
            case "4":
                limitInfVal = 4;
                limitSupVal = 5;
                break;
            case "5":
                limitInfVal = 5;
                limitSupVal = 6;
                break;
            default:
                limitInfVal = 0;
                limitSupVal = 6;
        }

        int page  = params.get("page") != null ? Integer.valueOf(params.get("page").toString())-1 : 0;
        Pageable pageRequest = PageRequest.of(page, 5);

        Page<RestauranteDTO> listaRestaurante = restauranteClienteService.listaRestaurantePaginada(texto, limitInfP, limitSupP, limitInfVal, limitSupVal, limitInfCat, limitSupCat, iddistritoactual,pageRequest);
        int totalPage = listaRestaurante.getTotalPages();
        if(totalPage > 0){
            List<Integer> pages = IntStream.rangeClosed(1,totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages",pages);
        }

        model.addAttribute("listaRestaurante", listaRestaurante.getContent());

        model.addAttribute("categorias",categoriasRestauranteRepository.findAll());
        model.addAttribute("idPrecio", idPrecio);
        model.addAttribute("idCategoria", idCategoria);
        model.addAttribute("texto", texto);
        model.addAttribute("val", val);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario.getIdusuario()));
        return "Cliente/listaRestaurantes";
    }

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
        usuario.setDireccionactual(direccionActual);
        httpSession.setAttribute("usuario", usuario);
        model.addAttribute("listaDistritos", distritosRepository.findAll());
        clienteRepository.save(usuario);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario.getIdusuario()));
        return "redirect:/cliente/listaDirecciones";
    }

    @PostMapping("/eliminarDireccion")
    public String eliminarDirecciones(@RequestParam("listaIdDireccionesAeliminar") List<String> listaIdDireccionesAeliminar, HttpSession session, Model model) {
        Usuario usuarioS = (Usuario) session.getAttribute("usuario");
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
        return "redirect:/cliente/listaDirecciones";
    }

    @PostMapping("/agregarDireccion")
    public String registrarNewDireccion(@RequestParam("direccion") String direccion, @RequestParam("distrito") Integer distrito, HttpSession httpSession, Model model) {
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
            Integer u_dist = distrito;
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

        for (Ubicacion u : listaDir) {
            if (u.getDireccion().equalsIgnoreCase(direccion)) {
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
            Distrito distritoEnviar = distritosRepository.getOne(distrito);
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
                              @RequestParam(value = "idRest",required = false) Integer idRest, //solo es necesario recibirla de restaurante a platos
                              @RequestParam(value = "texto",required = false) String texto,
                              @RequestParam(value = "idPrecio",required = false) String idPrecio,
                              Model model, HttpSession session) {
        Integer limitInf = 0;
        Integer limitSup = 5000;

        if (idRest == null) {
            idRest = (Integer) session.getAttribute("idRest");
        } else {
            session.setAttribute("idRest", idRest);
        }

        if(session.getAttribute("idPlato") != null){
            session.removeAttribute("idPlato");
        }

        Optional<Restaurante> restauranteOpt = restauranteRepository.findById(idRest);
        int page  = params.get("page") != null ? Integer.valueOf(params.get("page").toString())-1 : 0;
        Pageable pageRequest = PageRequest.of(page, 6);

        if(idPrecio == null || idPrecio.equals("")){
            idPrecio = "6";
        }

        if(texto == null){
            texto = "";
        }

        if(restauranteOpt.isPresent()){
           Restaurante restaurante = restauranteOpt.get();
            model.addAttribute("nombreRest", restaurante.getNombre());
        }

        switch (idPrecio){
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

        Page<PlatosDTO> listaPlato = platoClienteService.listaPlatoPaginada(idRest, texto, limitInf, limitSup, pageRequest);
        int totalPage = listaPlato.getTotalPages();
        if(totalPage > 0){
            List<Integer> pages = IntStream.rangeClosed(1,totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages",pages);
        }
        Usuario usuario1 = (Usuario) session.getAttribute("usuario");

        model.addAttribute("listaPlato",listaPlato.getContent());
        model.addAttribute("texto",texto);
        model.addAttribute("idPrecio",idPrecio);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
         return "/Cliente/listaProductos";
    }

    @GetMapping("/detallePlato")
    public String detallePlato(@RequestParam Map<String, Object> params,
                                @RequestParam(value = "texto",required = false) String texto,
                                @RequestParam(value = "idPrecio",required = false) String idPrecio,
                                @RequestParam(value = "idCategoria",required = false) String idCategoria,
                                @RequestParam(value = "idPlato",required = false) Integer idPlato, HttpSession session,
                                Model model) {
        Integer idRest = (Integer) session.getAttribute("idRest");
        Usuario usuario1 = (Usuario) session.getAttribute("usuario");

        if(idPlato == null){
            idPlato = (Integer) session.getAttribute("idPlato");
        }

        if(session.getAttribute("idPlato") == null){
            //session.removeAttribute("idPlato");
            session.setAttribute("idPlato",idPlato);
        }


        Optional<Restaurante> restauranteOpt = restauranteRepository.findById(idRest);
        Optional<Plato> platoOpt = platoRepository.findById(idPlato);


        int limitInfPe = 0;
        int limitSupPe = 0;
        int limitInfCa = 0;
        int limitSupCa = 0;
        int page  = params.get("page") != null ? Integer.valueOf(params.get("page").toString())-1 : 0;
        Pageable pageRequest = PageRequest.of(page, 5);

        if(platoOpt.isPresent() && restauranteOpt.isPresent()){
            Plato plato = platoOpt.get();
            Restaurante restaurante = restauranteOpt.get();

            if(idPrecio == null || idPrecio.equals("")){
                idPrecio = "6";
            }

            if(idCategoria == null || idCategoria.equals("")){
                idCategoria = "5";
            }

            if(texto == null){
                texto = "";
            }

            switch (idCategoria){
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

            switch (idPrecio){
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


            Page<ExtraDTO> listaExtras = extrasClienteService.listaExtrasDisponiblesPaginada(idRest, idPlato,texto, limitInfCa,limitSupCa, limitInfPe, limitSupPe,1,pageRequest);
            int totalPage = listaExtras.getTotalPages();
            if(totalPage > 0){
                List<Integer> pages = IntStream.rangeClosed(1,totalPage).boxed().collect(Collectors.toList());
                model.addAttribute("pages",pages);
            }

            model.addAttribute("plato",plato);
            model.addAttribute("listaExtras",listaExtras.getContent());
          //  model.addAttribute("idRest",idRest);
            model.addAttribute("idPrecio",idPrecio);
            model.addAttribute("idCategoria",idCategoria);
            model.addAttribute("texto",texto);
            model.addAttribute("nombreRest",restaurante.getNombre());
            model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
            return "Cliente/detallePlato";
        }else{
            //model.addAttribute("idRest",idRest);
            //model.addAttribute("idPlato",idPlato);
            model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
            return "redirect: cliente/listaPlatos?idRest="+idRest+"&idPlato="+idPlato;
        }
    }

    @GetMapping("/mostrarCarrito")
    public String mostrarCarrito(@RequestParam(value = "idPlato",required = false) Integer idPlato,
                                 @RequestParam(value = "idPage", required = false) String idPage,
                                 HttpSession session,
                                 Model model){
        //ArrayList<Plato_has_pedido> carrito = (ArrayList<Plato_has_pedido>) session.getAttribute("carrito");
        //List<Plato_has_pedido> carritoL = (List<Plato_has_pedido>) session.getAttribute("carrito");

        Integer idRest = (Integer) session.getAttribute("idRest");

        //en caso le cambie el html el disbled lo redireccionará al mismo sitio si no hay sesión de carrito
        if(session.getAttribute("carrito") == null){
            return "redirect:/cliente/listaPlatos?idRest="+idRest;
        }

        Usuario usuario1 = (Usuario) session.getAttribute("usuario");
        //model.addAttribute("carrito",carritoL);
        //model.addAttribute("idRest",idRest);
        model.addAttribute("idPlato",idPlato);
        model.addAttribute("idPage",idPage);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        model.addAttribute("idRest",idRest);
        return "Cliente/carritoCompras";
    }

    @PostMapping("/aniadirCarrito")
    public String aniadirCarrito(@RequestParam("idPlato") int idPlato,
                                 @RequestParam(value = "idPage", required = false) String idPage,
                                 @RequestParam("cantidadPlato") String cantidadPlato,
                                 HttpSession session,
                                 RedirectAttributes attr, Model model){

        //carrito
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
        Optional<Plato> platoOptional = platoRepository.findById(idPlato);
        int cantint = 0;
        try {
            cantint = Integer.parseInt(cantidadPlato);

            if(cantint <= 0 && cantint <= 20){
                if(idPage.equals("1")){
                    url = "detallePlato";
                    params = "?idRest="+idRest+"&idPlato="+idPlato;
                }else if(idPage.equals("0")){
                    url = "listaPlatos";
                    params = "?idRest="+idRest;
                }else{
                    url = "listaRestaurantes";
                }
                attr.addFlashAttribute("msgVal","Ingrese un número mayor a 0 y menor a 20");
                return "redirect:/cliente/"+url+params;
            }

        }catch (NumberFormatException e){
            if(idPage.equals("1")){
                url = "detallePlato";
                params = "?idRest="+idRest+"&idPlato="+idPlato;
            }else if(idPage.equals("0")){
                url = "listaPlatos";
                params = "?idRest="+idRest;
            }else{
                url = "listaRestaurantes";
            }
            attr.addFlashAttribute("msgValCant","Ingrese un número");
            return "redirect:/cliente/"+url+params;
        }

        if(platoOptional.isPresent()){
            //GUARDANDO TODOS LOS ATRIBUTOS NECESARIOS A CARRITO
            Plato plato = platoOptional.get();
            Plato_has_pedidoKey idComPlato = new Plato_has_pedidoKey();
            //SE GUARDARÁ TEMPORALMENTE EN SESIÓN CON UN CÓDIGO TEMPORAL QUE SE ACTUALIZARÁ
            String codigo = "CODIGOTEMPORAL";
            int puntero = 0;
            if(carrito.size() > 0){
                //TODO VALIDAR QUE CUANDO SE AGREGA UN PEDIDO DEL MISMO ID PLATO - ESTA CANTIDAD SEA LA SUMA
                for (int i = 0; i < carrito.size(); i++) {
                    if(idPlato == carrito.get(i).getIdplatohaspedido().getIdplato()){
                        puntero = i;
                        break;
                    }
                }
                if(idPlato == carrito.get(puntero).getIdplatohaspedido().getIdplato()){
                    carrito.get(puntero).setCantidad(carrito.get(puntero).getCantidad()+cantint);
                }else {
                    idComPlato.setIdplato(idPlato);
                    idComPlato.setCodigo(codigo);
                    php.setPlato(plato);
                    php.setCantidad(cantint);
                    php.setPreciounitario(BigDecimal.valueOf(plato.getPrecio()));
                    php.setIdplatohaspedido(idComPlato);
                    carrito.add(php);
                }

            }else{
                //TODO HAY QUE VALIDAR DE QUE VISTA SE ESTÁ AÑADIENDO AL CARRITO - XQ DE ESO DEPENDE EL COMENTARIO
                idComPlato.setIdplato(idPlato);
                idComPlato.setCodigo(codigo);

                //RECORDAR VOLVERLO NULL Y AÑADIR EL PEDIDO AL FINAL
                php.setPlato(plato);
                php.setCantidad(cantint);

                //TODO SI FUERA EL SUBTOTAL EN EL CARRITO SE GUARDARÍA PRECIO UNITARIO X CANTIDAD PLATO
                php.setPreciounitario(BigDecimal.valueOf(plato.getPrecio()));
                php.setIdplatohaspedido(idComPlato);
                carrito.add(php);
            }
            session.setAttribute("carrito",carrito);
            attr.addFlashAttribute("msgAdd", "Se agregó un plato al carrito");
        }else{
            attr.addFlashAttribute("msgNotFound", "No se encontro el plato");
        }
        //TODO por ahora solo funcionará si el flujo es LISTA DE PLATOS - DETALLE - VER CARRITO
        if(idPage.equals("1")){
            url = "detallePlato";
            params = "?idRest="+idRest+"&idPlato="+idPlato;
        }else if(idPage.equals("0")){
            url = "listaPlatos";
            params = "?idRest="+idRest;
        }else{
            session.removeAttribute("carrito");
            url = "listaRestaurantes";
        }
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "redirect:/cliente/"+url+params;
    }


    @GetMapping("/vaciarCarrito")
    public String vaciarCarrito(RedirectAttributes attr, HttpSession session){
        session.removeAttribute("carrito");
        return "redirect:/cliente/listaPlatos";
    }

    @GetMapping("/vaciarExtras")
    public String vaciarExtras(RedirectAttributes attr, HttpSession session){
        session.removeAttribute("extrasCarrito");
        return "redirect:/cliente/mostrarCarrito";
    }

    @PostMapping("/eliminar")
    public String eliminarPlatos(@RequestParam(value = "platoEliminar",required = false) List<Integer> platoEliminar, HttpSession session,
                                 Model model, @RequestParam(value = "idPage", required = false) String idPage){
        System.out.println(platoEliminar);
        // 3 elementos
        // 1 elemento - idPlato
        //en caso no seleccione nada


        if(platoEliminar == null){
            return "redirect:/cliente/mostrarCarrito";
        }

        List<Plato_has_pedido> carrito = (List<Plato_has_pedido>) session.getAttribute("carrito");
        Integer idRest = (Integer) session.getAttribute("idRest");

        for (Integer idPlato : platoEliminar) {
            for(int i = 0; i < carrito.size(); i++){
                if(idPlato == carrito.get(i).getIdplatohaspedido().getIdplato()){
                    carrito.remove(i);
                    break;
                }
            }
        }


        //en caso elimine el carrito quitar la sesión se debe
        if(carrito.size()==0){
            session.removeAttribute("carrito");
            return "redirect:/cliente/listaPlatos?idRest="+idRest;
        }
        model.addAttribute("idPage","0");
        //Actualizando el carrito
        session.setAttribute("carrito",carrito);
        return "redirect:/cliente/mostrarCarrito?idRest="+idRest;
    }

    @PostMapping("/eliminarExtras")
    public String eliminarExtras(@RequestParam(value = "extraEliminar",required = false) List<Integer> extraEliminar, HttpSession session,
                                 Model model){
        System.out.println(extraEliminar);
        // 3 elementos
        // 1 elemento - idPlato
        //en caso no seleccione nada

        Integer idRest = (Integer) session.getAttribute("idRest");

        if(extraEliminar == null){
            return "redirect:/cliente/mostrarExtrasCarrito";
        }

        List<Extra_has_pedido> carritoExtra = (List<Extra_has_pedido>) session.getAttribute("extrasCarrito");

        for (Integer idExtra : extraEliminar) {
            for(int i = 0; i < carritoExtra.size(); i++){
                if(idExtra == carritoExtra.get(i).getIdextra().getIdextra()){
                    carritoExtra.remove(i);
                    break;
                }
            }
        }


        //en caso elimine el carrito quitar la sesión se debe
        if(carritoExtra.size()==0){
            session.removeAttribute("extrasCarrito");
            return "redirect:/cliente/listaPlatos?idRest="+idRest;
        }
        //Actualizando el carrito
        session.setAttribute("extrasCarrito",carritoExtra);
        return "redirect:/cliente/mostrarExtrasCarrito?idRest="+idRest;
    }

    @GetMapping("/mostrarExtrasCarrito")
    public String mostrarExtrasCarrito(HttpSession session,
                                       Model model){
        Usuario usuario1 = (Usuario) session.getAttribute("usuario");
        if(session.getAttribute("extrasCarrito") == null){
            return "redirect:/cliente/motrarCarrito";
        }
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        // model.addAttribute("idRest",idRest);
        return "Cliente/carritoExtras";
    }

    @PostMapping("/aniadirExtras")
    public String aniadirExtras(@RequestParam("idExtra") Integer idExtra,
                                @RequestParam(value = "idPlato",required = false) Integer idPlato,
                                @RequestParam(value = "cantidadExtra") String cantidadExtra,
                                HttpSession session,
                                RedirectAttributes attr, Model model){
        //extras de carrito
        ArrayList<Extra_has_pedido> extrasCarrito = null;
        String urlDetalle = "";
        String params = "";
       // todo verificar noti
        Usuario usuario1 = (Usuario) session.getAttribute("usuario");
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));

        if(idPlato == null){
            idPlato = (Integer) session.getAttribute("idPlato");
        }

        Integer idRest = (Integer) session.getAttribute("idRest");

        if(session.getAttribute("extrasCarrito")==null){
            extrasCarrito = new ArrayList<>();
        }else{
            extrasCarrito = (ArrayList<Extra_has_pedido>) session.getAttribute("extrasCarrito");
        }

        int cantint = 0;
        try {
            cantint = Integer.parseInt(cantidadExtra);

            if(cantint <= 0 && cantint <= 20){
                urlDetalle = "detallePlato";
                params = "?idRest="+idRest+"&idPlato="+idPlato;

                attr.addFlashAttribute("msgVal","Ingrese un número mayor a 0 y menor a 20");
                return "redirect:/cliente/"+urlDetalle+params;
            }
        }catch (NumberFormatException e){
            urlDetalle = "detallePlato";
            params = "?idRest="+idRest+"&idPlato="+idPlato;

            attr.addFlashAttribute("msgValCant","Ingrese un número");
            return "redirect:/cliente/"+urlDetalle+params;
        }


        Extra_has_pedido ehp = new Extra_has_pedido();
        Optional<Extra> extraOptional = extraRepository.findById(idExtra);

        if(extraOptional.isPresent()){
            Extra extra = extraOptional.get();
            Extra_has_pedidoKey idComExtra = new Extra_has_pedidoKey();
            String codigo = "CODIGOTEMPORAL";

            int puntero = 0;
            if(extrasCarrito.size() > 0){
                //TODO VALIDAR QUE CUANDO SE AGREGA UN PEDIDO DEL MISMO ID PLATO - ESTA CANTIDAD SEA LA SUMA
                for (int i = 0; i < extrasCarrito.size(); i++) {
                    if(idExtra == extrasCarrito.get(i).getIdextra().getIdextra()){
                        puntero = i;
                        break;
                    }
                }
                if(idExtra == extrasCarrito.get(puntero).getIdextra().getIdextra()){
                    extrasCarrito.get(puntero).setCantidad(extrasCarrito.get(puntero).getCantidad()+cantint);
                }else {
                    idComExtra.setIdextra(idExtra);
                    idComExtra.setCodigo(codigo);
                    ehp.setExtra(extra);
                    ehp.setCantidad(cantint);
                    ehp.setPreciounitario(BigDecimal.valueOf(extra.getPreciounitario()));
                    ehp.setIdextra(idComExtra);
                    extrasCarrito.add(ehp);
                }

            }else{
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
            session.setAttribute("extrasCarrito",extrasCarrito);
            attr.addFlashAttribute("msgAddExtra", "Se agregó un extra al carrito");
        }else{
            attr.addFlashAttribute("msgNotFound", "No se encontró el extra");
        }

        urlDetalle = "detallePlato";
        params = "?idRest="+idRest+"&idPlato="+idPlato;
        return "redirect:/cliente/"+urlDetalle+params;
    }

    @PostMapping("/modificarExtra")
    public String modificarExtra(@RequestParam(value = "cantidad", required = false) List<String> cantidad,
                                 @RequestParam(value = "extraGuardar", required = false) List<Integer> extraGuardar,
                                 RedirectAttributes attr, Model model,
                                 HttpSession session){
        Extra extra = new Extra();
        List<Extra_has_pedido> carritoExtra = (List<Extra_has_pedido>) session.getAttribute("carritoExtra");

        int cantVal = 0;

        // LOS TAMAÑOS DE LOS ARREGLOS DEBEN SER IGUALES - INCLUSO SI NO INGRESA UNO ESTE SERÁ ""
       if (cantidad.size() != extraGuardar.size() ||
                extraGuardar.size() != carritoExtra.size()){
            return "redirect:/cliente/mostrarCarrito"; //TODO redireccionar al mismo sitio
        }

        for (int i = 0; i < cantidad.size(); i++) {
            try{
                cantVal = Integer.parseInt(cantidad.get(i));
                if(cantVal <= 0 && cantVal > 20){
                    attr.addFlashAttribute("msgInt","Ingrese una cantidad entre 0 y 20");
                    return "redirect:/cliente/mostrarCarrito";
                }
            }catch (NumberFormatException e){
                attr.addFlashAttribute("msgInt","Ingrese un número");
                return "redirect:/cliente/mostrarCarrito";
            }
        }

        for (int i = 0; i < carritoExtra.size(); i++) {
            carritoExtra.get(i).getIdextra().setIdextra(extraGuardar.get(i));
            carritoExtra.get(i).setCantidad(Integer.parseInt(cantidad.get(i)));
        }
        session.setAttribute("carritoExtra",carritoExtra);
        attr.addFlashAttribute("msgExtra", "Se actualizaron los datos correctamente");
        return "redirect:/cliente/mostrarCarrito";
    }


    @PostMapping("/terminarCompra")
    public String terminarCompra(@RequestParam("cantidad") List<String> cantidad,
                                 @RequestParam("platoGuardar") List<Integer> platoGuardar,
                                 @RequestParam("observacion") List<String> observacion,
                                 RedirectAttributes attr, Model model,
                                 HttpSession session){
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        List<Ubicacion> listaDirecciones = (List) session.getAttribute("poolDirecciones");
        //List<Ubicacion> direcciones_distritos = clienteRepository.findUbicacionActual(usuario.getIdusuario());

        Integer idRest = (Integer) session.getAttribute("idRest");
        ArrayList<Plato_has_pedido> carrito = (ArrayList<Plato_has_pedido>) session.getAttribute("carrito");
        ArrayList<Extra_has_pedido> carritoExtra = (ArrayList<Extra_has_pedido>) session.getAttribute("carritoExtra");

        Distrito distritoRestaurante = restauranteRepository.findDistritoById(idRest);
        double subTotalCarrito = 0.00;
        double subTotalExtras = 0.00;
        double delivery = 0.00;
        Ubicacion distritoActual = null;
        int cantVal = 0;

        System.out.println(observacion);
        System.out.println(platoGuardar);
        System.out.println(cantidad);
        System.out.println(carrito);

        // LOS TAMAÑOS DE LOS ARREGLOS DEBEN SER IGUALES - INCLUSO SI NO INGRESA UNO ESTE SERÁ ""
        if (cantidad.size() != observacion.size() ||
                observacion.size() != platoGuardar.size() ||
                platoGuardar.size() != carrito.size()){
            return "redirect:/cliente/mostrarCarrito";
        }

        for (int i = 0; i < cantidad.size(); i++) {
            try{
                 cantVal = Integer.parseInt(cantidad.get(i));
                 if(cantVal <= 0 && cantVal > 20){
                     attr.addFlashAttribute("msgInt","Ingrese una cantidad entre 0 y 20");
                     return "redirect:/cliente/mostrarCarrito";
                 }
            }catch (NumberFormatException e){
                attr.addFlashAttribute("msgInt","Ingrese un número");
                return "redirect:/cliente/mostrarCarrito";
            }
        }

        for(int i = 0; i < observacion.size(); i++){
            if(observacion.get(i).length() <= 256){
                attr.addFlashAttribute("msgLen","Ingrese un comentario menor a 256 carácteres");
                return "redirect:/cliente/mostrarCarrito";
            }
        }

        for(int i = 0; i < carrito.size(); i++){
            carrito.get(i).setObservacionplatillo(observacion.get(i));
            carrito.get(i).getIdplatohaspedido().setIdplato(platoGuardar.get(i));
            carrito.get(i).setCantidad(Integer.parseInt(cantidad.get(i)));
            subTotalCarrito = subTotalCarrito + carrito.get(i).getCantidad() * doubleValue(carrito.get(i).getPreciounitario());
        }
        for(int i = 0; i < carritoExtra.size(); i++){
            subTotalExtras = subTotalExtras + carritoExtra.get(i).getCantidad() * doubleValue(carritoExtra.get(i).getPreciounitario());
        }

        for (Ubicacion u : listaDirecciones) {
            if(u.getDireccion().equalsIgnoreCase(usuario.getDireccionactual())){
                distritoActual = u;
                if(u.getDistrito().getIddistrito() == distritoRestaurante.getIddistrito()){
                    delivery = 5.00;
                    break;
                }
            }
        }
        if(delivery == 0.00){ delivery = 8.00; }

        //TODO SETIEAR DETALLES DE PEDIDO - MONTO POR CADA CARRITO
        System.out.println(carrito);
        session.setAttribute("carrito",carrito);

        model.addAttribute("montoCarrito",subTotalCarrito);
        model.addAttribute("montoExtras",subTotalExtras);
        model.addAttribute("delivery",delivery);

        model.addAttribute("listaTarjetas",tarjetaRepository.findByUsuario(usuario));
        model.addAttribute("listaDirecciones",listaDirecciones);
        model.addAttribute("distritoActual",distritoActual);

        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario.getIdusuario()));
        return "Cliente/terminarCompra";
    }

    @PostMapping("/generarPedido")
    public String generarPedido(@RequestParam(value = "cupon", required = false) String idCupon,
                                @RequestParam(value = "ubicacion", required = false) String idUbicacion,
                                @RequestParam(value = "delivery", required = false) String precioDelivery,
                                @RequestParam(value = "metodoPago", required = false) String idmp,
                                HttpSession session){

        //TODO: llenar cupon
        Cupon cupon = null;

        boolean idCuponVal = false;
        try{
            int idCuponInt = Integer.parseInt(idCupon);
            Optional<Cupon> cuponOpt = cuponRepository.findById(idCuponInt);
            if(cuponOpt.isPresent()){
                cupon = cuponOpt.get();
                idCuponVal = true;
            }
        }catch (NumberFormatException e){
        }

        Ubicacion ubicacion = null;

        boolean idUbicVal= false;
        try{
            int idUbicInt= Integer.parseInt(idUbicacion);
            Optional<Ubicacion> ubiOpt = ubicacionRepository.findById(idUbicInt);
            if(ubiOpt.isPresent()){
                ubicacion = ubiOpt.get();
                idUbicVal = true;
            }
        }catch (NumberFormatException e){
        }

        //TODO: llenar metodoPago
        MetodoDePago metodoDePago = null;

        boolean idMetPaVal= false;
        try{
            int idMePaInt= Integer.parseInt(idmp);
            Optional<MetodoDePago> metPagOpt = metodoPagoRepository.findById(idMePaInt);
            if(metPagOpt.isPresent()){
                metodoDePago = metPagOpt.get();
                idMetPaVal = true;
            }
        }catch (NumberFormatException e){
        }

        Double precioDel = null;
        boolean precioDelVal = false;
        try{
            precioDel = Double.parseDouble(precioDelivery);
            precioDelVal = true;
        }catch (NumberFormatException e){
        }

        if(!precioDelVal || !idCuponVal || !idUbicVal || !idMetPaVal){
            return "redirect:/cliente/listaPlatos";
        }else {

            List<Plato_has_pedido> listaPlatos = (List<Plato_has_pedido>) session.getAttribute("carrito");
            List<Extra_has_pedido> listaExtra = (List<Extra_has_pedido>) session.getAttribute("carritoextras");

            BigDecimal precioTotalPlatos = new BigDecimal(0);
            for (int i = 0; i < listaPlatos.size(); i++) {
                BigDecimal subtotal = listaPlatos.get(i).getPreciounitario().multiply(new BigDecimal(listaPlatos.get(i).getCantidad()));
                precioTotalPlatos = precioTotalPlatos.add(subtotal);
            }

            BigDecimal precioTotalExtras = new BigDecimal(0);
            for (int i = 0; i < listaExtra.size(); i++) {
                BigDecimal subTotal1 = listaExtra.get(i).getPreciounitario().multiply(new BigDecimal(listaExtra.get(i).getCantidad()));
                precioTotalExtras = precioTotalExtras.add(subTotal1);
            }

            BigDecimal desc = new BigDecimal(cupon.getDescuento());
            BigDecimal precioTotal = precioTotalPlatos.add(precioTotalExtras);
            precioTotal = precioTotal.subtract(desc);

            Pedido pedido = new Pedido();

            pedido.setPreciototal(precioTotal.floatValue());

            String codigoAleatorio = "";
            while (true) {
                codigoAleatorio = generarCodigAleatorio();
                Pedido pedido1 = pedidoRepository.findByCodigo(codigoAleatorio);
                if (pedido1 == null) {
                    break;
                }
            }

            pedido.setCodigo(codigoAleatorio);

            pedido.setEstado(0);

            //seteo fecha
            Date date = new Date();
            DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            pedido.setFechapedido(hourdateFormat.format(date));
            //seteo mismoDistrito

            if (precioDel.equals(Double.parseDouble("5"))) {
                pedido.setTiempoentrega(45);
                pedido.setMismodistrito(true);
            } else {
                pedido.setTiempoentrega(60);
                pedido.setMismodistrito(false);
            }
            pedido.setRestaurante(restauranteRepository.findById(listaPlatos.get(0).getPlato().getIdrestaurante()).get());

            pedido.setCupon(cupon);

            Usuario cliente = (Usuario) session.getAttribute("usuario");
            pedido.setCliente(cliente);
            pedido.setMetodopago(metodoDePago);
            pedido.setUbicacion(ubicacion);

            pedido = pedidoRepository.save(pedido);

            for (Plato_has_pedido plato_has_pedido : listaPlatos) {
                Plato_has_pedidoKey plato_has_pedidoKey = new Plato_has_pedidoKey();
                plato_has_pedidoKey.setCodigo(pedido.getCodigo());
                plato_has_pedidoKey.setIdplato(plato_has_pedido.getPlato().getIdplato());
                plato_has_pedido.setIdplatohaspedido(plato_has_pedidoKey);
                platoHasPedidoRepository.save(plato_has_pedido);
            }

            for (Extra_has_pedido extra_has_pedido : listaExtra) {
                Extra_has_pedidoKey extra_has_pedidoKey = new Extra_has_pedidoKey();
                extra_has_pedidoKey.setCodigo(pedido.getCodigo());
                extra_has_pedidoKey.setIdextra(extra_has_pedido.getExtra().getIdextra());
                extra_has_pedido.setIdextra(extra_has_pedidoKey);
                extraHasPedidoRepository.save(extra_has_pedido);
            }

            return "redirect:/cliente/pedidoActual";
        }

    }

    @GetMapping("/listaReportes")
    public String listaReportes(Model model, HttpSession session) {

        Usuario usuario1 = (Usuario) session.getAttribute("usuario");
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/listaReportes";
    }

    //PEDIDO ACTUAL
    @GetMapping("/pedidoActual")
    public String pedidoActual(@RequestParam Map<String, Object> params, Model model, HttpSession httpSession,
                               @RequestParam(value = "texto", required = false) String texto,
                               @RequestParam(value = "estado", required = false) String estado) {
        if (httpSession.getAttribute("carrito") != null) {
            httpSession.removeAttribute("carrito");
        }

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        int page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        Pageable pageRequest = PageRequest.of(page, 5);


        if (texto == null) {
            texto = "";
        }
        if (estado == null) {
            estado = "7";
        }
        int limitSup = 6;
        int limitInf = 0;
        switch (estado) {
            case "0":
                limitSup = 0;
                limitInf = 0;
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
                limitInf = 0;
        }

        Page<PedidoDTO> listaPedidos = pedidoActualService.findPaginated(usuario1.getIdusuario(), texto, limitInf, limitSup, pageRequest);
        int totalPage = listaPedidos.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }

        model.addAttribute("listaPedidos", listaPedidos.getContent());
        //mandar valores
        model.addAttribute("texto", texto);
        model.addAttribute("estado", estado);

        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/listaPedidoActual";
    }

    @GetMapping("/cancelarPedido")
    public String cancelarPedido(@RequestParam("id") String id,
                                 Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        int idr = usuario.getIdusuario();
        Optional<Pedido> pedido1 = pedidoRepository.findById(id);
        if (pedido1.isPresent()) {
            Pedido pedido = pedido1.get();
            if (pedido.getEstado() == 0) {
                pedido.setEstado(2);
                pedidoRepository.save(pedido);
            }
            model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario.getIdusuario()));
        }
        return "redirect:/cliente/historialPedidos";
    }

    @GetMapping("/detallePedidoActual")
    public String detallePedidoActual(@RequestParam Map<String, Object> params, @RequestParam("codigo") String codigo, Model model, HttpSession session) {

        List<Pedido1DTO> pedido1DTOS = pedidoRepository.detalle1(codigo);
        if (pedido1DTOS.isEmpty()) {
            return "redirect:/cliente/historialPedidos";
        }

        model.addAttribute("listapedido1", pedidoRepository.detalle1(codigo));

        Usuario usuario1 = (Usuario) session.getAttribute("usuario");

        int page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        Pageable pageRequest = PageRequest.of(page, 5);

        if (codigo == null) {
            codigo = "";
        }
        Page<Plato_has_PedidoDTO> listaPedidos = detalle2Service.findPaginated2(codigo, pageRequest);
        int totalPage = listaPedidos.getTotalPages();
        if (totalPage > 0) {
            List<Integer> pages = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages", pages);
        }
        model.addAttribute("listapedido2", listaPedidos);
        model.addAttribute("codigo", codigo);


        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/detallePedidoActual";
    }

    //HISTORIAL PEDIDOS
    @GetMapping("/historialPedidos")
    public String historialPedidos(@RequestParam Map<String, Object> params, Model model, HttpSession httpSession,
                                   @RequestParam(value = "texto", required = false) String texto,
                                   @RequestParam(value = "estado", required = false) String estado) {

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");
        int page = params.get("page") != null ? Integer.valueOf(params.get("page").toString()) - 1 : 0;
        Pageable pageRequest = PageRequest.of(page, 5);

        if (texto == null) {
            texto = "";
        }
        if (estado == null) {
            estado = "7";
        }
        int limitSup = 6;
        int limitInf = 0;
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


        model.addAttribute("listaPedidos", listaPedidos);
        model.addAttribute("texto", texto);
        model.addAttribute("estado", estado);

        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));

        return "Cliente/listaHistorialPedidos";
    }

    @PostMapping("/valorarRest")
    public String valorarRest(Model model, HttpSession httpSession, @RequestParam("id") String id,
                              @RequestParam(value = "val") Integer valoraRest, @RequestParam("comentRest") String comentRest) {
        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");
        Pedido pedido = pedidoRepository.encontrarporId(id);
        if (pedido != null) {
            Pedido pedido1 = pedido;
            pedido1.setValoracionrestaurante(valoraRest);
            pedido1.setComentariorestaurante(comentRest);
            pedidoRepository.save(pedido1);
        }
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "redirect:/cliente/historialPedidos";
    }

    @PostMapping("/valorarRep")
    public String valorarRep(Model model, HttpSession httpSession, @RequestParam("id") String id,
                             @RequestParam(value = "val") Integer valoraRest, @RequestParam("comentRep") String comentRest) {
        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");
        System.out.println(valoraRest);
        System.out.println(comentRest);
        System.out.println(id);
        Pedido pedido = pedidoRepository.encontrarporId(id);
        if (pedido != null) {
            Pedido pedido1 = pedido;
            pedido1.setValoracionrepartidor(valoraRest);
            pedido1.setComentariorepartidor(comentRest);
            pedidoRepository.save(pedido1);
        }
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "redirect:/cliente/historialPedidos";
    }





    @GetMapping("/reporteDinero")
    public String reporteDinero(
            @RequestParam Map<String, Object> params,Model model, HttpSession httpSession,
            @RequestParam(value = "texto",required = false) String texto,
            @RequestParam(value = "nombrec",required = false) String nombrec,
            @RequestParam(value = "mes",required = false) String mes){

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        int page  = params.get("page") != null ? Integer.valueOf(params.get("page").toString())-1 : 0;
        Pageable pageRequest = PageRequest.of(page, 5);
        int limitSup;
        int limitInf;

        Calendar c1 = GregorianCalendar.getInstance();
        int m=c1.get(Calendar.MONTH) +1;
        if(texto==null&& mes==null && nombrec==null){
            mes=Integer.toString(m);
            limitSup=m;
            limitInf=m-1;
            texto = "";

            nombrec="";
        }else {

            if (texto == null) {
                texto = "";
            }

            if(nombrec==null){
                nombrec="";
            }

            try {
                limitSup = Integer.parseInt(mes);
                limitInf = limitSup - 1;

            } catch (NumberFormatException e) {
                limitSup = 0;
                limitInf = 0;
            }

            if (mes == null) {
                mes = "13";
            }
        }

        Page<ReporteDineroDTO> listapedidos= reporteDineroService.findpage(usuario1.getIdusuario(),limitInf,limitSup,texto,nombrec,pageRequest);
        int totalPage = listapedidos.getTotalPages();
        if(totalPage > 0){
            List<Integer> pages = IntStream.rangeClosed(1,totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages",pages);
        }
        BigDecimal totalsuma1= new BigDecimal(0);
        for(ReporteDineroDTO rep:listapedidos){
            System.out.println(rep.getDescuento());
            totalsuma1=totalsuma1.add(rep.getDescuento());
        }
        //Division
        /*
        int denom=listapedidos.getSize();
        //conversion
        BigDecimal denomBD= new BigDecimal(denom);


        // divide bg1 with bg2 with 3 scale
        totalsuma1 = totalsuma1.divide(denomBD, 2, RoundingMode.CEILING);

         */


        model.addAttribute("total",totalPage);
        System.out.println(totalsuma1);
        model.addAttribute("listapedidos",listapedidos);
        model.addAttribute("totalsuma",totalsuma1);
        model.addAttribute("texto",texto);
        model.addAttribute("mes",mes);
        model.addAttribute("nombrec",nombrec);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/reporteDineroCliente";
    }


    @GetMapping("/reportePedido")
    public String reportePedido(@RequestParam Map<String, Object> params ,Model model, HttpSession httpSession,
                                @RequestParam(value = "texto",required = false) String texto,
                                @RequestParam(value = "numpedidos",required = false) String numpedidos,
                                @RequestParam(value = "mes",required = false) String mes
                                ){


        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");
        Calendar c1 = GregorianCalendar.getInstance();
        int m=c1.get(Calendar.MONTH) +1;
        int page  = params.get("page") != null ? Integer.valueOf(params.get("page").toString())-1 : 0;
        Pageable pageRequest = PageRequest.of(page, 5);
        int limitSup;
        int limitInf;

        if(texto==null&& numpedidos==null&& mes==null){
            mes=Integer.toString(m);
            limitSup=6;
            limitInf=5;
            texto = "";
            numpedidos = "";
        }else {

            if (texto == null) {
                texto = "";
            }
            if (numpedidos == null) {
                numpedidos = "";
            }


            try {
                limitSup = Integer.parseInt(mes);
                limitInf = limitSup - 1;

            } catch (NumberFormatException e) {
                limitSup = 0;
                limitInf = 0;
            }

            if (mes == null) {
                mes = "13";
            }
        }

        Page<ReportePedido> listapedidos= reportePedidoCService.findPaginated3(usuario1.getIdusuario(),limitInf,limitSup,texto,numpedidos,pageRequest);

        List<ReporteTop3> listarestTop=pedidoRepository.reporteTop3Rest(usuario1.getIdusuario(),limitSup);
        List<ReporteTop3P> listaPl=pedidoRepository.reporteTop3Pl(usuario1.getIdusuario(),limitSup);
        int totalPage = listapedidos.getTotalPages();

        if(totalPage > 0){
            List<Integer> pages = IntStream.rangeClosed(1,totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages",pages);

        }

        BigDecimal totalsuma= new BigDecimal(0);
        for(ReportePedido rep:listapedidos){
            System.out.println(rep.getTotal());
            totalsuma=totalsuma.add(rep.getTotal());
        }

        System.out.println(totalsuma);
        model.addAttribute("totalsuma",totalsuma);
        model.addAttribute("listapedidos",listapedidos);
        model.addAttribute("listarestTop", listarestTop);
       model.addAttribute("listarestPl", listaPl);
       model.addAttribute("texto",texto);
       model.addAttribute("mes",mes);
       model.addAttribute("total",totalPage);
       model.addAttribute("numpedidos",numpedidos);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/reportePedidoCliente";
    }

    @GetMapping("/reporteTiempo")
    public String reporteTiempo(@RequestParam Map<String, Object> params,Model model, HttpSession httpSession,
                                @RequestParam(value = "texto",required = false) String texto,
                                @RequestParam(value = "numpedidos",required = false) String numpedidos,
                                @RequestParam(value = "mes",required = false) String mes){
        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");
        int page  = params.get("page") != null ? Integer.valueOf(params.get("page").toString())-1 : 0;
        Pageable pageRequest = PageRequest.of(page, 5);
        Calendar c1 = GregorianCalendar.getInstance();
        int m=c1.get(Calendar.MONTH) +1;



        int limitSup;
        int limitInf;

        if(texto==null&& numpedidos==null&& mes==null){
            mes=Integer.toString(m);
            limitSup=m;
            limitInf=m-1;
            texto = "";
            numpedidos = "";
        }else {

            if (texto == null) {
                texto = "";
            }
            if (numpedidos == null) {
                numpedidos = "";
            }


            try {
                limitSup = Integer.parseInt(mes);
                limitInf = limitSup - 1;

            } catch (NumberFormatException e) {
                limitSup = 0;
                limitInf = 0;
            }

            if (mes == null) {
                mes = "13";
            }
        }

        Page<ReportePedidoCDTO> listapedidos= reporteTiempoService.findPaginated3(usuario1.getIdusuario(),limitInf,limitSup,texto,numpedidos,pageRequest);
        int totalPage = listapedidos.getTotalPages();
        if(totalPage > 0){
            List<Integer> pages = IntStream.rangeClosed(1,totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages",pages);
        }
        int totalsuma1= 0;
        for(ReportePedidoCDTO rep:listapedidos){
           // System.out.println(rep.getTiempoEntrega());
            totalsuma1=totalsuma1+ rep.getTiempoentrega();
        }
        totalsuma1=totalsuma1/listapedidos.getSize();

        System.out.println(totalsuma1);
        model.addAttribute("listapedidos",listapedidos);
        model.addAttribute("totalsuma1",totalsuma1);
        model.addAttribute("texto",texto);
        model.addAttribute("mes",mes);
        model.addAttribute("numpedidos",numpedidos);
        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario1.getIdusuario()));
        return "Cliente/reporteTiempoCliente";
    }

    @GetMapping("/listaCupones")
    public String listaCupones(@RequestParam Map<String, Object> params ,@RequestParam(value = "texto",required = false) String texto,
                               @RequestParam(value = "descuento",required = false) String descuento,Model model, HttpSession httpSession) {

        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        int page  = params.get("page") != null ? Integer.valueOf(params.get("page").toString())-1 : 0;
        Pageable pageRequest = PageRequest.of(page, 5);
        int limitSup;
        int limitInf;


        if(texto==null ){
            texto= "";
        }
        if(descuento==null){
            descuento="7";
        }

        switch (descuento){
            case "1":
                limitSup=10;
                limitInf=0;
                break;

            case "2":
                limitSup=20;
                limitInf=10;
                break;

            case "3":
                limitSup=30;
                limitInf=20;
                break;

            case "4":
                limitSup=40;
                limitInf=30;
                break;

            default:
                limitSup=100;
                limitInf=0;
        }


        Page<CuponClienteDTO> cuponClienteDTOS = cuponClienteService.findPaginated2(usuario.getIdusuario(),texto, limitInf,limitSup,pageRequest);
        int totalPage = cuponClienteDTOS.getTotalPages();
        if(totalPage > 0){
            List<Integer> pages = IntStream.rangeClosed(1,totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages",pages);
        }
        model.addAttribute("total", totalPage);


        httpSession.setAttribute("listaCupones",cuponClienteDTOS);

        model.addAttribute("notificaciones", clienteRepository.notificacionCliente(usuario.getIdusuario()));
        model.addAttribute("listaCuponesenviar", cuponClienteDTOS);
        model.addAttribute("texto",texto);
        model.addAttribute("descuento",descuento);

        return "Cliente/listaCupones";
    }

    public String generarCodigAleatorio() {
        char[] chars = "1234567890".toCharArray();
        int charsLength = chars.length;
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        int tamCodigo = 10;
        for (int i = 0; i < tamCodigo; i++) {
            buffer.append(chars[random.nextInt(charsLength)]);
        }
        return buffer.toString();
    }

}
