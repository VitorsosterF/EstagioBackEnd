package com.backendestagio.Obras.repository;

import com.backendestagio.Obras.model.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findAllByOrderByDataCriacaoDesc();
    List<Notificacao> findByObraClienteIdOrderByDataCriacaoDesc(Long clienteId);
}
