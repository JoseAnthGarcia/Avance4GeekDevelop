package com.example.demo.controller;

import com.example.demo.dtos.PlatoPorPedidoDTO;
import com.example.demo.dtos.ReporteIngresosDTO;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


@Controller
@RequestMapping("/repartidor")
public class RepartidorController {

    @Autowired
    TipoMovilidadRepository tipoMovilidadRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    DistritosRepository distritosRepository;

    @Autowired
    MovilidadRepository movilidadRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    UbicacionRepository ubicacionRepository;

    @Autowired
    PedidoRepository pedidoRepository;


    @Autowired
    PedidoRepartidorService pedidoRepartidorService;

    @Autowired
    RestauranteRepository restauranteRepository;

    @Autowired
    ComentarioRepartidorService comentarioRepartidorService;

    @Autowired
    PedidoRepartidorServiceImpl pedidoRepartidorServiceImp;


    @GetMapping("/tipoReporte")
    public String tipoReporte(Model model,HttpSession httpSession){
        Ubicacion ubicacionActual = (Ubicacion) httpSession.getAttribute("ubicacionActual");
        List<Pedido> notificaciones = pedidoRepository.findByEstadoAndUbicacion_Distrito(4,ubicacionActual.getDistrito());
        model.addAttribute("notificaciones", notificaciones);

        return "Repartidor/reportes";
    }

    @GetMapping("/listaPedidos")
    public String verListaPedidos(Model model,HttpSession session,
                                  @RequestParam(value = "idPedido", required = false) String codigoPedido,
                                  @RequestParam(value = "numPag", required = false) Integer numPag,
                                  RedirectAttributes attr){
        Usuario repartidor = (Usuario) session.getAttribute("usuario");
        List<Pedido> pedidoAct = pedidoRepository.findByEstadoAndRepartidor(5, repartidor);
        Page<Pedido> pagina;
        if(numPag==null){
            numPag= 1;
        }

        int tamPag = 2;
        if(pedidoAct.size()==0){
            //Ubicacion ubicacionActual = (Ubicacion) session.getAttribute("ubicacionActual");
            List<Distrito> listaDistritos = distritosRepository.findAll();
            List<Ubicacion> direcciones = ubicacionRepository.findByUsuarioVal(repartidor);

            pagina = pedidoRepartidorService.pedidosPaginacion(numPag, tamPag, session);
            List<Pedido> pedidos =pagina.getContent();



            if(pedidos.size()!=0){
                if(codigoPedido==null){
                    Pedido pedido = pedidos.get(0);
                    model.addAttribute("pedidoDetalle", pedido);
                    List<PlatoPorPedidoDTO> platosPorPedido = pedidoRepository.platosPorPedido(pedido.getRestaurante().getIdrestaurante(), pedido.getCodigo());
                    model.addAttribute("platosPorPedido", platosPorPedido);
                }else{
                    Optional pedidoOptional =pedidoRepository.findById(codigoPedido);
                    if(pedidoOptional.isPresent()){
                        Pedido pedido = (Pedido) pedidoOptional.get();
                        model.addAttribute("pedidoDetalle", pedido);
                        List<PlatoPorPedidoDTO> platosPorPedido = pedidoRepository.platosPorPedido(pedido.getRestaurante().getIdrestaurante(), pedido.getCodigo());
                        model.addAttribute("platosPorPedido", platosPorPedido);

                    }
                }
            }
            Ubicacion ubicacionActual = (Ubicacion) session.getAttribute("ubicacionActual");
            List<Pedido> notificaciones = pedidoRepository.findByEstadoAndUbicacion_Distrito(4,ubicacionActual.getDistrito());
            model.addAttribute("notificaciones", notificaciones);
            model.addAttribute("tamPag",tamPag);
            model.addAttribute("currentPage",numPag);
            model.addAttribute("totalPages", pagina.getTotalPages());
            model.addAttribute("totalItems", pagina.getTotalElements());

            model.addAttribute("direcciones", direcciones);
            model.addAttribute("listaPedidos", pedidos);
            model.addAttribute("listaDistritos", listaDistritos);
            return "Repartidor/solicitudPedidos";
        }else{
            attr.addFlashAttribute("msg", "Debes culminar tu entrega para poder visualizar otras solicitudes de reparto.");
            return "redirect:/repartidor/pedidoActual";
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
    @GetMapping("/editarPerfil")
    public String editarPerfil(HttpSession httpSession, Model model) {
        Ubicacion ubicacionActual = (Ubicacion) httpSession.getAttribute("ubicacionActual");
        List<Pedido> notificaciones = pedidoRepository.findByEstadoAndUbicacion_Distrito(4,ubicacionActual.getDistrito());
        model.addAttribute("notificaciones", notificaciones);
        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        model.addAttribute("usuario", usuario);

        return "Repartidor/editarPerfil";

    }
    @PostMapping("/guardarEditar")
    public String guardarEdicion(@RequestParam("contraseniaConf") String contraseniaConf,
                                 @RequestParam("telefonoNuevo") String telefonoNuevo,
                                 HttpSession httpSession,
                                 Model model) {

        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        boolean valContra = true;



        Usuario usuario2 =usuarioRepository.findByTelefono(telefonoNuevo);

        if (BCrypt.checkpw(contraseniaConf, usuario.getContrasenia())) {
            valContra = false;
        }

        if (valContra || usuario2!=null ) {

            if (usuario2!=null) {
                int idusuario = usuario2.getIdusuario();
                int sesion =usuario.getIdusuario();
                if(idusuario==sesion){
                    model.addAttribute("msg1", "El telefono nuevo debe ser distinto al actual");
                }else{
                    model.addAttribute("msg1", "El telefono ingresado ya está registrado");
                }
                System.out.println("sesion "+ usuario.getIdusuario() +" user "+ usuario2.getIdusuario());
            }

            if (valContra) {
                model.addAttribute("msg", "Contraseña incorrecta");
            }

            model.addAttribute("usuario", usuario);
            return "Repartidor/editarPerfil";


        } else {
            usuario.setTelefono(telefonoNuevo); //usar save para actualizar
            httpSession.setAttribute("usuario", usuario); //TODO: preguntar profe
            usuarioRepository.save(usuario);
            return "redirect:/repartidor/listaPedidos";
        }


    }
    @GetMapping("/fotoPerfil")
    public ResponseEntity<byte[]> mostrarPerfil(@RequestParam("id") int id) {
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


    @GetMapping("/estadisticas")
    public String estadisticas(Model model,HttpSession session,
                               @RequestParam(value = "numPag", required = false) Integer numPag){
        Usuario repartidor = (Usuario) session.getAttribute("usuario");
        Page<Pedido> pagina;
        if(numPag==null){
            numPag= 1;
        }

        int tamPag = 5;
        pagina = comentarioRepartidorService.comentariosRepartidor(numPag, tamPag, repartidor.getIdusuario());
        List<Pedido> pedidos =pagina.getContent();
        model.addAttribute("tamPag",tamPag);
        model.addAttribute("currentPage",numPag);
        model.addAttribute("totalPages", pagina.getTotalPages());
        model.addAttribute("totalItems", pagina.getTotalElements());

        Integer valoracion = usuarioRepository.promedioValoracionRpartidor(repartidor.getIdusuario());
        model.addAttribute("listaPedidos", pedidos);
        model.addAttribute("valoracion", valoracion);

        Ubicacion ubicacionActual = (Ubicacion) session.getAttribute("ubicacionActual");
        List<Pedido> notificaciones = pedidoRepository.findByEstadoAndUbicacion_Distrito(4,ubicacionActual.getDistrito());
        model.addAttribute("notificaciones", notificaciones);

        return "/Repartidor/estadisticas";
    }

    @PostMapping("/cambiarDistrito")
    public String cambiarDistritoActual(@RequestParam("idubicacion") int idubicacion,HttpSession session){
        Usuario repartidor = (Usuario) session.getAttribute("usuario");
        Optional<Ubicacion> ubicacionOpt =  ubicacionRepository.findById(idubicacion);

        if(ubicacionOpt.isPresent()){
            Ubicacion ubicacion = ubicacionOpt.get();
            if(ubicacion.getUsuario().getIdusuario().intValue() == repartidor.getIdusuario().intValue()){
                session.setAttribute("ubicacionActual", ubicacion);
            }
        }

        return "redirect:/repartidor/listaPedidos";
    }

    @GetMapping("/pedidoActual")
    public String verPedidoActual(Model model,HttpSession session, RedirectAttributes attr){
        Usuario repartidor = (Usuario) session.getAttribute("usuario");
        List<Pedido> pedidoAct = pedidoRepository.findByEstadoAndRepartidor(5, repartidor);
        if(pedidoAct.size()!=0){
            List<Distrito> listaDistritos = distritosRepository.findAll();
            model.addAttribute("listaDistritos", listaDistritos);
            model.addAttribute("pedidoAct", pedidoAct);
            List<PlatoPorPedidoDTO> platosPorPedido = pedidoRepository.platosPorPedido(pedidoAct.get(0).getRestaurante().getIdrestaurante(), pedidoAct.get(0).getCodigo());
            model.addAttribute("platosPorPedido", platosPorPedido);
            List<Ubicacion> direcciones = ubicacionRepository.findByUsuarioVal(repartidor);
            model.addAttribute("direcciones", direcciones);

            Ubicacion ubicacionActual = (Ubicacion) session.getAttribute("ubicacionActual");
            List<Pedido> notificaciones = pedidoRepository.findByEstadoAndUbicacion_Distrito(4,ubicacionActual.getDistrito());
            model.addAttribute("notificaciones", notificaciones);
            return "Repartidor/pedidoActual";
        }else{
            attr.addFlashAttribute("msg", "No tienes ningún pedido actual.");
            return "redirect:/repartidor/listaPedidos";
        }
    }

    @GetMapping("/aceptarPedido")
    public String aceptarPedido(@RequestParam(value = "codigo", required = false) String codigo,
                                HttpSession session){
        if(codigo!=null){
            Optional<Pedido> pedidoOpt = pedidoRepository.findById(codigo);
            if(pedidoOpt.isPresent()){
                Pedido pedido = pedidoOpt.get();
                pedido.setRepartidor((Usuario) session.getAttribute("usuario"));
                pedido.setEstado(5);
                //TODO: TIEMPO DE ENTREGA??
                pedidoRepository.save(pedido);
            }
        }
        return "redirect:/repartidor/pedidoActual";
    }

    @GetMapping("/pedidoEntregado")
    public String pedidoEntregado(@RequestParam(value = "codigo", required = false) String codigo,
                                  HttpSession session){
        if(codigo!=null){
            Optional<Pedido> pedidoOpt = pedidoRepository.findById(codigo);
            Usuario repartidor = (Usuario) session.getAttribute("usuario");
            if(pedidoOpt.isPresent() &&
                    pedidoRepository.findByEstadoAndRepartidor(5, repartidor).get(0).getCodigo().equals(pedidoOpt.get().getCodigo())){
                Pedido pedido = pedidoOpt.get();
                pedido.setEstado(6);
                pedidoRepository.save(pedido);
            }
        }
        return "redirect:/repartidor/listaPedidos";

    }

    @PostMapping("/seleccionarDistrito")
    public  String distritoActual(HttpSession session){
        session.setAttribute("ubicacionActual", new Ubicacion());
        return "redirect:/repartidor/listaPedido";
    }


    @GetMapping("/reporteIngresos")
    public String reporteIngresos(@RequestParam(value = "anio", required = false) String anio,
                                  HttpSession session,
                                  Model model){
        int anioInt = 0;

        Date date = new Date();

        ZoneId timeZone = ZoneId.systemDefault();
        LocalDate getLocalDate = date.toInstant().atZone(timeZone).toLocalDate();
        System.out.println(getLocalDate.getYear());

        if(anio==null){
            anioInt = getLocalDate.getYear();
        }else{
            try{
                //TODO: ver si pone otros numeros
                anioInt = Integer.parseInt(anio);
                int anioMax = pedidoRepository.hallarMaxAnioPedido();
                int anioMin = pedidoRepository.hallarMinAnioPedido();
                if(anioInt>anioMax || anioInt<anioMin){
                    anioInt = getLocalDate.getYear();
                }
            }catch (NumberFormatException e){
                anioInt = getLocalDate.getYear();
            }
        }

        Usuario repartidor = (Usuario) session.getAttribute("usuario");

        List<ReporteIngresosDTO> reporteIngresosDTOS = pedidoRepository.reporteIngresos(anioInt, repartidor.getIdusuario());

        BigDecimal precioTotal = new BigDecimal(0);
        for(ReporteIngresosDTO reporte: reporteIngresosDTOS){
            precioTotal = precioTotal.add(new BigDecimal((reporte.getCantmd()==null?0:reporte.getCantmd())*4));
            precioTotal = precioTotal.add(new BigDecimal((reporte.getCantdd()==null?0:reporte.getCantdd())*6));
        }

        List<Integer> anios = new ArrayList<>();

        for(int i = pedidoRepository.hallarMinAnioPedido(); i<=pedidoRepository.hallarMaxAnioPedido(); i++){
            anios.add(i);
        }

        model.addAttribute("precioTotal", precioTotal);
        model.addAttribute("reporte", reporteIngresosDTOS);
        model.addAttribute("anioSelect", anioInt);
        model.addAttribute("anios", anios);

        Ubicacion ubicacionActual = (Ubicacion) session.getAttribute("ubicacionActual");
        List<Pedido> notificaciones = pedidoRepository.findByEstadoAndUbicacion_Distrito(4,ubicacionActual.getDistrito());
        model.addAttribute("notificaciones", notificaciones);

        return "Repartidor/reporteIngresos";
    }

    @GetMapping("/reporteDelivery")
    public String repoteDelivery(HttpSession session, Model model) {
        Usuario repartidor = (Usuario) session.getAttribute("usuario");
        List<Pedido> listaPedidoReporte = pedidoRepository.findByEstadoAndRepartidor(6, repartidor);
        model.addAttribute("listaPedidoReporte", listaPedidoReporte);
        return "Repartidor/reporteDeliverys";
        /*
        HttpSession session,
        @RequestParam(value = "pagAct", required = false) String pagAct,
        @RequestParam(value = "fechaMin", required = false) String fechaMin,
        @RequestParam(value = "fechaMax", required = false) String fechaMax,
        @RequestParam(value = "precioMin", required = false) String precioMin,
        @RequestParam(value = "precioMax", required = false) String precioMax,
        @RequestParam(value = "valoracion", required = false) String valoracion,
        @RequestParam(value = "nombreRest", required = false) String nombreRest

        int tamPag = 5;
        boolean pagActVal = false;
        boolean fechaMinVal = false;
        boolean fechaMaxVal = false;
        boolean precioMinVal = false;
        boolean precioMaxVal = false;
        boolean valoracionVal = false;
        boolean nombreRestVal = false;

        int pagActInt = -1;
        try{
            pagActInt = Integer.parseInt(pagAct);
        }catch(NumberFormatException e){
            pagActInt = 1;
        }

        if(valoracion==null){}



        Usuario repartidor = (Usuario) session.getAttribute("usuario");
        Page<Pedido> pedidoReportePage = pedidoRepartidorServiceImp.listaPedidosReporte(pagAct, tamPag, 6, repartidor, fechaMin, fechaMax, precioMin, precioMax, )
        */
    }


}
