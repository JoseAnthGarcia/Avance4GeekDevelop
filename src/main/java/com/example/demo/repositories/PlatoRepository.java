package com.example.demo.repositories;

import com.example.demo.entities.Plato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Integer> {

    List<Plato> findByDisponible(boolean disponible);

    @Query(value = "select p.*\n" +
            "from plato p\n" +
            "where p.idrestaurante = ?1 and p.disponible = ?2 and (nombre like %?3% or descripcion like %?3% ) " +
            "and ( precio <= ?4)\n" +
            "and ( precio > ?5)",
            nativeQuery = true)
    List<Plato> buscarInputBuscador(Integer inputID,Integer inputDisponible, String inputIngresado,Integer inputPmaximo, Integer inputPminimo);

    @Query(value = "select p.*\n" +
            "from plato p\n" +
            "where p.idrestaurante = ?1 and (nombre like %?2% or descripcion like %?2% )" +
            "and ( precio <= ?3)\n" +
            "and ( precio > ?4) ",
            nativeQuery = true)
    List<Plato> buscarInputBuscadorT(Integer inputID, String inputIngresado,Integer inputPmaximo, Integer inputPminimo);
///----------------Buscadores TExto---------///

    @Query(value = "select p.*\n" +
            "from plato p\n" +
            "where p.idrestaurante = ?1 and p.disponible = ?2 and (nombre like %?3% or descripcion like %?3% )"+
            "and ( precio <= ?4)\n" +
            "and ( precio > ?5) ",
            nativeQuery = true)
    List<Plato> buscarInputBuscadores(Integer inputID,Integer inputDisponibilidad, String inputIngresado,Integer inputPmax,Integer inputPmin );



    //////--------------buscadores-----------/////////

    @Query(value = "select p.*\n" +
            "from plato p\n" +
            "where p.idrestaurante = ?1 and (nombre like %?2% or descripcion like %?2% )",
            nativeQuery = true)
    List<Plato> buscarInputBuscadorTexto(Integer inputID, String inputIngresado);
    @Query(value = "select p.*\n" +
            "from plato p\n" +
            "where p.idrestaurante = ?1 and p.disponible = ?2 and (nombre like %?3% or descripcion like %?3% )",
            nativeQuery = true)
    List<Plato> buscarInputBuscadorTextoDisponibilidad(Integer inputID,Integer inputDisponible, String inputIngresado);
    @Query(value = "select p.*\n" +
            "from plato p\n" +
            "where p.idrestaurante = ?1 and (nombre like %?2% or descripcion like %?2% )"+
            "and ( precio <= ?3)\n" +
            "and ( precio > ?4) ",
            nativeQuery = true)
    List<Plato> buscarInputBuscadorTextoPrecio(Integer inputID, String inputIngresado,Integer inputPmax,Integer inputPmin );


    @Query(value = "select p.*\n" +
            "from plato p\n" +
            "where p.idrestaurante = ?1   and p.disponible = ?2 and (nombre like %?3% or descripcion like %?3% )" +
            "and ( precio <= ?3)\n" +
            "and ( precio > ?4) ",
            nativeQuery = true)
    List<Plato> buscarInputBuscadorT(Integer inputID, String inputIngresado);

    @Query(value = "select p.*\n" +
            "from plato p\n" +
            "where p.idrestaurante = ?1",
            nativeQuery = true)
    List<Plato> buscarInputBuscadorTD(Integer inputID, String inputIngresado);
}
