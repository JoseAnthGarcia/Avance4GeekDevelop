package com.example.demo.controller;

import com.example.demo.dtos.*;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import com.example.demo.service.PedidoService;
import com.example.demo.service.PedidoServiceImpl;
import com.example.demo.service.ReportePlatoService;
import com.example.demo.service.ReporteValoracionService;
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
    ReporteValoracionService reporteValoracionService;

    @Autowired
    RestauranteRepository restauranteRepository;

    @Autowired
    CategoriasRestauranteRepository categoriasRestauranteRepository;

    @Autowired
    UsuarioRepository usuarioRepository;


    @GetMapping("/registro")
    public String nuevoAdminRest(@ModelAttribute("adminRest") Usuario adminRest, Model model) {
        model.addAttribute("adminRest", new Usuario());
        return "AdminRestaurante/registroAR";
    }


    @PostMapping("/guardarAdminR")
    public String guardarAdminRest(@ModelAttribute("adminRest") @Valid Usuario adminRest, BindingResult bindingResult,
                                   @RequestParam("confcontra") String contra2, @RequestParam("photo") MultipartFile file, Model model) {


        List<Usuario> usuariosxcorreo = usuarioRepository.findUsuarioByCorreo(adminRest.getCorreo());
        if (!usuariosxcorreo.isEmpty()) {
            bindingResult.rejectValue("correo", "error.Usuario", "El correo ingresado ya se encuentra en la base de datos");
        }
        List<Usuario> usuariosxdni = usuarioRepository.findUsuarioByDni(adminRest.getDni());
        if (!usuariosxdni.isEmpty()) {
            bindingResult.rejectValue("dni", "error.Usuario", "El DNI ingresado ya se encuentra en la base de datos");
        }

        List<Usuario> usuariosxtelefono = usuarioRepository.findUsuarioByTelefono(adminRest.getTelefono());
        if (!usuariosxtelefono.isEmpty()) {
            bindingResult.rejectValue("telefono", "error.Usuario", "El telefono ingresado ya se encuentra en la base de datos");
        }
        String fileName = "";
        System.out.println(contra2);
        System.out.println(adminRest.getContrasenia());
        //se agrega rol:
        adminRest.setRol(rolRepository.findById(3).get());
        adminRest.setEstado(2);
        Date date=new Date();
        DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
        String fecharegistro = LocalDate.now().toString();
        fecharegistro=fecharegistro+" "+hourFormat.format(date);
        System.out.println(fecharegistro);
        adminRest.setFecharegistro(fecharegistro);
        Boolean fecha_naci = true;
        boolean validarFoto=true;
        int naci = 0;
        String[] parts = adminRest.getFechanacimiento().split("-");
        try{
            naci = Integer.parseInt(parts[0]);
            Calendar fecha = new GregorianCalendar();
            int anio = fecha.get(Calendar.YEAR);
            System.out.println("AÑOOOOOOO " + anio);
            System.out.println("Naciiiiii " + naci);
            if (anio - naci >= 18) {
                fecha_naci = false;
            }
    }catch(NumberFormatException e){
            System.out.println("Error capturado");
    }
        System.out.println("SOY LA FECH DE CUMPLE"+adminRest.getFechanacimiento());
        System.out.println("Soy solo fecha_naci "+fecha_naci);
        if (file!=null){
            System.out.println("No soy nul 1111111111111111111111111111111111111111111");
            System.out.println(file);
            if(file.isEmpty()){
                model.addAttribute("mensajefoto", "Debe subir una imagen");
                validarFoto = false;
            }else if (!file.getContentType().contains("jpeg") && !file.getContentType().contains("png") && !file.getContentType().contains("web")) {
                System.out.println("FILE NULL---- HECTOR CTM5");
                model.addAttribute("mensajefoto", "Ingrese un formato de imagen válido (p.e. JPEG,PNG o WEBP)");
                validarFoto = false;
            }
            fileName = file.getOriginalFilename();
            if (fileName.contains("..")){
                model.addAttribute("mensajefoto","No se premite '..' een el archivo");
                return "/AdminRestaurante/registroAR";
            }
        }

        if (bindingResult.hasErrors() || !contra2.equalsIgnoreCase(adminRest.getContrasenia()) || fecha_naci || !validarFoto) {
            if (fecha_naci) {
                model.addAttribute("msg7", "Solo pueden registrarse mayores de edad");
            }
            if (!contra2.equals(adminRest.getContrasenia())) {
                model.addAttribute("msg", "Las contraseñas no coinciden");
            }
            return "/AdminRestaurante/registroAR";
        } else if (validarFoto) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(adminRest.getContrasenia());
            adminRest.setContrasenia(hashedPassword);
            try {
                adminRest.setFoto(file.getBytes());
                adminRest.setFotonombre(fileName);
                adminRest.setFotocontenttype(file.getContentType());
                adminRestRepository.save(adminRest);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("mensajefoto", "Ocurrió un error al subir el archivo");
                return "/AdminRestaurante/registroAR";
            }
            return "redirect:/login";
        } else {
            return "/AdminRestaurante/registroAR";
        }
}

    @PostMapping("/guardarRestaurante")
    public String guardarRestaurante(@ModelAttribute("restaurante") @Valid Restaurante restaurante,
                                     BindingResult bindingResult, HttpSession session, Model model, @RequestParam("photo") MultipartFile file) {
        String fileName = "";
        model.addAttribute("listaDistritos", distritosRepository.findAll());
        model.addAttribute("listaCategorias", categoriasRestauranteRepository.findAll());

        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        restaurante.setFecharegistro(hourdateFormat.format(date));

        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        restaurante.setAdministrador(adminRest);
        System.out.println("SOY EL ID DEL ADMI" + adminRest.getDni());
        System.out.println("SOY EL ID DEL ADMI" + adminRest.getDni());
        System.out.println("SOY EL ID DEL ADMI" + adminRest.getDni());
        restaurante.setEstado(2);
        List<Categorias> listaCategorias = restaurante.getCategoriasRestaurante();
        Distrito distrito = restaurante.getDistrito();

        boolean dist_u_val = true;
        try {
            Integer id_distrito = distrito.getIddistrito();
            int dist_c = distritosRepository.findAll().size();
            for (int i = 1; i <= dist_c; i++) {
                if (id_distrito == i) {
                    dist_u_val = false;
                }
            }
        } catch (NullPointerException n) {
            dist_u_val = true;
        }

        boolean validarFoto = true;

        if (file!=null){
            System.out.println("No soy nul 1111111111111111111111111111111111111111111");
            System.out.println(file);
            if(file.isEmpty()){
                model.addAttribute("mensajefoto", "Debe subir una imagen");
                validarFoto = false;
            }else if (!file.getContentType().contains("jpeg") && !file.getContentType().contains("png") && !file.getContentType().contains("web")) {
                System.out.println("FILE NULL---- HECTOR CTM5");
                model.addAttribute("mensajefoto", "Ingrese un formato de imagen válido (p.e. JPEG,PNG o WEBP)");
                validarFoto = false;
            }
            fileName = file.getOriginalFilename();
            if (fileName.contains("..")){
                model.addAttribute("mensajefoto","No se premite '..' een el archivo");
                return "/AdminRestaurante/registroResturante";
            }
        }


        if (bindingResult.hasErrors() || listaCategorias.size() != 4 || file == null || dist_u_val||!validarFoto) {

            if (dist_u_val) {
                model.addAttribute("msg3", "Seleccione una de las opciones");
                model.addAttribute("msg5", "Complete sus datos");
            }
            model.addAttribute("listaDistritos", distritosRepository.findAll());
            model.addAttribute("listaCategorias", categoriasRestauranteRepository.findAll());
            if (listaCategorias.size() != 4) {
                model.addAttribute("msg", "Se deben seleccionar 4 categorías");
            }
            return "/AdminRestaurante/registroResturante";
        } else if (validarFoto){
            try {
                restaurante.setFoto(file.getBytes());
                restaurante.setFotonombre(fileName);
                restaurante.setFotocontenttype(file.getContentType());
                restauranteRepository.save(restaurante);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("mensajeFoto", "Ocurrió un error al subir el archivo");
                model.addAttribute("listaDistritos", distritosRepository.findAll());
                model.addAttribute("listaCategorias", categoriasRestauranteRepository.findAll());
                session.invalidate();
                return "/AdminRestaurante/registroResturante";
            }
            return "redirect:/plato/";
        }
        else {
            return "/AdminRestaurante/registroResturante";
        }
    }

    @GetMapping("/registroRest")
    public String registrarRestaurante(@ModelAttribute("restaurante") Restaurante restaurante, Model model) {
        model.addAttribute("listaDistritos", distritosRepository.findAll());
        model.addAttribute("listaCategorias", categoriasRestauranteRepository.findAll());
        return "AdminRestaurante/registroResturante";
    }

    @GetMapping("/paginabienvenida")
    public String paginaBienvenida(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        System.out.println(usuario.getNombres());
        int id = usuario.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        int estado=-1;
        try {
            estado=restaurante.getEstado();
        }catch (NullPointerException e){

        }

        model.addAttribute("estadoRestaurante",estado);
        model.addAttribute("listaDistritos", distritosRepository.findAll());
        model.addAttribute("listaCategorias", categoriasRestauranteRepository.findAll());
        return "AdminRestaurante/adminCreado";
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
    public String listaPedidos(Model model, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        return findPaginated("", 0, 0, 1, restaurante.getIdrestaurante(), model, session);
    }

    @GetMapping("/page")
    public String findPaginated(@ModelAttribute @RequestParam(value = "textBuscador", required = false) String textBuscador,
                                @ModelAttribute @RequestParam(value = "textEstado", required = false) Integer inputEstado,
                                @ModelAttribute @RequestParam(value = "textPrecio", required = false) Integer inputPrecio,
                                @RequestParam(value = "pageNo", required = false) Integer pageNo,
                                @RequestParam(value = "idrestaurante", required = false) Integer idrestaurante, Model model, HttpSession session) {

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
        if (inputEstado == null) {
            inputEstado = 0;
        }
        int inputEstadoMin;
        int inputEstadoMax;
        if (inputEstado == 0) {
            inputEstadoMin = 0;
            inputEstadoMax = 8;
        } else {
            inputEstadoMin = inputEstado - 1;
            inputEstadoMax = inputEstado - 1;
        }

        System.out.println(inputPrecio);
        if (inputPrecio == null) {
            inputPrecio = 0;
        }
        int inputPMax;
        int inputPMin;
        if (inputPrecio == 0) {
            inputPMin = 0;
            inputPMax = 100;
        } else if (inputPrecio == 4) {
            inputPMin = inputPrecio;
            inputPMax = 1000;
        } else {
            inputPMax = inputPrecio;
            inputPMin = inputPrecio;
        }
        System.out.println("#################");
        System.out.println(inputEstadoMin);
        System.out.println(inputEstadoMax);
        System.out.println("#################");
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        page = pedidoService.findPaginated(pageNo, pageSize, restaurante.getIdrestaurante(), textBuscador, inputEstadoMin, inputEstadoMax, inputPMin * 20 - 20, inputPMax * 20);
        listaPedidos = page.getContent();

        model.addAttribute("texto", textBuscador);
        model.addAttribute("textoE", inputEstado);
        model.addAttribute("textoP", inputPrecio);

        System.out.println(pageNo + "\n" + pageSize + "\n" + textBuscador + "\n" + inputEstadoMin + "\n" + inputEstadoMax + "\n" + inputPMin + "\n" + inputPMax);

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
                                Model model, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int idr = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(idr);
        Pedido pedido = pedidoRepository.pedidosXrestauranteXcodigo(restaurante.getIdrestaurante(), id);
        if (pedido != null) {
            if (pedido.getEstado() == 1) {
                pedido.setEstado(3);
                pedido.setComentrechazorest("Su pedido se está preparando");
                pedidoRepository.save(pedido);
            }
        }
        return "redirect:/restaurante/detallePedido?codigoPedido=" + id;
    }

    @GetMapping("/pedidoListo")
    public String listoPedido(@RequestParam("id") String id,
                              RedirectAttributes attr,
                              Model model, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int idr = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(idr);
        Pedido pedido = pedidoRepository.pedidosXrestauranteXcodigo(restaurante.getIdrestaurante(), id);
        if (pedido != null) {
            if (pedido.getEstado() == 3) {
                pedido.setEstado(4);
                pedido.setComentrechazorest("Su pedido terminó de prepararse, estamos buscando repartidor.");
                pedidoRepository.save(pedido);
            }
        }
        return "redirect:/restaurante/detallePedido?codigoPedido=" + id;
    }


    @GetMapping("/detallePedido")
    public String detalleDelPedido(Model model, HttpSession session, @RequestParam(value = "codigoPedido", required = false) String codigoPedido,
                                   @RequestParam(value = "v",required = false) String v) {
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
        System.out.println("##############V= "+v);
        model.addAttribute("detalles", detallesPedido);
        model.addAttribute("platos", listaPlatos);
        model.addAttribute("extras", listaExtras);
        model.addAttribute("sumaPlato", sumatotalPlato);
        model.addAttribute("sumaExtra", sumatotalExtra);
        model.addAttribute("v",v);
        return "AdminRestaurante/detallePedido";
    }

    @GetMapping("/reporteVentas")
    public String listaVentas(Model model, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        List<String> lista_codigos = pedidoRepository.listarPedidosXestadoXrestaurante(restaurante.getIdrestaurante(), 6);
        List<PedidoReporteDTO> lista = new ArrayList<PedidoReporteDTO>();
        for (String codigo : lista_codigos) {
            lista.add(pedidoRepository.pedidoReporte(codigo, codigo));
        }
        List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
        model.addAttribute("listaNotiRest",listaNotificacion);
        model.addAttribute("listareporte",lista);
        return "AdminRestaurante/reporteVentas";
    }

    @GetMapping("/reporteValoracion")
    public String listaValoracion(Model model, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        String pattern = "yyyy-MM-dd";
        DateFormat df = new SimpleDateFormat(pattern);
        Date today = Calendar.getInstance().getTime();
        String todayAsString = df.format(today);

        return findPaginatedRepVal(6, todayAsString, "3000-05-21", 1, restaurante.getIdrestaurante(), model, session);
    }

    @GetMapping("/pageVal")
    public String findPaginatedRepVal(@ModelAttribute @RequestParam(value = "inputValoracion", required = false) Integer inputValoracion,
                                      @ModelAttribute @RequestParam(value = "inputFechainicio", required = false) String fechainicio,
                                      @ModelAttribute @RequestParam(value = "inputFechafin", required = false) String fechafin,
                                      @RequestParam(value = "pageNo", required = false) Integer pageNo,
                                      @RequestParam(value = "idrestaurante", required = false) Integer idrestaurante, Model model, HttpSession session) {

        if (pageNo == null || pageNo == 0) {
            pageNo = 1;
        }
        int inputID = 1;
        int pageSize = 5;
        Page<ValoracionReporteDTO> page;
        List<ValoracionReporteDTO> listaValoracionReporte;

        //Manipular input de buscadores
        System.out.println(inputValoracion);
        String inputValoracion2;
        if (inputValoracion == null || inputValoracion == 6) {
            inputValoracion2 = "";
        } else {
            inputValoracion2 = String.valueOf(inputValoracion);
        }
        LocalDate fechafin2;
        if (fechafin == null || fechafin.equals("")) {
            fechafin2 = LocalDate.parse("3000-05-21");
        }else {
            fechafin2 = LocalDate.parse(fechafin);
        }
        LocalDate fechainicio2;
        if (fechainicio == null || fechainicio.equals("")){
            fechainicio2 = LocalDate.now();
        } else {
            fechainicio2 = LocalDate.parse(fechainicio);
        }

        String fechainicio3 = fechainicio2.toString();
        System.out.println(fechainicio3);
        String fechafin3 = fechafin2.toString();
        System.out.println(fechafin3);


        System.out.println(fechainicio3);
        System.out.println(fechafin3);

        System.out.println("#################");
        System.out.println("#################");

        //Obtener lista de reportes
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        page = reporteValoracionService.findPaginated(pageNo, pageSize, restaurante.getIdrestaurante(), 6, inputValoracion2, fechainicio3, fechafin3);
        listaValoracionReporte = page.getContent();

        //Enviar atributos a la vista
        model.addAttribute("inputValoracion", inputValoracion2);

        System.out.println(pageNo + "\n" + pageSize + "\n" + inputValoracion2 + "\n" + fechainicio3 + "\n" + fechafin3);

        //Enviar lista y valores para paginación
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
        return findPaginated2("", 0, 0, 1, restaurante.getIdrestaurante(), model, session);
    }

    @GetMapping("/page2")
    public String findPaginated2(@ModelAttribute @RequestParam(value = "textBuscador", required = false) String textBuscador,
                                 @ModelAttribute @RequestParam(value = "inputCantidad", required = false) Integer inputCantidad,
                                 @ModelAttribute @RequestParam(value = "inputCategoria", required = false) Integer inputCategoria,
                                 @RequestParam(value = "pageNo", required = false) Integer pageNo,
                                 @RequestParam(value = "idrestaurante", required = false) Integer idrestaurante, Model model, HttpSession session) {

        if (pageNo == null || pageNo == 0) {
            pageNo = 1;
        }
        int inputID = 1;
        int pageSize = 5;
        Page<PlatoReporteDTO> page;
        List<PlatoReporteDTO> listaPlatoReporte;

        //Manipular input de buscadores
        System.out.println(textBuscador);
        if (textBuscador == null) {
            textBuscador = "";
        }
        System.out.println(inputCategoria);
        String inputCategoria2;
        if (inputCategoria == null || inputCategoria == 0) {
            inputCategoria2 = "";
        } else {
            inputCategoria2 = String.valueOf(inputCategoria);
        }
        System.out.println(inputCantidad);
        if (inputCantidad == null) {
            inputCantidad = 0;
        }
        int inputCantidadMax;
        int inputCantidadMin;
        if (inputCantidad == 0) {
            inputCantidadMin = 0;
            inputCantidadMax = 1000;
        } else if (inputCantidad == 4) {
            inputCantidadMin = inputCantidad;
            inputCantidadMax = 1000;
        } else {
            inputCantidadMin = inputCantidad;
            inputCantidadMax = inputCantidad;
        }

        System.out.println("#################");
        System.out.println("#################");

        //Obtener lista de reportes
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        page = reportePlatoService.findPaginated(pageNo, pageSize, restaurante.getIdrestaurante(), 6, textBuscador, inputCategoria2, inputCantidadMin * 5 - 5, inputCantidadMax * 5);
        listaPlatoReporte = page.getContent();

        //Enviar atributos a la vista
        model.addAttribute("texto", textBuscador);
        model.addAttribute("textoC", inputCategoria);
        model.addAttribute("textoCant", inputCantidad);

        List<Categorias> listaCategorias = restaurante.getCategoriasRestaurante();
        model.addAttribute("listaCategorias", listaCategorias);

        System.out.println(listaCategorias.get(2).getIdcategoria());
        System.out.println(pageNo + "\n" + pageSize + "\n" + textBuscador + "\n" + inputCategoria2 + "\n" + inputCantidad);
        System.out.println(page.getTotalElements() + "hola" + page.getTotalPages() + " ok");
        //Enviar lista y valores para paginación
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listaPlatoReportes", listaPlatoReporte);
        return "AdminRestaurante/reportePlatos";
    }

    @GetMapping("/elegirReporte")
    public String elegirReporte() {
        return "AdminRestaurante/eleccionReporte";
    }


}
