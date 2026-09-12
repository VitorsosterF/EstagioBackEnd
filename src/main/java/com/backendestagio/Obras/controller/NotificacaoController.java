package com.backendestagio.Obras.controller;

import com.backendestagio.Obras.dto.NotificacaoManualRequest;
import com.backendestagio.Obras.model.Notificacao;
import com.backendestagio.Obras.model.Usuario;
import com.backendestagio.Obras.repository.UsuarioRepository;
import com.backendestagio.Obras.service.NotificacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/notificacoes")
@CrossOrigin(origins = "http://localhost:5173")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;
    private final UsuarioRepository usuarioRepository;

    public NotificacaoController(NotificacaoService notificacaoService, UsuarioRepository usuarioRepository) {
        this.notificacaoService = notificacaoService;
        this.usuarioRepository = usuarioRepository;
    }

    // Admin: todas as notificações enviadas, de todos os clientes.
    @GetMapping
    public ResponseEntity<?> listarTodas(Authentication authentication) {
        Usuario usuario = usuarioAutenticado(authentication);
        if (usuario == null) return ResponseEntity.status(401).build();
        if (!"Admin".equalsIgnoreCase(usuario.getPerfil())) {
            return ResponseEntity.status(403).body("Apenas administradores podem ver todas as notificações.");
        }
        return ResponseEntity.ok(notificacaoService.listarTodas());
    }

    // Qualquer usuário logado: só as notificações das obras em que ele é o cliente.
    @GetMapping("/minhas")
    public ResponseEntity<?> listarMinhas(Authentication authentication) {
        Usuario usuario = usuarioAutenticado(authentication);
        if (usuario == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(notificacaoService.listarPorCliente(usuario.getId()));
    }

    // Envio manual, restrito ao admin.
    @PostMapping
    public ResponseEntity<?> enviarManual(Authentication authentication, @RequestBody NotificacaoManualRequest request) {
        Usuario usuario = usuarioAutenticado(authentication);
        if (usuario == null) return ResponseEntity.status(401).build();
        if (!"Admin".equalsIgnoreCase(usuario.getPerfil())) {
            return ResponseEntity.status(403).body("Apenas administradores podem enviar notificações manualmente.");
        }
        if (request.getObraId() == null || request.getTemplateId() == null) {
            return ResponseEntity.badRequest().body("Informe a obra e o template.");
        }

        Optional<Notificacao> criada = notificacaoService.criarManual(request.getObraId(), request.getTemplateId());
        if (criada.isPresent()) {
            return ResponseEntity.ok(criada.get());
        }
        return ResponseEntity.badRequest().body("Obra ou template não encontrados.");
    }

    // Marcar como lida: o próprio cliente dono da notificação, ou o admin.
    @PatchMapping("/{id}/lida")
    public ResponseEntity<?> marcarComoLida(Authentication authentication, @PathVariable Long id) {
        Usuario usuario = usuarioAutenticado(authentication);
        if (usuario == null) return ResponseEntity.status(401).build();

        try {
            Notificacao notificacao = notificacaoService.marcarComoLida(id, usuario).orElse(null);
            return notificacao != null ? ResponseEntity.ok(notificacao) : ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    // Excluir: o próprio cliente dono da notificação, ou o admin.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(Authentication authentication, @PathVariable Long id) {
        Usuario usuario = usuarioAutenticado(authentication);
        if (usuario == null) return ResponseEntity.status(401).build();

        try {
            return notificacaoService.deletar(id, usuario)
                    ? ResponseEntity.ok().build()
                    : ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    private Usuario usuarioAutenticado(Authentication authentication) {
        if (authentication == null) return null;
        return usuarioRepository.findByEmail(authentication.getName()).orElse(null);
    }
}
