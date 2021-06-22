package com.example.demo.controller;

import com.example.demo.dtos.ValidarDniDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UsuarioDao {

    public ValidarDniDTO validarDni(String dni){
        RestTemplate restTemplate= new RestTemplate();

        String url="https://api.ateneaperu.com/api/Reniec/Dni?sNroDocumento="+dni;
        ResponseEntity<ValidarDniDTO> responseMap=restTemplate.getForEntity(url,ValidarDniDTO.class);
        ValidarDniDTO ValidarDniDTO= responseMap.getBody();

        return ValidarDniDTO;

    }
}
