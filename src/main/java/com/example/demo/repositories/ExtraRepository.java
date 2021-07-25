package com.example.demo.repositories;

import com.example.demo.dtos.ExtraDTO;
import com.example.demo.entities.Extra;
import com.example.demo.entities.Plato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;


@Repository
public interface ExtraRepository extends JpaRepository<Extra, Integer> {

    Extra findByIdextraAndIdrestaurante(int idExtra, int idRest);
    Extra findByIdextraAndIdrestauranteAndIdcategoriaextra(int idExtra, int idRest, int idcategoria);
    List<Extra> findByIdrestauranteAndDisponible(int idRest, boolean disp);

    Page<Extra> findByIdrestauranteAndIdcategoriaextraAndDisponibleAndNombreIsContainingAndPreciounitarioGreaterThanEqualAndPreciounitarioLessThanEqual(int idrestaurante,int idcategoriaextra, boolean disponible, String nombre, Pageable pageable, double inputPMin, double inputPMax);

    Page<Extra> findByIdrestauranteAndDisponible(int idrestaurante,boolean disponible, Pageable pageable);

        @Query(value = "select e.nombre, e.idextra, e.foto, e.fotocontenttype, e.fotonombre, e.preciounitario, ce.tipo,e.disponible from extra e \n" +
                "inner join categoriaextra ce on ce.idcategoriaextra = e.idcategoriaextra\n" +
                "inner join categoriaextra_has_plato chp on chp.idcategoriaextra = ce.idcategoriaextra\n" +
                "where e.idrestaurante = ?1 and chp.idplato = ?2 and e.disponible = 1 and e.nombre like %?3% \n" +
                " and (chp.idcategoriaextra > ?4 and chp.idcategoriaextra <= ?5) \n" +
                "     and (e.preciounitario > ?6 and e.preciounitario <= ?7)",nativeQuery = true, countQuery = "select count(*) from extra e \n" +
                "inner join categoriaextra ce on ce.idcategoriaextra = e.idcategoriaextra\n" +
                "inner join categoriaextra_has_plato chp on chp.idcategoriaextra = ce.idcategoriaextra\n" +
                "where e.idrestaurante = ?1 and chp.idplato = ?2 and e.disponible = 1 and e.nombre like %?3% \n" +
                " and (chp.idcategoriaextra > ?4 and chp.idcategoriaextra <= ?5) \n" +
                "      and (e.preciounitario > ?6 and e.preciounitario <= ?7)")
    Page<ExtraDTO> listaExtrasDisponibles(int idRestaurante, int idPlato,String texto, int limitInfCa, int limitSupCa,
                                          int limitInfPe, int limitSupPe, int disponible,Pageable pageable);
}
