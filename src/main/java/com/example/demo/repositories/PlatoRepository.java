package com.example.demo.repositories;

import com.example.demo.dtos.Plato_has_PedidoDTO;
import com.example.demo.dtos.PlatosDTO;
import com.example.demo.entities.Plato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, Integer> {


    Page<Plato> findByIdrestauranteAndDisponible(int id,boolean disponible, Pageable pageable);

    Page<Plato> findByIdrestauranteAndIdcategoriaplatoAndDisponibleAndNombreIsContainingAndPrecioGreaterThanEqualAndPrecioLessThanEqual(int idrestaurante,int idcategoriaplato,boolean disponible, String nombre, Pageable pageable, double inputPMin, double inputPMax);

    @Query(value = "select  p.idplato, p.nombre, p.precio, p.foto, p.fotocontenttype, p.fotonombre \n" +
            "from plato p where p.idrestaurante = ?1 and p.disponible = 1 and p.nombre like %?2% \n" +
            "and (p.precio >= ?3 and p.precio < ?4 )", nativeQuery = true , countQuery = "select count(*) \n" +
            "            from plato p where p.idrestaurante = ?1 and p.disponible = 1 and p.nombre like %?2% \n" +
            "           and (p.precio >= ?3 and p.precio < ?4 ) ")
    Page<PlatosDTO> listaPlato(int idRest, String texto, Integer limitInf, Integer limitSup, Pageable pageable);

}
