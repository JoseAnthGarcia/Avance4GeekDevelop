package com.example.demo.repositories;

import com.example.demo.entities.Urlcorreo;
import com.example.demo.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlCorreoRepository extends JpaRepository<Urlcorreo, Integer> {

    Urlcorreo findByCodigo(String codigo);
    Urlcorreo findByUsuario(Usuario usuario);

}
