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

    @Query(value= "select idextra, nombre, precioUnitario, disponible, idrestaurante, idcategoriaextra from extra where idrestaurante=?1", nativeQuery = true)
    List<Extra> listarExtra(int idrestaurante);

    Page<Extra> findByIdrestauranteAndDisponibleAndNombreIsContainingAndPreciounitarioGreaterThanEqualAndPreciounitarioLessThanEqual(int idrestaurante, boolean disponible, String nombre, Pageable pageable, double inputPMin, double inputPMax);


    Page<Extra> findByIdrestauranteAndDisponible(int idrestaurante,boolean disponible, Pageable pageable);


    @Query(value = "SELECT DISTINCT  e.idextra, e.nombre, e.precioUnitario FROM (extra e \n" +
            "inner join categoriaextra ce on ce.idcategoriaextra=e.idcategoriaExtra\n" +
            "inner join categoriaextra_has_plato cep on cep.idcategoriaextra=ce.idcategoriaextra\n" +
            "inner join plato p on p.idplato=cep.idplato\n" +
            "inner join restaurante re on p.idrestaurante=re.idrestaurante) \n" +
            "where e.nombre like %% and e.disponible=1 and re.idrestaurante=?2 and ce.idcategoriaextra=?3", nativeQuery = true)
    List<ExtraDTO> buscarExtraPornombre(String nombre, int idRestaurante, int categoria);

    @Query(value = "SELECT DISTINCT e.idextra, e.nombre, e.precioUnitario FROM (extra e \n" +
            "inner join categoriaextra ce on ce.idcategoriaextra=e.idcategoriaExtra\n" +
            "inner join categoriaextra_has_plato cep on cep.idcategoriaextra=ce.idcategoriaextra\n" +
            "inner join plato p on p.idplato=cep.idplato\n" +
            "inner join restaurante re on p.idrestaurante=re.idrestaurante)\n" +
            "where e.nombre like %?1% and (e.precioUnitario>?2 and e.precioUnitario<?3)  \n" +
            "and e.disponible=1 and re.idrestaurante=?4 and ce.idcategoriaextra=?5", nativeQuery = true)
    List<ExtraDTO> buscarExtraPornombreyPrecio(String nombre, double precio1, double precio2, int idRestaurante, int categoria);

    @Query(value = "SELECT DISTINCT e.idextra, e.nombre, e.precioUnitario FROM (extra e \n" +
            "inner join categoriaextra ce on ce.idcategoriaextra=e.idcategoriaExtra\n" +
            "inner join categoriaextra_has_plato cep on cep.idcategoriaextra=ce.idcategoriaextra\n" +
            "inner join plato p on p.idplato=cep.idplato\n" +
            "inner join restaurante re on p.idrestaurante=re.idrestaurante)\n" +
            "where (e.precioUnitario>?1 and e.precioUnitario<?2) and e.disponible=1 " +
            "and re.idrestaurante=?3 and ce.idcategoriaextra=?4 ", nativeQuery = true)
    List<ExtraDTO> buscarExtraPorPrecio(double precio1, double precio2, int idRestaurante, int categoria);
    @Query(value = "SELECT DISTINCT e.idextra, e.nombre, e.precioUnitario FROM (extra e \n" +
            "inner join categoriaextra ce on ce.idcategoriaextra=e.idcategoriaExtra\n" +
            "inner join categoriaextra_has_plato cep on cep.idcategoriaextra=ce.idcategoriaextra\n" +
            "inner join plato p on p.idplato=cep.idplato\n" +
            "inner join restaurante re on p.idrestaurante=re.idrestaurante)\n" +
            "where (e.precioUnitario>=?1) and e.disponible=1 and re.idrestaurante=?2 and ce.idcategoriaextra=?3 ", nativeQuery = true)
    List<ExtraDTO> buscarExtraPorPrecioAMAS(double precio1,int idRestaurante, int categoria);

    @Query(value = "SELECT DISTINCT e.idextra, e.nombre, e.precioUnitario FROM (extra e \n" +
            "inner join categoriaextra ce on ce.idcategoriaextra=e.idcategoriaExtra\n" +
            "inner join categoriaextra_has_plato cep on cep.idcategoriaextra=ce.idcategoriaextra\n" +
            "inner join plato p on p.idplato=cep.idplato\n" +
            "inner join restaurante re on p.idrestaurante=re.idrestaurante)\n" +
            "where e.nombre like %?1% and (e.precioUnitario>=?2)  \n" +
            "and e.disponible=1 and re.idrestaurante=?3 and ce.idcategoriaextra=?4", nativeQuery = true)
    List<ExtraDTO> buscarExtraPornombreyPrecioAMAS(String nombre, double precio1, int idRestaurante, int categoria);
    @Query(value = "SELECT DISTINCT e.idextra, e.nombre, e.precioUnitario FROM (extra e \n" +
            "inner join categoriaextra ce on ce.idcategoriaextra=e.idcategoriaExtra\n" +
            "inner join categoriaextra_has_plato cep on cep.idcategoriaextra=ce.idcategoriaextra\n" +
            "inner join plato p on p.idplato=cep.idplato\n" +
            "inner join restaurante re on p.idrestaurante=re.idrestaurante)\n" +
            "where re.idrestaurante=?1 and e.disponible=1 and ce.idcategoriaextra=?2", nativeQuery = true)
    List<ExtraDTO> lista( int idRestaurante, int categoria);
}
