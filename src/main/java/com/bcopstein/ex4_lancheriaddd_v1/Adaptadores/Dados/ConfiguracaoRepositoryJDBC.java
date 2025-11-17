package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Dados;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.ConfiguracaoRepository;

@Repository
public class ConfiguracaoRepositoryJDBC implements ConfiguracaoRepository {
    private final JdbcTemplate jdbcTemplate;

    public ConfiguracaoRepositoryJDBC(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<String> getValor(String chave) {
        String sql = "SELECT valor FROM configuracoes WHERE chave = ?";
        List<String> resultado = jdbcTemplate.query(
            sql,
            ps -> ps.setString(1, chave),
            (rs, rowNum) -> rs.getString("valor")
        );
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.get(0));
    }

    @Override
    public void setValor(String chave, String valor) {
        String sql = "MERGE INTO configuracoes (chave, valor) KEY(chave) VALUES (?, ?)";
        jdbcTemplate.update(sql, chave, valor);
    }
}