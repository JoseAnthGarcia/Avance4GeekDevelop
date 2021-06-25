package com.example.demo.controller;

import com.example.demo.entities.Tarjeta;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.TarjetaRepository;
import com.example.demo.repositories.UsuarioRepository;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Null;

@Controller
@RequestMapping("/tarjeta")
public class TarjetaController {

    @Autowired
    TarjetaRepository tarjetaRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @GetMapping("/lista")
    public String mostrarTarjetas(HttpSession httpSession, Model model, @ModelAttribute("tarjeta") Tarjeta tarjeta) {
        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        List<Tarjeta> listaTarejeta = tarjetaRepository.findByUsuario(usuario);
        model.addAttribute("listaTarjetas", listaTarejeta);
        model.addAttribute("tarjeta", "");
        model.addAttribute("nombre", "");
        model.addAttribute("apellido", "");
        model.addAttribute("cvv", "");
        model.addAttribute("mes","0");
        model.addAttribute("year","");
        model.addAttribute("notificaciones", usuarioRepository.notificacionCliente(usuario.getIdusuario()));
        return "Cliente/tarjetas";
    }

    @PostMapping("/guardar")
    public String guardarTarjeta(@RequestParam("numeroTarjeta") String numeroTarjeta, @RequestParam("nombreC") String nombreC, @RequestParam("apellidoC") String apellidoC,
                                 @RequestParam("idcvv") String idcvv, @RequestParam("mes") String mes, @RequestParam("year") String year, HttpSession httpSession, Model model,
                                 RedirectAttributes attr) {
        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");

        Boolean valNumT = false;
        Boolean valNumN = false;
        Boolean valNumaA = false;
        Boolean valNumCv = false;
        Boolean valyear = false;
        // VALIDACIÓN DE NUMERO
        try {
            if (numeroTarjeta == null) {
                valNumT = true;
            }
            if (numeroTarjeta.length() != 16) {
                valNumT = true;
            }
            Long numeroTarjeta1 = Long.parseLong(numeroTarjeta);
        } catch (NumberFormatException numberFormatException) {
            valNumT = true;
        }
        //validacion año
        try {
            if (year == null) {
                valyear = true;
            }
            if (year.length() != 4) {
                valyear = true;
            }
            Integer numeroTarjeta1 = Integer.parseInt(year);
        } catch (NumberFormatException numberFormatException) {
            valyear = true;
        }
        //VALIDACION MES
        Boolean mes_u = true;
        try {
            Integer u_dist = Integer.parseInt(mes);
            for (int i = 1; i <= 12; i++) {
                if (u_dist == i) {
                    mes_u = false;
                }
            }
        } catch (NullPointerException n) {
            mes_u = true;
        }
        //VALIDACIÓN NOMBRES

        Pattern pat = Pattern.compile("[a-zA-Z]{1,256}");
        Matcher mat = pat.matcher(nombreC);
        if (!mat.matches()) {
            valNumN = true;
        }
        if (nombreC.length() == 0) {
            valNumN = true;
        }
        //
        //VALIDACIÓN Apellidos

        pat = Pattern.compile("[a-zA-Z]{1,256}");
        mat = pat.matcher(apellidoC);
        if (!mat.matches()) {
            valNumaA = true;
        }
        if (apellidoC.length() == 0) {
            valNumaA = true;
        }
        //CVV
        try {
            if (idcvv == null) {
                valNumCv = true;
            }
            if (idcvv.length() != 3) {
                valNumCv = true;
            }
            Integer numeroTarjeta1 = Integer.parseInt(idcvv);
        } catch (NumberFormatException numberFormatException) {
            valNumCv = true;
        }
        //Validar cantidad tarjetas
        Boolean cantTarjetas = false;
        List<Tarjeta> listaTarjetas = tarjetaRepository.findByUsuario(usuario);
        if (listaTarjetas.size() == 5) {
            cantTarjetas = true;
        }
        if (valNumT || valNumN || valNumaA || valNumCv || cantTarjetas || mes_u || valyear) {


            if (valNumT && !cantTarjetas) {
                model.addAttribute("msg1", "Debe colocar 16 dígitos(números)");

            }
            if (valNumN && !cantTarjetas) {
                model.addAttribute("msg2", "Debe colocar su nombre");

            }
            if (valNumaA && !cantTarjetas) {
                model.addAttribute("msg3", "Debe colocar su apellido");

            }
            if (valNumCv && !cantTarjetas) {
                model.addAttribute("msg4", "Debe colocar 3 dígitos(números)");
            }
            if (cantTarjetas) {
                model.addAttribute("cantTarj", "Puede registrar 5 tarjetas como máximo");
            }
            if (mes_u) {
                model.addAttribute("mdg6", "Debe escoger un mes");
            }
            if (valyear) {
                model.addAttribute("msg5", "Debe colocar 4 dígitos(números)");
            }
            model.addAttribute("tarjeta", numeroTarjeta);
            model.addAttribute("nombre", nombreC);
            model.addAttribute("apellido", apellidoC);
            model.addAttribute("cvv", idcvv);
            model.addAttribute("year", year);
            model.addAttribute("mes", mes);
            model.addAttribute("listaTarjetas", listaTarjetas);
            model.addAttribute("notificaciones", usuarioRepository.notificacionCliente(usuario.getIdusuario()));
            return "Cliente/tarjetas";
        } else {
            Tarjeta tarjeta = new Tarjeta();
            tarjeta.setNumerotarjeta(numeroTarjeta.substring(0, 6) + numeroTarjeta.substring(12, 16));
            tarjeta.setIdtarjeta(numeroTarjeta.substring(6, 12)); //Por ahora, seran los numero restantes
            tarjeta.setUsuario((Usuario) httpSession.getAttribute("usuario"));
            tarjetaRepository.save(tarjeta);
            return "redirect:/tarjeta/lista";
        }
    }

    @PostMapping("/eliminar")
    public String eliminarTarjeta(HttpSession httpSession, @RequestParam("listaTarjetasSelecciones") List<Tarjeta> listaTarjetasSeleccionadas) {
        //validar existencia
        Usuario usuario = (Usuario) httpSession.getAttribute("usuario");
        List<Tarjeta> listaTarejeta = tarjetaRepository.findByUsuario(usuario);
        for (Tarjeta t : listaTarejeta) {
            for (Tarjeta tarjeta : listaTarjetasSeleccionadas) {
                if (t == tarjeta) {
                    tarjetaRepository.delete(tarjeta);
                }
            }
        }

        return "redirect:/tarjeta/lista";
    }


}
