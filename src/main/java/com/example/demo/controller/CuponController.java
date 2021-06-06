package com.example.demo.controller;

import ch.qos.logback.core.util.CachingDateFormatter;
import com.example.demo.dtos.NotifiRestDTO;
import com.example.demo.entities.Cupon;
import com.example.demo.entities.Extra;
import com.example.demo.entities.Restaurante;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.CuponRepository;

import java.text.DateFormat;
import java.time.LocalDate;

import com.example.demo.repositories.PedidoRepository;
import com.example.demo.repositories.RestauranteRepository;
import com.example.demo.service.CuponService;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.jws.WebParam;
import javax.naming.Binding;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/cupon")
public class CuponController {
    @Autowired
    CuponRepository cuponRepository;

    @Autowired
    CuponService cuponService;
    @Autowired
    RestauranteRepository restauranteRepository;
    @Autowired
    PedidoRepository pedidoRepository;

    @GetMapping(value = {"/lista", ""})
    public String listarCupones(Model model,HttpSession session) {
        String pattern = "yyyy-MM-dd";
        DateFormat df = new SimpleDateFormat(pattern);
        Date today = Calendar.getInstance().getTime();
        String todayAsString = df.format(today);
        System.out.println(todayAsString);

        return findPaginated("", "3000-05-21", todayAsString, 0, 1, model,session);
    }

    @GetMapping("/page")
    public String findPaginated(@ModelAttribute @RequestParam(value = "textBuscador", required = false) String textBuscador,
                                @ModelAttribute @RequestParam(value = "fechafin", required = false) String fechafin,
                                @ModelAttribute @RequestParam(value = "fechainicio", required= false) String fechainicio,
                                @ModelAttribute @RequestParam(value = "textPrecio", required = false) Integer inputPrecio,
                                @RequestParam(value = "pageNo", required = false) Integer pageNo, Model model,HttpSession session) {
        Usuario adminRest=(Usuario)session.getAttribute("usuario");
        int idadmin=adminRest.getIdusuario();
        Restaurante restaurante= restauranteRepository.encontrarRest(idadmin);
        List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
        model.addAttribute("listaNotiRest",listaNotificacion);
        if (pageNo == null || pageNo == 0) {
            pageNo = 1;
        }
        System.out.println(pageNo);
        int inputID = 1;
        int pageSize = 2;
        Page<Cupon> page;
        List<Cupon> listaCupon;

        if (textBuscador == null) {
            textBuscador = "";
        }
        System.out.println(textBuscador);
        LocalDate fechafin2 = LocalDate.parse(fechafin);
        if (fechafin == null || fechafin.equals("")) {
            fechafin2 = LocalDate.parse("3000-05-21");
        }
        LocalDate fechainicio2;
        if (fechainicio == null || fechainicio.equals("")){
            fechainicio2 = LocalDate.now();
        } else {
            fechainicio2 = LocalDate.parse(fechainicio);
        }
        System.out.println(fechainicio2);
        if (inputPrecio == null) {
            inputPrecio = 0;
        }
        int inputPMax;
        int inputPMin;
        if (inputPrecio == 0) {
            inputPMin = 0;
            inputPMax = 100;
        } else {
            inputPMax = inputPrecio;
            inputPMin = inputPrecio;
        }
        System.out.println(inputPrecio);
        page = cuponService.findPaginated2(pageNo, pageSize, textBuscador, fechainicio2, fechafin2, inputPMin * 5 - 5, inputPMax * 5);
        listaCupon = page.getContent();

        model.addAttribute("texto", textBuscador);
        model.addAttribute("textoP", inputPrecio);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listaCupon", listaCupon);

        return "AdminRestaurante/listaCupones";
    }



    @GetMapping("/nuevo")
    public String nuevoCupon(@ModelAttribute("cupon") Cupon cupon,Model model, HttpSession session) {
        Usuario adminRest=(Usuario)session.getAttribute("usuario");
        int idadmin=adminRest.getIdusuario();
        Restaurante restaurante= restauranteRepository.encontrarRest(idadmin);
        List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
        model.addAttribute("listaNotiRest",listaNotificacion);
        return "AdminRestaurante/nuevoCupon";
    }

    @PostMapping("/guardar")
    public String guardarCupon(@ModelAttribute("cupon") @Valid Cupon cupon, BindingResult bindingResult,
                               RedirectAttributes attributes,
                               Model model, HttpSession session) {
        //Cupon cVal = cuponRepository.buscarPorNombre(cupon.getNombre());
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
        model.addAttribute("listaNotiRest",listaNotificacion);
        if (bindingResult.hasErrors()) {
            return "AdminRestaurante/nuevoCupon";
        }


        System.out.println("################################");
        System.out.println(cupon.getFechafin());
        System.out.println(cupon.getFechainicio());
        if (cupon.getIdcupon() == 0) {
            cupon.setIdrestaurante(restaurante.getIdrestaurante());
            cupon.setFechainicio(LocalDate.now());
            cupon.setEstado(1);
            attributes.addFlashAttribute("creado", "Cupón creado exitosamente");
        } else {
            cupon.setIdrestaurante(restaurante.getIdrestaurante());
            attributes.addFlashAttribute("editado", "Cupón editado exitosamente");
        }

            /*if(cupon.getFechainicio().isEqual(cupon .getFechafin())){
                cupon.setDisponible(false);
            }*/

        cuponRepository.save(cupon);
        return "redirect:/cupon/lista";
    }

    @GetMapping("/editar")
    public String editarCupon(@ModelAttribute("cupon") Cupon cupon,
                              Model model,@RequestParam("fechainicio") String fechainicio,
                              @RequestParam("fechafin") String fechafin,
                              @RequestParam("id") int id,HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int ida = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(ida);
        List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
        model.addAttribute("listaNotiRest",listaNotificacion);
        Optional<Cupon> optionalCupon = cuponRepository.findById(id);
        System.out.println(fechainicio);
        if (optionalCupon.isPresent()) {
            cupon = optionalCupon.get();
            cupon.setFechainicio(LocalDate.parse(fechainicio));
            cupon.setFechafin(LocalDate.parse(fechafin));
            System.out.println("********" + cupon.getFechainicio());
            model.addAttribute("cupon", cupon);
            return "AdminRestaurante/nuevoCupon";
        } else {
            return "redirect:/cupon/lista";
        }
    }

    @GetMapping("/actualizar")
    public String actualizarCupon(@RequestParam("id") int id,
                                  @RequestParam("estado") String estado,
                                  RedirectAttributes attr,HttpSession session,Model model){
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int ida = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(ida);
        List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
        model.addAttribute("listaNotiRest",listaNotificacion);
        Optional<Cupon> optionalCupon = cuponRepository.findById(id);
        LocalDate date = LocalDate.now();
        if (optionalCupon.isPresent()) {
            Cupon cupon = optionalCupon.get();
            LocalDate fecha=cupon.getFechafin();
            String fecha2= fecha.toString();
            System.out.println("------Fecha parseada-----"+fecha2);
            System.out.println("------Fecha actual-----"+date);
            if(fecha.isAfter(date)) {
                switch (estado) {
                    case "0":
                        cupon.setEstado(0);
                        attr.addFlashAttribute("bloqueo", "Cupón publicado exitosamente");
                        break;
                    case "1":
                        cupon.setEstado(1);
                        attr.addFlashAttribute("activo", "Cupón desbloqueado exitosamente");
                        break;
                    case "2":
                        cupon.setEstado(2);
                        attr.addFlashAttribute("publicado", "Cupón publicado exitosamente");
                        break;
                }
                cuponRepository.save(cupon);
            }else{
                return "redirect:/cupon/lista";
            }
        }
        return "redirect:/cupon/lista";
    }

    /*@GetMapping("/eliminar")
    public  String eliminarCupon(@RequestParam("id") int id, RedirectAttributes attributes){
        Optional<Cupon> cuponOptional = cuponRepository.findById(id);
        if (cuponOptional.isPresent()){
            cuponRepository.deleteById(id);
            attributes.addFlashAttribute("eliminado", "Cupon eliminado exitosamente!");
        }
        return "redirect:/cupon/listar";
    }*/

   /*
   * @InitBinder("cupon")
    public void cuponValidator(WebDataBinder binder){
        PropertyEditorSupport fechaValidator = new PropertyEditorSupport(){

            @Override
            public void setAsText(String date) throws IllegalArgumentException {
                // dd-MM-yyyy
                System.out.println(date);
                System.out.println(date);
                System.out.println("date");

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                //obteniendo la fecha actual
                Date currentDate = new Date();
                Calendar c = new GregorianCalendar();


                String dateF = dateFormat.format(date); // yyyy-MM-dd
                String[] dateSplit =dateF.split("-");

                int anioActual = c.get(Calendar.YEAR);
                int anioCad = Integer.parseInt(dateSplit[2]);

                c.set(Calendar.YEAR, Integer.parseInt(dateSplit[0]));
                c.set(Calendar.MONTH, Integer.parseInt(dateSplit[1])-1);
                c.set(Calendar.DATE, Integer.parseInt(dateSplit[2]));

                Date dateCadu = c.getTime();
                // una mejor forma de recibir la fecha
                //obteniendo la fecha actual con el formato yyyy-MM-dd

                if(currentDate.compareTo(dateCadu) <= 0){
                    this.setValue(" ");
                }else{
                    this.setValue(date);
                }
                //se quiere validar que la fecha de caducidad sea mayor a la fecha actual pero que dure un año
            }
        };
        binder.registerCustomEditor(LocalDate.class, "fechafin",fechaValidator);
    }
   **/

}