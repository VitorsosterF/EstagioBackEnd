package com.backendestagio.Obras.service;

import com.backendestagio.Obras.model.Usuario;
import com.backendestagio.Obras.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder)
    {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos()
    {
        return usuarioRepository.findAll();
    }

    public Optional<String> criarUsuario(Usuario usuario)
    {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return Optional.of("Email já cadastrado.");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);
        return Optional.empty();
    }

    public Optional<Usuario> atualizarUsuario(Long id, Usuario usuarioAtualizado)
    {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNome(usuarioAtualizado.getNome());
            usuario.setSobrenome(usuarioAtualizado.getSobrenome());
            usuario.setEmail(usuarioAtualizado.getEmail());
            usuario.setPerfil(usuarioAtualizado.getPerfil());

            // Só atualiza a senha se uma nova foi enviada
            if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isBlank()) {
                usuario.setSenha(usuarioAtualizado.getSenha());
            }

            return usuarioRepository.save(usuario);
        });
    }

    public boolean deletarUsuario(Long id)
    {
        return usuarioRepository.findById(id).map(usuario -> {
            usuarioRepository.delete(usuario);
            return true;
        }).orElse(false);
    }
}