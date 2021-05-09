package com.example.demo.repositories;

import com.example.demo.entities.Plato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Integer> {


    Page<Plato> findByDisponible(boolean disponible, Pageable pageable);

    @Query(value = "select p.*\n" +
            "from plato p\n" +
            "where p.idrestaurante = ?1 and p.disponible = ?2 and (nombre like %?3% or descripcion like %?3% ) " +
            "and ( precio <= ?4)\n" +
            "and ( precio > ?5)",
            nativeQuery = true)
    List<Plato> buscarInputBuscador(Integer inputID, Integer inputDisponible, String inputIngresado, Integer inputPmaximo, Integer inputPminimo);

    @Query(value = "select p.*\n" +
            "from plato p\n" +
            "where p.idrestaurante = ?1 and (nombre like %?2% or descripcion like %?2% )" +
            "and ( precio <= ?3)\n" +
            "and ( precio > ?4) ",
            nativeQuery = true)
    List<Plato> buscarInputBuscadorT(Integer inputID, String inputIngresado, Integer inputPmaximo, Integer inputPminimo);
///----------------Buscadores TExto---------///

    @Query(value = "select p.*\n" +
            "from plato p\n" +
            "where p.idrestaurante = ?1 and p.disponible = ?2 and (nombre like %?3% or descripcion like %?3% )" +
            "and ( precio <= ?4)\n" +
            "and ( precio > ?5) ",
            nativeQuery = true)
    Page<Plato> buscarInputBuscadores(int inputID, int inputDisponibilidad, String inputIngresado, int inputPmax, int inputPmin, Pageable pageable);

    Page<Plato> findByDisponibleAndNombreIsContainingAndPrecioGreaterThanEqualAndPrecioLessThanEqual(boolean disponible, String nombre, Pageable pageable, double inputPMin, double inputPMax);

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
    List<Plato> buscarInputBuscadorTextoDisponibilidad(Integer inputID, Integer inputDisponible, String inputIngresado);

    @Query(value = "select p.*\n" +
            "from plato p\n" +
            "where p.idrestaurante = ?1 and (nombre like %?2% or descripcion like %?2% )" +
            "and ( precio <= ?3)\n" +
            "and ( precio > ?4) ",
            nativeQuery = true)
    List<Plato> buscarInputBuscadorTextoPrecio(Integer inputID, String inputIngresado, Integer inputPmax, Integer inputPmin);


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
