package com.example.demo.repositories;

import com.example.demo.dtos.*;
import com.example.demo.entities.Rol;
import com.example.demo.entities.Ubicacion;
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

    // TODO: 12/06/2021

   /* @Query(value = "SELECT * FROM geekdevelop.usuario\n" +
            " where 1 = 1\n" +
            " LIMIT ?1,10;", nativeQuery = true)
    Page<Usuario> usuarioslistapage(Integer numero);*/


    //List<Usuario> findByEstadoAndRolOrderByFecharegistroAsc(int estado, Rol rol);

    @Query(value = "select ub.* from ubicacion ub\n" +
            "inner join usuario u on u.idusuario = ub.idusuario\n" +
            "where u.idusuario = ?1 and ub.borrado = 0 ",nativeQuery = true)
    List<Ubicacion> findUbicacionActual(int idUsuario);

    Usuario findByCorreo(String correo);
    Usuario findByCorreoAndEstado(String correo, int estado);

    List<Usuario> findByEstadoOrderByFecharegistroAsc(int estado);

    Page<Usuario> findByEstadoAndRolOrderByFecharegistroAsc(int estado, Rol rol, Pageable pageable);

    //List<Usuario> findEmployeesByEmailAndEmployeeIdNot(String email,int id);

    List<Usuario> findUsuarioByTelefonoAndIdusuarioNot(String telefono, int id);

    @Query(value = "select * from usuario where dni = ?1", nativeQuery = true)
    Usuario findByDni (String dni);
    @Query(value = "select * from usuario where telefono = ?1", nativeQuery = true)
    Usuario findByTelefono (String telefono);

    // TODO: 13/06/2021
    /*@Query(value =" select * from usuario u, rol r where u.idrol = r.idrol and (u.estado = 0 or u.estado = 1) and r.tipo != 'administradorG' " , nativeQuery = true,
            countQuery = "select count(*) from usuario u, rol r where u.idrol = r.idrol and (u.estado = 0 or u.estado = 1) and r.tipo != 'administradorG' ")
    Page<Usuario> listaUsuarios(Pageable pageable);*/

    /*@Query(value =" select * from usuario u, rol r where u.idrol = r.idrol and u.estado = ?1  and r.tipo != 'administradorG' " ,
            countQuery = "select count(*) from usuario u, rol r where u.idrol = r.idrol and u.estado = ?1 and r.tipo != 'administradorG'",nativeQuery = true)
    Page<Usuario> listaUsuarios(Integer estado, Pageable pageable);*/

    @Query(value =" SELECT * FROM geekdevelop.usuario u \n" +
            "where  concat(lower(nombres),lower(apellidos)) like %?1% \n" +
            "and idrol != 2 \n" +
            "and ( `idrol` > ?2 and `idrol` <= ?3 ) \n" +
            "and ( `estado` > ?4 and `estado` <= ?5 ) " ,nativeQuery = true ,
            countQuery = "SELECT count(*) FROM geekdevelop.usuario u \n" +
                    "where  concat(lower(nombres),lower(apellidos)) like %?1% \n" +
                    "and idrol != 2 \n" +
                    "and ( `idrol` > ?2 and `idrol` <= ?3 ) \n" +
                    "and ( `estado` > ?4 and `estado` <= ?5 )")
    Page<Usuario> listaUsuarios(String texto, Integer inFrol, Integer maXrol, Integer miFestado, Integer maXestado, Pageable pageable);



    @Query(value ="select u.nombres as 'nombrecliente' ,u.apellidos as 'apellidocliente', r.nombre as 'nombrerest', \n" +
            "pe.codigo , pe.estado  , u1.nombres as 'nombrerepartidor',u1.apellidos as 'apellidorep', pe.fechapedido \n" +
            " , pe.preciototal, pe.valoracionrestaurante as 'valoracion' from pedido pe \n" +
            "\t\t\tleft join usuario u on u.idusuario=pe.idcliente\n" +
            "            left join restaurante r on r.idrestaurante=pe.idrestaurante\n" +
            "            left join usuario u1 on u1.idusuario=pe.idrepartidor\n" +
            "            where concat(lower(u.nombres),lower(u.apellidos),lower(pe.codigo)) like %?1% and pe.idrestaurante is not null \n" +
            "            and (pe.valoracionrestaurante is null or (pe.valoracionrestaurante >?2 and pe.valoracionrestaurante<=?3))  \n" +
            "            and (pe.preciototal> ?4 and pe.preciototal<=?5)and (pe.estado> ?6 and pe.estado <=?7 )\n "+
            "            order by pe.fechapedido ASC",nativeQuery = true,
            countQuery = "select count(*) from pedido pe \n" +
                    "\t\t\tleft join usuario u on u.idusuario=pe.idcliente\n" +
                    "            left join restaurante r on r.idrestaurante=pe.idrestaurante\n" +
                    "            left join usuario u1 on u1.idusuario=pe.idrepartidor\n" +
                    "            where concat(lower(u.nombres),lower(u.apellidos),lower(pe.codigo)) like %?1% and pe.idrepartidor is not null and pe.idrestaurante is not null \n" +
                    "            and (pe.valoracionrestaurante is null or (pe.valoracionrestaurante >?2 and pe.valoracionrestaurante<=?3))  \n" +
                    "            and (pe.preciototal> ?4 and pe.preciototal<=?5)and (pe.estado> ?6 and pe.estado <=?7 )\n "+
                    "            order by pe.fechapedido ASC")
    Page<UsuarioDtoCliente> listaUsuariosDtoCliente(String texto, Integer miFval, Integer maXval, Integer inFmont, Integer maXmont, Integer miFestado, Integer maXestado, Pageable pageable);

    @Query(value = "select u.nombres  ,u.apellidos  , count(u.idusuario) as 'cantpedidos'from pedido pe\n" +
            "\t\t\tleft join usuario u on u.idusuario=pe.idcliente\n" +
            "            where pe.idrepartidor is not null and pe.idrestaurante is not null\n" +
            "            and pe.estado= 6\n" +
            "            group by u.idusuario order by count(u.idusuario) desc;",nativeQuery = true)
    List<ClienteConMasPedidosDto> listaClienteConMasPedidos();

    /*@Query(value ="select  u.nombres, u.apellidos,u.dni,u.correo,u.telefono ,pe.valoracionrepartidor , \n" +
            "count(pe.idrepartidor) as 'cantpedidos'\n" +
            ", (4*count(pe.mismodistrito = 1)+6*count(pe.mismodistrito = 0)) as 'montototal'\n" +
            "from usuario u \n" +
            "left join pedido pe on u.idusuario = pe.idrepartidor \n" +
            "where  u.idrol=4 and pe.estado = 6  \n" +
            "and    concat(lower(u.nombres),lower(u.apellidos),lower(u.dni)) like %?1%\n" +
            "and (pe.valoracionrepartidor is null or (pe.valoracionrepartidor >?2 and pe.valoracionrepartidor<=?3))\n" +
            "and ('cantpedidos' > ?4 and 'cantpedidos' <=?5)\n" +
            "and ('montototal'> ?6 and 'montototal'<=?7)\n" +
            " group by pe.idrepartidor order by u.nombres",nativeQuery = true,
            countQuery = "select  count(*)\n" +
                    "from usuario u \n" +
                    "left join pedido pe on u.idusuario = pe.idrepartidor \n" +
                    "where  u.idrol=4 and pe.estado = 6  \n" +
                    "and    concat(lower(u.nombres),lower(u.apellidos),lower(u.dni)) like %?1% \n" +
                    "and (pe.valoracionrepartidor is null or (pe.valoracionrepartidor >?2 and pe.valoracionrepartidor<=?3))\n" +
                    "and (count(pe.idrepartidor) > ?4 and count(pe.idrepartidor) <=?5)\n" +
                    "and ((4*count(pe.mismodistrito = 1)+6*count(pe.mismodistrito = 0)) > ?6 and (4*count(pe.mismodistrito = 1)+6*count(pe.mismodistrito = 0)) <=?7)\n" +
                    " group by pe.idrepartidor order by u.nombres")
    Page<UsuarioDtoRepartidor> listaUsuariosDtoRepartidor(String texto, Integer miFval, Integer maXval, Integer miFestado, Integer maXestado, Integer inFmont, Integer maXmont, Pageable pageable);

    @Query(value ="select  u.nombres, u.apellidos,u.dni,u.correo,u.telefono ,pe.valoracionrepartidor , \n" +
            "pe.cantpedidos, pe.montototal \n" +
            "from usuario u \n" +
            "left join (select p.idrepartidor, round(AVG(p.valoracionrepartidor),0) as 'valoracionrepartidor' , \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\tcount(p.idrepartidor) as 'cantpedidos', \n" +
            "\t\t\t\t((4*p1.mismodistrito1) + (6*p0.mismodistrito0)) as 'montototal' \n" +
            "                from pedido p, usuario u,  \n" +
            "                (select p.idrepartidor, count(p.idrepartidor) as 'mismodistrito1' from pedido p, usuario u \n" +
            " where  p.idrepartidor= u.idusuario and p.mismodistrito = 1 group by p.idrepartidor) p1, \n" +
            " (select p.idrepartidor, count(p.idrepartidor) as 'mismodistrito0' from pedido p, usuario u \n" +
            " where  p.idrepartidor= u.idusuario and p.mismodistrito = 0 group by p.idrepartidor) p0 \n" +
            " where p.idrepartidor= u.idusuario and p1.idrepartidor= u.idusuario and p0.idrepartidor= u.idusuario and u.idrol=4 and p.estado = 6 ) pe on u.idusuario = pe.idrepartidor \n" +
            "where  concat(lower(u.nombres),lower(u.apellidos),lower(u.dni)) like %?1% \n" +
            "and (pe.valoracionrepartidor is null or (pe.valoracionrepartidor > ?2 and pe.valoracionrepartidor <= ?3 )) \n" +
            "and (cantpedidos > ?4 and cantpedidos <= ?5 ) \n" +
            "and (montototal> ?6  and montototal<= ?7 ) \n" +
            "group by pe.idrepartidor order by u.nombres",nativeQuery = true,
            countQuery = "select  count(*) \n" +
                    "from usuario u \n" +
                    "left join (select p.idrepartidor, round(AVG(p.valoracionrepartidor),0) as 'valoracionrepartidor' , \n" +
                    "\t\t\t\t\t\t\t\t\t\t\t\tcount(p.idrepartidor) as 'cantpedidos', \n" +
                    "\t\t\t\t((4*p1.mismodistrito1) + (6*p0.mismodistrito0)) as 'montototal' \n" +
                    "                from pedido p, usuario u,  \n" +
                    "                (select p.idrepartidor, count(p.idrepartidor) as 'mismodistrito1' from pedido p, usuario u \n" +
                    " where  p.idrepartidor= u.idusuario and p.mismodistrito = 1 group by p.idrepartidor) p1, \n" +
                    " (select p.idrepartidor, count(p.idrepartidor) as 'mismodistrito0' from pedido p, usuario u \n" +
                    " where  p.idrepartidor= u.idusuario and p.mismodistrito = 0 group by p.idrepartidor) p0 \n" +
                    " where p.idrepartidor= u.idusuario and p1.idrepartidor= u.idusuario and p0.idrepartidor= u.idusuario and u.idrol=4 and p.estado = 6 ) pe on u.idusuario = pe.idrepartidor \n" +
                    "where  concat(lower(u.nombres),lower(u.apellidos),lower(u.dni)) like %?1% \n" +
                    "and (pe.valoracionrepartidor is null or (pe.valoracionrepartidor > ?2 and pe.valoracionrepartidor <= ?3 )) \n" +
                    "and (cantpedidos > ?4 and cantpedidos <= ?5 ) \n" +
                    "and (montototal> ?6  and montototal<= ?7 ) \n" +
                    "group by pe.idrepartidor order by u.nombres")
    Page<UsuarioDtoRepartidor> listaUsuariosDtoRepartidor(String texto, Integer miFval, Integer maXval, Integer miFestado, Integer maXestado, Integer inFmont, Integer maXmont, Pageable pageable);
*/
    // TODO: 18/07/2021
    @Query(value ="select concat(r.apellidos, \" \" ,r.nombres) as `repartidor`, r.dni, r.correo, r.telefono,\n" +
            "\t\tcount(p.codigo) as `cantidad`,\n" +
            "        sum(if(p.mismodistrito = 1, 4,6)) as `gananciarepartidor`,\n" +
            "         if(round(avg(p.valoracionrepartidor),0) is null,0,round(avg(p.valoracionrepartidor),0)) as `valoracion`\n" +
            "from pedido p\n" +
            "inner join usuario r on p.idrepartidor = r.idusuario\n" +
            "where concat(r.apellidos,r.nombres,r.dni) like %?1% \n" +
            "group by p.idrepartidor\n" +
            "having (sum(if(p.mismodistrito = 1, 4,6)) > ?6 and\n" +
            "\t\tsum(if(p.mismodistrito = 1, 4,6)) <= ?7 ) and\n" +
            "        (count(p.codigo) > ?4 and count(p.codigo) <= ?5 ) and\n" +
            "        (if(round(avg(p.valoracionrepartidor),0) is null,0,round(avg(p.valoracionrepartidor),0)) >= ?2 \n" +
            "        and if(round(avg(p.valoracionrepartidor),0) is null,0,round(avg(p.valoracionrepartidor),0)) <=  ?3 )\n" +
            "order by count(p.codigo) desc",nativeQuery = true,
            countQuery = "select count(*) \n" +
                    "from pedido p\n" +
                    "inner join usuario r on p.idrepartidor = r.idusuario\n" +
                    "where concat(r.apellidos,r.nombres,r.dni) like %?1% \n" +
                    "group by p.idrepartidor\n" +
                    "having (sum(if(p.mismodistrito = 1, 4,6)) > ?6 and\n" +
                    "\t\tsum(if(p.mismodistrito = 1, 4,6)) <= ?7 ) and\n" +
                    "        (count(p.codigo) > ?4 and count(p.codigo) <= ?5 ) and\n" +
                    "        (if(round(avg(p.valoracionrepartidor),0) is null,0,round(avg(p.valoracionrepartidor),0)) >= ?2 \n" +
                    "        and if(round(avg(p.valoracionrepartidor),0) is null,0,round(avg(p.valoracionrepartidor),0)) <=  ?3 )\n" +
                    "order by count(p.codigo) desc")
    Page<UsuarioDtoRepartidor> listaUsuariosDtoRepartidor(String texto, Integer miFval, Integer maXval, Integer miFestado, Integer maXestado, Integer inFmont, Integer maXmont, Pageable pageable);

    @Query(value ="select concat(r.apellidos, \" \" ,r.nombres) as `repartidor`, r.dni, r.correo, r.telefono,\n" +
            "\t\tcount(p.codigo) as `cantidad`,\n" +
            "        sum(if(p.mismodistrito = 1, 4,6)) as `gananciarepartidor`,\n" +
            "         if(round(avg(p.valoracionrepartidor),0) is null,0,round(avg(p.valoracionrepartidor),0)) as `valoracion`\n" +
            "from pedido p\n" +
            "inner join usuario r on p.idrepartidor = r.idusuario\n" +
            "where concat(r.apellidos,r.nombres,r.dni) like %?1% \n" +
            "group by p.idrepartidor\n" +
            "having (sum(if(p.mismodistrito = 1, 4,6)) > ?6 and\n" +
            "\t\tsum(if(p.mismodistrito = 1, 4,6)) <= ?7 ) and\n" +
            "        (count(p.codigo) > ?4 and count(p.codigo) <= ?5 ) and\n" +
            "        (if(round(avg(p.valoracionrepartidor),0) is null,0,round(avg(p.valoracionrepartidor),0)) >= ?2 \n" +
            "        and if(round(avg(p.valoracionrepartidor),0) is null,0,round(avg(p.valoracionrepartidor),0)) <=  ?3 )\n" +
            "order by count(p.codigo) desc",nativeQuery = true,
            countQuery = "select count(*) \n" +
                    "from pedido p\n" +
                    "inner join usuario r on p.idrepartidor = r.idusuario\n" +
                    "where concat(r.apellidos,r.nombres,r.dni) like %?1% \n" +
                    "group by p.idrepartidor\n" +
                    "having (sum(if(p.mismodistrito = 1, 4,6)) > ?6 and\n" +
                    "\t\tsum(if(p.mismodistrito = 1, 4,6)) <= ?7 ) and\n" +
                    "        (count(p.codigo) > ?4 and count(p.codigo) <= ?5 ) and\n" +
                    "        (if(round(avg(p.valoracionrepartidor),0) is null,0,round(avg(p.valoracionrepartidor),0)) >= ?2 \n" +
                    "        and if(round(avg(p.valoracionrepartidor),0) is null,0,round(avg(p.valoracionrepartidor),0)) <=  ?3 )\n" +
                    "order by count(p.codigo) desc")
    List<UsuarioDtoRepartidor> listaUsuariosDtoRepartidorMas(String texto, Integer miFval, Integer maXval, Integer miFestado, Integer maXestado, Integer inFmont, Integer maXmont);

    @Query(value ="select concat(r.apellidos, \" \" ,r.nombres) as `repartidor`, r.dni, r.correo, r.telefono,\n" +
            "\t\tcount(p.codigo) as `cantidad`,\n" +
            "        sum(if(p.mismodistrito = 1, 4,6)) as `gananciarepartidor`,\n" +
            "         if(round(avg(p.valoracionrepartidor),0) is null,0,round(avg(p.valoracionrepartidor),0)) as `valoracion`\n" +
            "from pedido p\n" +
            "inner join usuario r on p.idrepartidor = r.idusuario\n" +
            "where concat(r.apellidos,r.nombres,r.dni) like %?1% \n" +
            "group by p.idrepartidor\n" +
            "having (sum(if(p.mismodistrito = 1, 4,6)) > ?6 and\n" +
            "\t\tsum(if(p.mismodistrito = 1, 4,6)) <= ?7 ) and\n" +
            "        (count(p.codigo) > ?4 and count(p.codigo) <= ?5 ) and\n" +
            "        (if(round(avg(p.valoracionrepartidor),0) is null,0,round(avg(p.valoracionrepartidor),0)) >= ?2 \n" +
            "        and if(round(avg(p.valoracionrepartidor),0) is null,0,round(avg(p.valoracionrepartidor),0)) <=  ?3 )\n" +
            "order by count(p.codigo) asc ",nativeQuery = true,
            countQuery = "select count(*) \n" +
                    "from pedido p\n" +
                    "inner join usuario r on p.idrepartidor = r.idusuario\n" +
                    "where concat(r.apellidos,r.nombres,r.dni) like %?1% \n" +
                    "group by p.idrepartidor\n" +
                    "having (sum(if(p.mismodistrito = 1, 4,6)) > ?6 and\n" +
                    "\t\tsum(if(p.mismodistrito = 1, 4,6)) <= ?7 ) and\n" +
                    "        (count(p.codigo) > ?4 and count(p.codigo) <= ?5 ) and\n" +
                    "        (if(round(avg(p.valoracionrepartidor),0) is null,0,round(avg(p.valoracionrepartidor),0)) >= ?2 \n" +
                    "        and if(round(avg(p.valoracionrepartidor),0) is null,0,round(avg(p.valoracionrepartidor),0)) <=  ?3 )\n" +
                    "order by count(p.codigo) asc ")
    List<UsuarioDtoRepartidor> listaUsuariosDtoRepartidorMenos(String texto, Integer miFval, Integer maXval, Integer miFestado, Integer maXestado, Integer inFmont, Integer maXmont);

    // TODO: 26/06/2021
    /*@Query(value ="select r.nombre, r.ruc, r.estado, pe.mismodistrito1, pe.mismodistrito0 ,pe.valoracionrestaurante , \n" +
            "pe.cantpedidos, pe.montototal \n" +
            "from restaurante r \n" +
            "left join (select p.idrestaurante, round(AVG(p.valoracionrestaurante),0) as 'valoracionrestaurante' , \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\tcount(p.idrestaurante) as 'cantpedidos', \n" +
            "\t\t\t\t((4*p1.mismodistrito1) + (6*p0.mismodistrito0)) as 'montototal' ,\n" +
            "                p1.mismodistrito1 ,p0.mismodistrito0\n" +
            "                from pedido p, restaurante u,  \n" +
            "                (select p.idrestaurante, count(p.idrestaurante) as 'mismodistrito1' \n" +
            "                from pedido p, restaurante u\n" +
            "                where  p.idrestaurante= u.idrestaurante and p.mismodistrito = 1 and p.estado = 6 \n" +
            "                group by p.idrestaurante) p1, \n" +
            "                ( select p.idrestaurante, count(p.idrestaurante) as 'mismodistrito0' \n" +
            "                from pedido p, restaurante u\n" +
            "                where  p.idrestaurante= u.idrestaurante and p.mismodistrito = 0 and p.estado = 6 \n" +
            "                group by p.idrestaurante) p0\n" +
            " where p.idrestaurante= u.idrestaurante and p1.idrestaurante= u.idrestaurante and p0.idrestaurante= u.idrestaurante)  pe on r.idrestaurante = pe.idrestaurante\n" +
            "WHERE concat(lower(r.nombre),lower(r.ruc)) like %?1%\n" +
            "and (pe.valoracionrestaurante is null or (pe.valoracionrestaurante > ?2 and pe.valoracionrestaurante<= ?3 ))\n" +
            "and (cantpedidos > ?4 and cantpedidos <= ?5 )\n" +
            "and (montototal> ?6 and montototal<= ?7 )\n" +
            "group by r.ruc\n" +
            "order by r.nombre",nativeQuery = true,
            countQuery = "select count(*) \n" +
                    "from restaurante r \n" +
                    "left join (select p.idrestaurante, round(AVG(p.valoracionrestaurante),0) as 'valoracionrestaurante' , \n" +
                    "\t\t\t\t\t\t\t\t\t\t\t\tcount(p.idrestaurante) as 'cantpedidos', \n" +
                    "\t\t\t\t((4*p1.mismodistrito1) + (6*p0.mismodistrito0)) as 'montototal' ,\n" +
                    "                p1.mismodistrito1 ,p0.mismodistrito0\n" +
                    "                from pedido p, restaurante u,  \n" +
                    "                (select p.idrestaurante, count(p.idrestaurante) as 'mismodistrito1' \n" +
                    "                from pedido p, restaurante u\n" +
                    "                where  p.idrestaurante= u.idrestaurante and p.mismodistrito = 1 \n" +
                    "                group by p.idrestaurante) p1, \n" +
                    "                ( select p.idrestaurante, count(p.idrestaurante) as 'mismodistrito0' \n" +
                    "                from pedido p, restaurante u\n" +
                    "                where  p.idrestaurante= u.idrestaurante and p.mismodistrito = 0 \n" +
                    "                group by p.idrestaurante) p0\n" +
                    " where p.idrestaurante= u.idrestaurante and p1.idrestaurante= u.idrestaurante and p0.idrestaurante= u.idrestaurante)  pe on r.idrestaurante = pe.idrestaurante\n" +
                    "WHERE concat(lower(r.nombre),lower(r.ruc)) like %?1%\n" +
                    "and (pe.valoracionrestaurante is null or (pe.valoracionrestaurante > ?2 and pe.valoracionrestaurante<= ?3 ))\n" +
                    "and (cantpedidos > ?4 and cantpedidos <= ?5 )\n" +
                    "and (montototal> ?6 and montototal<= ?7 )\n" +
                    "group by r.ruc\n" +
                    "order by r.nombre")
    Page<UsuarioDtoReporteVentas> listaUsuariosDtoReporteVentas(String texto, Integer miFval, Integer maXval, Integer miFestado, Integer maXestado, Integer inFmont, Integer maXmont, Pageable pageable);
*/
    // TODO: 18/07/2021
    @Query(value ="select r.idrestaurante,r.nombre, r.ruc, count(p.codigo) as `cantidadpedidos`, \n" +
            "\t\tsum(if(p.mismodistrito = 1, (p.preciototal - 5), (p.preciototal - 8))) as `total`, \n" +
            "        if(round(avg(p.valoracionrestaurante),0) is null,0,round(avg(p.valoracionrestaurante),0)) as `valoracion`  from pedido p\n" +
            "inner join restaurante r on p.idrestaurante = r.idrestaurante\n" +
            "where p.estado = 6 and concat(r.nombre,r.ruc) like %?1% \n" +
            "group by p.idrestaurante\n" +
            "having (sum(if(p.mismodistrito = 1, (p.preciototal - 5), (p.preciototal - 8))) > ?6 and\n" +
            "\t\tsum(if(p.mismodistrito = 1, (p.preciototal - 5), (p.preciototal - 8))) <= ?7 ) and \n" +
            "        (count(p.codigo) > ?4 and count(p.codigo) <= ?5 ) and\n" +
            "\t\t(avg(p.valoracionrestaurante) is null \n" +
            "        or (round(avg(p.valoracionrestaurante),0) >= ?2 and round(avg(p.valoracionrestaurante),0) <= ?3 ))\n" +
            "order by count(p.codigo) DESC",nativeQuery = true,
            countQuery = "select count(*) from pedido p\n" +
                    "inner join restaurante r on p.idrestaurante = r.idrestaurante\n" +
                    "where p.estado = 6 and concat(r.nombre,r.ruc) like %?1% \n" +
                    "group by p.idrestaurante\n" +
                    "having (sum(if(p.mismodistrito = 1, (p.preciototal - 5), (p.preciototal - 8))) > ?6 and\n" +
                    "\t\tsum(if(p.mismodistrito = 1, (p.preciototal - 5), (p.preciototal - 8))) <= ?7 ) and \n" +
                    "        (count(p.codigo) > ?4 and count(p.codigo) <= ?5 ) and\n" +
                    "\t\t(avg(p.valoracionrestaurante) is null \n" +
                    "        or (round(avg(p.valoracionrestaurante),0) >= ?2 and round(avg(p.valoracionrestaurante),0) <= ?3 ))\n" +
                    "order by count(p.codigo) DESC")
    Page<UsuarioDtoReporteVentas> listaUsuariosDtoReporteVentas(String texto, Integer miFval, Integer maXval, Integer miFestado, Integer maXestado, Integer inFmont, Integer maXmont, Pageable pageable);

    @Query(value ="select r.idrestaurante,r.nombre, r.ruc, count(p.codigo) as `cantidadpedidos`, \n" +
            "\t\tsum(if(p.mismodistrito = 1, (p.preciototal - 5), (p.preciototal - 8))) as `total`, \n" +
            "        if(round(avg(p.valoracionrestaurante),0) is null,0,round(avg(p.valoracionrestaurante),0)) as `valoracion`  from pedido p\n" +
            "inner join restaurante r on p.idrestaurante = r.idrestaurante\n" +
            "where p.estado = 6 and concat(r.nombre,r.ruc) like %?1% \n" +
            "group by p.idrestaurante\n" +
            "having (sum(if(p.mismodistrito = 1, (p.preciototal - 5), (p.preciototal - 8))) > ?6 and\n" +
            "\t\tsum(if(p.mismodistrito = 1, (p.preciototal - 5), (p.preciototal - 8))) <= ?7 ) and \n" +
            "        (count(p.codigo) > ?4 and count(p.codigo) <= ?5 ) and\n" +
            "\t\t(avg(p.valoracionrestaurante) is null \n" +
            "        or (round(avg(p.valoracionrestaurante),0) >= ?2 and round(avg(p.valoracionrestaurante),0) <= ?3 ))\n" +
            "order by count(p.codigo) DESC",nativeQuery = true,
            countQuery = "select count(*) from pedido p\n" +
                    "inner join restaurante r on p.idrestaurante = r.idrestaurante\n" +
                    "where p.estado = 6 and concat(r.nombre,r.ruc) like %?1% \n" +
                    "group by p.idrestaurante\n" +
                    "having (sum(if(p.mismodistrito = 1, (p.preciototal - 5), (p.preciototal - 8))) > ?6 and\n" +
                    "\t\tsum(if(p.mismodistrito = 1, (p.preciototal - 5), (p.preciototal - 8))) <= ?7 ) and \n" +
                    "        (count(p.codigo) > ?4 and count(p.codigo) <= ?5 ) and\n" +
                    "\t\t(avg(p.valoracionrestaurante) is null \n" +
                    "        or (round(avg(p.valoracionrestaurante),0) >= ?2 and round(avg(p.valoracionrestaurante),0) <= ?3 ))\n" +
                    "order by count(p.codigo) DESC")
    List<UsuarioDtoReporteVentas> listaUsuariosDtoReporteVentasMas(String texto, Integer miFval, Integer maXval, Integer miFestado, Integer maXestado, Integer inFmont, Integer maXmont);
    @Query(value ="select r.idrestaurante,r.nombre, r.ruc, count(p.codigo) as `cantidadpedidos`, \n" +
            "\t\tsum(if(p.mismodistrito = 1, (p.preciototal - 5), (p.preciototal - 8))) as `total`, \n" +
            "        if(round(avg(p.valoracionrestaurante),0) is null,0,round(avg(p.valoracionrestaurante),0)) as `valoracion`  from pedido p\n" +
            "inner join restaurante r on p.idrestaurante = r.idrestaurante\n" +
            "where p.estado = 6 and concat(r.nombre,r.ruc) like %?1% \n" +
            "group by p.idrestaurante\n" +
            "having (sum(if(p.mismodistrito = 1, (p.preciototal - 5), (p.preciototal - 8))) > ?6 and\n" +
            "\t\tsum(if(p.mismodistrito = 1, (p.preciototal - 5), (p.preciototal - 8))) <= ?7 ) and \n" +
            "        (count(p.codigo) > ?4 and count(p.codigo) <= ?5 ) and\n" +
            "\t\t(avg(p.valoracionrestaurante) is null \n" +
            "        or (round(avg(p.valoracionrestaurante),0) >= ?2 and round(avg(p.valoracionrestaurante),0) <= ?3 ))\n" +
            "order by count(p.codigo) ASC",nativeQuery = true,
            countQuery = "select count(*) from pedido p\n" +
                    "inner join restaurante r on p.idrestaurante = r.idrestaurante\n" +
                    "where p.estado = 6 and concat(r.nombre,r.ruc) like %?1% \n" +
                    "group by p.idrestaurante\n" +
                    "having (sum(if(p.mismodistrito = 1, (p.preciototal - 5), (p.preciototal - 8))) > ?6 and\n" +
                    "\t\tsum(if(p.mismodistrito = 1, (p.preciototal - 5), (p.preciototal - 8))) <= ?7 ) and \n" +
                    "        (count(p.codigo) > ?4 and count(p.codigo) <= ?5 ) and\n" +
                    "\t\t(avg(p.valoracionrestaurante) is null \n" +
                    "        or (round(avg(p.valoracionrestaurante),0) >= ?2 and round(avg(p.valoracionrestaurante),0) <= ?3 ))\n" +
                    "order by count(p.codigo) ASC")
    List<UsuarioDtoReporteVentas> listaUsuariosDtoReporteVentasMenos(String texto, Integer miFval, Integer maXval, Integer miFestado, Integer maXestado, Integer inFmont, Integer maXmont);

    @Query(value ="select r.idrestaurante,r.nombre, r.ruc, d.nombre as `distrito` , r.estado, count(p.codigo) as `cantidadpedidos`,\n" +
            "\t\tsum(if(p.mismodistrito = 1, 1, 0)) as `ingresostotalesmismodistrito`,\n" +
            "\t\tsum(if(p.mismodistrito = 0, 2, 0)) as `ingresostotalesdiferentedistrito`, \n" +
            "        sum(if(p.mismodistrito = 1, 1, 2)) as `ingresostotales` \n" +
            "from pedido p\n" +
            "inner join restaurante r on p.idrestaurante = r.idrestaurante\n" +
            "inner join distrito d on r.iddistrito = d.iddistrito\n" +
            "where p.estado = 6  and concat(r.nombre,r.ruc) like %?1% \n" +
            "\t\t\tand r.estado > ?2 and r.estado < ?3 and\n" +
            "\t\t(r.iddistrito > ?8 and r.iddistrito <= ?9 )\n" +
            "group by p.idrestaurante\n" +
            "having (sum(if(p.mismodistrito = 1, 1, 2)) > ?6  and\n" +
            "\t\tsum(if(p.mismodistrito = 1, 1, 2)) <= ?7 )\tand \n" +
            "        (count(p.codigo) > ?4 and count(p.codigo) <= ?5 ) \n" +
            "order by count(p.codigo) DESC",nativeQuery = true,
            countQuery = "select count(*) from pedido p\n" +
                    "inner join restaurante r on p.idrestaurante = r.idrestaurante\n" +
                    "inner join distrito d on r.iddistrito = d.iddistrito\n" +
                    "where p.estado = 6  and concat(r.nombre,r.ruc) like %?1% \n" +
                    "\t\t\tand r.estado > ?2 and r.estado < ?3 and\n" +
                    "\t\t(r.iddistrito > ?8 and r.iddistrito <= ?9 )\n" +
                    "group by p.idrestaurante\n" +
                    "having (sum(if(p.mismodistrito = 1, 1, 2)) > ?6  and\n" +
                    "\t\tsum(if(p.mismodistrito = 1, 1, 2)) <= ?7 )\tand \n" +
                    "        (count(p.codigo) > ?4 and count(p.codigo) <= ?5 ) \n" +
                    "order by count(p.codigo) DESC")
    Page<UsuarioDtoReporteIngresos> listaUsuariosDtoReporteIngresos(String texto, Integer miFestado2, Integer maXestado2, Integer miFestado, Integer maXestado, Integer inFmont, Integer maXmont,Integer minDistrito, Integer maxDistrito, Pageable pageable);




    @Query(value = "select datediff(now(),min(fechaRegistro)) from usuario", nativeQuery = true)
    int buscarFechaMinimaRepartidor();

    @Query(value = "select min(movilidad) from usuario", nativeQuery = true)
    int buscarIdMovilidadMinimaRepartidor();

    @Query(value = "select * from usuario \n" +
            "where idrol=3 and estado = 2 and\n" +
            "(lower(nombres) like %?1% or lower(apellidos) like %?2%) and (dni like %?3%)\n" +
            "and (fechaRegistro>= DATE_ADD(now(), INTERVAL ?4 DAY))", nativeQuery = true,
            countQuery ="select count(*) from usuario\n" +
                    "where idrol=3 and estado = 2 and\n" +
                    "(lower(nombres) like %?1% or lower(apellidos) like %?2%) and (dni like %?3%)\n" +
                    "and (fechaRegistro>= DATE_ADD(now(), INTERVAL ?4 DAY))")
    Page<Usuario> buscarAdministradorR(String nombres, String apellidos, String dni, int fechaRegistro, Pageable pageable);


    @Query(value = "select us.* from usuario us \n" +
            "left join movilidad m on us.idmovilidad = m.idmovilidad\n" +
            "where us.idrol=4 and\n" +
            "us.estado = 2 and\n" +
            "(lower(us.nombres) like %?1% or lower(us.apellidos) like %?2%) and\n" +
            "us.fechaRegistro>= DATE_ADD(now(), INTERVAL ?3 DAY)",
            nativeQuery = true,
            countQuery = "select count(*) from usuario us\n" +
                    "left join movilidad m on us.idmovilidad = m.idmovilidad\n" +
                    "where us.idrol=4 and\n" +
                    "us.estado = 2 and\n" +
                    "(lower(us.nombres) like %?1%\n" +
                    "or lower(us.apellidos) like %?2%)\n" +
                    "and us.fechaRegistro>= DATE_ADD(now(), INTERVAL ?3 DAY)")
    Page<Usuario> buscarRepartidoresSinMovilidad(String nombres, String apellidos, int fechaRegistro, Pageable pageable);

    @Query(value = "select u.* from usuario u\n" +
            "left join movilidad m on u.idmovilidad = m.idmovilidad\n" +
            "where u.idrol=4 and\n" +
            "u.estado = 2 and\n" +
            "(lower(u.nombres) like %?1% or lower(u.apellidos) like %?2%) and\n" +
            "u.fechaRegistro>= DATE_ADD(now(), INTERVAL ?3 DAY) and m.idtipomovilidad = ?4", nativeQuery = true
            , countQuery = "select count(*) from usuario u\n" +
            "left join movilidad m on u.idmovilidad = m.idmovilidad\n" +
            "where u.idrol=4 and\n" +
            "u.estado = 2 and\n" +
            "(lower(u.nombres) like %?1%\n" +
            "or lower(u.apellidos) like %?2%)\n" +
            "and u.fechaRegistro>= DATE_ADD(now(), INTERVAL ?3 DAY)\n" +
            "and m.idtipomovilidad = ?4")

    Page<Usuario> buscarRepartidoresConMovilidad(String nombres,String apellidos, int fechaRegistro, int idMovilidad, Pageable pageable);



    List<Usuario> findUsuarioByCorreo(String correo);
    List<Usuario> findUsuarioByDni(String dni);
    List<Usuario> findUsuarioByTelefono(String telefono);


    @Query(value = "select ub.iddistrito, ub.direccion, u.idusuario  from usuario u inner join ubicacion ub\n" +
            "on u.idusuario=ub.idusuario where u.idusuario=?;\n"
            ,nativeQuery = true)
    List<ClienteDTO> listaParaCompararDirecciones(int idusuario);

    //muestra la ganancia de un repartidor - la ganancia de un repartidor depende del atributo mismo distrito, entonces la ganancia sería
    //la cantidad de pedidos que tiene en un distrito *4 + la cantidad de pedidos que tiene fuera *6
    @Query(value = "SELECT ((select count(p.codigo) from pedido p, usuario u where p.mismodistrito = 1 and u.idusuario = p.idrepartidor and u.idusuario = ?1)*4 + (select count(p.codigo) from pedido p, usuario u where p.mismodistrito = 0 and u.idusuario = p.idrepartidor and u.idusuario = ?1)*6) as `ganancia` \n" +
            "FROM usuario u, rol r, pedido p \n" +
            "where u.idrol = r.idrol and u.idusuario = p.idrepartidor and r.idrol = 4 and u.idusuario = ?1 \n" +
            "group by u.idusuario", nativeQuery = true)
    BigDecimal gananciaRepartidor(int idrepartidor);

    //promedio de valoracion repartidor
    @Query(value = "select ceil(avg(p.valoracionrepartidor)) as `valoracion` from usuario u, pedido p, rol r\n" +
            "where u.idrol = r.idrol and u.idusuario = p.idrepartidor and r.idrol = 4 and u.idusuario = ?1 ", nativeQuery = true)
    Integer valoracionRepartidor(int idrepartidor);


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

    Usuario findByCorreoAndRol(String correo, Rol rol);


/*
    @Query(value = "select ub.iddistrito, ub.direccion, u.idusuario  from usuario u inner join ubicacion ub\n" +
            "on u.idusuario=ub.idusuario where u.idusuario=?;\n"
            ,nativeQuery = true)
    List<ClienteDTO> listaParaCompararDirecciones(int idusuario);


 */

    @Query(value = "SELECT p.codigo, p.preciototal, p.estado, r.nombre\n" +
            "            FROM pedido p\n" +
            "                        left join restaurante r on r.idrestaurante = p.idrestaurante\n" +
            "                        left join usuario u on  p.idcliente = u.idusuario\n" +
            "                        where (p.idcliente=?1) and (p.estado=0)\n" +
            "                        order by p.fechapedido desc limit 3" ,nativeQuery = true)
    List<NotiDTO> notificacionCliente(int id);

    @Query(value = "select ceil(avg(valoracionrepartidor)) as `valoracion` from pedido where  valoracionrepartidor is not null \n" +
            "and idrepartidor=?1 ", nativeQuery = true)
    Integer promedioValoracionRpartidor(int idrepartidor);


}
