package com.example.demo.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;

import com.example.demo.dtos.NotifiRestDTO;
import com.example.demo.dtos.ValidarDniDTO;
import com.example.demo.entities.*;
import com.example.demo.oauth.CustomOAuth2User;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
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
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller

public class LoginController {

    //todo change in presentation public String ip = "54.175.37.128.nip.io";
    public String ip = "107.20.88.235.nip.io";
    public String puerto = "8080";

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


    @Autowired
    TipoMovilidadRepository tipoMovilidadRepository;

    @Autowired
    MovilidadRepository movilidadRepository;

    @Autowired
    ValidarCorreoRepository validarCorreoRepository;

    @GetMapping("/login")
    public String loginForm(Model model, HttpSession httpSession) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            if (httpSession.getAttribute("noExisteCuentaGoogle") != null) {
                model.addAttribute("noExisteCuentaGoogle", true);
                httpSession.removeAttribute("noExisteCuentaGoogle");
            }
            return "Cliente/login";
        } else {
            String rol = "";
            for (GrantedAuthority role : authentication.getAuthorities()) {
                rol = role.getAuthority();
                break;
            }
            switch (rol) {
                case "cliente":

                    return "redirect:/cliente/listaRestaurantes";
                case "administradorG":
                    return "redirect:/admin/usuarios";
                case "administrador":
                    return "redirect:/admin/usuarios";
                case "administradorR":
                    return "redirect:/paginabienvenida";

                case "repartidor":

                    return "redirect:/repartidor/listaPedidos";

                default:
                    return "somewhere"; //no tener en cuenta
            }
        }

    }


    @GetMapping("/accessDenied")
    public String acces() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String rol = "";
        for (GrantedAuthority role : authentication.getAuthorities()) {
            rol = role.getAuthority();
            break;
        }
        switch (rol) {
            case "cliente":

                return "redirect:/cliente/listaRestaurantes";
            case "administradorG":
                return "redirect:/admin/usuarios";
            case "administrador":
                return "redirect:/admin/usuarios";
            case "administradorR":

                return "redirect:/paginabienvenida";

            case "repartidor":

                return "redirect:/repartidor/listaPedidos";

            default:
                return "somewhere"; //no tener en cuenta
        }


    }

    //Redirect HttpServletRequest req
    @GetMapping(value = "/redirectByRole")
    public String redirectByRole(Authentication auth, HttpSession session, HttpServletRequest httpServletRequest) {

        String rol = "";

        // TODO: 10/07/2021 Actualizacion de ultima fecha de ingreso
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //System.out.println("yyyy/MM/dd HH:mm:ss-> "+dtf.format(LocalDateTime.now()));

        for (GrantedAuthority role : auth.getAuthorities()) {
            rol = role.getAuthority();
            break;
        }

        String correo = auth.getName();
        Usuario usuario = null;
        if (rol.equals("ROLE_USER")) {
            //TODO: PODER FINDBYCORREOANDVALIDCORREO
            usuario = clienteRepository.findByCorreoAndEstado(correo, 1);
            if (usuario == null) {
                try {
                    httpServletRequest.logout();
                    session = httpServletRequest.getSession();
                    session.setAttribute("noExisteCuentaGoogle", true);
                } catch (Exception e) {
                }
            } else {
                rol = usuario.getRol().getTipo();

                //ELIMINO LA CREDENCIAL
                try {
                    httpServletRequest.logout();
                    session = httpServletRequest.getSession();
                } catch (Exception e) {
                }

                Set<GrantedAuthority> authorities = new HashSet<>();
                authorities.add(new SimpleGrantedAuthority(rol));
                Authentication reAuth = new UsernamePasswordAuthenticationToken("user", new
                        BCryptPasswordEncoder().encode("password"), authorities);
                SecurityContextHolder.getContext().setAuthentication(reAuth);
                // TODO: 10/07/2021 Actualizacion de ultima fecha de ingreso
                usuario.setUltimoingreso(dtf.format(LocalDateTime.now()));
                usuarioRepository.save(usuario);
                session.setAttribute("usuario", usuario);
            }
        } else {
            usuario = usuarioRepository.findByCorreo(correo);

            // TODO: 10/07/2021 Actualizacion de ultima fecha de ingreso
            usuario.setUltimoingreso(dtf.format(LocalDateTime.now()));
            usuarioRepository.save(usuario);
            session.setAttribute("usuario", usuario);

        }

        //redirect:
        switch (rol) {
            case "cliente":
                List<Ubicacion> listaDirecciones = ubicacionRepository.findByUsuarioVal(usuario);
                Distrito distritoActual = distritosRepository.findByUsuarioAndDireccion(usuario.getIdusuario(), usuario.getDireccionactual());
                session.setAttribute("poolDirecciones", listaDirecciones);
                session.setAttribute("distritoActual", distritoActual);

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
                if (restaurante == null || restaurante.getEstado() == 2 || restaurante.getEstado() == 3) {
                    return "redirect:/paginabienvenida";
                    //TODO: ojo ver restaurante
                    //return "redirect:/restaurante/paginabienvenida";
                } else if (restaurante.getEstado() == 1) {
                    return "redirect:/plato/";
                }
            case "repartidor":
                List<Ubicacion> listaDirecciones1 = ubicacionRepository.findByUsuarioVal(usuario);
                session.setAttribute("poolDirecciones", listaDirecciones1);
                session.setAttribute("ubicacionActual", listaDirecciones1.get(0));
                List<Pedido> pedidoAct = pedidoRepository.findByEstadoAndRepartidor(5, usuario);

                if (pedidoAct.size() == 0) {
                    return "redirect:/repartidor/listaPedidos";
                } else {
                    return "redirect:/repartidor/pedidoActual";
                }
            default:
                return "redirect:/login"; //no tener en cuenta
        }
    }

    public void login(HttpServletRequest req, String user, String pass) {

        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        authorities.add(new SimpleGrantedAuthority("ADMIN"));

        Authentication reAuth = new UsernamePasswordAuthenticationToken("user", new

                BCryptPasswordEncoder().encode("password"), authorities);

        SecurityContextHolder.getContext().setAuthentication(reAuth);
    }

    @GetMapping("/redirectByRolGoogle")
    public String redirectByRolGoogle(Authentication auth, HttpSession session, HttpServletRequest httpServletRequest) {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) auth.getPrincipal();

        // TODO: 10/07/2021 Actualizacion de ultima fecha de ingreso
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String correo = oAuth2User.getEmail();
        Usuario usuario = usuarioRepository.findByCorreo(correo);

        if (usuario == null) {
            try {
                httpServletRequest.logout();
            } catch (Exception e) {

            }
            return "redirect:/login";
        } else {
            String rol = usuario.getRol().getTipo();
            // TODO: 10/07/2021 Actualizacion de ultima fecha de ingreso
            usuario.setUltimoingreso(dtf.format(LocalDateTime.now()));
            usuarioRepository.save(usuario);
            session.setAttribute("usuario", usuario);


            //redirect:
            switch (rol) {
                case "cliente":
                    List<Ubicacion> listaDirecciones = ubicacionRepository.findByUsuarioVal(usuario);
                    Distrito distritoActual = distritosRepository.findByUsuarioAndDireccion(usuario.getIdusuario(), usuario.getDireccionactual());
                    session.setAttribute("poolDirecciones", listaDirecciones);
                    session.setAttribute("distritoActual", distritoActual);
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
                    if (restaurante == null || restaurante.getEstado() == 2) {
                        return "redirect:/paginabienvenida";
                        //TODO: ojo ver restaurante
                        //return "redirect:/restaurante/paginabienvenida";
                    } else if (restaurante.getEstado() == 1) {
                        return "redirect:/plato/";
                    }
                case "repartidor":
                    List<Ubicacion> listaDirecciones1 = ubicacionRepository.findByUsuarioVal(usuario);
                    session.setAttribute("poolDirecciones", listaDirecciones1);
                    session.setAttribute("ubicacionActual", listaDirecciones1.get(0));
                    List<Pedido> pedidoAct = pedidoRepository.findByEstadoAndRepartidor(5, usuario);

                    if (pedidoAct.size() == 0) {
                        return "redirect:/repartidor/listaPedidos";
                    } else {
                        return "redirect:/repartidor/pedidoActual";
                    }
                default:
                    return "somewhere"; //no tener en cuenta
            }
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
                                 @RequestParam("photo") MultipartFile file,
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

        //VALIDACIÓN DE FOTOS
        Boolean validarFoto = true;
        String fileName = "";
        if (file != null) {
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
                model.addAttribute("mensajefoto", "No se permite '..' een el archivo");
                return "Cliente/registro";
            }
        }

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

        UsuarioDao ud = new UsuarioDao();
        ValidarDniDTO udto = ud.validarDni(cliente.getDni());
        boolean dni_val = true;
        boolean usuario_val = true;
        boolean usuario_null = true;

        boolean apellido_val = true;
        boolean nombre_val = true;

        System.out.println(udto.getApellido_paterno() + " " + udto.getApellido_materno());
        System.out.println(cliente.getApellidos());
        System.out.println(cleanString(cliente.getApellidos()));

        if (udto.getSuccess().equals("true")) {
            if (cliente.getDni().equals(udto.getRuc())) {
                dni_val = false;
                // se uso contains para validar 3 nombres
                if (udto.getApellido_materno() != null && udto.getApellido_paterno() != null && udto.getNombres() != null) {
                    usuario_null = false;
                    if ((cleanString(cliente.getNombres()) + " " + cleanString(cliente.getApellidos())).equalsIgnoreCase(cleanString(udto.getNombres()) + " " + cleanString(udto.getApellido_paterno()) + " " + cleanString(udto.getApellido_materno()))) {
                        usuario_val = false;
                        nombre_val = false;
                        apellido_val = false;
                    } else {
                        if (cleanString(udto.getNombres()).toUpperCase().contains(cleanString(cliente.getNombres().toUpperCase()))) {
                            usuario_val = false;
                            nombre_val = false;
                        }
                        if (cleanString(cliente.getApellidos()).equalsIgnoreCase(cleanString(udto.getApellido_paterno())) ||
                                cleanString(cliente.getApellidos()).equalsIgnoreCase(cleanString(udto.getApellido_materno())) ||
                                cleanString(cliente.getApellidos()).equalsIgnoreCase((cleanString(udto.getApellido_paterno()) + " " + cleanString(udto.getApellido_materno())))) {
                            usuario_val = false;
                            apellido_val = false;
                        }
                    }
                }
            }
        } else {
            System.out.println("No encontro nada, sea xq no había nadie o xq ingreso cualquier ocsa");
        }

        if (bindingResult.hasErrors() || !contrasenia2.equals(cliente.getContrasenia()) || usuario_direccion || dist_u_val || fecha_naci
                || dni_val || usuario_val || usuario_null || apellido_val || nombre_val || !validarFoto) {

            //----------------------------------------

            if (dni_val) {
                model.addAttribute("msg8", "El DNI ingresado no es válido");
            }
            if (usuario_null) {
                model.addAttribute("msg10", "No hay persona registrado para este DNI");
            }
            if (usuario_val) {
                model.addAttribute("msg9", "El usuario no coincide con el propietario del DNI");
            }
            if (nombre_val) {
                model.addAttribute("msg11", "El nombre del usuario no coincide con el propietario del DNI");
            }
            if (apellido_val) {
                model.addAttribute("msg12", "El apellido del usuario no coincide con el propietario del DNI");
            }

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
            //

            try {
                cliente.setFoto(file.getBytes());
                cliente.setFotonombre(fileName);
                cliente.setFotocontenttype(file.getContentType());

            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("mensajefoto", "Ocurrió un error al subir el archivo");
                return "Cliente/registro";
            }
            //
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
    public String envioCorreo(@RequestParam("correo") String correo,
                              Model model, RedirectAttributes redAt) {

        boolean valcorreo = false;
        Usuario clientesxcorreo = clienteRepository.findByCorreoAndEstado(correo, 1);
        if (clientesxcorreo == null) {
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
                model.addAttribute("msg1", "El correo no está registrado o la cuenta no está activa.");
            }
            if (valVacio) {
                model.addAttribute("msg2", "Ingrese su correo.");
            }

            return "olvidoContrasenia";

        } else {
            if (clientesxcorreo.getRol().getIdrol() == 1 || clientesxcorreo.getRol().getIdrol() == 3
                    || clientesxcorreo.getRol().getIdrol() == 4 || clientesxcorreo.getRol().getIdrol() == 5) {
                Usuario cliente = usuarioRepository.findByCorreoAndRol(correo, clientesxcorreo.getRol());
                if (cliente != null) {
                    Validarcorreo validarcorreo = validarCorreoRepository.findByUsuario(cliente);
                    if (validarcorreo != null) {
                        validarCorreoRepository.delete(validarcorreo);
                    }
                    String codigoAleatorio = "";
                    while (true) {
                        codigoAleatorio = cipherPassword(generarCodigAleatorio());
                        Validarcorreo validarcorreo1 = validarCorreoRepository.findByHash(codigoAleatorio);
                        if (validarcorreo1 == null) {
                            break;
                        }
                    }

                    Validarcorreo validarcorreo2 = new Validarcorreo();
                    validarcorreo2.setUsuario(cliente);
                    validarcorreo2.setHash(codigoAleatorio);

                    //genero fecha:
                    Date date = new Date();
                    DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    validarcorreo2.setFecha(hourdateFormat.format(date));
                    validarCorreoRepository.save(validarcorreo2);

                    //genero url:
                    String url = "http://" + ip + ":" + puerto + "/foodDelivery/cambioContra?correo=" +
                            cliente.getCorreo() + "&id=" + codigoAleatorio;
                    String content = "Hemos recibido una solicitud de cambio de contraseña.\n"
                            + "Para cambiar su contraseña ingrese al siguiene enlace:\n" + url
                            + "\nEnlace valido por 10 min.\n(Si no ha solicitado el cambio de contraseña omita este correo)";
                    String subject = "OLVIDO DE CONTRASEÑA";
                    sendEmail(correo, subject, content);
                    redAt.addFlashAttribute("correoEnviado", true);
                }
            }
            return "redirect:/login";
        }


    }

    @GetMapping("/cambioContra")
    public String cambiarContra(@RequestParam(value = "id", required = false) String id,
                                @RequestParam(value = "correo", required = false) String correo,
                                RedirectAttributes redAt,Model model) {
        if (id == null || correo == null) {
            return "redirect:/login";
        } else {
            Validarcorreo validarcorreo = validarCorreoRepository.findByUsuario_CorreoAndHash(correo, id);
            if (validarcorreo != null) {
                String fecha = validarcorreo.getFecha();

                LocalDateTime dateTime = LocalDateTime.now();
                dateTime = dateTime.minusMinutes(10);
                String fechaComp = dateTime.toString().subSequence(0, 10) + " " + dateTime.toString().subSequence(11, 19);
                System.out.println(fechaComp);

                model.addAttribute("correo", correo);

                SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat date2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                boolean noValido = false;
                try {
                    Date fechaCompDate = date.parse(fechaComp);
                    Date fechaDB = date2.parse(fecha);
                    if (fechaCompDate.after(fechaDB)) {
                        noValido = true;
                    }
                } catch (ParseException e) {

                }

                if(noValido){
                    validarCorreoRepository.delete(validarcorreo);
                    redAt.addFlashAttribute("urlInvalido", true);
                    return "redirect:/login";
                }else{
                    model.addAttribute("id", id);
                    return "recuperarContra";
                }
            } else {
                return "redirect:/login";
            }
        }
    }

    @PostMapping("/actualizarContraOlvidada")
    public String actualizarContraOlvidada(@RequestParam("id") String id,
                                           @RequestParam("contra1") String contra1,
                                           @RequestParam("contra2") String contra2,
                                           Model model, RedirectAttributes redAt) {

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
            Validarcorreo validarcorreo = validarCorreoRepository.findByHash(id);

            String fecha = validarcorreo.getFecha();

            LocalDateTime dateTime = LocalDateTime.now();
            dateTime = dateTime.minusMinutes(10);
            String fechaComp = dateTime.toString().subSequence(0, 10) + " " + dateTime.toString().subSequence(11, 19);
            System.out.println(fechaComp);

            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat date2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            boolean noValido = false;
            try {
                Date fechaCompDate = date.parse(fechaComp);
                Date fechaDB = date2.parse(fecha);
                if (fechaCompDate.after(fechaDB)) {
                    noValido = true;
                }
            } catch (ParseException e) {

            }

            if(noValido){
                redAt.addFlashAttribute("urlInvalido", true);
            }else{
                Usuario usuario = validarcorreo.getUsuario();
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String nuevaContra = passwordEncoder.encode(contra1);
                usuario.setContrasenia(nuevaContra);
                usuarioRepository.save(usuario);
                redAt.addFlashAttribute("contraseniaAct", true);
            }
            validarCorreoRepository.delete(validarcorreo);
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
        String emailContent = templateEngine.process("Correo/clienteREgistrado", context);
        helper.setText(emailContent, true);
        javaMailSender.send(message);
    }

    /****   RECUPERAR CONTRASEÑA ***/
    @GetMapping("/recuperarContrasenia")
    public String recuperar() {
        return "olvidoContrasenia";
    }


    /**
     * ESTO ES DE RESTAURANTE LOGIN
     **/
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
            if (anio - naci >= 18) {
                fecha_naci = false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Error capturado");
        }
        if (file != null) {
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
                model.addAttribute("mensajefoto", "No se permite '..' een el archivo");
                return "AdminRestaurante/registroAR";
            }
        }

        UsuarioDao ud = new UsuarioDao();
        ValidarDniDTO udto = ud.validarDni(adminRest.getDni());
        boolean dni_val = true;
        boolean usuario_val = true;
        boolean usuario_null = true;

        boolean apellido_val = true;
        boolean nombre_val = true;

        if (udto.getSuccess().equals("true")) {
            if (adminRest.getDni().equals(udto.getRuc())) {
                dni_val = false;
                // se uso contains para validar 3 nombres
                if (udto.getApellido_materno() != null && udto.getApellido_paterno() != null && udto.getNombres() != null) {
                    usuario_null = false;
                    if ((cleanString(adminRest.getNombres()) + " " + cleanString(adminRest.getApellidos())).equalsIgnoreCase(cleanString(udto.getNombres()) + " " + cleanString(udto.getApellido_paterno()) + " " + cleanString(udto.getApellido_materno()))) {
                        usuario_val = false;
                        nombre_val = false;
                        apellido_val = false;
                    } else {
                        if (cleanString(udto.getNombres().toUpperCase()).contains(cleanString(adminRest.getNombres().toUpperCase()))) {
                            usuario_val = false;
                            nombre_val = false;
                        }
                        if (cleanString(adminRest.getApellidos()).equalsIgnoreCase(cleanString(udto.getApellido_paterno())) ||
                                cleanString(adminRest.getApellidos()).equalsIgnoreCase(cleanString(udto.getApellido_materno())) ||
                                cleanString(adminRest.getApellidos()).equalsIgnoreCase((cleanString(udto.getApellido_paterno()) + " " + cleanString(udto.getApellido_materno())))) {
                            usuario_val = false;
                            apellido_val = false;
                        }
                    }
                }
            }
        } else {
            System.out.println("No encontro nada, sea xq no había nadie o xq ingreso cualquier ocsa");
        }

        if (bindingResult.hasErrors() || !contra2.equalsIgnoreCase(adminRest.getContrasenia()) || fecha_naci || !validarFoto
                || dni_val || usuario_val || usuario_null || apellido_val || nombre_val) {
            if (dni_val) {
                model.addAttribute("msg8", "El DNI ingresado no es válido");
            }
            if (usuario_null) {
                model.addAttribute("msg10", "No hay persona registrado para este DNI");
            }
            if (usuario_val) {
                model.addAttribute("msg9", "El usuario no coincide con el propietario del DNI");
            }
            if (nombre_val) {
                model.addAttribute("msg11", "El nombre del usuario no coincide con el propietario del DNI");
            }
            if (apellido_val) {
                model.addAttribute("msg12", "El apellido del usuario no coincide con el propietario del DNI");
            }
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

        boolean v2 = true;
        List<Restaurante> restauranteByNombre = restauranteRepository.findRestauranteByNombre(restaurante.getNombre());
        if (!restauranteByNombre.isEmpty()) {
            model.addAttribute("nombreResta", "El nombre ingresado ya se encuentra en la base de datos");
            v2 = false;
        }
        List<Restaurante> restauranteByDireccion = restauranteRepository.findRestauranteByDireccion(restaurante.getDireccion());
        if (!restauranteByDireccion.isEmpty()) {
            model.addAttribute("direccionResta", "La dirección ingresada ya se encuentra en la base de datos");
            v2 = false;
        }

        List<Restaurante> restauranteByTelefono = restauranteRepository.findRestauranteByTelefono(restaurante.getTelefono());
        if (!restauranteByTelefono.isEmpty()) {
            model.addAttribute("telefonoResta", "El telefono ingresado ya se encuentra en la base de datos");
            v2 = false;
        }

        List<Restaurante> restauranteByRuc = restauranteRepository.findRestauranteByRuc(restaurante.getRuc());
        if (!restauranteByRuc.isEmpty()) {
            model.addAttribute("rucResta", "El RUC ingresado ya se encuentra en la base de datos");
            v2 = false;
        }
        RestauranteDao rd = new RestauranteDao();
        String success = rd.validarRuc(restaurante.getRuc());

        boolean v3 = true;
        if (success.equals("false")) {
            model.addAttribute("validarApi", "El RUC ingresado no es correcto");
            v3 = false;
        }

        String fileName = "";
        model.addAttribute("listaDistritos", distritosRepository.findAll());
        model.addAttribute("listaCategorias", categoriasRestauranteRepository.findAll());

        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        restaurante.setFecharegistro(hourdateFormat.format(date));

        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        restaurante.setAdministrador(adminRest);
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
                return "AdminRestaurante/registroResturante";
            }
        }


        if (bindingResult.hasErrors() || listaCategorias.size() != 4 || file == null || dist_u_val || !validarFoto || !v2 || !v3) {

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
        if (restaurant == null) {
            model.addAttribute("listaDistritos", distritosRepository.findAll());
            model.addAttribute("listaCategorias", categoriasRestauranteRepository.findAll());
            return "AdminRestaurante/registroResturante";
        } else {
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

    ///REPARTIDOR REGISTRO

    @GetMapping("/registroRepartidor")
    public String registroRepartidor(Model model, @ModelAttribute("usuario") Usuario usuario) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("movilidad", new Movilidad());
        model.addAttribute("distritosSeleccionados", new ArrayList<>());
        model.addAttribute("listatipoMovilidad", tipoMovilidadRepository.findAll());
        model.addAttribute("listaDistritos", distritosRepository.findAll());
        return "Repartidor/registro";
    }

    @PostMapping("/guardarRepartidor")
    public String guardarRepartidor(@ModelAttribute("usuario") @Valid Usuario usuario,
                                    BindingResult bindingResult,
                                    @RequestParam("photo") MultipartFile file,
                                    Movilidad movilidad,
                                    Model model,
                                    @RequestParam("contrasenia2") String contrasenia2,
                                    @RequestParam("licencia") String licencia,
                                    @RequestParam("placa") String placa,
                                    @RequestParam(value = "distritos", required = false) ArrayList<Distrito> distritos,
                                    RedirectAttributes redAt) {
        String dni = usuario.getDni();
        String telefono = usuario.getTelefono();
        String correo = usuario.getCorreo();
        Usuario usuario1 = usuarioRepository.findByDni(dni);
        Usuario usuario2 = usuarioRepository.findByTelefono(telefono);
        Usuario usuario3 = usuarioRepository.findByCorreo(correo);

        Movilidad movilidad1;
        Movilidad movilidad2;
        if (licencia.equals("") || placa.equals("")) {
            movilidad1 = null;
            movilidad2 = null;
        } else {
            movilidad1 = movilidadRepository.findByLicencia(licencia);
            movilidad2 = movilidadRepository.findByPlaca(placa);
        }

        Boolean errorMov = false;
        Boolean errorDist = false;
        Boolean errorSexo = false;
        Boolean validarFoto = true;
        String fileName = "";
        Boolean noHayMov = false;

        if (movilidad.getTipoMovilidad() == null || (movilidad.getTipoMovilidad().getIdtipomovilidad() == 7 && (!movilidad.getLicencia().equals("") || !movilidad.getPlaca().equals("")))) {
            errorMov = true;
            if (movilidad.getTipoMovilidad() == null) {
                noHayMov = true;
            }
        }
        if (movilidad.getTipoMovilidad() == null || (movilidad.getTipoMovilidad().getIdtipomovilidad() != 7 && (movilidad.getLicencia().equals("") || movilidad.getPlaca().equals("")))) {
            errorMov = true;
            if (movilidad.getTipoMovilidad() == null) {
                noHayMov = true;
            }
        }
        Boolean errorLicencia = false;
        Boolean errorPlaca = false;

        if (movilidad.getTipoMovilidad() != null) {

            if ((movilidad.getTipoMovilidad().getIdtipomovilidad() == 5 || movilidad.getTipoMovilidad().getIdtipomovilidad() == 6) && !movilidad.getLicencia().equals("")) {
                Pattern pat = Pattern.compile("^([A-Z]{1}\\d{8})$");
                Matcher mat = pat.matcher(licencia);
                if (!mat.matches()) {
                    errorLicencia = true;
                }
            }

            if ((movilidad.getTipoMovilidad().getIdtipomovilidad() == 5 || movilidad.getTipoMovilidad().getIdtipomovilidad() == 6) && !movilidad.getPlaca().equals("")) {

                Pattern pat = Pattern.compile("^([A-Z]{3}\\d{3})$");
                Matcher mat = pat.matcher(placa);
                if (!mat.matches()) {
                    errorPlaca = true;
                }
            }
        }
        if (distritos != null) {
            if (distritos.size() > 5 || distritos.isEmpty()) {
                errorDist = true;
            }
            for (Distrito d : distritos) {
                if (d == null) {
                    errorDist = true;
                }
            }

        }
        if (distritos == null) {
            errorDist = true;
        }


        Boolean errorFecha = true;
        try {
            String[] parts = usuario.getFechanacimiento().split("-");
            System.out.println(parts[0] + "Año");
            int naci = Integer.parseInt(parts[0]);
            Calendar fecha = new GregorianCalendar();
            int anio = fecha.get(Calendar.YEAR);
            if (anio - naci > 18) {
                errorFecha = false;
            }
        } catch (NumberFormatException n) {
            errorFecha = true;
        }
        if (usuario.getSexo().equals("") || (!usuario.getSexo().equals("Femenino") && !usuario.getSexo().equals("Masculino"))) {
            errorSexo = true;
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

        if (udto.getSuccess().equals("true")) {
            if (usuario.getDni().equals(udto.getRuc())) {
                dni_val = false;
                // se uso contains para validar 3 nombres
                if (udto.getApellido_materno() != null && udto.getApellido_paterno() != null && udto.getNombres() != null) {
                    usuario_null = false;
                    if ((cleanString(usuario.getNombres()) + " " + cleanString(usuario.getApellidos())).equalsIgnoreCase(cleanString(udto.getNombres()) + " " + cleanString(udto.getApellido_paterno()) + " " + cleanString(udto.getApellido_materno()))) {
                        usuario_val = false;
                        nombre_val = false;
                        apellido_val = false;
                    } else {
                        if (cleanString(udto.getNombres().toUpperCase()).contains(cleanString(usuario.getNombres().toUpperCase()))) {
                            usuario_val = false;
                            nombre_val = false;
                        }
                        if (cleanString(usuario.getApellidos()).equalsIgnoreCase(cleanString(udto.getApellido_paterno())) ||
                                cleanString(usuario.getApellidos()).equalsIgnoreCase(cleanString(udto.getApellido_materno())) ||
                                cleanString(usuario.getApellidos()).equalsIgnoreCase((cleanString(udto.getApellido_paterno()) + " " + cleanString(udto.getApellido_materno())))) {
                            usuario_val = false;
                            apellido_val = false;
                        }
                    }
                }
            }
        } else {
            System.out.println("No encontro nada, sea xq no había nadie o xq ingreso cualquier ocsa");
        }

        if (bindingResult.hasErrors() || !contrasenia2.equals(usuario.getContrasenia()) || usuario1 != null || usuario2 != null || usuario3 != null || errorMov || movilidad1 != null || movilidad2 != null ||
                errorDist || errorFecha || errorSexo || !validarFoto || dni_val || usuario_val || usuario_null || apellido_val || nombre_val || errorPlaca || errorLicencia || noHayMov) {

            if (dni_val) {
                model.addAttribute("msg11", "El DNI ingresado no es válido");
            }
            if (usuario_null) {
                model.addAttribute("msg12", "No hay persona registrado para este DNI");
            }
            if (usuario_val) {
                model.addAttribute("msg13", "El usuario no coincide con el propietario del DNI");
            }
            if (nombre_val) {
                model.addAttribute("msg14", "El nombre del usuario no coincide con el propietario del DNI");
            }
            if (apellido_val) {
                model.addAttribute("msg15", "El apellido del usuario no coincide con el propietario del DNI");
            }
            if (!contrasenia2.equals(usuario.getContrasenia())) {
                model.addAttribute("msg", "Las contraseñas no coinciden");
            }
            if (usuario1 != null) {
                model.addAttribute("msg2", "El DNI ingresado ya se encuentra en la base de datos");
            }
            if (usuario2 != null) {
                model.addAttribute("msg3", "El telefono ingresado ya se encuentra en la base de datos");
            }
            if (usuario3 != null) {
                model.addAttribute("msg4", "El correo ingresado ya se encuentra en la base de datos");
            }
            if (errorDist) {
                model.addAttribute("msg5", "Debe escoger entre 1 y 5 distritos válidos.");
            }
            if (errorMov) {
                model.addAttribute("msg6", "Si eligió bicicleta como medio de transporte, no puede ingresar placa ni licencia. En caso contrario, dichos campos son obligatorios.");
            }
            if (errorFecha) {
                model.addAttribute("msg7", "Debe ser mayor de edad para poder registrarse");
            }
            if (errorSexo) {
                model.addAttribute("msg8", "Seleccione una opción");
            }
            if (movilidad1 != null) {
                model.addAttribute("msg9", "La licencia ingresada ya se encuentra en la base de datos");
            }
            if (movilidad2 != null) {
                model.addAttribute("msg10", "La placa ingresada ya se encuentra en la base de datos");
            }
            if (errorPlaca) {
                model.addAttribute("msg16", "Ingrese una placa en el formato correcto. Ej: AAA111, ABC123");
            }
            if (errorLicencia) {
                model.addAttribute("msg17", "Ingrese una licencia en el formato correcto. Ej: Q12345678, R23432245");
            }
            if (noHayMov) {
                model.addAttribute("msg18", "Debe seleccionar una movilidad.");
            }

            model.addAttribute("usuario", usuario);
            model.addAttribute("movilidad", movilidad);
            model.addAttribute("distritosSeleccionados", distritos);

            model.addAttribute("listatipoMovilidad", tipoMovilidadRepository.findAll());
            model.addAttribute("listaDistritos", distritosRepository.findAll());
            return "Repartidor/registro";
        } else {

            try {
                usuario.setFoto(file.getBytes());
                usuario.setFotonombre(fileName);
                usuario.setFotocontenttype(file.getContentType());
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("mensajefoto", "Ocurrió un error al subir el archivo");
                return "Repartidor/registro";
            }
            //se agrega rol:
            usuario.setRol(rolRepository.findById(4).get());
            //
            if (movilidad.getTipoMovilidad().getIdtipomovilidad() == 7) {
                movilidad.setLicencia(null);
                movilidad.setPlaca(null);
            }
            movilidad = movilidadRepository.save(movilidad);
            usuario.setMovilidad(movilidad);

            //OBS: se cambia a 2 cuando valide su correo
            usuario.setEstado(-1);

            //Fecha de registro:
            Date date = new Date();
            DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            usuario.setFecharegistro(hourdateFormat.format(date));
            //
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(usuario.getContrasenia());
            System.out.println(hashedPassword);
            usuario.setContrasenia(hashedPassword);
            //--------
            usuario = usuarioRepository.save(usuario);

            for (Distrito distrito : distritos) {
                Ubicacion ubicacion = new Ubicacion();
                ubicacion.setUsuario(usuario);
                ubicacion.setDistrito(distrito);
                ubicacionRepository.save(ubicacion);
            }
            enviarCorreoValidacion(usuario.getCorreo());
            redAt.addFlashAttribute("usuarioCreado", true);

            return "redirect:/login";
        }

    }

    @GetMapping("/validarCuenta")
    public String validarCuenta(@RequestParam(value = "correo", required = false) String correo,
                                @RequestParam(value = "value", required = false) String codigoHash,
                                Model model) {
        if (correo != null && codigoHash != null) {
            Validarcorreo validarcorreo = validarCorreoRepository.findByUsuario_CorreoAndHash(correo, codigoHash);
            if (validarcorreo != null) {
//                validarCorreoRepository.delete(validarcorreo);
                Usuario usuario = usuarioRepository.findByCorreo(correo);
                usuario.setEstado(2);
                usuarioRepository.save(usuario);
                model.addAttribute("rol", usuario.getRol().getIdrol());
                return "cuentaValidada";
            } else {
                return "redirect:/cliente/login";
            }
        } else {
            return "redirect:/cliente/login";
        }
    }

    public void enviarCorreoValidacion(String correo) {
        String codigoHash = "";
        while (true) {
            codigoHash = cipherPassword(generarCodigAleatorio());
            Validarcorreo validarcorreo = validarCorreoRepository.findByHash(codigoHash);
            if (validarcorreo == null) {
                break;
            }
        }
        Validarcorreo validarcorreo = new Validarcorreo();
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        validarcorreo.setUsuario(usuario);
        validarcorreo.setHash(codigoHash);
        validarCorreoRepository.save(validarcorreo);

        String url = "http://" + ip + ":" + puerto + "/foodDelivery/validarCuenta?correo="
                + correo + "&value=" + codigoHash;
        String content = "Su cuenta ha sido creada exitosamente." +
                "Debe validar su correo para empezar a usar su cuenta.\n"
                + "Para validar su correo electrónico ingrese al siguiente link:\n" + url;
        String subject = "Bienvenido a Food Delivery!";
        sendEmail(correo, subject, content);
    }


    public String cleanString(String texto) {
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return texto;
    }


    public String cipherPassword(String text) {
        String hashedPassword = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < encodedhash.length; i++) {
                String hex = Integer.toHexString(0xff & encodedhash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            hashedPassword = hexString.toString();
        } catch (NoSuchAlgorithmException ex) {

        }

        return hashedPassword;
    }

}



