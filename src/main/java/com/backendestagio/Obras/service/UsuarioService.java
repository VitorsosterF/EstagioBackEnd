package com.backendestagio.Obras.service;

import com.backendestagio.Obras.dto.UsuarioRequest;
import com.backendestagio.Obras.model.Usuario;
import com.backendestagio.Obras.repository.ObraRepository;
import com.backendestagio.Obras.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObraRepository obraRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, ObraRepository obraRepository)
    {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.obraRepository = obraRepository;
    }

    public List<Usuario> listarTodos()
    {
        return usuarioRepository.findAll();
    }

    public Optional<String> criarUsuario(UsuarioRequest request)
    {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            return Optional.of("Email já cadastrado.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setSobrenome(request.getSobrenome());
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setPerfil(request.getPerfil());
        usuarioRepository.save(usuario);
        return Optional.empty();
    }

    public Optional<Usuario> atualizarUsuario(Long id, UsuarioRequest request)
    {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNome(request.getNome());
            usuario.setSobrenome(request.getSobrenome());
            usuario.setEmail(request.getEmail());
            usuario.setPerfil(request.getPerfil());

            if (request.getSenha() != null && !request.getSenha().isBlank()) {
                usuario.setSenha(passwordEncoder.encode(request.getSenha()));
            }

            return usuarioRepository.save(usuario);
        });
    }

    public boolean deletarUsuario(Long id)
    {
        return usuarioRepository.findById(id).map(usuario -> {
            boolean possuiObras = obraRepository.existsByClienteId(usuario.getId());
            if (possuiObras) {
                throw new RuntimeException("Usuário possui obras associadas e não pode ser excluído.");
            }
            usuarioRepository.delete(usuario);
            return true;
        }).orElse(false);
    }
}