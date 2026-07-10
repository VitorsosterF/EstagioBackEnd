package com.backendestagio.Obras.service;

import com.backendestagio.Obras.model.Obra;
import com.backendestagio.Obras.repository.ObraRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ObraService
{
    private final ObraRepository obraRepository;
    private final FileStorageService fileStorageService;

    public ObraService(ObraRepository obraRepository, FileStorageService fileStorageService)
    {
        this.obraRepository = obraRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<Obra> listarTodas()
    {
        return obraRepository.findAll();
    }

    public Optional<Obra> buscarPorId(Long id)
    {
        return obraRepository.findById(id);
    }

    public Obra criar(Obra obra, MultipartFile imagem) throws IOException
    {
        if (imagem != null && !imagem.isEmpty()) {
            obra.setImagemUrl(fileStorageService.salvar(imagem));
        }
        return obraRepository.save(obra);
    }

    public Optional<Obra> atualizar(Long id, Obra obraAtualizada, MultipartFile imagem) throws IOException
    {
        return obraRepository.findById(id).map(obra -> {
            obra.setNome(obraAtualizada.getNome());
            obra.setRua(obraAtualizada.getRua());
            obra.setNumero(obraAtualizada.getNumero());
            obra.setComplemento(obraAtualizada.getComplemento());
            obra.setClienteResponsavel(obraAtualizada.getClienteResponsavel());
            obra.setStatus(obraAtualizada.getStatus());
            obra.setDescricao(obraAtualizada.getDescricao());

            if (imagem != null && !imagem.isEmpty()) {
                fileStorageService.deletar(obra.getImagemUrl());
                try {
                    obra.setImagemUrl(fileStorageService.salvar(imagem));
                } catch (IOException e) {
                    throw new RuntimeException("Erro ao salvar imagem.", e);
                }
            }

            return obraRepository.save(obra);
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
}
