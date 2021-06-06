package com.example.demo.repositories;

import com.example.demo.dtos.CuponClienteDTO;
import com.example.demo.entities.Cupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Repository
public interface CuponRepository extends JpaRepository<Cupon, Integer> {

    @Query(value = "SELECT * FROM cupon where nombre = ?1",
            nativeQuery = true)
    Cupon buscarPorNombre(String nombre);

    Page<Cupon> findByNombreIsContainingAndFechainicioEqualsAndFechafinBeforeAndDescuentoGreaterThanEqualAndDescuentoLessThanEqual(String nombre, LocalDate fechainicio, LocalDate fechafin, int descuentoMin, int descuentoMax, Pageable pageable);

    @Query(value="select c.idcupon, c.nombre as 'nombrecupon', \n" +
            "c.descuento,c.fechafin, c.politica,r.nombre as 'nombrerestaurante', \n" +
            "chp.utilizado, cl.nombres as 'nombrescliente', cl.idusuario as 'idcliente'\n" +
            "from cupon c\n" +
            "left join restaurante r on r.idrestaurante=c.idrestaurante\n" +
            "left join cliente_has_cupon chp on chp.idcupon = c.idcupon\n" +
            "left join usuario cl on cl.idusuario = chp.idcliente\n" +
            "where c.estado=2;",nativeQuery = true)
    List<CuponClienteDTO> listaCupones();
}