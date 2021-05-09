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


    @Query(value = "select * from usuario where dni = ?1", nativeQuery = true)
    Usuario findByDni (String dni);
    @Query(value = "select * from usuario where telefono = ?1", nativeQuery = true)
    Usuario findByTelefono (String telefono);
    @Query(value = "select * from usuario where correo = ?1", nativeQuery = true)
    Usuario findByCorreo (String correo);

    @Query(value =" select * from usuario u, rol r where u.idrol = r.idrol and (u.estado = 0 or u.estado = 1) and r.tipo != 'administradorG' " , nativeQuery = true)
    List<Usuario> listaUsuarios();

    @Query(value = "select datediff(now(),min(fechaRegistro)) from usuario", nativeQuery = true)
    int buscarFechaMinimaRepartidor();

    @Query(value = "select min(movilidad) from usuario", nativeQuery = true)
    int buscarIdMovilidadMinimaRepartidor();

    @Query(value = "select u.* from usuario u\n" +
            "left join movilidad m on u.idmovilidad = m.idmovilidad\n" +
            "where u.idrol=4 and\n" +
            "u.estado = 2 and\n" +
            "(lower(u.nombres) like %?1% or lower(u.apellidos) like %?2%) and\n" +
            "u.fechaRegistro>= DATE_ADD(now(), INTERVAL ?3 DAY)", nativeQuery = true)
    List<Usuario> buscarRepartidoresSinMovilidad(String nombres,String apellidos, int fechaRegistro);

    @Query(value = "select u.* from usuario u\n" +
            "left join movilidad m on u.idmovilidad = m.idmovilidad\n" +
            "where u.idrol=4 and\n" +
            "u.estado = 2 and\n" +
            "(lower(u.nombres) like %?1% or lower(u.apellidos) like %?2%) and\n" +
            "u.fechaRegistro>= DATE_ADD(now(), INTERVAL ?3 DAY) and m.idtipomovilidad = ?4", nativeQuery = true)
    List<Usuario> buscarRepartidoresConMovilidad(String nombres,String apellidos, int fechaRegistro, int idMovilidad);

}
