package com.example.demo.repositories;

import com.example.demo.dtos.Pedido1DTO;
import com.example.demo.dtos.PedidoDTO;
import com.example.demo.dtos.Plato_has_PedidoDTO;
import com.example.demo.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.print.attribute.standard.MediaSize;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, String> {

    @Query(value = "(select datediff(now(),min(fechapedido)) from pedido) ", nativeQuery = true)
    int fechaPedidoMinimo();


    @Query(value = "select * from pedido p \n" +
            "inner join usuario u on u.idusuario = ?1 and p.idcliente = u.idusuario\n" +
            "inner join usuario r on r.idusuario = p.idrepartidor\n" +
            "inner join restaurante rest on p.idrestaurante = rest.idrestaurante\n" +
            "where concat(lower(rest.nombre),lower(r.nombres)) like %?2% \n" +
            "and concat(p.valoracionrepartidor,p.valoracionrestaurante) like %?3% \n" +
            "and (p.fechapedido >= DATE_ADD(now(), INTERVAL ?4 DAY)) ", nativeQuery = true)
    List<Pedido> pedidosPorCliente(int idCliente, String texto, int valoracion, int fechaPedido);


    @Query(value="select r.nombre, r.idrestaurante,p.fechapedido, p.tiempoentrega, p.estado, p.codigo,r.foto as 'foto'  from pedido p\n" +
            "            inner join restaurante r on p.idrestaurante = r.idrestaurante\n" +
            "            where p.idcliente = ?1  and concat(lower(r.nombre),lower(r.nombre)) like %?2%\n" +
            "            and p.estado >=?3 and  p.estado <=?4  order by p.codigo asc ", nativeQuery = true)

    List<PedidoDTO> pedidosTotales(int idCliente, String texto, int estado1, int estado2);

    @Query(value="SELECT pl.nombre as 'nombreplato' , php.cantidad, php.preciounitario\n" +
            ", php.observacionplatillo\n" +
            "  FROM geekdevelop.plato_has_pedido  php\n" +
            "inner join plato pl on pl.idplato= php.idplato\n" +
            "where php.codigo = ?1", nativeQuery = true)
    List<Plato_has_PedidoDTO> detalle2(String codigo);


    @Query(value ="select ped.codigo,r.nombre as 'nombrerest',\n" +
            "      clhp.utilizado ,ped.preciototal,ped.mismodistrito,ped.estado,ped.idmetodopago,\n" +
            "      ped.fechapedido, ped.tiempoentrega from pedido ped\n" +
            "      inner join restaurante r on r.idrestaurante = ped.idrestaurante\n" +
            "      inner join cliente_has_cupon clhp on ped.idcupon= clhp.idcupon\n" +
            "      where ped.codigo = ?1", nativeQuery = true)

    List<Pedido1DTO> detalle1(String codigo);

}
