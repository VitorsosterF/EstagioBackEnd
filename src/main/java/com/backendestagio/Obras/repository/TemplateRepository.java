package com.backendestagio.Obras.repository;

import com.backendestagio.Obras.model.Template;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    Optional<Template> findByPadraoNotificacaoStatusTrue();
}