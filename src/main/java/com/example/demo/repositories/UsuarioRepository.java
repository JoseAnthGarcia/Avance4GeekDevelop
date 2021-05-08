package com.example.demo.repositories;

import com.example.demo.entities.Rol;
import com.example.demo.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    //List<Usuario> findByEstadoAndRolOrderByFecharegistroAsc(int estado, Rol rol);

    List<Usuario> findByEstadoAndRolOrderByFecharegistroAsc(int estado, Rol rol);

    @Query(value = "select * from usuario where estado = 'bloqueado' or estado = 'activo' ", nativeQuery = true)
    List<Usuario> listaUsuariosAceptados();

    @Query(value = "select datediff(now(),min(fechaRegistro)) from usuario", nativeQuery = true)
    int buscarFechaMinimaRepartidor();

    @Query(value = "select min(movilidad) from usuario", nativeQuery = true)
    int buscarIdMovilidadMinimaRepartidor();

    @Query(value = "select u.* from usuario u\n" +
            "left join movilidad m on u.movilidad = m.idmovilidad\n" +
            "where u.rol='repartidor' and\n" +
            "u.estado = 'PENDIENTE' and \n" +
            "lower(u.nombres) like %?1% and\n" +
            "u.fechaRegistro>= DATE_ADD(now(), INTERVAL ?2 DAY)", nativeQuery = true)
    List<Usuario> buscarRepartidoresSinMovilidad(String nombres, int fechaRegistro);

    @Query(value = "select u.* from usuario u\n" +
            "left join movilidad m on u.movilidad = m.idmovilidad\n" +
            "where u.rol='repartidor' and\n" +
            "u.estado = 'PENDIENTE' and \n" +
            "lower(u.nombres) like %?1% and\n" +
            "u.fechaRegistro>= DATE_ADD(now(), INTERVAL ?2 DAY) and m.idtipomovilidad = ?3", nativeQuery = true)
    List<Usuario> buscarRepartidoresConMovilidad(String nombres, int fechaRegistro, int idMovilidad);

}
