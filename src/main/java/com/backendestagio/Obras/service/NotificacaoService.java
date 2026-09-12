package com.backendestagio.Obras.service;

import com.backendestagio.Obras.model.Notificacao;
import com.backendestagio.Obras.model.Obra;
import com.backendestagio.Obras.model.Template;
import com.backendestagio.Obras.model.Usuario;
import com.backendestagio.Obras.repository.NotificacaoRepository;
import com.backendestagio.Obras.repository.ObraRepository;
import com.backendestagio.Obras.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final ObraRepository obraRepository;
    private final TemplateRepository templateRepository;
    private static final Pattern PADRAO_VARIAVEL = Pattern.compile("\\{(\\w+)\\}");

    public NotificacaoService(NotificacaoRepository notificacaoRepository,
                               ObraRepository obraRepository,
                               TemplateRepository templateRepository) {
        this.notificacaoRepository = notificacaoRepository;
        this.obraRepository = obraRepository;
        this.templateRepository = templateRepository;
    }

    // Substitui as variáveis {chave} do corpo do template pelos dados da obra.
    // Variáveis não reconhecidas são deixadas como estão, para o admin perceber o problema.
    private String montarMensagem(Template template, Obra obra) {
        Usuario cliente = obra.getCliente();

        Map<String, String> variaveis = new HashMap<>();
        variaveis.put("nome_cliente", cliente.getNome() + " " + cliente.getSobrenome());
        variaveis.put("obra", obra.getNome());
        variaveis.put("status", obra.getStatus());
        String endereco = obra.getRua() + ", " + obra.getNumero();
        if (obra.getComplemento() != null && !obra.getComplemento().isBlank()) {
            endereco += " - " + obra.getComplemento();
        }
        variaveis.put("endereco", endereco);

        Matcher matcher = PADRAO_VARIAVEL.matcher(template.getCorpo());
        StringBuilder resultado = new StringBuilder();
        while (matcher.find()) {
            String chave = matcher.group(1);
            String valor = variaveis.getOrDefault(chave, matcher.group(0));
            matcher.appendReplacement(resultado, Matcher.quoteReplacement(valor));
        }
        matcher.appendTail(resultado);
        return resultado.toString();
    }

    // Disparada pelo ObraService quando o status de uma obra muda.
    // Só envia se houver um template marcado como padrão de notificação de status.
    public void criarNotificacaoAutomatica(Obra obra) {
        templateRepository.findByPadraoNotificacaoStatusTrue().ifPresent(template -> {
            Notificacao notificacao = new Notificacao();
            notificacao.setObra(obra);
            notificacao.setTemplate(template);
            notificacao.setMensagem(montarMensagem(template, obra));
            notificacaoRepository.save(notificacao);
        });
    }

    // Envio manual, feito pelo admin escolhendo obra + template.
    public Optional<Notificacao> criarManual(Long obraId, Long templateId) {
        Optional<Obra> obraOpt = obraRepository.findById(obraId);
        Optional<Template> templateOpt = templateRepository.findById(templateId);
        if (obraOpt.isEmpty() || templateOpt.isEmpty()) {
            return Optional.empty();
        }

        Obra obra = obraOpt.get();
        Template template = templateOpt.get();

        Notificacao notificacao = new Notificacao();
        notificacao.setObra(obra);
        notificacao.setTemplate(template);
        notificacao.setMensagem(montarMensagem(template, obra));
        return Optional.of(notificacaoRepository.save(notificacao));
    }

    public List<Notificacao> listarTodas() {
        return notificacaoRepository.findAllByOrderByDataCriacaoDesc();
    }

    public List<Notificacao> listarPorCliente(Long clienteId) {
        return notificacaoRepository.findByObraClienteIdOrderByDataCriacaoDesc(clienteId);
    }

    public Optional<Notificacao> marcarComoLida(Long id, Usuario usuario) {
        return notificacaoRepository.findById(id).map(notificacao -> {
            garantirPermissao(notificacao, usuario);
            notificacao.setLida(true);
            return notificacaoRepository.save(notificacao);
        });
    }

    public boolean deletar(Long id, Usuario usuario) {
        return notificacaoRepository.findById(id).map(notificacao -> {
            garantirPermissao(notificacao, usuario);
            notificacaoRepository.delete(notificacao);
            return true;
        }).orElse(false);
    }

    // Admin gerencia qualquer notificação; o cliente só as suas próprias (obra.cliente == ele).
    private void garantirPermissao(Notificacao notificacao, Usuario usuario) {
        boolean isAdmin = "Admin".equalsIgnoreCase(usuario.getPerfil());
        boolean isDono = notificacao.getObra().getCliente().getId().equals(usuario.getId());
        if (!isAdmin && !isDono) {
            throw new SecurityException("Você não tem permissão para gerenciar esta notificação.");
        }
    }
}
