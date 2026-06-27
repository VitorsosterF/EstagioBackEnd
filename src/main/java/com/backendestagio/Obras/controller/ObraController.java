package com.backendestagio.Obras.controller;

import com.backendestagio.Obras.model.Obra;
import com.backendestagio.Obras.repository.ObraRepository;
import com.backendestagio.Obras.service.ObraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/obras")
@CrossOrigin(origins = "http://localhost:5173") //pq o front ta na porta padrão
public class ObraController {

    private final ObraService obraService;

    public ObraController(ObraService obraService)
    {
        this.obraService = obraService;
    }

    @GetMapping
    public List<Obra> listar()
    {
        return obraService.listarTodas();
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Obra obra) {
        if (obra.getNome() == null || obra.getNome().isBlank() ||
                obra.getEndereco() == null || obra.getEndereco().isBlank() ||
                obra.getClienteResponsavel() == null || obra.getClienteResponsavel().isBlank() ||
                obra.getStatus() == null || obra.getStatus().isBlank()) {
            return ResponseEntity.badRequest().body("Campos obrigatórios não preenchidos.");
        }
        return ResponseEntity.ok(obraService.criarObra(obra));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Obra> atualizar(@PathVariable Long id, @RequestBody Obra obraAtualizada) {
        return obraService.atualizarObra(id, obraAtualizada)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (obraService.deletarObra(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}