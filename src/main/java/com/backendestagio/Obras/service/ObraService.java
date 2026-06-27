package com.backendestagio.Obras.service;

import com.backendestagio.Obras.model.Obra;
import com.backendestagio.Obras.repository.ObraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ObraService
{
    private final ObraRepository obraRepository;

    public ObraService(ObraRepository obraRepository)
    {
        this.obraRepository = obraRepository;
    }

    public List<Obra> listarTodas()
    {
        return obraRepository.findAll();
    }

    public Obra criarObra(Obra obra)
    {
        return obraRepository.save(obra);
    }

    public Optional<Obra> atualizarObra(long id, Obra obraAtualizada)
    {
        return obraRepository.findById(id).map(obra -> {
            obra.setNome(obraAtualizada.getNome());
            obra.setEndereco(obraAtualizada.getEndereco());
            obra.setClienteResponsavel(obraAtualizada.getClienteResponsavel());
            obra.setStatus(obraAtualizada.getStatus());
            obra.setDescricao(obraAtualizada.getDescricao());
            return obraRepository.save(obra);
        });
    }

    public boolean deletarObra(Long id)
    {
        return obraRepository.findById(id).map(obra ->{
            obraRepository.delete(obra);
            return true;
        }).orElse(false);
    }
}
