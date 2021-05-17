package com.example.demo.controller;


import com.example.demo.entities.Distrito;
import com.example.demo.entities.Ubicacion;
import com.example.demo.entities.Usuario;
import org.springframework.security.crypto.bcrypt.BCrypt;

import com.example.demo.repositories.DistritosRepository;
import com.example.demo.repositories.RolRepository;
import com.example.demo.repositories.UsuarioRepository;
import com.example.demo.repositories.UbicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller

@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    UsuarioRepository clienteRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    DistritosRepository distritosRepository;

    @Autowired
    UbicacionRepository ubicacionRepository;

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
        boolean telfUnico=true;

        List<Usuario> clientesxtelefono = clienteRepository.findUsuarioByTelefonoAndIdusuarioNot(telefonoNuevo, usuario1.getIdusuario());
        if (clientesxtelefono.isEmpty() ) {
            telfUnico=false;
        }



        int telfInt;
        try{
            telfInt = Integer.parseInt(telefonoNuevo);
        }catch (NumberFormatException e){
            telfInt = -1;
        }

        if(telfInt==-1 || telefonoNuevo.trim().equals("") || telefonoNuevo.length()!=9){
            telfValid =true;
        }

        if (BCrypt.checkpw(contraseniaConf,usuario1.getContrasenia())) {
            valContra = false;
        }

        if (valContra || telfValid || telfUnico){

            if(telfUnico){
                model.addAttribute("msg1", "El telefono ingresado ya está registrado");
            }
            if(valContra){
            model.addAttribute("msg", "Contraseña incorrecta");
            }
            if(telfValid){
            model.addAttribute("msg2", "Coloque 9 dígitos si desea actualizar");
            }
            return "Cliente/editarPerfil";


        } else {
            usuario1.setTelefono(telefonoNuevo); //usar save para actualizar
            httpSession.setAttribute("usuario",usuario1); //TODO: preguntar profe
            clienteRepository.save(usuario1);
            return "redirect:/cliente/listaRestaurantes";
        }


    }

    @GetMapping("/listaRestaurantes")
    public String listaRestaurantes(){

        return "Cliente/listaRestaurantes";
    }

    @GetMapping("/listaDirecciones")
    public String listaDirecciones(Model model,HttpSession httpSession){
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

        return "Cliente/listaDirecciones";
    }


    @PostMapping("/guardarDireccion")
    public String guardarDirecciones(HttpSession httpSession, @RequestParam("direccionactual") String direccionActual){
        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        usuario.setDireccionactual(direccionActual);
        httpSession.setAttribute("usuario",usuario);
        clienteRepository.save(usuario);
        return "redirect:/cliente/listaDirecciones";
    }

    @PostMapping("/eliminarDireccion")
    public String eliminarDirecciones(@RequestParam("listaIdDireccionesAeliminar") List<String> listaIdDireccionesAeliminar){

        for(String idUbicacion : listaIdDireccionesAeliminar){
            //validad int idUbicacion:
            int idUb = Integer.parseInt(idUbicacion);
            Ubicacion ubicacion = (ubicacionRepository.findById(idUb)).get();
            ubicacionRepository.delete(ubicacion);
        }

        return "redirect:/cliente/listaDirecciones";

    }

    @PostMapping("/agregarDireccion")
    public  String registrarNewDireccion(@RequestParam("direccion") String direccion, HttpSession httpSession,Model model ){
        boolean valNul=false;
        boolean valNew=false;
        boolean valLong= false;


        if(direccion.isEmpty()){
            valNul=true;
        }

        Usuario usuario1 = (Usuario) httpSession.getAttribute("usuario");
        List<Ubicacion> listaDir = ubicacionRepository.findByUsuario(usuario1);


        for(Ubicacion u:listaDir){
            if(u.getDireccion().equalsIgnoreCase(direccion)){
                valNew=true;
            }
        }
        if(listaDir.size()>5){
            valLong=true;
        }






        if(valNul|| valNew || valLong){
            if(valNul){
                model.addAttribute("msg", "No ingreso dirección");
            }
            if(valNew){
                model.addAttribute("msg1", "La direccion ingresda ya está registrada");
            }

            if(valLong){
                model.addAttribute("msg2", "Solo puede registrar 5 tarjetas");
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

            return "Cliente/listaDirecciones";

        }else{
            Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
            List<Ubicacion> listaDirecciones = (List) httpSession.getAttribute("poolDirecciones");
            Ubicacion ubicacion = new Ubicacion();
            ubicacion.setUsuario(usuario);
            ubicacion.setDireccion(direccion);
            //TODO: @JOHAM QUE PEDOS
            Distrito distrito = distritosRepository.getOne(1);
            ubicacion.setDistrito(distrito);
            listaDirecciones.add(ubicacion);
            ubicacionRepository.save(ubicacion);
            httpSession.setAttribute("listaDirecciones",listaDirecciones);
            return "redirect:/cliente/listaDirecciones";
        }

    }




    @GetMapping("/listaCupones")
    public String listacupones(){

        return "Cliente/listaCupones";
    }

    @GetMapping("/listaReportes")
    public String listaReportes(){
        return "Cliente/listaReportes";
    }


    //LISTA CATEGORIAS
    @GetMapping("/categorias")
    public String categorias(){
        return "Cliente/categorías";
    }

    //PEDIDO ACTUAL
    @GetMapping("/pedidoActual")
    public String pedidoActual(){
        return "Cliente/listaPedidoActual";
    }



    //HISTORIAL PEDIDOS
    @GetMapping("/historialPedidos")
    public String historialPedidos(){
        return "Cliente/listaHistorialPedidos";
    }









}
