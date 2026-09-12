package com.backendestagio.Obras.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

// Migração pontual (rodada uma única vez, na primeira subida do app após esta
// mudança): a antiga coluna de texto "cliente_responsavel" em obras virou uma
// relação de verdade com usuarios (cliente_id). Aqui a gente casa cada obra
// pelo nome completo do cliente e depois remove a coluna antiga. Se a coluna
// já não existir (banco novo, ou migração já rodada), não faz nada.
@Component
public class ClienteResponsavelMigration implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public ClienteResponsavelMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        Integer colunaAntigaExiste = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns " +
                        "WHERE table_name = 'obras' AND column_name = 'cliente_responsavel'",
                Integer.class
        );
        if (colunaAntigaExiste == null || colunaAntigaExiste == 0) {
            return;
        }

        jdbcTemplate.update(
                "UPDATE obras o SET cliente_id = u.id " +
                        "FROM usuarios u " +
                        "WHERE o.cliente_id IS NULL " +
                        "AND o.cliente_responsavel IS NOT NULL " +
                        "AND CONCAT(u.nome, ' ', u.sobrenome) = o.cliente_responsavel"
        );

        jdbcTemplate.execute("ALTER TABLE obras DROP COLUMN cliente_responsavel");

        System.out.println("Migração: coluna cliente_responsavel migrada para cliente_id e removida.");
    }
}
