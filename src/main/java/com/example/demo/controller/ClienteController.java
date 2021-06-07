package com.example.demo.controller;


import com.example.demo.dtos.*;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.service.ExtrasClienteService;
import com.example.demo.service.PedidoActualService;
import com.example.demo.service.PlatoClienteService;
import com.example.demo.service.RestauranteClienteService;
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
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.aspectj.runtime.internal.Conversions.doubleValue;

@Controller

@RequestMapping("/cliente")
public class ClienteController {

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

    @GetMapping("/editarPerfil")
    public String editarPerfil(HttpSession httpSession, Model model) {

        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        model.addAttribute("usuario", usuario);

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
            return "Cliente/editarPerfil";


        } else {
            usuario1.setTelefono(telefonoNuevo); //usar save para actualizar
            httpSession.setAttribute("usuario", usuario1); //TODO: preguntar profe
            clienteRepository.save(usuario1);
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

        return "Cliente/listaRestaurantes";
    }
    @GetMapping("/listaDirecciones")
    public String listaDirecciones(Model model, HttpSession httpSession) {
        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");

        List<Ubicacion> listaDirecciones = ubicacionRepository.findByUsuario(usuario);
        model.addAttribute("listaDirecciones", listaDirecciones);

        ArrayList<Ubicacion> listaUbicacionesSinActual = new ArrayList<>();

        for (Ubicacion ubicacion : listaDirecciones) {
            if (!ubicacion.getDireccion().equals(usuario.getDireccionactual())) {
                listaUbicacionesSinActual.add(ubicacion);
            }
        }
        model.addAttribute("listaDistritos", distritosRepository.findAll());
        model.addAttribute("direccionesSinActual", listaUbicacionesSinActual);

        return "Cliente/listaDirecciones";
    }


    @PostMapping("/guardarDireccion")
    public String guardarDirecciones(HttpSession httpSession, @RequestParam("direccionactual") String direccionActual, Model model){
        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        usuario.setDireccionactual(direccionActual);
        httpSession.setAttribute("usuario",usuario);
        model.addAttribute("listaDistritos", distritosRepository.findAll());
        clienteRepository.save(usuario);

        return "redirect:/cliente/listaDirecciones";
    }

    @PostMapping("/eliminarDireccion")
    public String eliminarDirecciones(@RequestParam("listaIdDireccionesAeliminar") List<String> listaIdDireccionesAeliminar, HttpSession session, Model model){

        for (String idUbicacion : listaIdDireccionesAeliminar) {
            //validad int idUbicacion:
            int idUb = Integer.parseInt(idUbicacion);
            Ubicacion ubicacion = (ubicacionRepository.findById(idUb)).get();
            Usuario usuarioS = (Usuario) session.getAttribute("usuario");
            model.addAttribute("listaDistritos", distritosRepository.findAll());
            if(!ubicacion.getDireccion().equalsIgnoreCase(usuarioS.getDireccionactual())){
                ubicacionRepository.delete(ubicacion);
            }

        }
        return "redirect:/cliente/listaDirecciones";
    }

    @PostMapping("/agregarDireccion")
    public  String registrarNewDireccion(@RequestParam("direccion") String direccion, @RequestParam("distrito") Integer distrito,HttpSession httpSession,Model model ){
        boolean valNul=false;
        boolean valNew=false;
        boolean valLong= false;


        if (direccion.isEmpty()) {
            valNul = true;
        }

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");
        List<Ubicacion> listaDir = ubicacionRepository.findByUsuario(usuario1);
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

        if(valNul|| valNew || valLong || dist_u_val){
            if(valNul){
                model.addAttribute("msg", "No ingresó dirección");
            }
            if(valNew){
                model.addAttribute("msg1", "La dirección ingresda ya está registrada");
            }

            if(valLong){
                model.addAttribute("msg2", "Solo puede registrar 6 direcciones");
            }
            if(dist_u_val){

                    model.addAttribute("msg3", "Seleccione una de las opciones");
            }

           Usuario usuario = (Usuario) httpSession.getAttribute("usuario");

            List<Ubicacion> listaDirecciones = ubicacionRepository.findByUsuario(usuario);
            model.addAttribute("listaDirecciones", listaDirecciones);

            ArrayList<Ubicacion> listaUbicacionesSinActual = new ArrayList<>();

            for(Ubicacion ubicacion: listaDirecciones){
                if(!ubicacion.getDireccion().equals(usuario.getDireccionactual())){
                    listaUbicacionesSinActual.add(ubicacion);
                }
            }

            model.addAttribute("direccionesSinActual", listaUbicacionesSinActual);
            model.addAttribute("listaDistritos", distritosRepository.findAll());
            return "Cliente/listaDirecciones";

        }else{
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
            httpSession.setAttribute("listaDirecciones",listaDirecciones);
            model.addAttribute("listaDistritos", distritosRepository.findAll());
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

        model.addAttribute("listaPlato",listaPlato.getContent());
        model.addAttribute("texto",texto);
        model.addAttribute("idPrecio",idPrecio);
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
            return "Cliente/detallePlato";
        }else{
            //model.addAttribute("idRest",idRest);
            //model.addAttribute("idPlato",idPlato);
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

        //model.addAttribute("carrito",carritoL);
        //model.addAttribute("idRest",idRest);
        model.addAttribute("idPlato",idPlato);
        model.addAttribute("idPage",idPage);
        return "Cliente/carritoCompras";
    }

    @PostMapping("/aniadirCarrito")
    public String aniadirCarrito(@RequestParam("idPlato") int idPlato,
                                 @RequestParam(value = "idPage", required = false) String idPage,
                                 @RequestParam("cantidadPlato") String cantidadPlato,
                                 HttpSession session,
                                 RedirectAttributes attr) {
        //carrito
        String url = "";
        String params = "";
        ArrayList<Plato_has_pedido> carrito = null;
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

        if(session.getAttribute("extrasCarrito") == null){
            return "redirect:/cliente/motrarCarrito";
        }
       // model.addAttribute("idRest",idRest);
        return "Cliente/carritoExtras";
    }

    @PostMapping("/aniadirExtras")
    public String aniadirExtras(@RequestParam("idExtra") Integer idExtra,
                                @RequestParam(value = "idPlato",required = false) Integer idPlato,
                                @RequestParam(value = "cantidadExtra") String cantidadExtra,
                                HttpSession session,
                                RedirectAttributes attr){
        //extras de carrito
        ArrayList<Extra_has_pedido> extrasCarrito = null;
        String urlDetalle = "";
        String params = "";

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
            attr.addFlashAttribute("msgNotFound", "No se encontro el extra");
        }

        urlDetalle = "detallePlato";
        params = "?idRest="+idRest+"&idPlato="+idPlato;
        return "redirect:/cliente/"+urlDetalle+params;
    }

    @PostMapping("/terminarCompra")
    public String terminarCompra(@RequestParam("cantidad") List<String> cantidad,
                                 @RequestParam("platoGuardar") List<Integer> platoGuardar,
                                 @RequestParam("observacion") List<String> observacion,
                                 RedirectAttributes attr, Model model,
                                 HttpSession session){
        Pedido pedido = new Pedido();
        System.out.println(observacion);
        System.out.println(platoGuardar);
        System.out.println(cantidad);
        ArrayList<Plato_has_pedido> carrito = (ArrayList<Plato_has_pedido>) session.getAttribute("carrito");
        System.out.println(carrito);

        // LOS TAMAÑOS DE LOS ARREGLOS DEBEN SER IGUALES - INCLUSO SI NO INGRESA UNO ESTE SERÁ ""
        if (cantidad.size() != observacion.size() ||
                observacion.size() != platoGuardar.size() ||
                platoGuardar.size() != carrito.size()){
            return "redirect:/cliente/mostrarCarrito";
        }
        int cantVal = 0;
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
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        double suma = 0.00;
        double delivery = 0.00;
        for(int i = 0; i < carrito.size(); i++){
            carrito.get(i).setObservacionplatillo(observacion.get(i));
            carrito.get(i).getIdplatohaspedido().setIdplato(platoGuardar.get(i));
            carrito.get(i).setCantidad(Integer.parseInt(cantidad.get(i)));
            suma = suma + carrito.get(i).getCantidad() * doubleValue(carrito.get(i).getPreciounitario());
        }

       // model.addAttribute("")

        //pedido.setPreciototal(suma);




        //TODO SETIEAR DETALLES DE PEDIDO - MONTO POR CADA CARRITO
        System.out.println(carrito);
        session.setAttribute("carrito",carrito);
        return "Cliente/terminarCompra";
    }


    @GetMapping("/listaCupones")
    public String listacupones() {

        return "Cliente/listaCupones";
    }

    @GetMapping("/listaReportes")
    public String listaReportes() {

        return "Cliente/listaReportes";
    }

    //PEDIDO ACTUAL
    @GetMapping("/pedidoActual")
    public String pedidoActual(@RequestParam Map<String, Object> params, Model model, HttpSession httpSession,
                               @RequestParam(value = "texto",required = false) String texto,
                               @RequestParam(value = "estado",required = false) String estado) {
        if(httpSession.getAttribute("carrito") != null){
            httpSession.removeAttribute("carrito");
        }

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        int page  = params.get("page") != null ? Integer.valueOf(params.get("page").toString())-1 : 0;
        Pageable pageRequest = PageRequest.of(page, 5);


        if(texto==null ){
            texto= "";
        }
        if(estado==null){
            estado="7";
        }
        int limitSup=6;
        int limitInf=0;
        switch (estado){
            case "1":
                 limitSup=1;
                 limitInf=0;
                break;

            case "3":
                limitSup=3;
                limitInf=2;
                break;

            case "4":
                limitSup=4;
                limitInf=3;
                break;
            case "5":
                limitSup=5;
                limitInf=4;
                break;

            default:
                 limitSup=6;
                limitInf=0;
        }

        Page<PedidoDTO> listaPedidos = pedidoActualService.findPaginated(usuario1.getIdusuario(), texto,limitInf,limitSup, pageRequest);
        int totalPage = listaPedidos.getTotalPages();
        if(totalPage > 0){
            List<Integer> pages = IntStream.rangeClosed(1,totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages",pages);
        }

        model.addAttribute("listaPedidos",listaPedidos.getContent());
        //mandar valores
        model.addAttribute("texto",texto);
        model.addAttribute("estado", estado);

        return "Cliente/listaPedidoActual";
    }


    @GetMapping("/detallePedidoActual")
    public String detallePedidoActual(@RequestParam("codigo") String codigo, Model model){

        model.addAttribute("listapedido1", pedidoRepository.detalle1(codigo));
        model.addAttribute("listapedido2", pedidoRepository.detalle2(codigo));


        return "Cliente/detallePedidoActual";
    }
    //HISTORIAL PEDIDOS
    @GetMapping("/historialPedidos")
    public String historialPedidos(Model model, HttpSession httpSession) {

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        String texto= "";

        List<PedidoValoracionDTO> listaPedidos=pedidoRepository.pedidosTotales2(usuario1.getIdusuario(), texto,0,6);
        List<PedidoValoracionDTO> listaPedidoA= new ArrayList<PedidoValoracionDTO>();
        for(PedidoValoracionDTO ped: listaPedidos){
            if(ped.getEstado()==6 || ped.getEstado()==2  ){
                listaPedidoA.add(ped);
            }
        }

        model.addAttribute("listaPedidos",listaPedidoA);



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
        String texto= "";
        List<PedidoValoracionDTO> listaPedidos=pedidoRepository.pedidosTotales2(usuario1.getIdusuario(), texto,0,6);
        List<PedidoValoracionDTO> listaPedidoA= new ArrayList<PedidoValoracionDTO>();
        for(PedidoValoracionDTO ped: listaPedidos){
            if(ped.getEstado()==6 || ped.getEstado()==2  ){
                listaPedidoA.add(ped);
            }
        }

        model.addAttribute("listaPedidos", listaPedidoA);
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
        String texto = "";
        List<PedidoValoracionDTO> listaPedidos = pedidoRepository.pedidosTotales2(usuario1.getIdusuario(), texto, 0, 6);
        List<PedidoValoracionDTO> listaPedidoA = new ArrayList<PedidoValoracionDTO>();
        for (PedidoValoracionDTO ped : listaPedidos) {
            if (ped.getEstado() == 6 || ped.getEstado() == 2) {
                listaPedidoA.add(ped);
            }
        }

        model.addAttribute("listaPedidos", listaPedidoA);
        return "redirect:/cliente/historialPedidos";
    }





    @GetMapping("/reporteDinero")
    public String reporteDinero(Model model, HttpSession httpSession){
        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        List<ReportePedido> listapedidos= pedidoRepository.reportexmes(usuario1.getIdusuario(),0,12,0,12);
        BigDecimal totalsuma1= new BigDecimal(0);
        for(ReportePedido rep:listapedidos){
            System.out.println(rep.getDescuento());
            totalsuma1.add(rep.getDescuento());
        }
        System.out.println(totalsuma1);
        model.addAttribute("listapedidos",listapedidos);
        model.addAttribute("totalsuma",totalsuma1);

        return "Cliente/reporteDineroCliente";
    }


    @GetMapping("/reportePedido")
    public String reportePedido(Model model, HttpSession httpSession){

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        List<ReportePedido> listapedidos= pedidoRepository.reportexmes(usuario1.getIdusuario(),0,12,0,12);
        List<ReporteTop3> listarestTop=pedidoRepository.reporteTop3Rest(usuario1.getIdusuario(),6);
        List<ReporteTop3P> listaPl=pedidoRepository.reporteTop3Pl(usuario1.getIdusuario(),6);
        BigDecimal totalsuma= new BigDecimal(0);
        for(ReportePedido rep:listapedidos){
            totalsuma.add(rep.getTotal());
        }
        model.addAttribute("totalsuma",totalsuma);
        model.addAttribute("listapedidos",listapedidos);
        model.addAttribute("listarestTop", listarestTop);


       model.addAttribute("listarestPl", listaPl);
        return "Cliente/reportePedidoCliente";
    }

    @GetMapping("/reporteTiempo")
    public String reporteTiempo(Model model, HttpSession httpSession){
        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        List<ReportePedido> listapedidos= pedidoRepository.reportexmes(usuario1.getIdusuario(),0,12,0,12);
        int totalsuma1= 0;
        for(ReportePedido rep:listapedidos){
           // System.out.println(rep.getTiempoEntrega());
            totalsuma1=totalsuma1+ rep.getTiempoEntrega();
        }
        System.out.println(totalsuma1);
        model.addAttribute("listapedidos",listapedidos);
        model.addAttribute("totalsuma",totalsuma1);
        return "Cliente/reporteTiempoCliente";
    }

  /*  @GetMapping("/listaCupones")
    public String listaCupones(Model model, HttpSession httpSession) {

        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        String direccionactual = usuario.getDireccionactual();
        int iddistritoactual = 1;
        //buscar que direccion de milista de direcciones coincide con mi direccion actual

        List<ClienteDTO> listadirecc = clienteRepository.listaParaCompararDirecciones(usuario.getIdusuario());

        for (ClienteDTO cl : listadirecc) {
            if (cl.getDireccion().equalsIgnoreCase(direccionactual)) {
                iddistritoactual = cl.getIddistrito();
                break;
            }
        }

       // model.addAttribute("listaCupones", restauranteRepository.listaRestaurante(iddistritoactual));
        return "Cliente/listaCupones";
    }*/

}
