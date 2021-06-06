package com.example.demo.repositories;

import com.example.demo.dtos.*;
import com.example.demo.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value="select r.nombre, r.idrestaurante,p.fechapedido, p.tiempoentrega, p.estado, p.codigo,r.foto as 'foto' , p.valoracionrestaurante, p.comentariorestaurante, p.valoracionrepartidor,p.comentariorepartidor  " +
            "from pedido p\n" +
            "            inner join restaurante r on p.idrestaurante = r.idrestaurante\n" +
            "            where p.idcliente = ?1  and concat(lower(r.nombre),lower(r.nombre)) like %?2%\n" +
            "            and p.estado >=?3 and  p.estado <=?4  order by p.codigo asc ", nativeQuery = true)

    List<PedidoValoracionDTO> pedidosTotales2(int idCliente, String texto, int estado1, int estado2);


    List<Pedido> findByEstadoAndUbicacion_Distrito(int estado, Distrito distrito);

    Pedido findByEstadoAndRepartidor(int estado, Usuario repartidor);


    Page<Pedido> findByRestaurante_IdrestauranteAndCliente_NombresIsContainingAndEstadoGreaterThanEqualAndEstadoLessThanEqualAndPreciototalGreaterThanEqualAndPreciototalLessThanEqualOrderByEstadoAsc(int idrestaurante, String nombre, int inputEstadoMin, int inputEstadoMax, double inputPMin, double inputPMax, Pageable pageable);

    @Query(value = "select *from pedido where idrestaurante=?1 and codigo=?2 ", nativeQuery = true)
    Pedido pedidosXrestauranteXcodigo (int idrestaurante, String codigo);

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

    @Query(value="select * from pedido",nativeQuery = true)
    Pedido encontrarporId(String id);

    @Query(value="SELECT codigo FROM pedido where idrestaurante=?1 and estado=?2 ", nativeQuery = true)
    List<String> listarPedidosXestadoXrestaurante(int codigo, int estado);

    @Query(value="select php.codigo as codigo, date_format(p.fechapedido,'%d-%m-%y')  as fecha, sum(php.cantidad*php.preciounitario) as montoplatos, ex.monto as montoextras,\n" +
            "\tc.descuento as descuento, p.preciototal as preciototal from plato_has_pedido php\n" +
            "\tinner join pedido p on php.codigo=p.codigo\n" +
            "\tinner join cupon c on c.idcupon=p.idcupon\n" +
            "    inner join (select extra.codigo, sum(extra.preciounitario*extra.cantidad) as monto from extra_has_pedido extra\n" +
            "    where extra.codigo=?1) ex on ex.codigo=p.codigo\n" +
            "  where php.codigo=?2", nativeQuery = true)
    PedidoReporteDTO pedidoReporte(String codigo, String codigo2);

    @Query(value="select pe.codigo as codigo, pe.valoracionrestaurante as valoracion, date_format(pe.fechapedido,'%d-%m-%y')  as fecha, pe.comentariorestaurante as comentario from pedido pe\n" +
            "  where pe.codigo=?1", nativeQuery = true)
    ValoracionReporteDTO valoracionReporte(String codigo);

    @Query(value="select * from (select pl.idplato as id,pl.nombre as nombre, c.nombre as nombrecat, sum(php.cantidad) as suma from plato_has_pedido php \n" +
            "inner join pedido p on php.codigo=p.codigo\n" +
            "inner join plato pl on pl.idplato=php.idplato \n" +
            "inner join categoriarestaurante c on pl.idcategoriarestaurante=c.idcategoria \n" +
            "where p.idrestaurante=?1 and p.estado=?2 and (pl.nombre like %?3%) and (c.idcategoria like %?4%)\n" +
            "group by php.idplato) as T2 having suma >= ?5 and suma <=?6", nativeQuery = true)
    Page<PlatoReporteDTO> reportePlato(int id, int estado, String nombre, String idcategoria, int cantMin, int cantMax, Pageable pageable);



    @Query(value = "select r.idrestaurante, r.nombre as 'nombrerest' , count(r.idrestaurante) as \"numpedidos\"\n" +
            ",EXTRACT(MONTH from p.fechapedido) as 'mes' , sum(p.preciototal) as'total', p.fechapedido \n" +
            ",c.nombre as 'nombrecupon', c.descuento,p.codigo,p.tiempoentrega\n" +
            "from pedido p \n" +
            "inner join restaurante r on p.idrestaurante=r.idrestaurante \n" +
            "left join cliente_has_cupon clhp on p.idcupon = clhp.idcupon\n" +
            "inner join cupon c on c.idcupon = clhp.idcupon\n" +
            "where p.idcliente=?1  and clhp.utilizado=1 and\n" +
            "  (EXTRACT(MONTH from p.fechapedido) > ?2  and  EXTRACT(MONTH from p.fechapedido)<=?3 )\n" +
            "group by r.idrestaurante\n" +
            " having (count(r.idrestaurante ) >?4  and  count(r.idrestaurante ) <=?5) \n" +
            " order by \"numpedidos\"  desc", nativeQuery = true)

    List<ReportePedido> reportexmes(int idcliente, int limit1mes, int limit2mes,int limitcant1, int limitcant2);


    @Query(value="select r.nombre as 'nombrerest' , count(p.idrestaurante) as `numpedidos` ,\n" +
            "EXTRACT(MONTH from p.fechapedido) as `mes` from pedido p \n" +
            "inner join restaurante r on p.idrestaurante=r.idrestaurante \n" +
            "where p.idcliente=?1 and EXTRACT(MONTH from p.fechapedido) = ?2  group by p.idrestaurante limit 1,3",nativeQuery = true)
    List<ReporteTop3> reporteTop3Rest(int idcliente, int mes);

    @Query(value="select  pl.nombre ,sum(cantidad) as 'totalplato' from pedido p \n" +
            "inner join plato_has_pedido php on p.codigo=php.codigo\n" +
            "inner join plato pl on php.idplato=pl.idplato\n" +
            "where  p.idcliente=?1 and EXTRACT(MONTH from p.fechapedido) = ?2  \n" +
            "group by pl.idplato order by sum(cantidad) desc  limit 0,3 ", nativeQuery = true)

    List<ReporteTop3P> reporteTop3Pl(int idcliente, int mes);

}














