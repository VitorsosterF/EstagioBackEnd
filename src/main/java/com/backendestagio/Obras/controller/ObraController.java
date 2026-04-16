package com.backendestagio.Obras.controller;

import com.backendestagio.Obras.model.Obra;
import com.backendestagio.Obras.repository.ObraRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/obras")
@CrossOrigin(origins = "http://localhost:5173") //pq o front ta na porta padrão
public class ObraController {

    private final ObraRepository obraRepository;

    public ObraController(ObraRepository obraRepository) {
        this.obraRepository = obraRepository;
    }

    @GetMapping
    public List<Obra> listar() {
        return obraRepository.findAll();
    }

    @PostMapping
    public Obra criar(@RequestBody Obra obra) {
        return obraRepository.save(obra);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Obra> atualizar(@PathVariable Long id, @RequestBody Obra obraAtualizada) {
        return obraRepository.findById(id).map(obra -> {
            obra.setNome(obraAtualizada.getNome());
            obra.setEndereco(obraAtualizada.getEndereco());
            obra.setClienteResponsavel(obraAtualizada.getClienteResponsavel());
            obra.setStatus(obraAtualizada.getStatus());
            obra.setDescricao(obraAtualizada.getDescricao());
            
            return ResponseEntity.ok(obraRepository.save(obra));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return obraRepository.findById(id).map(obra -> {
            obraRepository.delete(obra);
            return ResponseEntity.ok().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}