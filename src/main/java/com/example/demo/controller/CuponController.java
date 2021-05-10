package com.example.demo.controller;

import com.example.demo.entities.Cupon;
import com.example.demo.entities.Extra;
import com.example.demo.repositories.CuponRepository;

import java.time.LocalDate;

import com.example.demo.service.CuponService;
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

    @GetMapping(value = {"/lista", ""})
    public String listarCupones(Model model) {

        return findPaginated("", LocalDate.parse("3000-05-21"), 0, 1, model);
    }

    @GetMapping("/page")
    public String findPaginated(@ModelAttribute @RequestParam(value = "textBuscador", required = false) String textBuscador,
                                @ModelAttribute @RequestParam(value = "fechafin", required = false) LocalDate fechafin,
                                @ModelAttribute @RequestParam(value = "textPrecio", required = false) Integer inputPrecio,
                                @RequestParam(value = "pageNo", required = false) Integer pageNo, Model model) {

        if (pageNo == null || pageNo == 0) {
            pageNo = 1;
        }
        System.out.println(pageNo);
        int inputID = 1;
        int pageSize = 1;
        Page<Cupon> page;
        List<Cupon> listaCupon;

        if (textBuscador == null) {
            textBuscador = "";
        }
        System.out.println(textBuscador);

        if (fechafin == null) {
            fechafin = LocalDate.parse("3000-05-21");
        }

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
        page = cuponService.findPaginated2(pageNo, pageSize, textBuscador, fechafin, inputPMin * 5 - 5, inputPMax * 5);
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
    public String nuevoCupon(@ModelAttribute("cupon") Cupon cupon) {
        return "AdminRestaurante/nuevoCupon";
    }

    @PostMapping("/guardar")
    public String guardarCupon(@ModelAttribute("cupon") @Valid Cupon cupon, BindingResult bindingResult,
                               RedirectAttributes attributes,
                               Model model) {
        //Cupon cVal = cuponRepository.buscarPorNombre(cupon.getNombre());

        if (bindingResult.hasErrors()) {
            return "AdminRestaurante/nuevoCupon";
        }

        cupon.setIdrestaurante(2);

        if (cupon.getIdcupon() == 0) {
            cupon.setFechainicio(LocalDate.now());
            cupon.setEstado(1);
            attributes.addFlashAttribute("creado", "Cupon creado exitosamente!");
        } else {
            attributes.addFlashAttribute("editado", "Cupon editado exitosamente!");
        }

            /*if(cupon.getFechainicio().isEqual(cupon .getFechafin())){
                cupon.setDisponible(false);
            }*/

        cuponRepository.save(cupon);
        return "redirect:/cupon/lista";
    }

    @GetMapping("/editar")
    public String editarCupon(@ModelAttribute("cupon") Cupon cupon,
                              Model model,
                              @RequestParam("id") int id) {
        Optional<Cupon> optionalCupon = cuponRepository.findById(id);
        if (optionalCupon.isPresent()) {
            cupon = optionalCupon.get();
            model.addAttribute("cupon", cupon);
            return "AdminRestaurante/nuevoCupon";
        } else {
            return "redirect:/cupon/lista";
        }
    }

    @GetMapping("/bloquear")
    public String bloquearCupon(@RequestParam("id") int id, RedirectAttributes attr) {
        Optional<Cupon> optionalCupon = cuponRepository.findById(id);
        if (optionalCupon.isPresent()) {
            Cupon cupon = optionalCupon.get();
            cupon.setEstado(0);
            cuponRepository.save(cupon);
            attr.addFlashAttribute("bloqueo", "Cupón bloqueado exitosamente");
        }
        return "redirect:/cupon/lista";
    }

    @GetMapping("/publicar")
    public String publicarCupon(@RequestParam("id") int id, RedirectAttributes attr) {
        Optional<Cupon> optionalCupon = cuponRepository.findById(id);
        if (optionalCupon.isPresent()) {
            Cupon cupon = optionalCupon.get();
            cupon.setEstado(2);
            cuponRepository.save(cupon);
            attr.addFlashAttribute("publicado", "Cupón publicado exitosamente");
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