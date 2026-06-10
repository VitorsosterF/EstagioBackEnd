package com.backendestagio.Obras.controller;

import com.backendestagio.Obras.model.Usuario;
import com.backendestagio.Obras.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController
{
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository)
    {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<Usuario> listar()
    {
        return usuarioRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Usuario usuario)
    {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent())
        {
            return ResponseEntity.status(409).body("Email já cadastrado");
        }
        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody Usuario usuarioAtualizado)
    {
        return usuarioRepository.findById(id).map(usuario ->
        {
            usuario.setNome(usuarioAtualizado.getNome());
            usuario.setSobrenome(usuarioAtualizado.getSobrenome());
            usuario.setEmail(usuarioAtualizado.getEmail());
            usuario.setSenha(usuarioAtualizado.getSenha());
            usuario.setPerfil(usuarioAtualizado.getPerfil());
            return ResponseEntity.ok(usuarioRepository.save(usuario));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id)
    {
        return usuarioRepository.findById(id).map(usuario ->
        {
            usuarioRepository.delete(usuario);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}