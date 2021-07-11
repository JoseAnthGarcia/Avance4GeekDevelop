package com.example.demo.repositories;

import com.example.demo.entities.Validarcorreo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidarCorreoRepository extends JpaRepository<Validarcorreo,Integer> {
    Validarcorreo findByHash(String hash);
    Validarcorreo findByUsuario_CorreoAndHash(String correo, String hash);
}
