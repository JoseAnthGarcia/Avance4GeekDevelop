package com.example.demo.repositories;

import com.example.demo.entities.Usuario_has_distrito;
import com.example.demo.entities.Usuario_has_distritoKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Usuario_has_distritoRepository extends JpaRepository<Usuario_has_distrito, Usuario_has_distritoKey> {
}
