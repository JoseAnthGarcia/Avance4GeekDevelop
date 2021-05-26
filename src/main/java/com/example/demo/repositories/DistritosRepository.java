package com.example.demo.repositories;

import com.example.demo.entities.Distrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistritosRepository extends JpaRepository<Distrito, Integer> {

    Distrito findByIddistrito(int iddistrito);
}
