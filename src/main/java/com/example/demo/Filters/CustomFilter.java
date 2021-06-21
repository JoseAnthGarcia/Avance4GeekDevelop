package com.example.demo.Filters;

import com.example.demo.entities.Restaurante;
import com.example.demo.entities.Usuario;
import com.example.demo.repositories.RestauranteRepository;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@Component
public class CustomFilter implements javax.servlet.Filter {
    final
    RestauranteRepository restauranteRepository;

    public CustomFilter(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response= (HttpServletResponse) resp;
        HttpSession session= request.getSession();
        Usuario adminRest = (Usuario) session.getAttribute("usuario");
        int id = adminRest.getIdusuario();
        System.out.println(id);

        Restaurante restaurante = restauranteRepository.encontrarRest(id);
        System.out.println(restaurante.getEstado()+"shskjgd###############");
        if(restaurante.getEstado()==0 || restaurante.getEstado()==2){
         response.sendRedirect(request.getContextPath()+"/login");
        }else {
            chain.doFilter(req, resp);
        }
    }
}
