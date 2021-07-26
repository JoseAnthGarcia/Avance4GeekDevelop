package com.example.demo.repositories;

import com.example.demo.entities.Cliente_has_cupon;
import com.example.demo.entities.Cliente_has_cuponKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteHasCuponRepository extends JpaRepository<Cliente_has_cupon, Cliente_has_cuponKey> {

    //Lista para validar que se eliminen
    @Query(value = "select * from cliente_has_cupon chc where chc.idcupon = ?1 and utilizado = 0 ", nativeQuery = true)
    List<Cliente_has_cupon> findByIdCupon(int idCupon);

}
