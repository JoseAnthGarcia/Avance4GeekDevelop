package com.example.demo.repositories;

import com.example.demo.dtos.PlatosDTO;
import com.example.demo.dtos.RestauranteDTO;
import com.example.demo.entities.Distrito;
import com.example.demo.entities.Restaurante;
import com.example.demo.entities.Rol;
import com.example.demo.entities.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Integer> {
    Page<Restaurante> findByEstado(int estado, Pageable pageable);
    List<Restaurante> findRestauranteByNombre(String nombre);
    List<Restaurante> findRestauranteByDireccion(String direccion);
    List<Restaurante> findRestauranteByTelefono(String telefono);
    List<Restaurante> findRestauranteByRuc(String ruc);

    @Query(value = "select * from restaurante where idadministrador = ?1", nativeQuery = true)
    Restaurante encontrarRest (int id);

    @Query(value = "select datediff(now(),min(fechaRegistro)) from restaurante", nativeQuery = true)
    int buscarFechaMinimaRestaurante();
    //estado 2= pendiente
    //estado 1= aceptado
    //estado 3= rechazado
    @Query(value = "select * from restaurante where estado=2 and\n" +
            "(lower(nombre) like %?1% ) and (ruc like %?2%)\n" +
            "and (fechaRegistro>= DATE_ADD(now(), INTERVAL ?3 DAY))", nativeQuery = true)
    Page<Restaurante> buscarRest(String nombreRest, String ruc,int fechaRegistro, Pageable pageable);

    @Query(value ="select r.idrestaurante, r.nombre, r.foto, r.fotocontenttype, r.fotonombre\n" +
            "            , ceil(t.`prom_val`) as 'valoracion'\n" +
            "            , t.`cant_val` as 'calificaciones' \n" +
            "            , truncate(t2.`prom_prec`,2) as 'preciopromedio'\n" +
            "            ,t3.`categorias` ,t3.`idcategorias` from restaurante r\n" +
            "            left join (select  r.idrestaurante,\n" +
            "            avg(p.valoracionrestaurante) as `prom_val`,\n" +
            "            count(p.valoracionrestaurante) as `cant_val` from  pedido p\n" +
            "            left join restaurante r on p.idrestaurante=r.idrestaurante group by r.idrestaurante)\n" +
            "            t on t.idrestaurante=r.idrestaurante\n" +
            "            left join ( select  r.idrestaurante, avg(p.precio) as `prom_prec`  from plato p\n" +
            "            join restaurante r on p.idrestaurante=r.idrestaurante group by r.idrestaurante) t2 on t2.idrestaurante=r.idrestaurante\n" +
            "            left join (select rhcr.idrestaurante, group_concat( cr.nombre separator ' - ') as `categorias`,\n" +
            "            group_concat(cr.idcategoria separator '-') as `idcategorias`\n" +
            "            from restaurante_has_categoriarestaurante rhcr\n" +
            "            left join categoriarestaurante cr on rhcr.idcategoria=cr.idcategoria\n" +
            "            group by rhcr.idrestaurante) t3 on t3.idrestaurante=r.idrestaurante\n" +
            "            where r.estado = 1\n" +
            "            and (t2.`prom_prec` >= ?1 and t2.`prom_prec` < ?2) \n" +
            "            and t.`prom_val` is null or (t.`prom_val` > ?3 and t.`prom_val` <= ?4)      \n" +
            "            having r.nombre like %?5%  \n" +
            "            and t3.`idcategorias` like %?6% \n" +
            "            or t3.`idcategorias` like %?7 \n" +
            "            or t3.`idcategorias` like ?8% order by r.iddistrito = ?9 DESC ", nativeQuery = true)
    Page<RestauranteDTO> listaRestaurante(Integer limitInfP, Integer limitSupP,Integer limitInfVal, Integer limitSupVal, String texto, String id1,String id2, String id3,Integer iddistrito, Pageable pageable);


    @Query(value = "select r.idrestaurante, r.nombre, r.foto, r.fotocontenttype, r.fotonombre\n" +
            "            , ceil(t.`prom_val`) as 'valoracion'\n" +
            "            , t.`cant_val` as 'calificaciones' \n" +
            "            , truncate(t2.`prom_prec`,2) as 'preciopromedio'\n" +
            "            ,t3.`categorias` ,t3.`idcategorias` from restaurante r\n" +
            "            left join (select  r.idrestaurante,\n" +
            "            avg(p.valoracionrestaurante) as `prom_val`,\n" +
            "            count(p.valoracionrestaurante) as `cant_val` from  pedido p\n" +
            "            left join restaurante r on p.idrestaurante=r.idrestaurante group by r.idrestaurante)\n" +
            "            t on t.idrestaurante=r.idrestaurante\n" +
            "            left join ( select  r.idrestaurante, avg(p.precio) as `prom_prec`  from plato p\n" +
            "            join restaurante r on p.idrestaurante=r.idrestaurante group by r.idrestaurante) t2 on t2.idrestaurante=r.idrestaurante\n" +
            "            left join (select rhcr.idrestaurante, group_concat( cr.nombre separator ' - ') as `categorias`,\n" +
            "            group_concat(cr.idcategoria separator '-') as `idcategorias`\n" +
            "            from restaurante_has_categoriarestaurante rhcr\n" +
            "            left join categoriarestaurante cr on rhcr.idcategoria=cr.idcategoria\n" +
            "            group by rhcr.idrestaurante) t3 on t3.idrestaurante=r.idrestaurante\n" +
            "            where r.estado = 1\n" +
            "            and (t2.`prom_prec` >= ?1 and t2.`prom_prec` < ?2) \n" +
            "            and (t.`prom_val` > ?3 and t.`prom_val` <= ?4)      \n" +
            "            having r.nombre like %?5%  \n" +
            "            and t3.`idcategorias` like %?6% \n" +
            "            or t3.`idcategorias` like %?7 \n" +
            "            or t3.`idcategorias` like ?8% order by r.iddistrito = ?9 DESC " , nativeQuery = true)
    Page<RestauranteDTO> listaRestaurante2(Integer limitInfP, Integer limitSupP,Integer limitInfVal, Integer limitSupVal, String texto, String id1,String id2, String id3,Integer iddistrito, Pageable pageable);

}
