package com.backendestagio.Obras.service;

import com.backendestagio.Obras.dto.ObraRequest;
import com.backendestagio.Obras.model.Obra;
import com.backendestagio.Obras.model.Usuario;
import com.backendestagio.Obras.repository.ObraRepository;
import com.backendestagio.Obras.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ObraService
{
    private final ObraRepository obraRepository;
    private final UsuarioRepository usuarioRepository;
    private final FileStorageService fileStorageService;
    private final NotificacaoService notificacaoService;

    public ObraService(ObraRepository obraRepository,
                        UsuarioRepository usuarioRepository,
                        FileStorageService fileStorageService,
                        NotificacaoService notificacaoService)
    {
        this.obraRepository = obraRepository;
        this.usuarioRepository = usuarioRepository;
        this.fileStorageService = fileStorageService;
        this.notificacaoService = notificacaoService;
    }

    public List<Obra> listarTodas()
    {
        return obraRepository.findAll();
    }

    public Optional<Obra> buscarPorId(Long id)
    {
        return obraRepository.findById(id);
    }

    public Obra criar(ObraRequest request, MultipartFile imagem) throws IOException
    {
        Usuario cliente = buscarCliente(request.getClienteId());

        Obra obra = new Obra();
        obra.setNome(request.getNome());
        obra.setRua(request.getRua());
        obra.setNumero(request.getNumero());
        obra.setComplemento(request.getComplemento());
        obra.setCliente(cliente);
        obra.setStatus(request.getStatus());
        obra.setDescricao(request.getDescricao());

        if (imagem != null && !imagem.isEmpty()) {
            obra.setImagemUrl(fileStorageService.salvar(imagem));
        }
        return obraRepository.save(obra);
    }

    public Optional<Obra> atualizar(Long id, ObraRequest request, MultipartFile imagem) throws IOException
    {
        return obraRepository.findById(id).map(obra -> {
            Usuario cliente = buscarCliente(request.getClienteId());
            String statusAnterior = obra.getStatus();

            obra.setNome(request.getNome());
            obra.setRua(request.getRua());
            obra.setNumero(request.getNumero());
            obra.setComplemento(request.getComplemento());
            obra.setCliente(cliente);
            obra.setStatus(request.getStatus());
            obra.setDescricao(request.getDescricao());

            if (imagem != null && !imagem.isEmpty()) {
                fileStorageService.deletar(obra.getImagemUrl());
                try {
                    obra.setImagemUrl(fileStorageService.salvar(imagem));
                } catch (IOException e) {
                    throw new RuntimeException("Erro ao salvar imagem.", e);
                }
            }

            Obra obraSalva = obraRepository.save(obra);

            if (!statusAnterior.equals(obraSalva.getStatus())) {
                notificacaoService.criarNotificacaoAutomatica(obraSalva);
            }

            return obraSalva;
        });
    }

    public boolean deletar(Long id)
    {
        return obraRepository.findById(id).map(obra -> {
            fileStorageService.deletar(obra.getImagemUrl());
            obraRepository.delete(obra);
            return true;
        }).orElse(false);
    }

    private Usuario buscarCliente(Long clienteId)
    {
        return usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente informado não existe."));
    }
}
