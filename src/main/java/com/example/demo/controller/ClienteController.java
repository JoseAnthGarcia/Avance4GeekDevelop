package com.example.demo.controller;


import com.example.demo.dtos.*;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.service.PedidoActualService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

@Controller

@RequestMapping("/cliente")
public class ClienteController {

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
    public String listaRestaurantes(Model model, HttpSession httpSession) {
        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        String direccionactual = usuario.getDireccionactual();
        int iddistritoactual = 1;
        Integer limitInfP = 0;
        Integer limitSupP = 5000;
        Integer limitInfVal = 0;
        Integer limitSupVal = 6;

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
                limitSupVal = 1;
                break;
            case "2":
                limitInfVal = 2;
                limitSupVal = 2;
                break;
            case "3":
                limitInfVal = 3;
                limitSupVal = 3;
                break;
            case "4":
                limitInfVal = 4;
                limitSupVal = 4;
                break;
            case "5":
                limitInfVal = 5;
                limitSupVal = 5;
                break;
            default:
                limitInfVal = 0;
                limitSupVal = 6;
        }

        List<RestauranteDTO> listaRestaurante = restauranteRepository.listaRestaurante(texto, limitInfP, limitSupP, limitInfVal, limitSupVal,iddistritoactual);
        model.addAttribute("listaRestaurante", listaRestaurante);
        model.addAttribute("idPrecio", idPrecio);
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
    public String listaplatos(@RequestParam("idRest") int idRest,
                              @RequestParam(value = "texto",required = false) String texto,
                              @RequestParam(value = "idPrecio",required = false) String idPrecio,
                              Model model) {
        Integer limitInf = 0;
        Integer limitSup = 5000;
        Optional<Restaurante> restauranteOpt = restauranteRepository.findById(idRest);

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

        List<PlatosDTO> listaPlato = platoRepository.listaPlato(idRest, texto, limitInf, limitSup);
        model.addAttribute("listaPlato",listaPlato);
        model.addAttribute("idRest",idRest);
        model.addAttribute("texto",texto);
        model.addAttribute("idPrecio",idPrecio);
         return "/Cliente/listaProductos";
    }

    @GetMapping("/detallePlato")
    public String detallePedido(@RequestParam("idRest") int idRest,
                                @RequestParam("idPlato") int idPlato,
                                Model model) {
        Optional<Restaurante> restauranteOpt = restauranteRepository.findById(idRest);
        Optional<Plato> platoOpt = platoRepository.findById(idPlato);

        if(platoOpt.isPresent() || restauranteOpt.isPresent()){
            Plato plato = platoOpt.get();
            Restaurante restaurante = restauranteOpt.get();
            List<ExtraDTO> listaExtras = extraRepository.listaExtrasDisponibles(idRest);

            model.addAttribute("plato",plato);
            model.addAttribute("listaExtras",listaExtras);
            model.addAttribute("idRest",idRest);
            model.addAttribute("nombreRest",restaurante.getNombre());
            return "Cliente/detallePlato";
        }else{
            //model.addAttribute("idRest",idRest);
            //model.addAttribute("idPlato",idPlato);
            return "redirect: cliente/listaPlatos?idRest="+idRest+"&idPlato="+idPlato;
        }
    }

    @GetMapping("/mostrarCarrito")
    public String mostrarCarrito(@RequestParam("idRest") Integer idRest,
                                 Model model){
        //ArrayList<Plato_has_pedido> carrito = (ArrayList<Plato_has_pedido>) session.getAttribute("carrito");
        //List<Plato_has_pedido> carritoL = (List<Plato_has_pedido>) session.getAttribute("carrito");

        //model.addAttribute("carrito",carritoL);
        model.addAttribute("idRest",idRest);
        return "Cliente/carritoCompras";
    }

    @PostMapping("/aniadirCarrito")
    public String aniadirCarrito(@RequestParam("idPlato") Integer idPlato,
                                 @RequestParam("idRest") Integer idRest,
                                 @RequestParam("cantidadPlato") int cantidadPlato,
                                 HttpSession session,
                                 RedirectAttributes attr){
        //carrito
        ArrayList<Plato_has_pedido> carrito = new ArrayList<>();
        Plato_has_pedido php = new Plato_has_pedido();
        Optional<Plato> platoOptional = platoRepository.findById(idPlato);

        if(platoOptional.isPresent()){
            //GUARDANDO TODOS LOS ATRIBUTOS NECESARIOS A CARRITO
            Plato plato = platoOptional.get();
            Plato_has_pedidoKey idComPlato = new Plato_has_pedidoKey();
            //SE GUARDARÁ TEMPORALMENTE EN SESIÓN CON UN CÓDIGO TEMPORAL QUE SE ACTUALIZARÁ
            String codigo = "CODIGOTEMPORAL";

            //TODO HAY QUE VALIDAR DE QUE VISTA SE ESTÁ AÑADIENDO AL CARRITO - XQ DE ESO DEPENDE EL COMENTARIO
            idComPlato.setIdplato(idPlato);
            idComPlato.setCodigo(codigo);

            php.setPlato(plato);
            //TODO VALIDAR QUE CUANDO SE AGREGA UN PEDIDO DEL MISMO ID PLATO - ESTA CANTIDAD SEA LA SUMA
            php.setCantidad(cantidadPlato);
            //TODO SI FUERA EL SUBTOTAL EN EL CARRITO SE GUARDARÍA PRECIO UNITARIO X CANTIDAD PLATO
            php.setPreciounitario(BigDecimal.valueOf(plato.getPrecio()));
            php.setIdplatohaspedido(idComPlato);

            carrito.add(php);
            session.setAttribute("carrito",carrito);
            attr.addFlashAttribute("msgAdd", "Se agregó un plato al carrito");
        }else{
            attr.addFlashAttribute("msgNotFound", "No se encontro el plato");
        }
        //TODO por ahora solo funcionará si el flujo es LISTA DE PLATOS - DETALLE - VER CARRITO
        String urlDetalle = "detallePlato";
        String params = "?idRest="+idRest+"&idPlato="+idPlato;
        return "redirect:/cliente/"+urlDetalle+params;
    }

    @PostMapping("/restarCarrito")
    public String restarCarrito(){

        return "/";
    }

    @GetMapping("/mostrarExtrasCarrito")
    public String mostrarExtrasCarrito(@RequestParam("idRest") Integer idRest,
                                       Model model){
        model.addAttribute("idRest",idRest);
        return "Cliente/carritoExtras";
    }

    @PostMapping("/aniadirExtras")
    public String aniadirExtras(@RequestParam(value = "IdExtra") Integer idExtra,
                                @RequestParam("idRest") Integer idRest,
                                @RequestParam("idPlato") Integer idPlato,
                                @RequestParam(value = "cantidadExtra") int cantidadExtra,
                                HttpSession session,
                                RedirectAttributes attr){
        //extras de carrito
        ArrayList<Extra_has_pedido> extrasCarrito = new ArrayList<>();
        Extra_has_pedido ehp = new Extra_has_pedido();
        Optional<Extra> extraOptional = extraRepository.findById(idExtra);

        if(extraOptional.isPresent()){
            Extra extra = extraOptional.get();
            Extra_has_pedidoKey idComExtra = new Extra_has_pedidoKey();
            String codigo = "CODIGOTEMPORAL";

            idComExtra.setIdextra(idExtra);
            idComExtra.setCodigo(codigo);

            ehp.setCantidad(cantidadExtra);
            ehp.setPreciounitario(BigDecimal.valueOf(extra.getPreciounitario()));
            ehp.setIdextra(idComExtra);

            extrasCarrito.add(ehp);
            session.setAttribute("extrasCarrito",extrasCarrito);
            attr.addFlashAttribute("msgAddExtra", "Se agregó un extra al carrito");
        }else{
            attr.addFlashAttribute("msgNotFound", "No se encontro el plato");
        }

        //TODO por ahora solo funcionará si el flujo es LISTA DE PLATOS - DETALLE - VER CARRITO
        String urlDetalle = "detallePlato";
        String params = "?idRest="+idRest+"&idPlato="+idPlato;
        return "redirect:/cliente/"+urlDetalle+params;
    }


    @PostMapping("/restarExtrasCarrito")
    public String restarExtrasCarrito(){

        return "/";
    }

    @GetMapping("/terminarCompra")
    public String terminarCompra(){

        return "/";
    }


    @GetMapping("/listaCupones")
    public String listacupones() {

        return "Cliente/listaCupones";
    }

    @GetMapping("/listaReportes")
    public String listaReportes() {

        return "Cliente/listaReportes";
    }


    //LISTA CATEGORIAS
    @GetMapping("/categorias")
    public String categorias() {
        return "Cliente/categorías";
    }

    //PEDIDO ACTUAL
    @GetMapping("/pedidoActual")
    public String pedidoActual(@RequestParam Map<String, Object> params, Model model, HttpSession httpSession,
                               @RequestParam(value = "texto",required = false) String texto,
                               @RequestParam(value = "estado",required = false) String estado) {
        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        int page  = params.get("page") != null ? Integer.valueOf(params.get("page").toString())-1 : 0;
        PageRequest pageRequest = PageRequest.of(page, 5);


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

        System.out.println("BEFORE QUERY");


        Page<PedidoDTO> listaPedidos=pedidoActualService.findPaginated(usuario1.getIdusuario(), texto,limitInf,limitSup, pageRequest);

        int totalPage = listaPedidos.getTotalPages();

        if(totalPage > 0){
            List<Integer> pages = IntStream.rangeClosed(1,totalPage).boxed().collect(Collectors.toList());
            model.addAttribute("pages",pages);
        }

        List<PedidoDTO> listaPedidoActual= new ArrayList<PedidoDTO>();
        for(PedidoDTO ped: listaPedidos){
            if(ped.getEstado()==1 || ped.getEstado()==3 || ped.getEstado()==4 || ped.getEstado()==5 ){
                listaPedidoActual.add(ped);
            }
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
        model.addAttribute("listapedido3", pedidoRepository.detalle1(codigo));



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
                              @RequestParam(value = "val") Integer valoraRest, @RequestParam("comentRest") String comentRest){
        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");
        System.out.println(valoraRest);
        System.out.println(comentRest);
        System.out.println(id);
        Optional<Pedido> pedido= pedidoRepository.findById(id);
        if(pedido.isPresent()){
            Pedido pedido1= pedido.get();
            pedido1.setValoracionrestaurante(valoraRest);
            pedido1.setComentariorestaurante(comentRest);
        }
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



}
