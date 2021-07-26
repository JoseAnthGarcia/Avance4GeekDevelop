package com.example.demo.controller;

import com.example.demo.dtos.ValidarRucDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestauranteDao {

        
    public String validarRuc(String ruc){
        RestTemplate restTemplate= new RestTemplate();

        String url="https://api.ateneaperu.com/api/Sunat/Ruc?sNroDocumento="+ruc;
        ResponseEntity<ValidarRucDTO> responseMap=restTemplate.getForEntity(url,ValidarRucDTO.class);
        ValidarRucDTO validarRucDTO= responseMap.getBody();

        return validarRucDTO.getSuccess();

    }

}
