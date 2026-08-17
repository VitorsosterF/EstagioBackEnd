package com.backendestagio.Obras.service;

import com.backendestagio.Obras.model.Template;
import com.backendestagio.Obras.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.LinkedHashSet;

@Service
public class TemplateService {

    private final TemplateRepository templateRepository;
    private static final Pattern PADRAO_VARIAVEL = Pattern.compile("\\{(\\w+)\\}");

    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    private String extrairVariaveis(String corpo) {
        LinkedHashSet<String> encontradas = new LinkedHashSet<>();
        Matcher matcher = PADRAO_VARIAVEL.matcher(corpo);
        while (matcher.find()) {
            encontradas.add(matcher.group(1));
        }
        return String.join(", ", encontradas);
    }

    public List<Template> listarTodos() {
        return templateRepository.findAll();
    }

    public Optional<Template> buscarPorId(Long id) {
        return templateRepository.findById(id);
    }

    public Template criar(Template template) {
        template.setVariaveis(extrairVariaveis(template.getCorpo()));
        return templateRepository.save(template);
    }

    public Optional<Template> atualizar(Long id, Template templateAtualizado) {
        return templateRepository.findById(id).map(template -> {
            template.setTitulo(templateAtualizado.getTitulo());
            template.setTipo(templateAtualizado.getTipo());
            template.setCorpo(templateAtualizado.getCorpo());
            template.setVariaveis(extrairVariaveis(templateAtualizado.getCorpo()));
            return templateRepository.save(template);
        });
    }

    public boolean deletar(Long id) {
        return templateRepository.findById(id).map(template -> {
            templateRepository.delete(template);
            return true;
        }).orElse(false);
    }
}