package com.example.demo.repositories;

import com.example.demo.entities.Extra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ExtraRepository extends JpaRepository<Extra, Integer> {
}
