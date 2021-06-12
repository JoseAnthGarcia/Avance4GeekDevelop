package com.example.demo.repositories;

import com.example.demo.entities.Distrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DistritosRepository extends JpaRepository<Distrito, Integer> {


    @Query(value = "select  d.iddistrito, d.nombre from distrito d " +
            "       inner join restaurante r on r.iddistrito = d.iddistrito" +
            "       where r.idrestaurante = ?1 ",nativeQuery = true)
    Distrito findDistritoById(int idRest);

    Distrito findByIddistrito(int iddistrito);
}
