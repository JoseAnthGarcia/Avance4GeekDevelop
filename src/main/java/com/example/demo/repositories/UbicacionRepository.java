package com.example.demo.repositories;

import com.example.demo.entities.Usuario;
import com.example.demo.entities.Ubicacion;
import com.example.demo.entities.Usuario_has_distritoKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpSession;
import java.util.List;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {

    @Query(value="select * from ubicacion where idusuario=?1 and borrado = 0",nativeQuery = true)
    List<Ubicacion> findByUsuarioVal(Usuario usuario);
}
