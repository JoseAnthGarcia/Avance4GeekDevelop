package com.example.demo.repositories;

import com.example.demo.entities.Movilidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovilidadRepository extends JpaRepository<Movilidad, Integer> {

    Movilidad findByPlaca(String placa);

    Movilidad findByLicencia(String licencia);
}
