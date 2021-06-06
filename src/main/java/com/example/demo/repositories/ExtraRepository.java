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


    Page<Extra> findByIdrestauranteAndIdcategoriaextraAndDisponibleAndNombreIsContainingAndPreciounitarioGreaterThanEqualAndPreciounitarioLessThanEqual(int idrestaurante,int idcategoriaextra, boolean disponible, String nombre, Pageable pageable, double inputPMin, double inputPMax);


    Page<Extra> findByIdrestauranteAndDisponible(int idrestaurante,boolean disponible, Pageable pageable);

    @Query(value = "SELECT e.idextra, e.foto, e.fotocontenttype, e.fotonombre, e.nombre, e.preciounitario, ce.tipo FROM extra e\n" +
            "inner join categoriaextra ce on e.idcategoriaextra = ce.idcategoriaextra\n" +
            "where e.idrestaurante = ?1 and e.disponible = 1\n" +
            "order by ce.idcategoriaextra;",nativeQuery = true)
    List<ExtraDTO> listaExtrasDisponibles(int idRestaurante);
}
