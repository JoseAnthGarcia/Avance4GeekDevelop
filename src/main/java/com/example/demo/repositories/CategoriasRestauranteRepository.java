package com.example.demo.repositories;

import com.example.demo.entities.Categorias;
import com.example.demo.entities.Cupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriasRestauranteRepository extends JpaRepository<Categorias, Integer> {

    @Query(value = "select cr.* from restaurante_has_categoriarestaurante rhcr\n" +
            "left join categoriarestaurante cr on rhcr.idcategoria=cr.idcategoria\n" +
            "where rhcr.idrestaurante = ?1 ", nativeQuery = true)
    List<Categorias> findCategoriasByIdrestaurante(Integer idrestaurante);
}
