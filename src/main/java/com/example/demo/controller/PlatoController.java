package com.example.demo.controller;

import com.example.demo.entities.CategoriaExtra;
import com.example.demo.entities.Plato;
import com.example.demo.repositories.CategoriaExtraRepository;
import com.example.demo.repositories.PlatoRepository;
import com.example.demo.service.PlatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/plato")
public class    PlatoController {

    @Autowired
    PlatoRepository platoRepository;

    @Autowired
    PlatoService platoService;
    @Autowired
    CategoriaExtraRepository categoriaExtraRepository;

    @GetMapping(value = {"/lista", ""})
    public String listaPlatos(Model model) {
        return findPaginated("", 1, 0, 1, model);
    }

    /*@PostMapping("/textSearch")
    public String buscador(@RequestParam("textBuscador") String textBuscador,
                           @RequestParam("textDisponible") Integer inputDisponible,
                           @RequestParam("textPrecio") Integer inputPrecio, Model model){
        model.addAttribute("texto", textBuscador);
        model.addAttribute("textoD", inputDisponible);
        model.addAttribute("textoP", inputPrecio);
        return findPaginated(textBuscador, inputDisponible, inputPrecio, 1, model);
    }*/

    @GetMapping("/page")
    public String findPaginated(@ModelAttribute @RequestParam(value = "textBuscador", required = false) String textBuscador,
                                @ModelAttribute @RequestParam(value = "textDisponible", required = false) Integer inputDisponible,
                                @ModelAttribute @RequestParam(value = "textPrecio", required = false) Integer inputPrecio,
                                @RequestParam(value = "pageNo", required = false) Integer pageNo, Model model){

        if(pageNo==null || pageNo==0){
            pageNo=1;
        }

        int inputID = 1;
        int pageSize = 5;
        Page<Plato> page;
        List<Plato> listaPlatos;
        System.out.println(textBuscador);
        if(textBuscador==null){
            textBuscador="";
        }
        if(inputDisponible==null){
            inputDisponible=1;
        }
        boolean disponibilidad;
        disponibilidad= inputDisponible != 0;
        System.out.println(inputPrecio);
        if(inputPrecio==null){
            inputPrecio=0;
        }
        int inputPMax;
        int inputPMin;
        if (inputPrecio==0){
            inputPMin=0;
            inputPMax=100;
        }else {
            inputPMax=inputPrecio;
            inputPMin=inputPrecio;
        }
        page = platoService.findPaginated2(pageNo, pageSize, 1,1,textBuscador, disponibilidad, inputPMin*5-5, inputPMax*5);//harcodeado
        listaPlatos= page.getContent();

        model.addAttribute("texto", textBuscador);
        model.addAttribute("textoD", inputDisponible);
        model.addAttribute("textoP", inputPrecio);

        System.out.println(pageNo + "\n" + pageSize + "\n" + textBuscador + "\n" + disponibilidad + "\n" + inputPMin + "\n" +inputPMax);

        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listaPlatos", listaPlatos);

        return "AdminRestaurante/listaPlatos";

    }

    @GetMapping("/nuevo")
    public String crearPlato(@ModelAttribute("plato") Plato plato,
                             Model model) {
        model.addAttribute("listaCategoria",categoriaExtraRepository.findAll());
        return "/AdminRestaurante/nuevoPlato";
    }

    @PostMapping("/guardar")
    public String guardarPlato(@ModelAttribute("plato") @Valid Plato plato,BindingResult bindingResult,
                               @RequestParam(value = "photo",required = false) MultipartFile file,RedirectAttributes attr, Model model) {
        model.addAttribute("listaCategoria",categoriaExtraRepository.findAll());
        String fileName ="";
        if (file!=null){
            if(file.isEmpty()){
                model.addAttribute("mensajefoto", "Debe subir una imagen");
                return "/AdminRestaurante/nuevoPlato";
            }
            fileName = file.getOriginalFilename();
            if (fileName.contains("..")){
                model.addAttribute("mensajefoto","No se premite '..' een el archivo");
                return "/AdminRestaurante/nuevoPlato";
            }
        }


        plato.setIdrestaurante(1); //Jarcodeado
        plato.setIdcategoriaplato(1); //Jarcodeado
        plato.setDisponible(true); //default expresion !!!!


        if(bindingResult.hasErrors()){
            if (plato.getIdplato() == 0) {
                System.out.println("estoy 1");
                return "/AdminRestaurante/nuevoPlato";
            } else {
                Optional<Plato> optPlato = platoRepository.findById(plato.getIdplato());
                if (optPlato.isPresent()) {
                    System.out.println("estoy 2");
                    return "/AdminRestaurante/nuevoPlato";
                } else {

                    return "redirect:/plato/lista";
                }
            }
        } else {
            if (plato.getIdplato() == 0) {

                attr.addFlashAttribute("msg", "Plato creado exitosamente");
                attr.addFlashAttribute("tipo", "saved");
                System.out.println("estoy 3");
                try{
                    plato.setFoto(file.getBytes());
                    plato.setFotonombre(fileName);
                    plato.setFotocontenttype(file.getContentType());
                    platoRepository.save(plato);
                }catch (IOException e){
                    e.printStackTrace();
                    model.addAttribute("mensajefoto","Ocurrió un error al subir el archivo");
                    return "/AdminRestaurante/nuevoPlato";
                }

            } else {
                Optional<Plato> optPlato = platoRepository.findById(plato.getIdplato());
                if (optPlato.isPresent()) {
                    System.out.println("estoy 4");
                    Optional<Plato> platoOptional=platoRepository.findById(plato.getIdplato());
                    plato.setFoto(platoOptional.get().getFoto());
                    plato.setFotonombre(platoOptional.get().getFotonombre());
                    plato.setFotocontenttype(platoOptional.get().getFotocontenttype());
                    platoRepository.save(plato);
                    attr.addFlashAttribute("tipo", "saved");
                    attr.addFlashAttribute("msg", "Plato actualizado exitosamente");
                }
            }
            return "redirect:/plato/lista";
        }

    }


    @GetMapping("/imagen/{id}")
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable("id") int id){
        Optional<Plato> optionalPlato=platoRepository.findById(id);
        if (optionalPlato.isPresent()){
            Plato p = optionalPlato.get();
            byte[] imagenBytes=p.getFoto();
            HttpHeaders httpHeaders= new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(p.getFotocontenttype()));
            return new ResponseEntity<>(imagenBytes,httpHeaders, HttpStatus.OK);
        }else {
            return null;
        }
    }
    @GetMapping("/editar")
    public String editarPlato(@RequestParam("id") int id,
                              Model model,
                              @ModelAttribute("plato") Plato plato) {
        Optional<Plato> platoOptional = platoRepository.findById(id);
        if (platoOptional.isPresent()) {
            plato = platoOptional.get();
            model.addAttribute("plato", plato);
            model.addAttribute("listaCategoria", categoriaExtraRepository.findAll());
            return "/AdminRestaurante/nuevoPlato";
        } else {
            return "redirect:/plato/lista";
        }
    }

    @GetMapping("/borrar")
    public String borrarPlato(@RequestParam("id") int id ,RedirectAttributes attr) {
        Optional<Plato> platoOptional = platoRepository.findById(id);
        if (platoOptional.isPresent()) {
            Plato plato = platoOptional.get();
            plato.setDisponible(false);
            platoRepository.save(plato);
            attr.addFlashAttribute("msg", "Plato borrado exitosamente");
            attr.addFlashAttribute("tipo", "borrado");
        }

        return "redirect:/plato/lista";
    }

    @GetMapping("/prueba")
    public String borrarPlato() {
        return "/AdminRestaurante/prueba";
    }


}
