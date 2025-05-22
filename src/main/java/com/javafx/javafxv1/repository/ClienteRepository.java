package com.javafx.javafxv1.repository;

import com.javafx.javafxv1.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByCpf(String cpf);

    // Busca por nome (com filtro parcial, case insensitive)
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

}
