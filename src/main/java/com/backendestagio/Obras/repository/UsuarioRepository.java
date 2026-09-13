package com.backendestagio.Obras.repository;

import com.backendestagio.Obras.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>
{
    Optional<Usuario> findByEmail(String email);

    // findAll() não garante ordem nenhuma — o front usa a ordem de retorno
    // para inferir "usuários recentes", então precisa ser explícito aqui.
    List<Usuario> findAllByOrderByIdAsc();
}