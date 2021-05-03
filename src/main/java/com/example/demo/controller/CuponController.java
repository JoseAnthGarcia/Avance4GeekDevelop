package com.example.demo.controller;

import com.example.demo.entities.Cupon;
import com.example.demo.repositories.CuponRepository;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping(value = {"/lista", ""})
    public String listarCupones(Model model) {
        model.addAttribute("listaCupon", cuponRepository.findAll());
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
        if(bindingResult.hasErrors()){
            return "AdminRestaurante/nuevoCupon";
        }

        System.out.println(cupon.getIdrestaurante());
        System.out.println(cupon.getNombre());
        cupon.setIdrestaurante(12);

        if (cupon.getIdcupon() == 0) {
            cupon.setFechainicio(LocalDate.now());
            attributes.addFlashAttribute("creado", "Cupon creado exitosamente!");
        } else {
            attributes.addFlashAttribute("editado", "Cupon editado exitosamente!");
        }
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

    /*@GetMapping("/eliminar")
    public  String eliminarCupon(@RequestParam("id") int id, RedirectAttributes attributes){
        Optional<Cupon> cuponOptional = cuponRepository.findById(id);
        if (cuponOptional.isPresent()){
            cuponRepository.deleteById(id);
            attributes.addFlashAttribute("eliminado", "Cupon eliminado exitosamente!");
        }
        return "redirect:/cupon/listar";
    }*/

    @InitBinder("cupon")
    public void fechaValidador(WebDataBinder binder){

        PropertyEditorSupport fechaValidator = new PropertyEditorSupport(){
            /*@Override
            public void setAsText(Date date) throws IllegalArgumentException {
                // dd-MM-yyyy
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MM dd");
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

                if((currentDate.compareTo(dateCadu) <= 0){
                    //lanza excepcion
                }

                //se quiere validar que la fecha de caducidad sea mayor a la fecha actual pero que dure un año

            } */


        };


    }

}
