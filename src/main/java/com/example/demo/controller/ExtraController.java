package com.example.demo.controller;

import com.example.demo.dtos.ExtraDTO;
import com.example.demo.entities.Cupon;
import com.example.demo.entities.Extra;
import com.example.demo.entities.Plato;
import com.example.demo.repositories.ExtraRepository;
import com.example.demo.service.ExtraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/extra")
public class ExtraController {
    int idrestaurante = 1;
    int idcategoriaextra=1;
    //ELIMINAR ESTO
    @Autowired
    ExtraRepository extraRepository;

    @Autowired
    ExtraService extraService;


    @GetMapping(value = {"/lista", ""})
    public String listarExtra(Model model) {
        //model.addAttribute("listaExtras", extraRepository.listarExtra(idrestaurante));
        return findPaginated("", 0, 1, model);
        /*
        int precios = 0;
        System.out.println(nombre);
        try {
            precios = Integer.parseInt(rango);
        } catch (NumberFormatException e) {
            System.out.println("Falló el rango de precios\n");
        } catch (NullPointerException e) {
            System.out.println("Mandaron null\n");
        }
        double precio1;
        double precio2;
        if (nombre != null & precios == 0) {
            model.addAttribute("listaExtras", extraRepository.buscarExtraPornombre(nombre, idrestaurante, categoria));
        } else if (nombre == null & precios != 0) {
            switch (precios) {
                case 1:
                    precio1 = 0.0;
                    precio2 = 15.0;
                    model.addAttribute("listaExtras", extraRepository.buscarExtraPorPrecio(precio1, precio2, idrestaurante, categoria));
                    break;
                case 2:
                    precio1 = 15.0;
                    precio2 = 35.0;
                    model.addAttribute("listaExtras", extraRepository.buscarExtraPorPrecio(precio1, precio2, idrestaurante, categoria));
                    break;
                case 3:
                    precio1 = 35.0;
                    precio2 = 60.0;
                    model.addAttribute("listaExtras", extraRepository.buscarExtraPorPrecio(precio1, precio2, idrestaurante, categoria));
                    break;
                case 4:
                    precio1 = 60.0;
                    model.addAttribute("listaExtras", extraRepository.buscarExtraPorPrecioAMAS(precio1, idrestaurante, categoria));
                    break;
                default:
                    model.addAttribute("listaExtras", extraRepository.buscarExtraPornombre(nombre, idrestaurante, categoria));
                    break;
            }
        } else if (nombre != null & precios != 0) {
            switch (precios) {
                case 1:
                    precio1 = 0.0;
                    precio2 = 15.0;
                    model.addAttribute("listaExtras", extraRepository.buscarExtraPornombreyPrecio(nombre, precio1, precio2, idrestaurante, categoria));
                    break;
                case 2:
                    precio1 = 15.0;
                    precio2 = 35.0;
                    model.addAttribute("listaExtras", extraRepository.buscarExtraPornombreyPrecio(nombre, precio1, precio2, idrestaurante, categoria));
                    break;
                case 3:
                    precio1 = 35.0;
                    precio2 = 60.0;
                    model.addAttribute("listaExtras", extraRepository.buscarExtraPornombreyPrecio(nombre, precio1, precio2, idrestaurante, categoria));
                    break;
                case 4:
                    precio1 = 60.0;
                    model.addAttribute("listaExtras", extraRepository.buscarExtraPornombreyPrecioAMAS(nombre, precio1, idrestaurante, categoria));
                    break;
                default:
                    model.addAttribute("listaExtras", extraRepository.buscarExtraPornombre(nombre, idrestaurante, categoria));
                    break;
            }
        } else if (nombre == null & precios == 0) {
            model.addAttribute("listaExtras", extraRepository.lista(idrestaurante, categoria));
        }
        model.addAttribute("texto", nombre);
        model.addAttribute("textoP", precios);
         */
        //return "AdminRestaurante/listaExtras";
    }


    @GetMapping("/page")
    public String findPaginated(@RequestParam(value = "textBuscador", required = false) String nombre,
                                @RequestParam(value = "textPrecio", required = false) Integer inputPrecio,
                                @RequestParam(value = "pageNo", required = false) Integer pageNo, Model model) {
        System.out.println(pageNo);
        if (pageNo == null || pageNo == 0) {
            pageNo = 1;
        }
        int inputID = 1;
        int pageSize = 1;
        Page<Extra> page;
        List<Extra> listaExtras;
        System.out.println(nombre);
        if (nombre == null) {
            nombre = "";
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
        } else {
            inputPMax = inputPrecio;
            inputPMin = inputPrecio;
        }
        page = extraService.findPaginated2(pageNo, pageSize, nombre, inputPMin * 5, inputPMax * 5 - 5);
        listaExtras = page.getContent();

        model.addAttribute("texto", nombre);
        model.addAttribute("textoP", inputPrecio);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listaExtras", listaExtras);

        return "AdminRestaurante/listaExtras";

    }

    @GetMapping("/nuevo")
    public String nuevoExtra(@ModelAttribute("extra") Extra extra) {
        return "AdminRestaurante/nuevoExtra";
    }

    @PostMapping("/guardar")
    public String guardarExtra(@ModelAttribute("extra") @Valid Extra extra, BindingResult bindingResult,
                               RedirectAttributes attr,
                               Model model) {
       /* Cupon cVal = extraRepository.buscarPorNombre(cupon.getNombre());

        if(cVal == null){
            if(bindingResult.hasErrors()){
                return "AdminRestaurante/nuevoCupon";
            }

            cupon.setIdrestaurante(2);

            if (cupon.getIdcupon() == 0) {
                cupon.setFechainicio(LocalDate.now());
                cupon.setDisponible(true);
                attributes.addFlashAttribute("creado", "Cupon creado exitosamente!");
            } else {
                attributes.addFlashAttribute("editado", "Cupon editado exitosamente!");
            }

            if(cupon.getFechainicio().isEqual(cupon.getFechafin())){
                cupon.setDisponible(false);
            }

            extraRepository.save(cupon);
            return "redirect:/cupon/lista";
        }else{
            model.addAttribute("val","Este nombre ya está registrado");
            return "AdminRestaurante/nuevoCupon";
        }*/
        extra.setIdrestaurante(idrestaurante); //Jarcodeado
        extra.setDisponible(true); //default expresion !!!!
        extra.setIdcategoriaextra(idcategoriaextra);
        if (bindingResult.hasErrors()) {
            if (extra.getIdextra() == 0) {
                return "/AdminRestaurante/nuevoExtra";
            } else {
                Optional<Extra> optExtra = extraRepository.findById(extra.getIdextra());
                if (optExtra.isPresent()) {
                    return "/AdminRestaurante/nuevoExtra";
                } else {
                    return "redirect:/extra/lista";
                }
            }
        } else {
            if (extra.getIdextra() == 0) {

                attr.addFlashAttribute("msg", "Extra creado exitosamente");
                attr.addFlashAttribute("msg2", "Extra editado exitosamente");
                extraRepository.save(extra);
            } else {
                Optional<Extra> optExtra = extraRepository.findById(extra.getIdextra());
                if (optExtra.isPresent()) {
                    extraRepository.save(extra);
                    attr.addFlashAttribute("msg2", "Extra editado exitosamente");
                    attr.addFlashAttribute("msg", "Extra creado exitosamente");
                }
            }
            return "redirect:/extra/lista";
        }
    }
    @GetMapping("/editar")
    public String editarExtra(@RequestParam("id") int id,
                              Model model,
                              @ModelAttribute("extra") Extra extra) {
        Optional<Extra> extraOptional = extraRepository.findById(id);
        if (extraOptional.isPresent()) {
            extra = extraOptional.get();
            model.addAttribute("extra", extra);
            return "/AdminRestaurante/nuevoExtra";
        } else {
            return "redirect:/extra/lista";
        }
    }
    @GetMapping("/borrar")
    public String borrarExtra(@RequestParam("id") int id ,RedirectAttributes attr) {
        Optional<Extra> extraOptional = extraRepository.findById(id);
        if (extraOptional.isPresent()) {
            Extra extra = extraOptional.get();
            extra.setDisponible(false);
            extraRepository.save(extra);
            attr.addFlashAttribute("msg3", "Extra borrado exitosamente");
        }

        return "redirect:/extra/lista";
    }
}
