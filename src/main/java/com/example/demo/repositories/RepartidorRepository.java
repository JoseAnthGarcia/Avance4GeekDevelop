package com.example.demo.repositories;

import com.example.demo.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RepartidorRepository {

    /*@Query(value = "select datediff(now(),min(fechaRegistro)) from usuario", nativeQuery = true)
    int buscarFechaMinimaRepartidor();

    @Query(value = "select min(movilidad) from usuario", nativeQuery = true)
    int buscarIdMovilidadMinimaRepartidor(Pageable pageable);

    @Query(value = "select u.* from usuario u\n" +
            "left join movilidad m on u.idmovilidad = m.idmovilidad\n" +
            "where u.idrol=4 and\n" +
            "u.estado = 2 and\n" +
            "(lower(u.nombres) like %?1% or lower(u.apellidos) like %?2%) and\n" +
            "u.fechaRegistro>= DATE_ADD(now(), INTERVAL ?3 DAY)", nativeQuery = true)
    Page<Usuario> buscarRepartidoresSinMovilidad(String nombres, String apellidos, int fechaRegistro, Pageable pageable);

    @Query(value = "select u.* from usuario u\n" +
            "left join movilidad m on u.idmovilidad = m.idmovilidad\n" +
            "where u.idrol=4 and\n" +
            "u.estado = 2 and\n" +
            "(lower(u.nombres) like %?1% or lower(u.apellidos) like %?2%) and\n" +
            "u.fechaRegistro>= DATE_ADD(now(), INTERVAL ?3 DAY) and m.idtipomovilidad = ?4", nativeQuery = true)
    Page<Usuario> buscarRepartidoresConMovilidad(String nombres,String apellidos, int fechaRegistro, int idMovilidad, Pageable pageable);

*/
}
