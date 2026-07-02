package com.backendestagio.Obras.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "obras")
public class Obra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String rua;

    @Column(nullable = false)
    private String numero;

    @Column
    private String complemento;

    @Column(name = "cliente_responsavel", nullable = false)
    private String clienteResponsavel;

    @Column(nullable = false)
    private String status;

    @Column(nullable = true)
    private String descricao;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "imagem_url")
    private String imagemUrl;

    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }

    @PrePersist
    public void prePersist()
    {
        if (this.criadoEm == null)
        {
            this.criadoEm = LocalDateTime.now();
        }
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getNome()
    {
        return nome;
    }

    public void setNome(String nome)
    {
        this.nome = nome;
    }

    public String getRua()
    {
        return rua;
    }

    public void setRua(String rua)
    {
        this.rua = rua;
    }

    public String getNumero()
    {
        return numero;
    }

    public void setNumero(String numero)
    {
        this.numero = numero;
    }

    public String getComplemento()
    {
        return complemento;
    }

    public void setComplemento(String complemento)
    {
        this.complemento = complemento;
    }

    public String getClienteResponsavel()
    {
        return clienteResponsavel;
    }

    public void setClienteResponsavel(String clienteResponsavel)
    {
        this.clienteResponsavel = clienteResponsavel;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDescricao()
    {
        return descricao;
    }

    public void setDescricao(String descricao)
    {
        this.descricao = descricao;
    }

    public LocalDateTime getCriadoEm()
    {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm)
    {
        this.criadoEm = criadoEm;
    }
}