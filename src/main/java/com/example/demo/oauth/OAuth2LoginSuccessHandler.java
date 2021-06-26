package com.example.demo.oauth;

import com.example.demo.entities.Usuario;
import com.example.demo.repositories.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    HttpSession httpSession;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByCorreo(oAuth2User.getEmail());

        if(usuario==null) {
            authentication.setAuthenticated(false);
            httpSession.setAttribute("noExisteCuentaGoogle", true);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

}
