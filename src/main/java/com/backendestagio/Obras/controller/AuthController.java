package com.backendestagio.Obras.controller;

import com.backendestagio.Obras.config.JwtService;
import com.backendestagio.Obras.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UsuarioRepository usuarioRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        System.out.println("Entrou no método login");
        String email = body.get("email");
        String senha = body.get("senha");

        return usuarioRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(senha, u.getSenha()))
                .map(u -> ResponseEntity.ok(Map.of(
                        "token", jwtService.gerarToken(u.getEmail()),
                        "nome", u.getNome(),
                        "sobrenome", u.getSobrenome()
                )))
                .orElse(ResponseEntity.status(401).build());
    }
}