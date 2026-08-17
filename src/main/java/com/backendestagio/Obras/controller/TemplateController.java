package com.backendestagio.Obras.controller;

import com.backendestagio.Obras.model.Template;
import com.backendestagio.Obras.service.TemplateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/templates")
@CrossOrigin(origins = "http://localhost:5173")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public List<Template> listar() {
        return templateService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Template> buscarPorId(@PathVariable Long id) {
        return templateService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Template template) {
        if (template.getTitulo() == null || template.getTitulo().isBlank() ||
                template.getTipo() == null || template.getTipo().isBlank() ||
                template.getCorpo() == null || template.getCorpo().isBlank()) {
            return ResponseEntity.badRequest().body("Campos obrigatórios não preenchidos.");
        }
        return ResponseEntity.ok(templateService.criar(template));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Template> atualizar(@PathVariable Long id, @RequestBody Template templateAtualizado) {
        return templateService.atualizar(id, templateAtualizado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (templateService.deletar(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}