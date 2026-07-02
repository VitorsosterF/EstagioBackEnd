package com.backendestagio.Obras.controller;

import com.backendestagio.Obras.model.Obra;
import com.backendestagio.Obras.service.ObraService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/obras")
@CrossOrigin(origins = "http://localhost:5173") //pq o front ta na porta padrão
public class ObraController {

    private final ObraService obraService;
    private final ObjectMapper objectMapper;

    public ObraController(ObraService obraService, ObjectMapper objectMapper)
    {
        this.obraService = obraService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public List<Obra> listar()
    {
        return obraService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Obra> buscarPorId(@PathVariable Long id) {
        return obraService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> criar(
            @RequestPart("obra") String obraJson,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem) throws IOException {

        Obra obra = objectMapper.readValue(obraJson, Obra.class);

        if (obra.getNome() == null || obra.getNome().isBlank() ||
                obra.getRua() == null || obra.getRua().isBlank() ||
                obra.getComplemento() == null || obra.getComplemento().isBlank() ||
                obra.getNumero() == null || obra.getNumero().isBlank() ||
                obra.getClienteResponsavel() == null || obra.getClienteResponsavel().isBlank() ||
                obra.getStatus() == null || obra.getStatus().isBlank()) {
            return ResponseEntity.badRequest().body("Campos obrigatórios não preenchidos.");
        }

        return ResponseEntity.ok(obraService.criar(obra, imagem));
    }


    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestPart("obra") String obraJson,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem) throws IOException {

        Obra obraAtualizada = objectMapper.readValue(obraJson, Obra.class);

        return obraService.atualizar(id, obraAtualizada, imagem)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (obraService.deletar(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}