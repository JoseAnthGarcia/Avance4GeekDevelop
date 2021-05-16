package com.example.demo.repositories;

import com.example.demo.entities.Usuario;
import com.example.demo.entities.Ubicacion;
import com.example.demo.entities.Usuario_has_distritoKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {

    List<Ubicacion> findByUsuario(Usuario usuario);

}
