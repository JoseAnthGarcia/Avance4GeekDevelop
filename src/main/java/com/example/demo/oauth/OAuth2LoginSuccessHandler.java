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


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication ) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        //Map<String, Object> atributis = oAuth2User.getAttributes();
        String email = oAuth2User.getEmail();
        System.out.println("Email del usuario : "+ email);
        String nombre = oAuth2User.getName();
        Usuario usuario=usuarioRepository.findByCorreo(email);


        if(usuario == null){
            //register
            createNewUserAfterOAuthLoginSuccess(email, nombre);
        }else{
            //update

            updateNewUserAfterOAuthLoginSuccess(usuario, nombre);

        }


        super.onAuthenticationSuccess(request, response, authentication);
    }

    public void createNewUserAfterOAuthLoginSuccess(String email, String name){

        Usuario usuario = new Usuario();
        //usuario.setEmail(email);
        //usuario.setName(name);
        usuarioRepository.save(usuario);

    }
    public void updateNewUserAfterOAuthLoginSuccess(Usuario usuario, String name){

        //usuario.setName(name);
        usuarioRepository.save(usuario);

    }
}
