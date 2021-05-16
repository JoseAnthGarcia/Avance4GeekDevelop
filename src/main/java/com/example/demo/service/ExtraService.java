package com.example.demo.service;

import com.example.demo.dtos.ExtraDTO;
import com.example.demo.entities.Extra;
import com.example.demo.entities.Plato;
import org.springframework.data.domain.Page;

public interface ExtraService {

    Page<Extra> findPaginated(int pageNo, int pageSize);

    Page<Extra> findPaginated2(int pageNo, int pageSize,int idrestaurante,int idcategoria, String nombre, double inputPMin, double inputPMax);
}
