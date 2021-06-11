package com.example.demo.controller;

import com.example.demo.dtos.ExtraDTO;
import com.example.demo.dtos.NotifiRestDTO;
import com.example.demo.entities.*;
import com.example.demo.repositories.CategoriaExtraRepository;
import com.example.demo.repositories.ExtraRepository;
import com.example.demo.repositories.PedidoRepository;
import com.example.demo.repositories.RestauranteRepository;
import com.example.demo.service.ExtraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.sql.In;
import org.springframework.data.web.PageableDefault;
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
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/extra")
public class ExtraController {

    @Autowired
    ExtraRepository extraRepository;

    @Autowired
    ExtraService extraService;
    @Autowired
    RestauranteRepository restauranteRepository;
    @Autowired
    CategoriaExtraRepository categoriaExtraRepository;
    @Autowired
    PedidoRepository pedidoRepository;
    @GetMapping(value = {"/categoria",""})
    public  String listaCategorias(Model model, @RequestParam(value = "idcategoria",required = false) Integer id, HttpSession session){
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int idadmin = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(idadmin);
        List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
        model.addAttribute("listaNotiRest",listaNotificacion);
        return "AdminRestaurante/extras";
    }
    @GetMapping("/lista")
    public String listarExtra(Model model,@RequestParam(value = "idcategoria",required = false) Integer id, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int idadmin = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(idadmin);
        List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
        model.addAttribute("listaNotiRest",listaNotificacion);
        if (id==null){
            return "redirect:/extra/categoria";
        }else {
        return findPaginated("", 0, 1,id,model, session);}
    }


    @GetMapping("/page")
    public String findPaginated(@ModelAttribute @RequestParam(value = "textBuscador", required = false) String textBuscador,
                                @ModelAttribute @RequestParam(value = "textPrecio", required = false) Integer inputPrecio,
                                @RequestParam(value = "pageNo", required = false) Integer pageNo,
                                @RequestParam(value = "idcategoria", required = false) Integer id, Model model, HttpSession session) {
        System.out.println(id);
        if (id==null){
            return "redirect:/extra";
        }else {
            Usuario adminRest = (Usuario) session.getAttribute("usuario");
            int idadmin = adminRest.getIdusuario();
            Restaurante restaurante = restauranteRepository.encontrarRest(idadmin);
            List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
            model.addAttribute("listaNotiRest",listaNotificacion);
            int idrestaurante = restaurante.getIdrestaurante();
            System.out.println(id);
            System.out.println(pageNo);
            if (pageNo == null || pageNo == 0) {
                pageNo = 1;
            }
            int inputID = 1;
            int pageSize = 5;
            Page<Extra> page;
            List<Extra> listaExtras;
            System.out.println(textBuscador);
            if (textBuscador == null) {
                textBuscador = "";
            }
            System.out.println(inputPrecio);
            if (inputPrecio == null) {
                inputPrecio = 0;
            }
            int inputPMax;
            int inputPMin;
            if (inputPrecio == 0) {
                inputPMin = 0;
                inputPMax = 1000;
            } else if (inputPrecio == 4) {
                inputPMin = inputPrecio;
                inputPMax = 1000;
            } else {
                inputPMax = inputPrecio;
                inputPMin = inputPrecio;
            }
            page = extraService.findPaginated2(pageNo, pageSize, idrestaurante, id, textBuscador, inputPMin * 5 - 5, inputPMax * 5);
            listaExtras = page.getContent();
            List<CategoriaExtra> listaCategoriaExtra = categoriaExtraRepository.findAll();
            model.addAttribute("texto", textBuscador);
            model.addAttribute("textoP", inputPrecio);

            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("totalItems", page.getTotalElements());
            model.addAttribute("listaExtras", listaExtras);
            model.addAttribute("listaCategoria", listaCategoriaExtra);
            model.addAttribute("idcategoria", id);

            return "AdminRestaurante/listaExtras";
        }
    }

    @GetMapping("/nuevo")
    public String nuevoExtra(@ModelAttribute("extra") Extra extra, @RequestParam(value = "idcategoria") int id, Model model,HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int idadmin = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(idadmin);
        List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
        model.addAttribute("listaNotiRest",listaNotificacion);
        model.addAttribute("idcategoria", id);
        return "AdminRestaurante/nuevoExtra";
    }

    @PostMapping("/guardar")
    public String guardarExtra(@ModelAttribute("extra") @Valid Extra extra, BindingResult bindingResult,
                               RedirectAttributes attr,
                               Model model,@RequestParam(value = "photo",required = false) MultipartFile file
            ,HttpSession session,@RequestParam(value = "idcategoria") int idc) {
        Usuario adminRest=(Usuario)session.getAttribute("usuario");
        int idadmin=adminRest.getIdusuario();
        Restaurante restaurante= restauranteRepository.encontrarRest(idadmin);
        List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
        model.addAttribute("listaNotiRest",listaNotificacion);
        int idrestaurante=restaurante.getIdrestaurante();
        System.out.println("id categoriaaaaaaaa "+idc);
        System.out.println("id categoriaaaaaaaa "+idc);
        System.out.println("id categoriaaaaaaaa "+idc);
        System.out.println("id categoriaaaaaaaa "+idc);
        System.out.println("id categoriaaaaaaaa "+idc);
        System.out.println("id categoriaaaaaaaa "+idc);
        System.out.println("id categoriaaaaaaaa "+idc);
        System.out.println("id categoriaaaaaaaa "+idc);
        System.out.println("id categoriaaaaaaaa "+idc);
        System.out.println("id categoriaaaaaaaa "+idc);
        System.out.println("id categoriaaaaaaaa "+idc);
        System.out.println("id categoriaaaaaaaa "+idc);
        System.out.println("id categoriaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa "+idc);

        String fileName ="";
        boolean validarFoto=true;
        if (file!=null){
            if(file.isEmpty()){
                model.addAttribute("mensajefoto", "Debe subir una imagen");
                validarFoto = false;
            }else if (!file.getContentType().contains("jpeg") && !file.getContentType().contains("png") && !file.getContentType().contains("web")) {
                System.out.println("FILE NULL---- HECTOR CTM5");
                model.addAttribute("mensajefoto", "Ingrese un formato de imagen válido (p.e. JPEG,PNG o WEBP)");
                validarFoto = false;
            }
            fileName = file.getOriginalFilename();
            if (fileName.contains("..")){
                model.addAttribute("idcategoria",idc);
                model.addAttribute("mensajefoto","No se premite '..' een el archivo");
                return "AdminRestaurante/nuevoExtra";
            }
        }

        extra.setIdrestaurante(idrestaurante); //Jarcodeado
        extra.setDisponible(true); //default expresion !!!!
        extra.setIdcategoriaextra(idc);
        if (bindingResult.hasErrors()) {
            if (extra.getIdextra() == 0&&!validarFoto) {
                model.addAttribute("idcategoria",idc);
                return "AdminRestaurante/nuevoExtra";
            }
            if (extra.getIdextra() == 0) {
                model.addAttribute("idcategoria",idc);
                return "AdminRestaurante/nuevoExtra";
            }else {
                Optional<Extra> optExtra = extraRepository.findById(extra.getIdextra());
                if (optExtra.isPresent()) {
                    model.addAttribute("idcategoria",idc);
                    return "AdminRestaurante/nuevoExtra";
                } else {
                    model.addAttribute("idcategoria",idc);
                    return "redirect:/extra/lista?idcategoria="+idc;
                }
            }
        } else if (validarFoto) {
            if (extra.getIdextra() == 0) {
                try{
                    extra.setFoto(file.getBytes());
                    extra.setFotonombre(fileName);
                    extra.setFotocontenttype(file.getContentType());
                    attr.addFlashAttribute("msg", "Extra creado exitosamente");
                    model.addAttribute("idcategoria",idc);
                    extraRepository.save(extra);
                }catch (IOException e){
                    e.printStackTrace();
                    model.addAttribute("idcategoria",idc);
                    model.addAttribute("mensajefoto","Ocurrió un error al subir el archivo");
                    return "AdminRestaurante/nuevoExtra";
                }

            } else {
                Optional<Extra> optExtra = extraRepository.findById(extra.getIdextra());
                if (optExtra.isPresent()) {
                    Optional<Extra> extraOptional=extraRepository.findById(extra.getIdextra());
                    extra.setFoto(extraOptional.get().getFoto());
                    extra.setFotonombre(extraOptional.get().getFotonombre());
                    extra.setFotocontenttype(extraOptional.get().getFotocontenttype());
                    extraRepository.save(extra);
                    attr.addFlashAttribute("msg2", "Extra editado exitosamente");
                }
            }
            model.addAttribute("idcategoria",idc);
            return "redirect:/extra/lista?idcategoria="+idc;
        }
        else {
            return "AdminRestaurante/nuevoExtra";
        }
    }


    @GetMapping("/imagen/{id}")
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable("id") int id){
        Optional<Extra> optionalExtra=extraRepository.findById(id);
        if (optionalExtra.isPresent()){
            Extra extra = optionalExtra.get();
            byte[] imagenBytes=extra.getFoto();
            HttpHeaders httpHeaders= new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(extra.getFotocontenttype()));
            return new ResponseEntity<>(imagenBytes,httpHeaders, HttpStatus.OK);
        }else {
            return null;
        }
    }

    @GetMapping("/editar")
    public String editarExtra(@RequestParam("id") int id,
                              Model model,
                              @ModelAttribute("extra") Extra extra,@RequestParam(value = "idcategoria") int idc,HttpSession session) {
        Optional<Extra> extraOptional = extraRepository.findById(id);
        Usuario adminRest=(Usuario)session.getAttribute("usuario");
        int idadmin=adminRest.getIdusuario();
        Restaurante restaurante= restauranteRepository.encontrarRest(idadmin);
        List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
        model.addAttribute("listaNotiRest",listaNotificacion);
        model.addAttribute("idcategoria",idc);
        if (extraOptional.isPresent()) {
            extra = extraOptional.get();
            model.addAttribute("extra", extra);
            return "AdminRestaurante/nuevoExtra";
        } else {
            return "redirect:/extra/lista";
        }
    }

    @GetMapping("/borrar")
    public String borrarExtra(@RequestParam("id") int id, RedirectAttributes attr,
                              @RequestParam(value = "idcategoria") int idc,Model model) {
        Optional<Extra> extraOptional = extraRepository.findById(id);
        if (extraOptional.isPresent()) {
            Extra extra = extraOptional.get();
            extra.setDisponible(false);
            extraRepository.save(extra);
            attr.addFlashAttribute("msg3", "Extra borrado exitosamente");
        }
        model.addAttribute("idcategoria",idc);
        return "redirect:/extra/lista?idcategoria="+idc;
    }
}
