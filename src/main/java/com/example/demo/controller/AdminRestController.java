package com.example.demo.controller;

import com.example.demo.dtos.*;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.Multipart;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/restaurante")
public class AdminRestController {

    @Autowired
    PedidoService pedidoService;
    @Autowired
    UsuarioRepository adminRestRepository;
    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    RolRepository rolRepository;
    @Autowired
    DistritosRepository distritosRepository;

    @Autowired
    ReportePlatoService reportePlatoService;

    @Autowired
    ReporteVentasService reporteVentasService;

    @Autowired
    ReporteValoracionService reporteValoracionService;

    @Autowired
    RestauranteRepository restauranteRepository;

    @Autowired
    CategoriasRestauranteRepository categoriasRestauranteRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping("/imagenadmin/{id}")
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable("id") String id) {
        Optional<Usuario> usuarioOptional = Optional.ofNullable(usuarioRepository.findByDni(id));
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            byte[] imagenBytes = usuario.getFoto();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(usuario.getFotocontenttype()));
            return new ResponseEntity<>(imagenBytes, httpHeaders, HttpStatus.OK);
        } else {
            return null;
        }
    }

    @GetMapping("/imagen/{id}")
    public ResponseEntity<byte[]> mostrarImagenAdminR(@PathVariable("id") int id) {
        Optional<Usuario> optionalUsuario = adminRestRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario adminR = optionalUsuario.get();
            byte[] imagenBytes = adminR.getFoto();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(adminR.getFotocontenttype()));
            return new ResponseEntity<>(imagenBytes, httpHeaders, HttpStatus.OK);
        } else {
            return null;
        }
    }

    @GetMapping("/imagenRest/{id}")
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable("id") int id) {
        Optional<Restaurante> optional = restauranteRepository.findById(id);
        if (optional.isPresent()) {
            Restaurante p = optional.get();
            byte[] imagenBytes = p.getFoto();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(p.getFotocontenttype()));
            return new ResponseEntity<>(imagenBytes, httpHeaders, HttpStatus.OK);
        } else {
            return null;
        }
    }

    @GetMapping("/listaPedidos")
    public String listaPedidos(Model model, HttpSession session) throws ParseException {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String today = dtf.format(now);
        System.out.println(today);

        return findPaginated("", "0", "0", today, "3000-05-21 00:00:00", 1, restaurante.getIdrestaurante(), model, session);
    }

    @GetMapping("/page")
    public String findPaginated(@ModelAttribute @RequestParam(value = "textBuscador", required = false) String textBuscador,
                                @ModelAttribute @RequestParam(value = "textEstado", required = false) String inputEstado,
                                @ModelAttribute @RequestParam(value = "inputPrecio", required = false) String inputPrecio,
                                @ModelAttribute @RequestParam(value = "fechainicio", required = false) String fechainicio,
                                @ModelAttribute @RequestParam(value = "fechafin", required = false) String fechafin,
                                @RequestParam(value = "pageNo", required = false) Integer pageNo,
                                @RequestParam(value = "idrestaurante", required = false) Integer idrestaurante, Model model, HttpSession session) throws ParseException {

        if (pageNo == null || pageNo == 0) {
            pageNo = 1;
        }
        int inputID = 1;
        int pageSize = 5;
        Page<Pedido> page;
        List<Pedido> listaPedidos;
        System.out.println(textBuscador);
        if (textBuscador == null) {
            textBuscador = "";
        }

        System.out.println(inputEstado);
        int inputEstado2;
        int inputEstadoMin;
        int inputEstadoMax;

        if (inputEstado == null) {
            inputEstado2 = 0;
        }

        try {
            inputEstado2 = Integer.parseInt(inputEstado);
            if (inputEstado2 == 0) {
                inputEstadoMin = 0;
                inputEstadoMax = 8;
            } else if (inputEstado2 > 7) {
                return "redirect:/restaurante/listaPedidos";
            } else {
                inputEstadoMin = inputEstado2 - 1;
                inputEstadoMax = inputEstado2 - 1;
            }
        } catch (NumberFormatException e) {
            return "redirect:/restaurante/listaPedidos";
        }

        System.out.println(inputPrecio);
        int inputPrecioInt;
        int inputPMax;
        int inputPMin;

        if (inputPrecio == null) {
            inputPrecioInt = 0;
        }

        try {
            inputPrecioInt = Integer.parseInt(inputPrecio);
            if (inputPrecioInt == 0) {
                inputPMin = 0;
                inputPMax = 1000;
            } else if (inputPrecioInt == 4) {
                inputPMin = inputPrecioInt;
                inputPMax = 1000;
            } else if (inputPrecioInt > 4) {
                return "redirect:/restaurante/listaPedidos";
            } else {
                inputPMin = inputPrecioInt;
                inputPMax = inputPrecioInt;
            }
        } catch (NumberFormatException e) {
            return "redirect:/restaurante/listaPedidos";
        }

        System.out.println(fechafin + "############");
        Date fechafin2;
        String fechafin3;
        if (fechafin == null || fechafin.equals("") || fechafin.equalsIgnoreCase("null")) {
            try {
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                fechafin2 = simpleDateFormat.parse("3000-05-21");
                String pattern2 = "yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
                fechafin3 = simpleDateFormat2.format(fechafin2);
                System.out.println(fechafin3);
            } catch (ParseException e) {
                return "redirect:/restaurante/listaPedidos";
            }

        } else {
            try {
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                fechafin2 = simpleDateFormat.parse(fechafin);
                String pattern2 = "yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
                fechafin3 = simpleDateFormat2.format(fechafin2);
                System.out.println(fechafin3);
                model.addAttribute("fechafin", fechafin3);
            } catch (ParseException e) {
                return "redirect:/restaurante/listaPedidos";
            }

        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String today = dateFormat.format(date);
        System.out.println(today);

        Date fechainicio2;
        String fechainicio3;
        if (fechainicio == null || fechainicio.equals("") || fechainicio.equalsIgnoreCase("null")) {
            try {
                fechainicio2 = date;
                String pattern = "yyyy-MM-dd 00:00:00";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                fechainicio3 = simpleDateFormat.format(fechainicio2);
                System.out.println(fechainicio3);
            } catch (IllegalFormatException e) {
                return "redirect:/restaurante/listaPedidos";
            }

        } else {
            try {
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                fechainicio2 = simpleDateFormat.parse(fechainicio);
                String pattern2 = "yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat(pattern2);
                fechainicio3 = simpleDateFormat2.format(fechainicio2);
                System.out.println(fechainicio3);
                model.addAttribute("fechainicio", fechainicio3);
            } catch (ParseException e) {
                return "redirect:/restaurante/listaPedidos";
            }
        }

        System.out.println("#################");
        System.out.println(inputEstadoMin);
        System.out.println(inputEstadoMax);
        System.out.println("#################");
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        page = pedidoService.findPaginated(pageNo, pageSize, restaurante.getIdrestaurante(), textBuscador, inputEstadoMin, inputEstadoMax, inputPMin * 20 - 20, inputPMax * 20, fechainicio3, fechafin3);
        listaPedidos = page.getContent();

        model.addAttribute("texto", textBuscador);
        model.addAttribute("textoE", inputEstado);
        model.addAttribute("textoP", inputPrecio);

        System.out.println(pageNo + "\n" + pageSize + "\n" + page.getTotalElements() + "\n" + textBuscador + "\n" + inputEstado + "\n" + inputPrecio + "\n" + inputEstadoMin + "\n" + inputEstadoMax + "\n" + inputPMin + "\n" + inputPMax + "\n" + fechainicio3 + "\n" + fechafin3);

        model.addAttribute("pageSize", pageSize);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listaPedidos", listaPedidos);
        return "AdminRestaurante/listaPedidos";
    }

    @GetMapping("/rechazarPedido")
    public String rechazarPedido(@RequestParam("id") String id, @RequestParam("comentarioAR") String comentarioAR,
                                 RedirectAttributes attr,
                                 Model model, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int idr = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(idr);
        Pedido pedido = pedidoRepository.pedidosXrestauranteXcodigo(restaurante.getIdrestaurante(), id);
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.println(comentarioAR.getClass());
        if (pedido != null) {
            if (pedido.getEstado() == 0) {
                if (!comentarioAR.equals("")) {
                    pedido.setEstado(2);
                    pedido.setComentrechazorest(comentarioAR);
                    pedidoRepository.save(pedido);
                    attr.addFlashAttribute("msg", "Pedido rechazado exitosamente");
                } else {
                    attr.addFlashAttribute("msg3", "Debe ingresar un motivo válido");
                }

            }
        }
        return "redirect:/restaurante/listaPedidos";
    }

    @GetMapping("/aceptarPedido")
    public String aceptarPedido(@RequestParam("id") String id,
                                RedirectAttributes attr,
                                Model model, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int idr = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(idr);
        Pedido pedido = pedidoRepository.pedidosXrestauranteXcodigo(restaurante.getIdrestaurante(), id);
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        if (pedido != null) {
            if (pedido.getEstado() == 0) {
                pedido.setEstado(1);
                pedido.setComentrechazorest("Su pedido ha sido aceptado");
                pedidoRepository.save(pedido);
                attr.addFlashAttribute("msg2", "Pedido aceptado exitosamente");
            }
        }
        return "redirect:/restaurante/listaPedidos";
    }

    @GetMapping("/prepararPedido")
    public String preparaPedido(@RequestParam("id") String id,
                                RedirectAttributes attr,
                                Model model, HttpSession session,
                                @RequestParam(value = "v", required = false) String v) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int idr = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(idr);
        Pedido pedido = pedidoRepository.pedidosXrestauranteXcodigo(restaurante.getIdrestaurante(), id);
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        if (pedido != null) {
            if (pedido.getEstado() == 1) {
                pedido.setEstado(3);
                pedido.setComentrechazorest("Su pedido se está preparando");
                pedidoRepository.save(pedido);
            }
        }
        return "redirect:/restaurante/detallePedido?v=" + v + "&codigoPedido=" + id;
    }

    @GetMapping("/pedidoListo")
    public String listoPedido(@RequestParam("id") String id,
                              RedirectAttributes attr,
                              Model model, HttpSession session,
                              @RequestParam(value = "v", required = false) String v) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int idr = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(idr);
        Pedido pedido = pedidoRepository.pedidosXrestauranteXcodigo(restaurante.getIdrestaurante(), id);
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        if (pedido != null) {
            if (pedido.getEstado() == 3) {
                pedido.setEstado(4);
                pedido.setComentrechazorest("Su pedido terminó de prepararse, estamos buscando repartidor.");
                pedidoRepository.save(pedido);
            }
        }
        return "redirect:/restaurante/detallePedido?v=" + v + "&codigoPedido=" + id;
    }


    @GetMapping("/detallePedido")
    public String detalleDelPedido(Model model, HttpSession
            session, @RequestParam(value = "codigoPedido", required = false) String codigoPedido,
                                   @RequestParam(value = "v", required = false) String v) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        if (codigoPedido == null || codigoPedido.isEmpty()) {
            return "redirect:/restaurante/listaPedidos";
        }
        List<DetallePedidoDTO> detallesPedido = pedidoRepository.detallePedido(restaurante.getIdrestaurante(), codigoPedido);
        if (detallesPedido.isEmpty() || detallesPedido == null) {
            return "redirect:/restaurante/listaPedidos";
        }
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        List<PlatoPorPedidoDTO> listaPlatos = pedidoRepository.platosPorPedido(restaurante.getIdrestaurante(), codigoPedido);
        List<ExtraPorPedidoDTO> listaExtras = pedidoRepository.extrasPorPedido(codigoPedido);
        BigDecimal sumatotalPlato = new BigDecimal("0.00");
        BigDecimal sumatotalExtra = new BigDecimal("0.00");
        for (int i = 0; i < listaPlatos.size(); i++) {
            sumatotalPlato = sumatotalPlato.add(listaPlatos.get(i).getPreciototal());
        }
        for (int i = 0; i < listaExtras.size(); i++) {
            sumatotalExtra = sumatotalExtra.add(listaExtras.get(i).getPreciototal());
        }
        System.out.println("##############V= " + v);
        model.addAttribute("detalles", detallesPedido);
        model.addAttribute("platos", listaPlatos);
        model.addAttribute("extras", listaExtras);
        model.addAttribute("sumaPlato", sumatotalPlato);
        model.addAttribute("sumaExtra", sumatotalExtra);
        model.addAttribute("v", v);
        return "AdminRestaurante/detallePedido";
    }

    @GetMapping("/reporteVentas")
    public String listaVentas(Model model, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        return findPaginatedRepVen("", "1980-05-21", "3000-05-21", "0", 1, restaurante.getIdrestaurante(), model, session);
    }

    @GetMapping("/pageVen")
    public String findPaginatedRepVen(@ModelAttribute @RequestParam(value = "textCodigo", required = false) String
                                              textCodigo,
                                      @ModelAttribute @RequestParam(value = "fechainicio", required = false) String fechainicio,
                                      @ModelAttribute @RequestParam(value = "fechafin", required = false) String fechafin,
                                      @ModelAttribute @RequestParam(value = "inputPrecio", required = false) String inputPrecio,
                                      @RequestParam(value = "pageNo", required = false) Integer pageNo,
                                      @RequestParam(value = "idrestaurante", required = false) Integer idrestaurante, Model model, HttpSession
                                              session) {

        if (pageNo == null || pageNo == 0) {
            pageNo = 1;
        }
        int inputID = 1;
        int pageSize = 5;
        Page<PedidoReporteDTO> page;
        List<PedidoReporteDTO> listaVentasReporte;

        //Manipular input de buscadores
        System.out.println(textCodigo);
        if (textCodigo == null) {
            textCodigo = "";
        }
        System.out.println(inputPrecio);
        int inputPrecioInt;
        int inputPrecioMax;
        int inputPrecioMin;

        if (inputPrecio == null) {
            inputPrecioInt = 0;
        }

        try {
            inputPrecioInt = Integer.parseInt(inputPrecio);
            if (inputPrecioInt == 0) {
                inputPrecioMin = 0;
                inputPrecioMax = 1000;
            } else if (inputPrecioInt == 4) {
                inputPrecioMin = inputPrecioInt;
                inputPrecioMax = 1000;
            } else if (inputPrecioInt > 4) {
                return "redirect:/restaurante/reporteVentas";
            } else {
                inputPrecioMin = inputPrecioInt;
                inputPrecioMax = inputPrecioInt;
            }
        } catch (NumberFormatException e) {
            return "redirect:/restaurante/reporteVentas";
        }


        Date fechafin2;
        Date fechainicio2;
        if (fechafin == null || fechafin.equals("") || fechafin.equalsIgnoreCase("null")) {
            fechafin = "3000-05-21";
        } else {
            try {
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                fechafin2 = simpleDateFormat.parse(fechafin);
                model.addAttribute("fechafin", fechafin);
            } catch (ParseException e) {
                return "redirect:/restaurante/reporteVentas";
            }


        }

        if (fechainicio == null || fechainicio.equals("") || fechainicio.equalsIgnoreCase("null")) {
            fechainicio = "1980-05-21";
        } else {
            try {
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                fechainicio2 = simpleDateFormat.parse(fechainicio);
                model.addAttribute("fechainicio", fechainicio);
            } catch (ParseException e) {
                return "redirect:/restaurante/reporteVentas";
            }
        }


        System.out.println(fechainicio);
        System.out.println(fechafin);

        System.out.println("#################");
        System.out.println("#################");

        //Obtener lista de reportes
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        page = reporteVentasService.findPaginated(pageNo, pageSize, restaurante.getIdrestaurante(), 6, fechainicio, fechafin, textCodigo, inputPrecioMin * 20 - 20, inputPrecioMax * 20);
        listaVentasReporte = page.getContent();

        //Enviar atributos a la vista
        model.addAttribute("textCodigo", textCodigo);
        model.addAttribute("inputPrecio", inputPrecio);

        System.out.println(pageNo + "\n" + page.getTotalElements() + "\n" + page.getTotalPages() + "\n" + inputPrecioInt + "\n" + fechainicio + "\n" + fechafin);

        //Enviar lista y valores para paginación
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listaVentasReportes", listaVentasReporte);
        return "AdminRestaurante/reporteVentas";
    }

    @GetMapping("/reporteValoracion")
    public String listaValoracion(Model model, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        return findPaginatedRepVal("6", "1980-05-21", "3000-05-21", 1, restaurante.getIdrestaurante(), model, session);
    }

    @GetMapping("/pageVal")
    public String findPaginatedRepVal
            (@ModelAttribute @RequestParam(value = "inputValoracion", required = false) String inputValoracion,
             @ModelAttribute @RequestParam(value = "fechainicio", required = false) String fechainicio,
             @ModelAttribute @RequestParam(value = "fechafin", required = false) String fechafin,
             @RequestParam(value = "pageNo", required = false) Integer pageNo,
             @RequestParam(value = "idrestaurante", required = false) Integer idrestaurante, Model model, HttpSession
                     session) {

        if (pageNo == null || pageNo == 0) {
            pageNo = 1;
        }
        int inputID = 1;
        int pageSize = 5;
        Page<ValoracionReporteDTO> page;
        List<ValoracionReporteDTO> listaValoracionReporte;

        //Manipular input de buscadores
        System.out.println(inputValoracion);
        int inputValoracion2;
        if (inputValoracion == null) {
            inputValoracion = "";
        } else {
            try {
                inputValoracion2 = Integer.parseInt(inputValoracion);
                if (inputValoracion2 > 6) {
                    return "redirect:/restaurante/reporteValoracion";
                } else if (inputValoracion2 == 6){
                    inputValoracion="";
                }
            } catch (NumberFormatException e) {
                return "redirect:/restaurante/reporteValoracion";
            }

        }

        Date fechafin2;
        Date fechainicio2;
        if (fechafin == null || fechafin.equals("") || fechafin.equalsIgnoreCase("null")) {
            fechafin = "3000-05-21";
        } else {
            try {
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                fechafin2 = simpleDateFormat.parse(fechafin);
                model.addAttribute("fechafin", fechafin);
            } catch (ParseException e) {
                return "redirect:/restaurante/reporteValoracion";
            }

        }

        if (fechainicio == null || fechainicio.equals("") || fechainicio.equalsIgnoreCase("null")) {
            fechainicio = "1980-05-21";
        } else {
            try {
                String pattern = "yyyy-MM-dd";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                fechainicio2 = simpleDateFormat.parse(fechainicio);
                model.addAttribute("fechainicio", fechainicio);
            } catch (ParseException e) {
                return "redirect:/restaurante/reporteValoracion";
            }
        }


        System.out.println(fechainicio);
        System.out.println(fechafin);

        System.out.println("#################");
        System.out.println("#################");

        //Obtener lista de reportes
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        page = reporteValoracionService.findPaginated(pageNo, pageSize, restaurante.getIdrestaurante(), 6, inputValoracion, fechainicio, fechafin);
        listaValoracionReporte = page.getContent();

        //Enviar atributos a la vista
        model.addAttribute("inputValoracion", inputValoracion);

        System.out.println(pageNo + "\n" + pageSize + "\n" + inputValoracion + "\n" + fechainicio + "\n" + fechafin);

        //Enviar lista y valores para paginación
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listaValoracionReportes", listaValoracionReporte);
        return "AdminRestaurante/reporteValoraciones";
    }

    @GetMapping("/reportePlatos")
    public String reportePlato(Model model, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        return findPaginatedRepPla("", "0", "0", 1, restaurante.getIdrestaurante(), model, session);
    }

    @GetMapping("/pagePla")
    public String findPaginatedRepPla(@ModelAttribute @RequestParam(value = "textBuscador", required = false) String
                                              textBuscador,
                                      @ModelAttribute @RequestParam(value = "inputCantidad", required = false) String inputCantidad,
                                      @ModelAttribute @RequestParam(value = "inputCategoria", required = false) String inputCategoria,
                                      @RequestParam(value = "pageNo", required = false) Integer pageNo,
                                      @RequestParam(value = "idrestaurante", required = false) Integer idrestaurante, Model model, HttpSession
                                              session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        List<Categorias> listaCategorias = restaurante.getCategoriasRestaurante();
        int inputCategoria2;
        System.out.println(inputCategoria);
        System.out.println(listaCategorias.get(0).getIdcategoria());
        System.out.println(listaCategorias.get(1).getIdcategoria());
        System.out.println(listaCategorias.get(2).getIdcategoria());
        System.out.println(listaCategorias.get(3).getIdcategoria());
        if (inputCategoria == null) {
            return "redirect:/restaurante/reportePlatos";
        } else {
            try {
                inputCategoria2 = Integer.parseInt(inputCategoria);
                int i = 1;
                for (Categorias categoria : listaCategorias) {
                    if (categoria.getIdcategoria() == inputCategoria2) {
                        System.out.println("entre1");
                        inputCategoria = String.valueOf(inputCategoria2);
                        break;
                    } else if (inputCategoria2 == 0) {
                        System.out.println("entre2");
                        inputCategoria = "";
                        break;
                    } else {
                        if (i == 4) {
                            System.out.println("entre3");
                            return "redirect:/restaurante/reportePlatos";
                        }
                    }
                    i ++;
                }
                if (pageNo == null || pageNo == 0) {
                    pageNo = 1;
                }
                int inputID = 1;
                int pageSize = 5;
                Page<PlatoReporteDTO> page;
                List<PlatoReporteDTO> listaPlatoReporte;

                //Manipular input de buscadores

                if (textBuscador == null) {
                    textBuscador = "";
                }


                System.out.println(inputCantidad);
                int inputCantidadInt;
                int inputCantidadMax;
                int inputCantidadMin;
                if (inputCantidad == null) {
                    inputCantidadInt = 0;
                }
                try {
                    inputCantidadInt = Integer.parseInt(inputCantidad);
                    if (inputCantidadInt == 0) {
                        inputCantidadMin = 0;
                        inputCantidadMax = 1000;
                    } else if (inputCantidadInt == 4) {
                        inputCantidadMin = inputCantidadInt;
                        inputCantidadMax = 1000;
                    } else if (inputCantidadInt > 4) {
                        return "redirect:/restaurante/reportePlatos";
                    } else {
                        inputCantidadMin = inputCantidadInt;
                        inputCantidadMax = inputCantidadInt;
                    }
                } catch (NumberFormatException e) {
                    return "redirect:/restaurante/reportePlatos";
                }

                System.out.println("#################");
                System.out.println("#################");

                //Obtener lista de reportes

                List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
                model.addAttribute("listaNotiRest", listaNotificacion);
                page = reportePlatoService.findPaginated(pageNo, pageSize, restaurante.getIdrestaurante(), 6, textBuscador, inputCategoria, inputCantidadMin * 5 - 5, inputCantidadMax * 5);
                listaPlatoReporte = page.getContent();

                //Enviar atributos a la vista
                model.addAttribute("texto", textBuscador);
                model.addAttribute("textoC", inputCategoria);
                model.addAttribute("textoCant", inputCantidad);


                model.addAttribute("listaCategorias", listaCategorias);


                System.out.println(pageNo + "\n" + pageSize + "\n" + textBuscador + "\n" + inputCategoria + "\n" + inputCantidad);

                //Enviar lista y valores para paginación
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("currentPage", pageNo);
                model.addAttribute("totalPages", page.getTotalPages());
                model.addAttribute("totalItems", page.getTotalElements());
                model.addAttribute("listaPlatoReportes", listaPlatoReporte);
                return "AdminRestaurante/reportePlatos";
            } catch (NumberFormatException e) {
                return "redirect:/restaurante/reportePlatos";
            }
        }

    }

    @GetMapping("/elegirReporte")
    public String elegirReporte(Model model, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        return "AdminRestaurante/eleccionReporte";
    }


}
