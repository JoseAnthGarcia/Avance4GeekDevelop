package com.example.demo.controller;

import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    @GetMapping("/tipoReporte")
    public String tipoReporte(){
        return "Repartidor/reportes";
    }

    @GetMapping("/listaPedidos")
    public String verListaPedidos(Model model,HttpSession session){
        Usuario repartidor = (Usuario) session.getAttribute("usuario");
        Pedido pedidoAct = pedidoRepository.findByEstadoAndRepartidor(5, repartidor);
        if(pedidoAct==null){
            Ubicacion ubicacionActual = (Ubicacion) session.getAttribute("ubicacionActual");
            List<Distrito> listaDistritos = distritosRepository.findAll();
            List<Ubicacion> direcciones = ubicacionRepository.findByUsuario(repartidor);
            List<Pedido> pedidos = pedidoRepository.findByEstadoAndUbicacion_Distrito(4, ubicacionActual.getDistrito());
            model.addAttribute("direcciones", direcciones);
            model.addAttribute("listaPedidos", pedidos);
            model.addAttribute("listaDistritos", listaDistritos);
            return "Repartidor/solicitudPedidos";
        }else{
            return "redirect:/repartidor/pedidoActual";
        }
    }

    @GetMapping("/estadisticas")
    public String estadisticas(){
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
    public String verPedidoActual(Model model,HttpSession session){
        Usuario repartidor = (Usuario) session.getAttribute("usuario");
        Pedido pedidoAct = pedidoRepository.findByEstadoAndRepartidor(5, repartidor);
        if(pedidoAct!=null){
            List<Distrito> listaDistritos = distritosRepository.findAll();
            model.addAttribute("listaDistritos", listaDistritos);
            model.addAttribute("pedidoAct", pedidoAct);
            List<Ubicacion> direcciones = ubicacionRepository.findByUsuario(repartidor);
            model.addAttribute("direcciones", direcciones);
            return "Repartidor/pedidoActual";
        }else{
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
        return "redirect:/repartidor/aceptarPedido";
    }

    @GetMapping("/pedidoEntegado")
    public String pedidoEntregado(@RequestParam(value = "codigo", required = false) String codigo,
                                  HttpSession session){
        if(codigo!=null){
            Optional<Pedido> pedidoOpt = pedidoRepository.findById(codigo);
            Usuario repartidor = (Usuario) session.getAttribute("usuario");
            if(pedidoOpt.isPresent() &&
                    pedidoRepository.findByEstadoAndRepartidor(5, repartidor).getCodigo().equals(pedidoOpt.get().getCodigo())){
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

    @GetMapping("/registroRepartidor")
    public String registroRepartidor(Model model, @ModelAttribute("usuario") Usuario usuario) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("movilidad", new Movilidad());
        model.addAttribute("distritosSeleccionados", new ArrayList<>());

        model.addAttribute("listatipoMovilidad", tipoMovilidadRepository.findAll());
        model.addAttribute("listaDistritos", distritosRepository.findAll());


        return "/Repartidor/registro";
    }

    @GetMapping("/reporteDeliverys")
    public String reporteDeliverys(){
        return "/Repartidor/reporteDeliverys";
    }
    @GetMapping("/reporteIngresos")
    public String reporteIngresos(){
        return "/Repartidor/reporteIngresos";
    }



    @PostMapping("/guardarRepartidor")
    public String guardarRepartidor(@ModelAttribute("usuario") @Valid Usuario usuario,
                                    BindingResult bindingResult,
                                    Movilidad movilidad, Model model,
                                    @RequestParam("contrasenia2") String contrasenia2,
                                    @RequestParam("distritos") ArrayList<Distrito> distritos) {
        String dni = usuario.getDni();
        String telefono = usuario.getTelefono();
        String correo =  usuario.getCorreo();
        Usuario usuario1 =usuarioRepository.findByDni(dni);
        Usuario usuario2 =usuarioRepository.findByTelefono(telefono);
        Usuario usuario3 =usuarioRepository.findByCorreo(correo);
        Boolean errorMov = false;
        Boolean errorDist=false;
        Boolean errorSexo= false;

        if (movilidad.getTipoMovilidad().getIdtipomovilidad() == 3 && (!movilidad.getLicencia().equals("") || !movilidad.getPlaca().equals(""))) {
            errorMov= true;
        }
        if(movilidad.getTipoMovilidad().getIdtipomovilidad() != 3 && (movilidad.getLicencia().equals("")||movilidad.getPlaca().equals(""))){
            errorMov= true;
        }
        if(distritos.size()>5 || distritos.isEmpty()){
            errorDist=true;
        }

        Boolean errorFecha = true;
        try {
            String[] parts = usuario.getFechanacimiento().split("-");
            System.out.println(parts[0]+"Año");
            int naci = Integer.parseInt(parts[0]);
            Calendar fecha = new GregorianCalendar();
            int anio = fecha.get(Calendar.YEAR);
            if (anio - naci >18) {
                errorFecha = false;
            }
        } catch (NumberFormatException n) {
            errorFecha = true;
        }
        if(usuario.getSexo().equals("") || (!usuario.getSexo().equals("Femenino") && !usuario.getSexo().equals("Masculino"))){
            errorSexo=true;
        }
        if(bindingResult.hasErrors() || !contrasenia2.equals(usuario.getContrasenia()) || usuario1!= null || usuario2!= null|| usuario3!= null  || errorMov ||
                errorDist || errorFecha || errorSexo){
            if(!contrasenia2.equals(usuario.getContrasenia())){
                model.addAttribute("msg", "Las contraseñas no coinciden");
            }
            if(usuario1!=null){
                model.addAttribute("msg2", "El DNI ingresado ya se encuentra en la base de datos");
            }
            if(usuario2!=null){
                model.addAttribute("msg3", "El telefono ingresado ya se encuentra en la base de datos");
            }
            if(usuario3!=null){
                model.addAttribute("msg4", "El correo ingresado ya se encuentra en la base de datos");
            }
            if(errorDist){
                model.addAttribute("msg5", "Debe escoger entre 1 y 5 distritos");
            }
            if(errorMov){
                model.addAttribute("msg6", "Si eligió bicicleta como medio de transporte, no puede ingresar placa ni licencia. En caso contrario, dichos campos son obligatorios.");
            }
            if(errorFecha){
                model.addAttribute("msg7", "Debe ser mayor de edad para poder registrarse");
            }
            if(errorSexo){
                model.addAttribute("msg8", "Seleccione una opción");
            }

            model.addAttribute("usuario", usuario);
            model.addAttribute("movilidad", movilidad);
            model.addAttribute("distritosSeleccionados", distritos);

            model.addAttribute("listatipoMovilidad", tipoMovilidadRepository.findAll());
            model.addAttribute("listaDistritos", distritosRepository.findAll());
            return "/Repartidor/registro";
        }else {


            //se agrega rol:
            usuario.setRol(rolRepository.findById(4).get());
            //
            if (movilidad.getTipoMovilidad().getIdtipomovilidad() == 6) {
                movilidad.setLicencia(null);
                movilidad.setPlaca(null);
            }
            movilidad = movilidadRepository.save(movilidad);
            usuario.setMovilidad(movilidad);

            //OBS: ------
            usuario.setEstado(2);

            //Fecha de registro:
            Date date = new Date();
            DateFormat hourdateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            usuario.setFecharegistro(hourdateFormat.format(date));
            //

            //--------
            usuario = usuarioRepository.save(usuario);

            for (Distrito distrito : distritos) {
                Ubicacion ubicacion = new Ubicacion();
                ubicacion.setUsuario(usuario);
                ubicacion.setDistrito(distrito);
                ubicacionRepository.save(ubicacion);
            }
            return "redirect:/repartidor/registroRepartidor";
        }


    }

    /*
    @PostMapping("/guardarRepartidor")
    public String guardarRepartidor(@ModelAttribute("usuario") Usuario usuario, BindingResult bindingResult,
                                    RedirectAttributes attr, Model model,
                                    @RequestAttribute("contrasenia1") String contrasenia1, @RequestAttribute("contrasenia2") String contrasenia2) {

        if (bindingResult.hasErrors()) {


            model.addAttribute("listatipoMovilidad", tipoMovilidadRepository.findAll());
            model.addAttribute("listaDistritos", distritosRepository.findAll());

            return "Repartidor/registro";
        }else if (contrasenia1!=contrasenia2){
            attr.addFlashAttribute("msg", "Las contraseñas no coinciden");
            model.addAttribute("listatipoMovilidad", tipoMovilidadRepository.findAll());
            model.addAttribute("listaDistritos", distritosRepository.findAll());

            return "Repartidor/registro";

        }else {

            usuarioRepository.save(usuario);
            return "redirect:/x";
        }*/

}
