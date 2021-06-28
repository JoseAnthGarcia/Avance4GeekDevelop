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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
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
    UsuarioRepository usuarioRepository;
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
    @GetMapping("/imagenadmin/{id}")
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable("id") String id) {
        Optional<Usuario> usuarioOptional = Optional.ofNullable(usuarioRepository.findByDni(id));
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            byte[] imagenBytes = usuario.getFoto();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(usuario.getFotocontenttype()));
            return new ResponseEntity<>(imagenBytes, httpHeaders, HttpStatus.OK);
        } else {
            return null;
        }
    }

    @GetMapping(value = {"/categoria", ""})
    public String listaCategorias(Model model, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();

        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        List<Categorias> listaCategorias = restaurante.getCategoriasRestaurante();
        model.addAttribute("listaCategorias", listaCategorias);
        model.addAttribute("estadoRestaurante", restaurante.getEstado());
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        return "AdminRestaurante/categorias";
    }

    @GetMapping("/lista")
    public String listaPlatos(Model model, HttpSession session, @RequestParam(value = "idcategoria", required = false) String idcategoria) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        model.addAttribute("idcategoria", idcategoria);

        int idcat;
        if (idcategoria == null) {
            return "redirect:/plato/categoria";
        }else {
            try {
                idcat=Integer.parseInt(idcategoria);
                List<Categorias> listaCategorias = restaurante.getCategoriasRestaurante();
                for (Categorias lista : listaCategorias) {
                    if (lista.getIdcategoria() == idcat) {
                        model.addAttribute("nombreCate", lista.getNombre());
                        break;
                    }
                }
                return findPaginated("", 1, "0", "1" , idcategoria, restaurante.getIdrestaurante(), model, session);
            }catch(NumberFormatException e){
                return "redirect:/plato/categoria";
            }
        }
    }


    @GetMapping("/page")
    public String findPaginated(@ModelAttribute @RequestParam(value = "textBuscador", required = false) String textBuscador,
                                @ModelAttribute @RequestParam(value = "inputDisponible", required = false) Integer inputDisponible,
                                @ModelAttribute @RequestParam(value = "inputPrecio", required = false) String inputPrecio,
                                @RequestParam(value = "pageNo", required = false) String pageNo1, @RequestParam(value = "idcategoria", required = false) String idcategoria,
                                @RequestParam(value = "idrestaurante", required = false) Integer idrestaurante, Model model, HttpSession session) {

        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        List<Categorias> listaCategorias = restaurante.getCategoriasRestaurante();
        int idcat;
        if (idcategoria == null) {
            return "redirect:/plato/categoria";
        } else {
            try {
                idcat = Integer.parseInt(idcategoria);
                for (Categorias categoria : listaCategorias) {
                    if (categoria.getIdcategoria() == idcat) {
                        int pageNo = 0;
                        if (pageNo1 == null) {
                            pageNo = 1;
                        }
                        try {
                            pageNo = Integer.parseInt(pageNo1);
                            if (pageNo == 0) {
                                pageNo = 1;
                            }
                        } catch (NumberFormatException e) {
                            pageNo = 1;
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
                        int inputPrecioInt;
                        int inputPMax;
                        int inputPMin;

                        if (inputPrecio == null) {
                            inputPrecioInt = 0;
                        }

                        try {
                            inputPrecioInt = Integer.parseInt(inputPrecio);
                            if (inputPrecioInt == 0) {
                                inputPMin = 0;
                                inputPMax = 1000;
                            } else if (inputPrecioInt == 4) {
                                inputPMin = inputPrecioInt;
                                inputPMax = 1000;
                            } else if (inputPrecioInt > 4) {
                                return "redirect:/plato/lista";
                            } else {
                                inputPMin = inputPrecioInt;
                                inputPMax = inputPrecioInt;
                            }
                        } catch (NumberFormatException e) {
                            return "redirect:/plato/lista";
                        }


                        page = platoService.findPaginated2(pageNo, pageSize, restaurante.getIdrestaurante(), idcat, textBuscador, disponibilidad, inputPMin * 15 - 15, inputPMax * 15);
                        listaPlatos = page.getContent();

                        model.addAttribute("texto", textBuscador);
                        model.addAttribute("textoD", inputDisponible);
                        model.addAttribute("textoP", inputPrecio);

                        model.addAttribute("nombreCate", categoria.getNombre());
                        model.addAttribute("listaCategorias", listaCategorias);
                        model.addAttribute("idcategoria", idcategoria);
                        System.out.println(pageNo + "\n" + pageSize + "\n" + textBuscador + "\n" + disponibilidad + "\n" + inputPMin + "\n" + inputPMax + "\n" + idcategoria);

                        model.addAttribute("pageSize", pageSize);
                        model.addAttribute("currentPage", pageNo);
                        model.addAttribute("totalPages", page.getTotalPages());
                        model.addAttribute("totalItems", page.getTotalElements());
                        model.addAttribute("listaPlatos", listaPlatos);
                        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
                        model.addAttribute("listaNotiRest", listaNotificacion);

                        return "AdminRestaurante/listaPlatos";
                    }
                }
                return "redirect:/plato/categoria";

            }catch(NumberFormatException e){
                return "redirect:/plato/categoria";
            }
        }

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
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        model.addAttribute("listaCategorias", listaCategorias);
        for (Categorias lista : listaCategorias) {
            if (lista.getIdcategoria() == idcategoria) {
                model.addAttribute("nombreCate", lista.getNombre());
                break;
            }
        }
        return "AdminRestaurante/nuevoPlato";
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
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
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

        if (file != null) {
            System.out.println("No soy nul 1111111111111111111111111111111111111111111");
            System.out.println(file);
            if (file.isEmpty()) {
                model.addAttribute("mensajefoto", "Debe subir una imagen");
                validarFoto = false;
            } else if (!file.getContentType().contains("jpeg") && !file.getContentType().contains("png") && !file.getContentType().contains("web")) {

                model.addAttribute("mensajefoto", "Ingrese un formato de imagen válido (p.e. JPEG,PNG o WEBP)");
                validarFoto = false;
            }
            fileName = file.getOriginalFilename();
            if (fileName.contains("..")) {
                model.addAttribute("mensajefoto", "No se premite '..' een el archivo");
                return "AdminRestaurante/nuevoPlato";
            }
        }


        if (bindingResult.hasErrors()) {
            if (plato.getIdplato() == 0 && !validarFoto) {
                return "AdminRestaurante/nuevoPlato";
            }
            if (plato.getIdplato() == 0) {
                return "AdminRestaurante/nuevoPlato";
            } else {
                Optional<Plato> platoOptional = platoRepository.findById(plato.getIdplato());
                if (platoOptional.isPresent()) {
                    return "AdminRestaurante/nuevoPlato";
                } else {

                    return "redirect:/plato/lista?idcategoria=" + idcategoria;
                }
            }
        } else if (validarFoto) {
            if (plato.getIdplato() == 0) {
                try {
                    plato.setFoto(file.getBytes());
                    plato.setFotonombre(fileName);
                    plato.setFotocontenttype(file.getContentType());
                    attr.addFlashAttribute("msg", "Plato creado exitosamente");
                    platoRepository.save(plato);
                } catch (IOException e) {
                    e.printStackTrace();
                    model.addAttribute("idcategoria", idcategoria);
                    model.addAttribute("mensajefoto", "Ocurrió un error al subir el archivo");
                    return "AdminRestaurante/nuevoPlato";
                }

            } else {
                Optional<Plato> optionalPlato = platoRepository.findById(plato.getIdplato());
                if (optionalPlato.isPresent()) {
                    Optional<Plato> platoOptional = platoRepository.findById(plato.getIdplato());
                    plato.setFoto(platoOptional.get().getFoto());
                    plato.setFotonombre(platoOptional.get().getFotonombre());
                    plato.setFotocontenttype(platoOptional.get().getFotocontenttype());
                    attr.addFlashAttribute("msg2", "Plato editado exitosamente");
                    platoRepository.save(plato);
                }
            }
            return "redirect:/plato/lista?idcategoria=" + idcategoria;
        } else {
            return "AdminRestaurante/nuevoPlato";
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
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
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
            return "AdminRestaurante/nuevoPlato";
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
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
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
            attr.addFlashAttribute("msg3", "Plato borrado exitosamente");
            attr.addFlashAttribute("tipo", "borrado");
        }

        return "redirect:/plato/lista?idcategoria=" + idcategoria;
    }
    /*
    @InitBinder("plato")
    public void validatorDataBinding(WebDataBinder binder) {
        PropertyEditorSupport integerValidator = new PropertyEditorSupport() {
            public void setAsDouble(String precio) throws IllegalArgumentException {
                try {
                    this.setValue(Double.parseDouble(precio));
                } catch (NumberFormatException e) {
                    this.setValue(0);
                }
            }
        };
        binder.registerCustomEditor(Double.class, "precio", integerValidator);
    }
*/
}
