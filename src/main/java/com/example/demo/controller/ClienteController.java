package com.example.demo.controller;


import com.example.demo.dtos.*;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
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

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpSession;
import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        //buscar que direccion de milista de direcciones coincide con mi direccion actual

        List<ClienteDTO> listadirecc = clienteRepository.listaParaCompararDirecciones(usuario.getIdusuario());

        for (ClienteDTO cl : listadirecc) {
            if (cl.getDireccion().equalsIgnoreCase(direccionactual)) {
                iddistritoactual = cl.getIddistrito();
                break;
            }
        }

        model.addAttribute("listaRestaurante", restauranteRepository.listaRestaurante(iddistritoactual));
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
                limitSup = 50;
                break;
            default:
                limitInf = 0;
                limitSup = 5000;
        }

        List<PlatosDTO> listaPlato = platoRepository.listaPlato(idRest, texto, limitInf, limitSup);
        model.addAttribute("listaPlato",listaPlato);
        model.addAttribute("idRest",idRest);
         return "/Cliente/listaProductos";
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
    public String pedidoActual(Model model, HttpSession httpSession,
                               @RequestParam(value = "texto",required = false) String texto,
                               @RequestParam(value = "estado",required = false) String estado) {
        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");

        texto= "";

        List<PedidoDTO> listaPedidos=pedidoRepository.pedidosTotales(usuario1.getIdusuario(), texto,0,6);
        List<PedidoDTO> listaPedidoActual= new ArrayList<PedidoDTO>();
        for(PedidoDTO ped: listaPedidos){
            if(ped.getEstado()==1 || ped.getEstado()==3 || ped.getEstado()==4 || ped.getEstado()==5 ){
                listaPedidoActual.add(ped);
            }
        }

        model.addAttribute("listaPedidos",listaPedidoActual);

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

    @GetMapping("/listaCupones")
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

        model.addAttribute("listaCupones", restauranteRepository.listaRestaurante(iddistritoactual));
        return "Cliente/listaCupones";
    }

}
