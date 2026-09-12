package com.backendestagio.Obras.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// DTO de entrada para o envio manual de notificação (feito pelo admin).
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificacaoManualRequest {

    private Long obraId;
    private Long templateId;

    public Long getObraId() { return obraId; }
    public void setObraId(Long obraId) { this.obraId = obraId; }

    public Long getTemplateId() { return templateId; }
    public void setTemplateId(Long templateId) { this.templateId = templateId; }
}
