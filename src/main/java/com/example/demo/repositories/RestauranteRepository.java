package com.example.demo.repositories;

import com.example.demo.entities.Restaurante;
import com.example.demo.entities.Rol;
import com.example.demo.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Integer> {
    Page<Restaurante> findByEstado(int estado, Pageable pageable);


    @Query(value = "select * from restaurante where idadministrador = ?1", nativeQuery = true)
    Restaurante encontrarRest (int id);

    ;

    @Query(value = "select datediff(now(),min(fechaRegistro)) from restaurante", nativeQuery = true)
    int buscarFechaMinimaRestaurante();
    //estado 2= pendiente
    //estado 1= aceptado
    //estado 3= rechazado
    @Query(value = "select * from restaurante where estado=2 and\n" +
            "(lower(nombre) like %?1% ) and (ruc like %?2%)\n" +
            "and (fechaRegistro>= DATE_ADD(now(), INTERVAL ?3 DAY))", nativeQuery = true)
    Page<Restaurante> buscarRest(String nombreRest, String ruc,int fechaRegistro, Pageable pageable);
}
