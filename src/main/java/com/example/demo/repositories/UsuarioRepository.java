package com.example.demo.repositories;

import com.example.demo.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    List<Usuario> findByEstadoAndRolOrderByFecharegistroAsc(String estado, String rol);

    @Query(value = "select * from usuario where estado = 'bloqueado' or estado = 'activo' ", nativeQuery = true)
    List<Usuario> listaUsuariosAceptados();

}
