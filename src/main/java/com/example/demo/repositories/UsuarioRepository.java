package com.example.demo.repositories;

import com.example.demo.entities.Rol;
import com.example.demo.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.math.BigDecimal;
import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    //List<Usuario> findByEstadoAndRolOrderByFecharegistroAsc(int estado, Rol rol);

    Page<Usuario> findByEstadoAndRolOrderByFecharegistroAsc(int estado, Rol rol, Pageable pageable);


    @Query(value = "select * from usuario where dni = ?1", nativeQuery = true)
    Usuario findByDni (String dni);
    @Query(value = "select * from usuario where telefono = ?1", nativeQuery = true)
    Usuario findByTelefono (String telefono);


    @Query(value =" select * from usuario u, rol r where u.idrol = r.idrol and (u.estado = 0 or u.estado = 1) and r.tipo != 'administradorG' " , nativeQuery = true)
    List<Usuario> listaUsuarios();

    @Query(value = "select datediff(now(),min(fechaRegistro)) from usuario", nativeQuery = true)
    int buscarFechaMinimaRepartidor();

    @Query(value = "select min(movilidad) from usuario", nativeQuery = true)
    int buscarIdMovilidadMinimaRepartidor();

    @Query(value = "select * from usuario \n" +
            "where idrol=3 and estado = 2 and\n" +
            "(lower(nombres) like %?1% or lower(apellidos) like %?2%) and (dni like %?3%)\n" +
            "and (fechaRegistro>= DATE_ADD(now(), INTERVAL ?4 DAY))", nativeQuery = true)
    Page<Usuario> buscarAdministradorR(String nombres, String apellidos, String dni, int fechaRegistro, Pageable pageable);


    @Query(value = "select us.* from usuario us \n" +
            "left join movilidad m on us.idmovilidad = m.idmovilidad\n" +
            "where us.idrol=4 and\n" +
            "us.estado = 2 and\n" +
            "(lower(us.nombres) like %?1% or lower(us.apellidos) like %?2%) and\n" +
            "us.fechaRegistro>= DATE_ADD(now(), INTERVAL ?3 DAY)", nativeQuery = true)
    Page<Usuario> buscarRepartidoresSinMovilidad(String nombres, String apellidos, int fechaRegistro, Pageable pageable);

    @Query(value = "select u.* from usuario u\n" +
            "left join movilidad m on u.idmovilidad = m.idmovilidad\n" +
            "where u.idrol=4 and\n" +
            "u.estado = 2 and\n" +
            "(lower(u.nombres) like %?1% or lower(u.apellidos) like %?2%) and\n" +
            "u.fechaRegistro>= DATE_ADD(now(), INTERVAL ?3 DAY) and m.idtipomovilidad = ?4", nativeQuery = true)
    Page<Usuario> buscarRepartidoresConMovilidad(String nombres,String apellidos, int fechaRegistro, int idMovilidad, Pageable pageable);

    Usuario findByCorreo(String correo);

    List<Usuario> findUsuarioByCorreo(String correo);
    List<Usuario> findUsuarioByDni(String dni);
    List<Usuario> findUsuarioByTelefono(String telefono);

    //muestra la ganancia de un repartidor - la ganancia de un repartidor depende del atributo mismo distrito, entonces la ganancia sería
    //la cantidad de pedidos que tiene en un distrito *4 + la cantidad de pedidos que tiene fuera *6
    @Query(value = "SELECT ((select count(p.codigo) from pedido p, usuario u where p.mismodistrito = 1 and u.idusuario = p.idrepartidor and u.idusuario = 3)*4 + (select count(p.codigo) from pedido p, usuario u where p.mismodistrito = 0 and u.idusuario = p.idrepartidor and u.idusuario = 3)*6) as `ganancia` \n" +
            "FROM usuario u, rol r, pedido p \n" +
            "where u.idrol = r.idrol and u.idusuario = p.idrepartidor and r.idrol = 4 and u.idusuario = ?1 \n" +
            "group by u.idusuario ", nativeQuery = true)
    BigDecimal gananciaRepartidor(int idrepartidor);

    //promedio de valoracion repartidor
    @Query(value = "select ceil(avg(p.valoracionrepartidor)) as `valoracion` from usuario u, pedido p, rol r\n" +
            "where u.idrol = r.idrol and u.idusuario = p.idrepartidor and r.idrol = 4 and u.idusuario = 8 ", nativeQuery = true)
    int valoracionRepartidor(int idrepartidor);


    @Query(value = "select * from usuario u, rol r where u.idrol = r.idrol and concat(lower(u.nombres),lower(u.apellidos)) like %?1%\n" +
            "and (u.fechaRegistro >= DATE_ADD(now(), INTERVAL ?2 DAY)) and u.idrol = ?3 and u.estado = ?4 ",nativeQuery = true)
    List<Usuario> buscadorUsuario(String texto, int fechaRegistro, int idRol, int estado);

    @Query(value = "select * from usuario u, rol r where u.idrol = r.idrol and concat(lower(u.nombres),lower(u.apellidos)) like %?1%\n" +
            "and (u.fechaRegistro >= DATE_ADD(now(), INTERVAL ?2 DAY)) and u.estado = ?3 ",nativeQuery = true)
    List<Usuario> buscadorUsuarioSinRol(String texto, int fechaRegistro, int idRol);

    @Query(value = "select * from usuario u, rol r where u.idrol = r.idrol and concat(lower(u.nombres),lower(u.apellidos)) like %?1%\n" +
            "and (u.fechaRegistro >= DATE_ADD(now(), INTERVAL ?2 DAY)) and u.estado = ?3 ",nativeQuery = true)
    List<Usuario> buscadorUsuarioSinEstado(String texto, int fechaRegistro, int estado);

    @Query(value = "select * from usuario u, rol r where u.idrol = r.idrol and concat(lower(u.nombres),lower(u.apellidos)) like %?1%\n" +
            "and (u.fechaRegistro >= DATE_ADD(now(), INTERVAL ?2 DAY)) ",nativeQuery = true)
    List<Usuario> buscadorUsuarioSinEstadoNiRol(String texto, int fechaRegistro);
}
