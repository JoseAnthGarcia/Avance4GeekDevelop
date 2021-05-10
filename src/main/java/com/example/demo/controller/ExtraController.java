package com.example.demo.controller;

import com.example.demo.dtos.ExtraDTO;
import com.example.demo.entities.Cupon;
import com.example.demo.entities.Extra;
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


@Controller
@RequestMapping("/extra")
public class ExtraController {
    int idrestaurante = 1;
    //ELIMINAR ESTO
    int categoria = 1;
    @Autowired
    ExtraRepository extraRepository;

    @Autowired
    ExtraService extraService;


    @GetMapping(value = {"/lista", ""})
    public String listarExtra(Model model, @RequestParam(value = "textBuscador", required = false) String nombre
            , @RequestParam(value = "textPrecio", required = false) String rango) {
        //model.addAttribute("listaExtras", extraRepository.listarExtra(idrestaurante));
        return findPaginated(1,model);
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
    public String findPaginated(@RequestParam("pageNo") int pageNo, Model model) {

        int pageSize = 2;

        System.out.println(pageNo);
        Page<Extra> page = extraService.findPaginated(pageNo, pageSize);
        List<Extra> listaExtras = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listaExtras", listaExtras);

        return "AdminRestaurante/listaExtras";

    }


    @GetMapping("/nuevo")
    public String nuevoCupon(@ModelAttribute("cupon") Cupon cupon) {
        return "AdminRestaurante/nuevoExtra";
    }

    @PostMapping("/guardar")
    public String guardarCupon(@ModelAttribute("cupon") @Valid Cupon cupon, BindingResult bindingResult,
                               RedirectAttributes attributes,
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

        return "AdminRestaurante/nuevoCupon";
    }

    /*@GetMapping("/editar")
    public String editarCupon(@ModelAttribute("cupon") Cupon cupon,
                              Model model,
                              @RequestParam("id") int id) {
        Optional<Extra> optionalCupon = extraRepository.findById(id);
        if (optionalCupon.isPresent()) {
            cupon = ;
            model.addAttribute("cupon", cupon);
            return "AdminRestaurante/nuevoCupon";
        } else {
            return "redirect:/cupon/lista";
        }
    }*/
}
