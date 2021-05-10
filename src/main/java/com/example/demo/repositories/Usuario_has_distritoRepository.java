package com.example.demo.repositories;

import com.example.demo.entities.Usuario_has_distrito;
import com.example.demo.entities.Usuario_has_distritoKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Usuario_has_distritoRepository extends JpaRepository<Usuario_has_distrito, Usuario_has_distritoKey> {

    @Query(value = "select * from usuario_has_distrito where idusuario =  ?1",nativeQuery = true)
    List<Usuario_has_distrito> direccionesPorUsuario(int id);
}
