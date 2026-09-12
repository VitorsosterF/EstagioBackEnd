-- Script para popular o banco de dados com dados de teste
-- Execute uma única vez após criar o banco: psql -h localhost -U postgres -d construgestor -f data.sql

-- Usuários (mix Admin/Cliente)
-- Senhas encriptadas com BCrypt (senha: 123456)
INSERT INTO usuarios (nome, sobrenome, email, senha, perfil) VALUES
('Carlos', 'Silva', 'carlos@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/1Pq', 'Admin'),
('Maria', 'Santos', 'maria@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/1Pq', 'Cliente'),
('João', 'Oliveira', 'joao@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/1Pq', 'Cliente'),
('Ana', 'Ferreira', 'ana@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/1Pq', 'Cliente'),
('Pedro', 'Costa', 'pedro@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/1Pq', 'Admin');

-- Templates (mix Email/WhatsApp, alguns como padrão de status)
INSERT INTO templates (titulo, tipo, corpo, variaveis, criado_em, padrao_notificacao_status) VALUES
('Notificação de Status', 'Email', 'Olá {nome_cliente}, sua obra {obra} está {status}. Localizada em {endereco}.', 'nome_cliente, obra, status, endereco', NOW(), true),
('Atualização WhatsApp', 'WhatsApp', 'Oi! Sua obra foi atualizada! Status: {status}', 'status', NOW(), false),
('Relatório Semanas', 'Email', 'Relatório: Obra {obra} está em {status}. Cliente: {nome_cliente}', 'obra, status, nome_cliente', NOW(), false),
('Conclusão Projeto', 'WhatsApp', 'Parabéns! {obra} foi concluída! 🎉', 'obra', NOW(), false),
('Aviso Importante', 'Email', '{nome_cliente}, você tem uma mensagem sobre a obra {obra}. Status atual: {status}', 'nome_cliente, obra, status', NOW(), false);

-- Obras (5 unidades, vinculadas aos clientes)
INSERT INTO obras (nome, rua, numero, complemento, cliente_id, status, descricao, criado_em) VALUES
('Reforma Residencial', 'Rua A', '123', 'Apto 101', 2, 'Em andamento', 'Reforma completa do apartamento', NOW()),
('Construção Comercial', 'Avenida B', '456', 'Loja 01', 3, 'Não iniciada', 'Construção de loja no bairro central', NOW()),
('Ampliação Casa', 'Rua C', '789', null, 4, 'Pausada', 'Ampliação de dois quartos', NOW()),
('Piscina Residencial', 'Avenida D', '321', 'Condomínio X', 2, 'Concluída', 'Construção de piscina com paisagismo', NOW()),
('Obra Comercial', 'Rua E', '654', 'Sala 5', 3, 'Em andamento', 'Construção de escritório', NOW());

-- Notificações de exemplo (algumas já lidas, algumas não)
INSERT INTO notificacoes (mensagem, data_criacao, lida, obra_id, template_id) VALUES
('Olá Maria Santos, sua obra Reforma Residencial está Em andamento. Localizada em Rua A, 123 - Apto 101.', NOW() - INTERVAL '2 days', true, 1, 1),
('Olá João Oliveira, sua obra Construção Comercial está Não iniciada. Localizada em Avenida B, 456 - Loja 01.', NOW() - INTERVAL '1 day', false, 2, 1),
('Olá Ana Ferreira, sua obra Ampliação Casa está Pausada. Localizada em Rua C, 789.', NOW() - INTERVAL '12 hours', false, 3, 1),
('Olá Maria Santos, sua obra Piscina Residencial está Concluída. Localizada em Avenida D, 321 - Condomínio X.', NOW() - INTERVAL '6 hours', false, 4, 1),
('Olá João Oliveira, sua obra Obra Comercial está Em andamento. Localizada em Rua E, 654 - Sala 5.', NOW(), false, 5, 1);
