package com.example.demo.controller;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/restaurante")
public class AdminRestController {
    @Autowired
    UsuarioRepository adminRestRepository;
    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    RolRepository rolRepository;
    @Autowired
    DistritosRepository distritosRepository;

    @Autowired
    RestauranteRepository restauranteRepository;

    @Autowired
    CategoriasRestauranteRepository categoriasRestauranteRepository;


    @GetMapping("/registro")
    public String nuevoAdminRest(@ModelAttribute("adminRest") Usuario adminRest, Model model) {
        model.addAttribute("adminRest", new Usuario());
        return "AdminRestaurante/registroAR";
    }


    @PostMapping("/guardarAdminR")
    public String guardarAdminRest(@ModelAttribute("adminRest") @Valid Usuario adminRest, BindingResult bindingResult,
                                   @RequestParam("confcontra") String contra2, @RequestParam("photo") MultipartFile file, Model model) {

        String fileName = "";

        System.out.println(contra2);
        System.out.println(adminRest.getContrasenia());
        //se agrega rol:
        adminRest.setRol(rolRepository.findById(3).get());
        adminRest.setEstado(2);
        String fecharegistro = LocalDate.now().toString();
        adminRest.setFecharegistro(fecharegistro);
        Boolean fecha_naci = true;
        try {
            String[] parts = adminRest.getFechanacimiento().split("-");
            int naci = Integer.parseInt(parts[0]);
            Calendar fecha = new GregorianCalendar();
            int anio = fecha.get(Calendar.YEAR);
            System.out.println("AÑOOOOOOO " + anio);
            System.out.println("Naciiiiii " + naci);
            if (anio - naci >= 18) {
                fecha_naci = false;
            }
        } catch (NumberFormatException n) {
            n.printStackTrace();
        }
        System.out.println("SOY LA FECH DE CUMPLE"+adminRest.getFechanacimiento());
        System.out.println("Soy solo fecha_naci "+fecha_naci);

        System.out.println("");
        if (file != null) {
            if (file.isEmpty()) {
                model.addAttribute("mensajefoto", "Debe subir una imagen");
            }
            fileName = file.getOriginalFilename();
            if (fileName.contains("..")) {
                model.addAttribute("mensajefoto", "No se premite '..' een el archivo");
            }
        }
        if (bindingResult.hasErrors() || !contra2.equalsIgnoreCase(adminRest.getContrasenia())||fecha_naci) {
            if (fecha_naci) {
                model.addAttribute("msg7", "Solo pueden registrarse mayores de edad");
            }
            if (!contra2.equals(adminRest.getContrasenia())) {
                model.addAttribute("msg", "Las contraseñas no coinciden");
            }
            return "/AdminRestaurante/registroAR";
        } else {
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
        Distrito distrito =restaurante.getDistrito();

        boolean dist_u_val=true;
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


        if (bindingResult.hasErrors() || listaCategorias.size() != 4 || file == null || dist_u_val) {
            if (file.isEmpty()) {
                model.addAttribute("mensajeFoto", "Debe subir una imagen");
            }
            fileName = file.getOriginalFilename();
            if (fileName.contains("..")) {
                model.addAttribute("mensajeFoto", "No se premite '..' een el archivo");
            }
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
        } else {
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
    }

    @GetMapping("/registroRest")
    public String registrarRestaurante(@ModelAttribute("restaurante") Restaurante restaurante, Model model) {
        model.addAttribute("listaDistritos", distritosRepository.findAll());
        model.addAttribute("listaCategorias", categoriasRestauranteRepository.findAll());
        return "AdminRestaurante/registroResturante";
    }

    @GetMapping("/paginabienvenida")
    public String paginaBienvenida(Model model) {
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
        List<Pedido> listaPedidos =pedidoRepository.pedidosXrestaurante(restaurante.getIdrestaurante()); ;
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
        if (pedido!=null) {
            if(pedido.getEstado()==0) {
                if(!comentarioAR.equals("")){
                    pedido.setEstado(2);
                    pedido.setComentrechazorest(comentarioAR);
                    pedidoRepository.save(pedido);
                    attr.addFlashAttribute("msg", "Pedido rechazado exitosamente");
                }else{
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

}
