package com.example.demo.controller;

import com.example.demo.dtos.NotifiRestDTO;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Controller

public class LoginController {
    @Autowired
    CategoriasRestauranteRepository categoriasRestauranteRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    UsuarioRepository clienteRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    DistritosRepository distritosRepository;

    @Autowired
    UbicacionRepository ubicacionRepository;
    @Autowired
    RestauranteRepository restauranteRepository;

    @Autowired
    UrlCorreoRepository urlCorreoRepository;

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    UsuarioRepository adminRestRepository;


    @GetMapping("/login")
    public String loginForm() {
        return "Cliente/login";
    }


    @GetMapping("/accessDenied")
    public String acces() {
        return "/accessDenied";
    }
    
    //Redirect HttpServletRequest req
    @GetMapping(value = "/redirectByRole")
    public String redirectByRole(Authentication auth, HttpSession session) {
        String rol = "";

        for (GrantedAuthority role : auth.getAuthorities()) {
            rol = role.getAuthority();
            break;
        }

        String correo = auth.getName();
        Usuario usuario = usuarioRepository.findByCorreo(correo);

        session.setAttribute("usuario", usuario);


        //redirect:
        switch (rol) {
            case "cliente":
                List<Ubicacion> listaDirecciones = ubicacionRepository.findByUsuarioVal(usuario);
                session.setAttribute("poolDirecciones", listaDirecciones);
                return "redirect:/cliente/listaRestaurantes";
            case "administradorG":
                return "redirect:/admin/usuarios";
            case "administrador":
                return "redirect:/admin/usuarios";
            case "administradorR":
                Restaurante restaurante = null;
                try {
                    restaurante = restauranteRepository.encontrarRest(usuario.getIdusuario());
                } catch (NullPointerException e) {
                    System.out.println("Fallo");
                }
                if (restaurante == null|| restaurante.getEstado()==2) {
                    return "redirect:/paginabienvenida";
                } else if(restaurante.getEstado()==1){
                    return "redirect:/plato/";
                }
            case "repartidor":
                List<Ubicacion> listaDirecciones1 = ubicacionRepository.findByUsuarioVal(usuario);
                session.setAttribute("poolDirecciones", listaDirecciones1);
                session.setAttribute("ubicacionActual", listaDirecciones1.get(0));
                Pedido pedidoAct = pedidoRepository.findByEstadoAndRepartidor(5, usuario);

                if(pedidoAct==null){
                    return "redirect:/repartidor/listaPedidos";
                }else{
                    return "redirect:/repartidor/pedidoActual";
                }

            default:
                return "somewhere"; //no tener en cuenta
        }
    }

    //REGISTRO CLIENTE


    @GetMapping("/ClienteNuevo")
    public String nuevoCliente(@ModelAttribute("cliente") Usuario cliente, Model model) {
        // String direccion;
        model.addAttribute("ubicacion", new Ubicacion());
        model.addAttribute("listaDistritos", distritosRepository.findAll());
        return "Cliente/registro";

    }




    @PostMapping("/ClienteGuardar")
    public String guardarCliente(@ModelAttribute("cliente") @Valid Usuario cliente, BindingResult bindingResult,
                                 @ModelAttribute("ubicacion") @Valid Ubicacion ubicacion,
                                 BindingResult bindingResult2,
                                Model model, RedirectAttributes attr, @RequestParam("contrasenia2") String contrasenia2) throws MessagingException {


        List<Usuario> clientesxcorreo = clienteRepository.findUsuarioByCorreo(cliente.getCorreo());
        if (!clientesxcorreo.isEmpty()) {
            bindingResult.rejectValue("correo", "error.Usuario", "El correo ingresado ya se encuentra en la base de datos");
        }
        List<Usuario> clientesxdni = clienteRepository.findUsuarioByDni(cliente.getDni());
        if (!clientesxdni.isEmpty()) {
            bindingResult.rejectValue("dni", "error.Usuario", "El DNI ingresado ya se encuentra en la base de datos");
        }

        List<Usuario> clientesxtelefono = clienteRepository.findUsuarioByTelefono(cliente.getTelefono());
        if (!clientesxtelefono.isEmpty()) {
            bindingResult.rejectValue("telefono", "error.Usuario", "El telefono ingresado ya se encuentra en la base de datos");
        }


        Boolean usuario_direccion = ubicacion.getDireccion().equalsIgnoreCase("") || ubicacion.getDireccion() == null;
        Boolean dist_u_val = true;



        try {
            Integer u_dist = ubicacion.getDistrito().getIddistrito();
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
        Boolean fecha_naci = true;
        try {
            String[] parts = cliente.getFechanacimiento().split("-");
            int naci = Integer.parseInt(parts[0]);
            Calendar fecha = new GregorianCalendar();
            int anio = fecha.get(Calendar.YEAR);

            if (anio - naci >= 18) {
                fecha_naci = false;
            }
        } catch (NumberFormatException n) {
        }




        if (bindingResult.hasErrors() || !contrasenia2.equals(cliente.getContrasenia()) || usuario_direccion || dist_u_val || fecha_naci) {

            //----------------------------------------




            if (usuario_direccion) {
                model.addAttribute("msg2", "Complete sus datos");
            }
            if (fecha_naci) {
                model.addAttribute("msg7", "Solo pueden registrarse mayores de edad");
            }
            if (dist_u_val) {
                model.addAttribute("msg3", "Seleccione una de las opciones");
                model.addAttribute("msg5", "Complete sus datos");
            }


            if (!contrasenia2.equals(cliente.getContrasenia())) {
                model.addAttribute("msg", "Las contraseñas no coinciden");
            }




            //   String direccion;
            model.addAttribute("Usuario_has_distrito", new Ubicacion());
            //distritos
            model.addAttribute("distritosSeleccionados", new ArrayList<>());
            //distritos -->
            model.addAttribute("listaDistritos", distritosRepository.findAll());
            model.addAttribute("direccion", ubicacion.getDireccion());


            return "Cliente/registro";
        } else {
            cliente.setEstado(1);
            cliente.setRol(rolRepository.findById(1).get());
            String fechanacimiento = LocalDate.now().toString();
            cliente.setFecharegistro(fechanacimiento);

            attr.addFlashAttribute("msg", "Cliente creado exitosamente");

            Date date = new Date();
            DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            cliente.setFecharegistro(hourdateFormat.format(date));


            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(cliente.getContrasenia());
            System.out.println(hashedPassword);
            cliente.setContrasenia(hashedPassword);

            //guardamos direccion actual:
            cliente.setDireccionactual(ubicacion.getDireccion());

            clienteRepository.save(cliente);

            ubicacion.setUsuario(cliente);


            ubicacionRepository.save(ubicacion);

            /////----------------Envio Correo--------------------/////

            sendHtmlMailREgistrado(cliente.getCorreo(), "Cliente registrado html", cliente);


            /////-----------------------------------------  ------/////


            return "redirect:/cliente/login";

        }

    }

    @RequestMapping("/olvidoContrasenia")
    public String olvidoContrasenia() {
        return "olvidoContrasenia";
    }

    @PostMapping("/enviarCorreoOlvidoContra")
    public String envioCorreo(@RequestParam("correo") String correo, Model model) {

        boolean valcorreo = false;
        Usuario clientesxcorreo = clienteRepository.findByCorreo(correo);
        if (clientesxcorreo==null) {
            valcorreo = true;
        }

        boolean valVacio = false;
        if (correo.isEmpty()) {
            System.out.println("VACIO");
            valVacio = true;
        }

        if (valVacio || valcorreo) {
            if (valcorreo) {
                System.out.println("validacion correo");
                model.addAttribute("msg1", "El correo no está registrado");
            }
            if (valVacio) {
                model.addAttribute("msg2", "Ingrese su correo");
            }

            return "olvidoContrasenia";

        } else {
            if(clientesxcorreo.getRol().getIdrol()==1 ||clientesxcorreo.getRol().getIdrol()==3||clientesxcorreo.getRol().getIdrol()==4) {
                Usuario cliente = usuarioRepository.findByCorreoAndRol(correo, clientesxcorreo.getRol());
                if (cliente != null) {
                    Urlcorreo urlcorreo1 = urlCorreoRepository.findByUsuario(cliente);
                    if (urlcorreo1 != null) {
                        urlCorreoRepository.delete(urlcorreo1);
                    }
                    String codigoAleatorio = "";
                    while (true) {
                        codigoAleatorio = generarCodigAleatorio();
                        Urlcorreo urlcorreo2 = urlCorreoRepository.findByCodigo(codigoAleatorio);
                        if (urlcorreo2 == null) {
                            break;
                        }
                    }
                    Urlcorreo urlcorreo3 = new Urlcorreo();
                    urlcorreo3.setCodigo(codigoAleatorio);
                    urlcorreo3.setUsuario(cliente);

                    //genero fecha:
                    Date date = new Date();
                    DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    urlcorreo3.setFecha(hourdateFormat.format(date));
                    urlCorreoRepository.save(urlcorreo3);

                    //genero url:
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    String urlPart = passwordEncoder.encode(cliente.getDni() + codigoAleatorio);
                    String url = "http://localhost:8080/avance6/cambioContra?id=" + urlPart;
                    String content = "Para cambio de contraseña:\n" + url;
                    String subject = "OLVIDE MI CONTRASEÑA";
                    sendEmail(correo, subject, content);
                }
            }
            return "redirect:/login";
        }


    }

    @GetMapping("/cambioContra")
    public String cambiarContra(@RequestParam("id") String id, Model model) {
        List<Urlcorreo> listaUrlCorreo = urlCorreoRepository.findAll();
        Boolean redireccionar = false;
        for (Urlcorreo urlcorreo : listaUrlCorreo) {
            String comparar = urlcorreo.getUsuario().getDni() + urlcorreo.getCodigo();
            if (BCrypt.checkpw(comparar, id)) {
                redireccionar = true;

                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                id = passwordEncoder.encode(comparar);

                model.addAttribute("id", id);
            }
        }

        if (redireccionar) {
            return "recuperarContra";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/actualizarContraOlvidada")
    public String actualizarContraOlvidada(@RequestParam("id") String id,
                                           @RequestParam("contra1") String contra1,
                                           @RequestParam("contra2") String contra2, Model model) {

        boolean valLong1 = false;
        boolean valLong2 = false;
        boolean valIguales = true;
        boolean valVacio1 = false;
        boolean valVacio2 = false;
        if (contra1.isEmpty()) {
            valVacio1 = true;
        }
        if (contra2.isEmpty()) {
            valVacio2 = true;
        }
        if (contra1.length() < 8) {
            valLong1 = true;
        }
        if (contra2.length() < 8) {
            valLong2 = true;
        }

        if (contra1.equalsIgnoreCase(contra2)) {
            valIguales = false;
        }

        if (valLong1 || valLong2 || valIguales || valVacio1 || valVacio2) {

            if (valLong1) {
                model.addAttribute("msg1", "Ingrese de 8 caracteres a mas");
            }
            if (valLong2) {
                model.addAttribute("msg2", "Ingrese de 8 caracteres a mas");
            }
            if (valIguales) {
                model.addAttribute("msg3", "Las contraseñas no coinciden");
            }
            if (valVacio1) {
                model.addAttribute("msg4", "Recuadro vacio");
            }
            if (valVacio2) {
                model.addAttribute("msg5", "Recuadro vacio");
            }
            model.addAttribute("id", id);

            return "recuperarContra";
        } else {

            List<Urlcorreo> listaUrlCorreo = urlCorreoRepository.findAll();
            for (Urlcorreo urlcorreo : listaUrlCorreo) {
                String comparar = urlcorreo.getUsuario().getDni() + urlcorreo.getCodigo();
                if (BCrypt.checkpw(comparar, id)) {
                    //TODO: MANDAR CODIGO EXPIRADO Y BORRAR SI YA ESTA EXPIRADO
                    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                    String nuevaContra = passwordEncoder.encode(contra1);
                    urlcorreo.getUsuario().setContrasenia(nuevaContra);
                    usuarioRepository.save(urlcorreo.getUsuario());
                    urlCorreoRepository.delete(urlcorreo);
                }
            }
            return "redirect:/login";
        }
    }


    //generar codigo aleatorio:
    public String generarCodigAleatorio() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        int charsLength = chars.length;
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        int tamCodigo = 5;
        for (int i = 0; i < tamCodigo; i++) {
            buffer.append(chars[random.nextInt(charsLength)]);
        }
        return buffer.toString();
    }

    //Pasamos por parametro: destinatario, asunto y el mensaje
    public void sendEmail(String to, String subject, String content) {

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(content);

        javaMailSender.send(email);
    }


    public void sendHtmlMailREgistrado(String to, String subject, Usuario usuario) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        Context context = new Context();
        context.setVariable("user", usuario.getNombres());
        context.setVariable("id", usuario.getDni());
        String emailContent = templateEngine.process("/Correo/clienteREgistrado", context);
        helper.setText(emailContent, true);
        javaMailSender.send(message);
    }

    /****   RECUPERAR CONTRASEÑA ***/
    @GetMapping("/recuperarContrasenia")
    public String recuperar() {
        return "olvidoContrasenia";
    }




    /** ESTO ES DE RESTAURANTE LOGIN**/
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
        Date date = new Date();
        DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
        String fecharegistro = LocalDate.now().toString();
        fecharegistro = fecharegistro + " " + hourFormat.format(date);
        System.out.println(fecharegistro);
        adminRest.setFecharegistro(fecharegistro);
        Boolean fecha_naci = true;
        boolean validarFoto = true;
        int naci = 0;
        String[] parts = adminRest.getFechanacimiento().split("-");
        try {
            naci = Integer.parseInt(parts[0]);
            Calendar fecha = new GregorianCalendar();
            int anio = fecha.get(Calendar.YEAR);
            System.out.println("AÑOOOOOOO " + anio);
            System.out.println("Naciiiiii " + naci);
            if (anio - naci >= 18) {
                fecha_naci = false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error capturado");
        }
        System.out.println("SOY LA FECH DE CUMPLE" + adminRest.getFechanacimiento());
        System.out.println("Soy solo fecha_naci " + fecha_naci);
        if (file != null) {
            System.out.println("No soy nul 1111111111111111111111111111111111111111111");
            System.out.println(file);
            if (file.isEmpty()) {
                model.addAttribute("mensajefoto", "Debe subir una imagen");
                validarFoto = false;
            } else if (!file.getContentType().contains("jpeg") && !file.getContentType().contains("png") && !file.getContentType().contains("web")) {
                System.out.println("FILE NULL---- HECTOR CTM5");
                model.addAttribute("mensajefoto", "Ingrese un formato de imagen válido (p.e. JPEG,PNG o WEBP)");
                validarFoto = false;
            }
            fileName = file.getOriginalFilename();
            if (fileName.contains("..")) {
                model.addAttribute("mensajefoto", "No se premite '..' een el archivo");
                return "AdminRestaurante/registroAR";
            }
        }

        if (bindingResult.hasErrors() || !contra2.equalsIgnoreCase(adminRest.getContrasenia()) || fecha_naci || !validarFoto) {
            if (fecha_naci) {
                model.addAttribute("msg7", "Solo pueden registrarse mayores de edad");
            }
            if (!contra2.equals(adminRest.getContrasenia())) {
                model.addAttribute("msg", "Las contraseñas no coinciden");
            }
            return "AdminRestaurante/registroAR";
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
                return "AdminRestaurante/registroAR";
            }
            return "AdminRestaurante/solicitudAR";
        } else {
            return "AdminRestaurante/registroAR";
        }
    }

    @PostMapping("/guardarRestaurante")
    public String guardarRestaurante(@ModelAttribute("restaurante") @Valid Restaurante restaurante,
                                     BindingResult bindingResult, HttpSession session, Model model, @RequestParam("photo") MultipartFile file) {

        List<Restaurante> restauranteByNombre = restauranteRepository.findRestauranteByNombre(restaurante.getNombre());
        if (!restauranteByNombre.isEmpty()) {
            model.addAttribute("nombreResta", "El nombre ingresado ya se encuentra en la base de datos");
        }
        List<Restaurante> restauranteByDireccion = restauranteRepository.findRestauranteByDireccion(restaurante.getDireccion());
        if (!restauranteByDireccion.isEmpty()) {
            model.addAttribute("direccionResta", "La dirección ingresada ya se encuentra en la base de datos");
        }

        List<Restaurante> restauranteByTelefono = restauranteRepository.findRestauranteByTelefono(restaurante.getTelefono());
        if (!restauranteByTelefono.isEmpty()) {
            model.addAttribute("telefonoResta", "El telefono ingresado ya se encuentra en la base de datos");
        }

        List<Restaurante> restauranteByRuc = restauranteRepository.findRestauranteByRuc(restaurante.getRuc());
        if (!restauranteByRuc.isEmpty()) {
            model.addAttribute("rucResta", "El RUC ingresado ya se encuentra en la base de datos");
        }

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

        if (file != null) {
            System.out.println("No soy nul 1111111111111111111111111111111111111111111");
            System.out.println(file);
            if (file.isEmpty()) {
                model.addAttribute("mensajefoto", "Debe subir una imagen");
                validarFoto = false;
            } else if (!file.getContentType().contains("jpeg") && !file.getContentType().contains("png") && !file.getContentType().contains("web")) {
                System.out.println("FILE NULL---- HECTOR CTM5");
                model.addAttribute("mensajefoto", "Ingrese un formato de imagen válido (p.e. JPEG,PNG o WEBP)");
                validarFoto = false;
            }
            fileName = file.getOriginalFilename();
            if (fileName.contains("..")) {
                model.addAttribute("mensajefoto", "No se premite '..' een el archivo");
                return "AdminRestaurante/registroResturante";
            }
        }


        if (bindingResult.hasErrors() || listaCategorias.size() != 4 || file == null || dist_u_val || !validarFoto) {

            if (dist_u_val) {
                model.addAttribute("msg3", "Seleccione una de las opciones");
                model.addAttribute("msg5", "Complete sus datos");
            }
            model.addAttribute("listaDistritos", distritosRepository.findAll());
            model.addAttribute("listaCategorias", categoriasRestauranteRepository.findAll());
            if (listaCategorias.size() != 4) {
                model.addAttribute("msg", "Se deben seleccionar 4 categorías");
            }
            return "AdminRestaurante/registroResturante";
        } else if (validarFoto) {
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
                return "AdminRestaurante/registroResturante";
            }
            return "redirect:/paginabienvenida";
        } else {
            return "AdminRestaurante/registroResturante";
        }
    }

    @GetMapping("/registroRest")
    public String registrarRestaurante(@ModelAttribute("restaurante") Restaurante restaurante, Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        int id = usuario.getIdusuario();
        Restaurante restaurant = restauranteRepository.encontrarRest(id);
        int estado = -1;
        try {
            estado = restaurant.getEstado();
        } catch (NullPointerException e) {

        }
        if(restaurant==null){
            model.addAttribute("listaDistritos", distritosRepository.findAll());
            model.addAttribute("listaCategorias", categoriasRestauranteRepository.findAll());
            return "AdminRestaurante/registroResturante";
        }else{
            return "redirect:/paginabienvenida";
        }

    }
    @GetMapping("/paginabienvenida")
    public String paginaBienvenida(Model model, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        System.out.println(usuario.getNombres());
        int id = usuario.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        int estado = -1;
        try {
            estado = restaurante.getEstado();
        } catch (NullPointerException e) {

        }

        model.addAttribute("estadoRestaurante", estado);
        model.addAttribute("listaDistritos", distritosRepository.findAll());
        model.addAttribute("listaCategorias", categoriasRestauranteRepository.findAll());
        return "AdminRestaurante/adminCreado";
    }

}
