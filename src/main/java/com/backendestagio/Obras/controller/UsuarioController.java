package com.backendestagio.Obras.controller;

import com.backendestagio.Obras.model.Usuario;
import com.backendestagio.Obras.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController
{
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService)
    {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Usuario> listar()
    {
        return usuarioService.listarTodos();
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Usuario usuario) {
        if (usuario.getNome() == null || usuario.getNome().isBlank() ||
                usuario.getSobrenome() == null || usuario.getSobrenome().isBlank() ||
                usuario.getEmail() == null || usuario.getEmail().isBlank() ||
                usuario.getSenha() == null || usuario.getSenha().isBlank() ||
                usuario.getPerfil() == null || usuario.getPerfil().isBlank()) {
            return ResponseEntity.badRequest().body("Campos obrigatórios não preenchidos.");
        }

        return usuarioService.criarUsuario(usuario)
                .map(erro -> ResponseEntity.status(409).body(erro))
                .orElse(ResponseEntity.ok().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody Usuario usuarioAtualizado) {
        return usuarioService.atualizarUsuario(id, usuarioAtualizado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletar(@PathVariable Long id) {
        try {
            if (usuarioService.deletarUsuario(id)) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }
}