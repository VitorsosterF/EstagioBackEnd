package com.backendestagio.Obras.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// DTO de entrada para criar/atualizar Obra. Ignora campos extras que o front
// eventualmente mande (id, cliente, criadoEm, imagemUrl) ao reenviar o objeto
// inteiro no multipart.
@JsonIgnoreProperties(ignoreUnknown = true)
public class ObraRequest {

    private String nome;
    private String rua;
    private String numero;
    private String complemento;
    private Long clienteId;
    private String status;
    private String descricao;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
