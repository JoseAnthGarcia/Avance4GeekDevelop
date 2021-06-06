package com.example.demo.controller;

import com.example.demo.dtos.NotifiRestDTO;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

@Controller
@RequestMapping("/plato")
public class PlatoController {

    @Autowired
    PlatoRepository platoRepository;

    @Autowired
    PlatoService platoService;
    @Autowired
    CategoriaExtraRepository categoriaExtraRepository;
    @Autowired
    RestauranteRepository restauranteRepository;
    @Autowired
    CategoriasRestauranteRepository categoriaRespository;
    @Autowired
    PedidoRepository pedidoRepository;

    @GetMapping(value = {"/categoria", ""})
    public String listaCategorias(Model model, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();

        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        List<Categorias> listaCategorias = restaurante.getCategoriasRestaurante();
        model.addAttribute("listaCategorias", listaCategorias);
        model.addAttribute("estadoRestaurante",restaurante.getEstado());
        List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
        model.addAttribute("listaNotiRest",listaNotificacion);
        return "AdminRestaurante/categorias";
    }

    @GetMapping("/lista")
    public String listaPlatos(Model model, HttpSession session, @RequestParam(value = "idcategoria", required = false) Integer idcategoria) {
        if (idcategoria == null) {
            return "redirect:/plato/categoria";
        }
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        System.out.println(adminRest.getIdusuario());
        System.out.println(restaurante.getIdrestaurante());
        model.addAttribute("idcategoria", idcategoria);
        List<Categorias> listaCategorias = restaurante.getCategoriasRestaurante();
        for (Categorias lista : listaCategorias) {
            if (lista.getIdcategoria() == idcategoria) {
                model.addAttribute("nombreCate", lista.getNombre());
                break;
            }
        }
        System.out.println("SOOOOOOOOOOOOOOOOOOOOOOOOOOOOOY idcateeeeeeee " + idcategoria);
        return findPaginated("", 1, 0, 1, idcategoria, restaurante.getIdrestaurante(), model, session);
    }


    @GetMapping("/page")
    public String findPaginated(@ModelAttribute @RequestParam(value = "textBuscador", required = false) String textBuscador,
                                @ModelAttribute @RequestParam(value = "textDisponible", required = false) Integer inputDisponible,
                                @ModelAttribute @RequestParam(value = "textPrecio", required = false) Integer inputPrecio,
                                @RequestParam(value = "pageNo", required = false) Integer pageNo, @RequestParam(value = "idcategoria", required = false) Integer idcategoria,
                                @RequestParam(value = "idrestaurante", required = false) Integer idrestaurante, Model model, HttpSession session) {

        if (pageNo == null || pageNo == 0) {
            pageNo = 1;
        }
        if (idcategoria == null) {
            return "redirect:/plato/categoria";
        }
        int inputID = 1;
        int pageSize = 5;
        Page<Plato> page;
        List<Plato> listaPlatos;
        System.out.println(textBuscador);
        if (textBuscador == null) {
            textBuscador = "";
        }
        if (inputDisponible == null) {
            inputDisponible = 1;
        }
        boolean disponibilidad;
        disponibilidad = inputDisponible != 0;
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
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        page = platoService.findPaginated2(pageNo, pageSize, restaurante.getIdrestaurante(), idcategoria, textBuscador, disponibilidad, inputPMin * 15 - 15, inputPMax * 15);
        listaPlatos = page.getContent();

        model.addAttribute("texto", textBuscador);
        model.addAttribute("textoD", inputDisponible);
        model.addAttribute("textoP", inputPrecio);

        List<Categorias> listaCategorias = restaurante.getCategoriasRestaurante();
        model.addAttribute("listaCategorias", listaCategorias);
        model.addAttribute("idcategoria", idcategoria);
        for (Categorias categoria : listaCategorias) {
            if (categoria.getIdcategoria() == idcategoria) {
                System.out.println(categoria.getNombre());
                model.addAttribute("nombreCate", categoria.getNombre());
                break;
            }
        }
        System.out.println(pageNo + "\n" + pageSize + "\n" + textBuscador + "\n" + disponibilidad + "\n" + inputPMin + "\n" + inputPMax + "\n" + idcategoria);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listaPlatos", listaPlatos);
        List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
        model.addAttribute("listaNotiRest",listaNotificacion);

        return "AdminRestaurante/listaPlatos";

    }

    @GetMapping("/nuevo")
    public String crearPlato(@ModelAttribute("plato") Plato plato,
                             Model model, @RequestParam(value = "idcategoria", required = false) Integer idcategoria, HttpSession session) {
        model.addAttribute("listaCategoria", categoriaExtraRepository.findAll());
        model.addAttribute("idcategoria", idcategoria);
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        List<Categorias> listaCategorias = restaurante.getCategoriasRestaurante();
        List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
        model.addAttribute("listaNotiRest",listaNotificacion);
        model.addAttribute("listaCategorias", listaCategorias);
        for (Categorias lista : listaCategorias) {
            if (lista.getIdcategoria() == idcategoria) {
                model.addAttribute("nombreCate", lista.getNombre());
                break;
            }
        }
        return "/AdminRestaurante/nuevoPlato";
    }

    @PostMapping("/guardar")
    public String guardarPlato(@ModelAttribute("plato") @Valid Plato plato, BindingResult bindingResult,
                               @RequestParam(value = "photo", required = false) MultipartFile file, RedirectAttributes attr, Model model,
                               HttpSession session, @RequestParam(value = "idcategoria", required = false) Integer idcategoria) {
        model.addAttribute("listaCategoria", categoriaExtraRepository.findAll());
        String fileName = "";
        model.addAttribute("idcategoria", idcategoria);

        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
        model.addAttribute("listaNotiRest",listaNotificacion);
        List<Categorias> listaCategorias = restaurante.getCategoriasRestaurante();
        model.addAttribute("listaCategorias", listaCategorias);
        for (Categorias lista : listaCategorias) {
            if (lista.getIdcategoria() == idcategoria) {
                model.addAttribute("nombreCate", lista.getNombre());
                break;
            }
        }

        plato.setIdrestaurante(restaurante.getIdrestaurante());
        plato.setIdcategoriaplato(idcategoria);
        plato.setDisponible(true);

        boolean validarFoto = true;

        if (file!=null){
            System.out.println("No soy nul 1111111111111111111111111111111111111111111");
            System.out.println(file);
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
                model.addAttribute("mensajefoto","No se premite '..' een el archivo");
                return "/AdminRestaurante/nuevoPlato";
            }
        }



        if (bindingResult.hasErrors()) {
            if (plato.getIdplato() == 0&&!validarFoto) {
                return "/AdminRestaurante/nuevoPlato";
            }
            if(plato.getIdplato() == 0 ){
                return "/AdminRestaurante/nuevoPlato";
            }else {
                Optional<Plato> platoOptional = platoRepository.findById(plato.getIdplato() );
                if (platoOptional.isPresent()) {
                    return "/AdminRestaurante/nuevoPlato";
                } else {

                    return "redirect:/plato/lista?idcategoria="+idcategoria;
                }
            }
        } else if (validarFoto){
            if (plato.getIdplato() == 0) {
                try{
                    plato.setFoto(file.getBytes());
                    plato.setFotonombre(fileName);
                    plato.setFotocontenttype(file.getContentType());
                    platoRepository.save(plato);
                }catch (IOException e){
                    e.printStackTrace();
                    model.addAttribute("idcategoria",idcategoria);
                    model.addAttribute("mensajefoto","Ocurrió un error al subir el archivo");
                    return "/AdminRestaurante/nuevoPlato";
                }

            } else {
                Optional<Plato> optionalPlato = platoRepository.findById(plato.getIdplato());
                if (optionalPlato.isPresent()) {
                    Optional<Plato> platoOptional=platoRepository.findById(plato.getIdplato());
                    plato.setFoto(platoOptional.get().getFoto());
                    plato.setFotonombre(platoOptional.get().getFotonombre());
                    plato.setFotocontenttype(platoOptional.get().getFotocontenttype());
                    platoRepository.save(plato);
                }
            }
            return "redirect:/plato/lista?idcategoria="+idcategoria;
        }
        else {
            return "/AdminRestaurante/nuevoPlato";
        }
    }


    @GetMapping("/imagen/{id}")
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable("id") int id) {
        Optional<Plato> optionalPlato = platoRepository.findById(id);
        if (optionalPlato.isPresent()) {
            Plato p = optionalPlato.get();
            byte[] imagenBytes = p.getFoto();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(p.getFotocontenttype()));
            return new ResponseEntity<>(imagenBytes, httpHeaders, HttpStatus.OK);
        } else {
            return null;
        }
    }

    @GetMapping("/imagenC/{id}")
    public ResponseEntity<byte[]> mostrarImagenCategoria(@PathVariable("id") int id) {
        Optional<Categorias> optional = categoriaRespository.findById(id);
        if (optional.isPresent()) {
            Categorias p = optional.get();
            byte[] imagenBytes = p.getFoto();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(p.getFotocontenttype()));
            return new ResponseEntity<>(imagenBytes, httpHeaders, HttpStatus.OK);
        } else {
            return null;
        }
    }

    @GetMapping("/editar")
    public String editarPlato(@RequestParam("id") int id,
                              Model model,
                              @ModelAttribute("plato") Plato plato,
                              @RequestParam(value = "idcategoria", required = false) Integer idcategoria, HttpSession session) {
        Optional<Plato> platoOptional = platoRepository.findById(id);
        Optional<Categorias> listaca = categoriaRespository.findById(idcategoria);
        model.addAttribute("idcategoria", idcategoria);
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int idr = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(idr);
        List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
        model.addAttribute("listaNotiRest",listaNotificacion);
        List<Categorias> listaCategorias = restaurante.getCategoriasRestaurante();
        model.addAttribute("listaCategorias", listaCategorias);
        for (Categorias lista : listaCategorias) {
            if (lista.getIdcategoria() == idcategoria) {
                model.addAttribute("nombreCate", lista.getNombre());
                break;
            }
        }
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
    public String borrarPlato(@RequestParam("id") int id,
                              RedirectAttributes attr, @RequestParam(value = "idcategoria", required = false) Integer idcategoria,
                              Model model, HttpSession session) {
        Optional<Plato> platoOptional = platoRepository.findById(id);
        model.addAttribute("idcategoria", idcategoria);
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int idr = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(idr);
        List<NotifiRestDTO> listaNotificacion= pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(),3);
        model.addAttribute("listaNotiRest",listaNotificacion);
        List<Categorias> listaCategorias = restaurante.getCategoriasRestaurante();
        model.addAttribute("listaCategorias", listaCategorias);
        for (Categorias lista : listaCategorias) {
            if (lista.getIdcategoria() == idcategoria) {
                model.addAttribute("nombreCate", lista.getNombre());
                break;
            }
        }
        if (platoOptional.isPresent()) {
            Plato plato = platoOptional.get();
            plato.setDisponible(false);
            platoRepository.save(plato);
            attr.addFlashAttribute("msg", "Plato borrado exitosamente");
            attr.addFlashAttribute("tipo", "borrado");
        }

        return "redirect:/plato/lista?idcategoria=" + idcategoria;
    }

}
