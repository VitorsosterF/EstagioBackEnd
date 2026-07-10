package com.backendestagio.Obras.repository;

import com.backendestagio.Obras.model.Obra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObraRepository extends JpaRepository<Obra, Long>
{
    boolean existsByClienteResponsavel(String clienteResponsavel);
}