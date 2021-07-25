package com.example.demo.controller;

import com.example.demo.dtos.*;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
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
    UsuarioRepository usuarioRepository;
    @Autowired
    ExtraService extraService;
    @Autowired
    RestauranteRepository restauranteRepository;
    @Autowired
    CategoriaExtraRepository categoriaExtraRepository;
    @Autowired
    PedidoRepository pedidoRepository;


    @GetMapping(value = {"/categoria", ""})
    public String listaCategorias(Model model, @RequestParam(value = "idcategoria", required = false) Integer id, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int idadmin = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(idadmin);
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        List<CredencialRest1DTO> credencialRest1DTOS=pedidoRepository.credencialRest(restaurante.getIdrestaurante());
        List<CredencialRest2DTO> platoTOP = pedidoRepository.platoMasVendido(restaurante.getIdrestaurante());
        List<CredencialRest2DTO> platoDOWN= pedidoRepository.platoMenosVendido(restaurante.getIdrestaurante());
        List<CredencialPedidosDTO> pedidosDTOList= pedidoRepository.pedidosCredencia(restaurante.getIdrestaurante());
        model.addAttribute("credencial1", credencialRest1DTOS);
        model.addAttribute("platoMasVendido", platoTOP);
        model.addAttribute("platoMenosVendido", platoDOWN);
        model.addAttribute("pedidosCredenciales",pedidosDTOList);
        return "AdminRestaurante/extras";
    }

    @GetMapping("/lista")
    public String listarExtra(Model model, @RequestParam(value = "idcategoria", required = false) String id, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int idadmin = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(idadmin);
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        List<CredencialRest1DTO> credencialRest1DTOS=pedidoRepository.credencialRest(restaurante.getIdrestaurante());
        List<CredencialRest2DTO> platoTOP = pedidoRepository.platoMasVendido(restaurante.getIdrestaurante());
        List<CredencialRest2DTO> platoDOWN= pedidoRepository.platoMenosVendido(restaurante.getIdrestaurante());
        List<CredencialPedidosDTO> pedidosDTOList= pedidoRepository.pedidosCredencia(restaurante.getIdrestaurante());
        model.addAttribute("credencial1", credencialRest1DTOS);
        model.addAttribute("platoMasVendido", platoTOP);
        model.addAttribute("platoMenosVendido", platoDOWN);
        model.addAttribute("pedidosCredenciales",pedidosDTOList);
        int idcategoria;
        if (id == null) {
            return "redirect:/extra/categoria";
        } else {
            try {
                idcategoria=Integer.parseInt(id);
                return findPaginated("", "0", "1", id, model, session);
            }catch(NumberFormatException e){
                return "redirect:/extra/categoria";
            }
        }
    }


    @GetMapping("/page")
    public String findPaginated(@ModelAttribute @RequestParam(value = "textBuscador", required = false) String textBuscador,
                                @ModelAttribute @RequestParam(value = "textPrecio", required = false) String inputPrecio,
                                @RequestParam(value = "pageNo", required = false) String pageNo1,
                                @RequestParam(value = "idcategoria", required = false) String idcategoria, Model model, HttpSession session) {
        System.out.println(idcategoria);
        int idcat;
        if (idcategoria == null) {
            return "redirect:/extra";
        } else {
            try {
                idcat = Integer.parseInt(idcategoria);
                if (idcat >= 1 && idcat <= 4) {
                    Usuario adminRest = (Usuario) session.getAttribute("usuario");
                    int idadmin = adminRest.getIdusuario();
                    Restaurante restaurante = restauranteRepository.encontrarRest(idadmin);
                    List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
                    model.addAttribute("listaNotiRest", listaNotificacion);
                    List<CredencialRest1DTO> credencialRest1DTOS=pedidoRepository.credencialRest(restaurante.getIdrestaurante());
                    List<CredencialRest2DTO> platoTOP = pedidoRepository.platoMasVendido(restaurante.getIdrestaurante());
                    List<CredencialRest2DTO> platoDOWN= pedidoRepository.platoMenosVendido(restaurante.getIdrestaurante());
                    List<CredencialPedidosDTO> pedidosDTOList= pedidoRepository.pedidosCredencia(restaurante.getIdrestaurante());
                    model.addAttribute("credencial1", credencialRest1DTOS);
                    model.addAttribute("platoMasVendido", platoTOP);
                    model.addAttribute("platoMenosVendido", platoDOWN);
                    model.addAttribute("pedidosCredenciales",pedidosDTOList);
                    int idrestaurante = restaurante.getIdrestaurante();
                    System.out.println(idcat);
                    System.out.println(pageNo1);
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
                    int pageSize = 10;
                    Page<Extra> page;
                    List<Extra> listaExtras;
                    System.out.println(textBuscador);
                    if (textBuscador == null) {
                        textBuscador = "";
                    }
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
                            return "redirect:/extra/lista";
                        } else {
                            inputPMin = inputPrecioInt;
                            inputPMax = inputPrecioInt;
                        }
                    } catch (NumberFormatException e) {
                        return "redirect:/extra/lista";
                    }

                    page = extraService.findPaginated2(pageNo, pageSize, idrestaurante, idcat, textBuscador, inputPMin * 5 - 5, inputPMax * 5);
                    listaExtras = page.getContent();
                    List<CategoriaExtra> listaCategoriaExtra = categoriaExtraRepository.findAll();
                    model.addAttribute("texto", textBuscador);
                    model.addAttribute("textoP", inputPrecio);

                    model.addAttribute("currentPage", pageNo);
                    model.addAttribute("totalPages", page.getTotalPages());
                    model.addAttribute("totalItems", page.getTotalElements());
                    model.addAttribute("listaExtras", listaExtras);
                    model.addAttribute("listaCategoria", listaCategoriaExtra);
                    model.addAttribute("idcategoria", idcat);

                    return "AdminRestaurante/listaExtras";
                } else {
                    return "redirect:/extra/categoria";
                }
            } catch (NumberFormatException | MethodArgumentTypeMismatchException e) {
                return "redirect:/extra/categoria";
            }
        }
    }

    @GetMapping("/nuevo")
    public String nuevoExtra(@ModelAttribute("extra") Extra extra, @RequestParam(value = "idcategoria") int id, Model model, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int idadmin = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(idadmin);
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        List<CredencialRest1DTO> credencialRest1DTOS=pedidoRepository.credencialRest(restaurante.getIdrestaurante());
        List<CredencialRest2DTO> platoTOP = pedidoRepository.platoMasVendido(restaurante.getIdrestaurante());
        List<CredencialRest2DTO> platoDOWN= pedidoRepository.platoMenosVendido(restaurante.getIdrestaurante());
        List<CredencialPedidosDTO> pedidosDTOList= pedidoRepository.pedidosCredencia(restaurante.getIdrestaurante());
        model.addAttribute("credencial1", credencialRest1DTOS);
        model.addAttribute("platoMasVendido", platoTOP);
        model.addAttribute("platoMenosVendido", platoDOWN);
        model.addAttribute("pedidosCredenciales",pedidosDTOList);
        model.addAttribute("idcategoria", id);
        return "AdminRestaurante/nuevoExtra";
    }

    @PostMapping("/guardar")
    public String guardarExtra(@ModelAttribute("extra") @Valid Extra extra, BindingResult bindingResult,
                               RedirectAttributes attr,
                               Model model, @RequestParam(value = "photo", required = false) MultipartFile file
            , HttpSession session, @RequestParam(value = "idcategoria") int idc) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int idadmin = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(idadmin);
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        List<CredencialRest1DTO> credencialRest1DTOS=pedidoRepository.credencialRest(restaurante.getIdrestaurante());
        List<CredencialRest2DTO> platoTOP = pedidoRepository.platoMasVendido(restaurante.getIdrestaurante());
        List<CredencialRest2DTO> platoDOWN= pedidoRepository.platoMenosVendido(restaurante.getIdrestaurante());
        List<CredencialPedidosDTO> pedidosDTOList= pedidoRepository.pedidosCredencia(restaurante.getIdrestaurante());
        model.addAttribute("credencial1", credencialRest1DTOS);
        model.addAttribute("platoMasVendido", platoTOP);
        model.addAttribute("platoMenosVendido", platoDOWN);
        model.addAttribute("pedidosCredenciales",pedidosDTOList);
        int idrestaurante = restaurante.getIdrestaurante();
        boolean bool=false;
        List<Extra>listaTotalExtras= extraRepository.findByIdrestauranteAndDisponible(idrestaurante,true);
        if(extra.getIdextra()==0) {
            for (Extra lista : listaTotalExtras) {
                if (lista.getNombre().equalsIgnoreCase(extra.getNombre())) {
                    model.addAttribute("mensajerepetido", "Este nombre del extra ya se encuentra registrado en el restaurante");
                    bool = true;
                    break;
                }
            }
        }
        String fileName = "";
        boolean validarFoto = true;
        if (file != null) {
            if (file.isEmpty()) {
                model.addAttribute("mensajefoto", "Debe subir una imagen");
                validarFoto = false;
            } else if (!file.getContentType().contains("jpeg") && !file.getContentType().contains("png") && !file.getContentType().contains("web")) {
                model.addAttribute("mensajefoto", "Ingrese un formato de imagen válido (p.e. JPEG,PNG o WEBP)");
                validarFoto = false;
            }
            fileName = file.getOriginalFilename();
            if (fileName.contains("..")) {
                model.addAttribute("idcategoria", idc);
                model.addAttribute("mensajefoto", "No se permite '..' en el archivo");
                return "AdminRestaurante/nuevoExtra";
            }
        }

        extra.setIdrestaurante(idrestaurante); //Jarcodeado
        extra.setDisponible(true); //default expresion !!!!
        extra.setIdcategoriaextra(idc);
        if (bindingResult.hasErrors() || bool) {
            if (extra.getIdextra() == 0 && !validarFoto) {
                model.addAttribute("idcategoria", idc);
                return "AdminRestaurante/nuevoExtra";
            }
            if (extra.getIdextra() == 0) {
                model.addAttribute("idcategoria", idc);
                return "AdminRestaurante/nuevoExtra";
            } else {
                Optional<Extra> optExtra = extraRepository.findById(extra.getIdextra());
                if (optExtra.isPresent()) {
                    model.addAttribute("idcategoria", idc);
                    return "AdminRestaurante/nuevoExtra";
                } else {
                    model.addAttribute("idcategoria", idc);
                    return "redirect:/extra/lista?idcategoria=" + idc;
                }
            }
        } else if (validarFoto) {
            if (extra.getIdextra() == 0) {
                try {
                    extra.setFoto(file.getBytes());
                    extra.setFotonombre(fileName);
                    extra.setFotocontenttype(file.getContentType());
                    attr.addFlashAttribute("msg", "Extra creado exitosamente");
                    model.addAttribute("idcategoria", idc);
                    extraRepository.save(extra);
                } catch (IOException e) {
                    e.printStackTrace();
                    model.addAttribute("idcategoria", idc);
                    model.addAttribute("mensajefoto", "Ocurrió un error al subir el archivo");
                    return "AdminRestaurante/nuevoExtra";
                }

            } else {
                Optional<Extra> optExtra = extraRepository.findById(extra.getIdextra());
                if (optExtra.isPresent()) {
                    Optional<Extra> extraOptional = extraRepository.findById(extra.getIdextra());
                    extra.setFoto(extraOptional.get().getFoto());
                    extra.setFotonombre(extraOptional.get().getFotonombre());
                    extra.setFotocontenttype(extraOptional.get().getFotocontenttype());
                    extraRepository.save(extra);
                    attr.addFlashAttribute("msg2", "Extra editado exitosamente");
                }
            }
            model.addAttribute("idcategoria", idc);
            return "redirect:/extra/lista?idcategoria=" + idc;
        } else {
            return "AdminRestaurante/nuevoExtra";
        }
    }


    @GetMapping("/imagen/{id}")
    public ResponseEntity<byte[]> mostrarImagen(@PathVariable("id") int id) {
        Optional<Extra> optionalExtra = extraRepository.findById(id);
        if (optionalExtra.isPresent()) {
            Extra extra = optionalExtra.get();
            byte[] imagenBytes = extra.getFoto();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.parseMediaType(extra.getFotocontenttype()));
            return new ResponseEntity<>(imagenBytes, httpHeaders, HttpStatus.OK);
        } else {
            return null;
        }
    }
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

    @GetMapping("/editar")
    public String editarExtra(@RequestParam("id") String id,
                              Model model,
                              @ModelAttribute("extra") Extra extra, @RequestParam(value = "idcategoria") String idc, HttpSession session) {


        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int idadmin = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(idadmin);
        List<NotifiRestDTO> listaNotificacion = pedidoRepository.notificacionPeidosRestaurante(restaurante.getIdrestaurante(), 3);
        model.addAttribute("listaNotiRest", listaNotificacion);
        List<CredencialRest1DTO> credencialRest1DTOS=pedidoRepository.credencialRest(restaurante.getIdrestaurante());
        List<CredencialRest2DTO> platoTOP = pedidoRepository.platoMasVendido(restaurante.getIdrestaurante());
        List<CredencialRest2DTO> platoDOWN= pedidoRepository.platoMenosVendido(restaurante.getIdrestaurante());
        List<CredencialPedidosDTO> pedidosDTOList= pedidoRepository.pedidosCredencia(restaurante.getIdrestaurante());
        model.addAttribute("credencial1", credencialRest1DTOS);
        model.addAttribute("platoMasVendido", platoTOP);
        model.addAttribute("platoMenosVendido", platoDOWN);
        model.addAttribute("pedidosCredenciales",pedidosDTOList);
        int idextra;
        try {
            idextra=Integer.parseInt(id);
            model.addAttribute("idcategoria", idc);
            Optional<Extra> extraOptional = extraRepository.findById(idextra);
            Extra extra2=extraRepository.findByIdextraAndIdrestauranteAndIdcategoriaextra(idextra,restaurante.getIdrestaurante(),Integer.parseInt(idc));
            if (extraOptional.isPresent() && extra2!=null) {
                extra = extraOptional.get();
                model.addAttribute("extra", extra);
                return "AdminRestaurante/nuevoExtra";
            } else {
                return "redirect:/extra/lista?idcategoria="+idc;
            }
        }catch(NumberFormatException e){
            System.out.println("Falló");
            return "redirect:/extra/lista?idcategoria="+idc;
        }
    }

    @GetMapping("/borrar")
    public String borrarExtra(@RequestParam("id") int id, RedirectAttributes attr,
                              @RequestParam(value = "idcategoria") int idc, Model model, HttpSession session) {
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int idr = adminRest.getIdusuario();
        Restaurante restaurante = restauranteRepository.encontrarRest(idr);
        Extra extraxd = extraRepository.findByIdextraAndIdrestaurante(id, restaurante.getIdrestaurante());
        if (extraxd!=null) {
            extraxd.setDisponible(false);
            extraRepository.save(extraxd);
            attr.addFlashAttribute("msg3", "Extra borrado exitosamente");
        }
        model.addAttribute("idcategoria", idc);
        return "redirect:/extra/lista?idcategoria=" + idc;
    }
    @InitBinder("extra")
    public void validatorDataBinding(WebDataBinder binder) {
        PropertyEditorSupport integerValidator = new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {

                try {
                    System.out.println("xd1");
                    this.setValue(Double.parseDouble(text));

                    if(text.contains("E")){
                        String[] parts = text.split("E");
                        String part1 = parts[0];
                        String part2 = parts[1];

                        if((Double.parseDouble(part1)>1.7) && (Double.parseDouble(part2)>=308)){

                            this.setValue(0.0);
                        }
                        else if ((Double.parseDouble(part2)>308)){

                            this.setValue(0.0);
                        }
                        else{
                            System.out.println("Válido");
                        }
                    }
                } catch (NumberFormatException e) {

                    this.setValue(text);
                }
            }
        };
        binder.registerCustomEditor(Object.class, "preciounitario", integerValidator);
    }
}
