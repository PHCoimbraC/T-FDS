package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstoqueRepository extends JpaRepository<ItemEstoque, Long> {
    
    @Query("SELECT i FROM ItemEstoque i WHERE i.ingrediente.id = :ingredienteId")
    Optional<ItemEstoque> findByIngredienteId(@Param("ingredienteId") Long ingredienteId);
}