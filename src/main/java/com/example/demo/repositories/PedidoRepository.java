package com.example.demo.repositories;

import com.example.demo.dtos.DetallePedidoDTO;
import com.example.demo.dtos.ExtraPorPedidoDTO;
import com.example.demo.dtos.PedidoDTO;
import com.example.demo.dtos.PlatoPorPedidoDTO;
import com.example.demo.entities.Distrito;
import com.example.demo.entities.Pedido;
import com.example.demo.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    @Query(value="select r.nombre, p.fechapedido, p.tiempoentrega, p.estado, p.codigo  from pedido p\n" +
            "            inner join usuario u on u.idusuario = ?1 and p.idcliente = u.idusuario\n" +
            "            inner join restaurante r on p.idrestaurante = r.idrestaurante\n" +
            "            where concat(lower(r.nombre),lower(r.nombre)) like %?2%\n" +
            "            and p.estado >=?3 and  p.estado <=?4 ;", nativeQuery = true)

    List<PedidoDTO> pedidosTotales(int idCliente, String texto, int estado1, int estado2);


    List<Pedido> findByEstadoAndUbicacion_Distrito(int estado, Distrito distrito);

    Pedido findByEstadoAndRepartidor(int estado, Usuario repartidor);

    @Query(value = "select*from pedido where (idrestaurante=?1) order by estado", nativeQuery = true)
    List<Pedido> pedidosXrestaurante (int id);

    @Query(value = "SELECT pe.codigo, concat(u.nombres,' ',u.apellidos) as cliente, concat(ubi.direccion,'-',dis.nombre) as direccion, pe.fechapedido, cu.nombre as cupon,\n" +
            "cu.descuento as descuento, pe.estado as estado, pago.tipo as metodopago, pe.comentariorestaurante as comentario,\n" +
            " pe.preciototal FROM pedido pe\n" +
            "inner join usuario u on pe.idcliente = u.idusuario \n" +
            "left join cupon cu on pe.idcupon=cu.idcupon \n" +
            "inner join metodopago pago on pe.idmetodopago=pago.idmetodopago\n" +
            "inner join restaurante res on res.idrestaurante=pe.idrestaurante\n" +
            "inner join ubicacion ubi on pe.idubicacion=ubi.idubicacion\n" +
            "inner join distrito dis on dis.iddistrito=ubi.iddistrito\n" +
            "where res.idrestaurante=?1 and pe.codigo=?2",nativeQuery = true)
    List<DetallePedidoDTO> detallePedido(int idrestaurante, String codigopedido);

    @Query(value = "SELECT p.nombre,php.preciounitario, php.cantidad, php.preciounitario*php.cantidad as preciototal FROM plato_has_pedido php\n" +
            "inner join pedido pe on pe.codigo=php.codigo\n" +
            "inner join restaurante res on res.idrestaurante=pe.idrestaurante\n" +
            "inner join plato p on p.idplato=php.idplato\n" +
            "where pe.idrestaurante=?1 and php.codigo=?2",nativeQuery = true)
    List<PlatoPorPedidoDTO> platosPorPedido(int idrestaurante, String codigopedido);

    @Query(value = "SELECT e.nombre, ehp.preciounitario,ehp.cantidad,ehp.preciounitario*ehp.cantidad as preciototal  FROM extra_has_pedido ehp\n" +
            "inner join extra e on e.idextra=ehp.idextra\n" +
            "inner join pedido pe on pe.codigo=ehp.codigo\n" +
            "where ehp.codigo=?1",nativeQuery = true)
    List<ExtraPorPedidoDTO> extrasPorPedido(String codigopedido);
}
