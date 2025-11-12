package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Dados;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.UsuarioRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Usuario;

@Repository
public class UsuarioRepositoryJDBC implements UsuarioRepository {
    private final JdbcTemplate jdbcTemplate;

    public UsuarioRepositoryJDBC(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        List<Usuario> usuarios = jdbcTemplate.query(
            sql,
            ps -> ps.setString(1, email),
            this::mapRowToUsuario
        );
        return usuarios.isEmpty() ? Optional.empty() : Optional.of(usuarios.get(0));
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        List<Usuario> usuarios = jdbcTemplate.query(
            sql,
            ps -> ps.setLong(1, id),
            this::mapRowToUsuario
        );
        return usuarios.isEmpty() ? Optional.empty() : Optional.of(usuarios.get(0));
    }

    @Override
    public Optional<Usuario> findByCpf(String cpf) {
        String sql = "SELECT * FROM usuarios WHERE cpf = ?";
        List<Usuario> usuarios = jdbcTemplate.query(
            sql,
            ps -> ps.setString(1, cpf),
            this::mapRowToUsuario
        );
        return usuarios.isEmpty() ? Optional.empty() : Optional.of(usuarios.get(0));
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        String sql = """
            INSERT INTO usuarios (nome, cpf, celular, endereco, email, senha, tipo, data_cadastro, ativo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getCpf());
            ps.setString(3, usuario.getCelular());
            ps.setString(4, usuario.getEndereco());
            ps.setString(5, usuario.getEmail());
            ps.setString(6, usuario.getSenha());
            ps.setString(7, usuario.getTipo().name());
            ps.setObject(8, usuario.getDataCadastro());
            ps.setBoolean(9, usuario.isAtivo());
            return ps;
        }, keyHolder);
        
        Long id = keyHolder.getKey().longValue();
        return findById(id).orElse(usuario);
    }

    @Override
    public void atualizar(Usuario usuario) {
        String sql = """
            UPDATE usuarios 
            SET nome = ?, cpf = ?, celular = ?, endereco = ?, senha = ?, ativo = ?
            WHERE id = ?
        """;
        
        jdbcTemplate.update(sql,
            usuario.getNome(),
            usuario.getCpf(),
            usuario.getCelular(),
            usuario.getEndereco(),
            usuario.getSenha(),
            usuario.isAtivo(),
            usuario.getId()
        );
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByCpf(String cpf) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE cpf = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, cpf);
        return count != null && count > 0;
    }

    private Usuario mapRowToUsuario(ResultSet rs, int rowNum) throws SQLException {
        return new Usuario(
            rs.getLong("id"),
            rs.getString("nome"),
            rs.getString("cpf"),
            rs.getString("celular"),
            rs.getString("endereco"),
            rs.getString("email"),
            rs.getString("senha"),
            Usuario.TipoUsuario.valueOf(rs.getString("tipo")),
            rs.getObject("data_cadastro", LocalDateTime.class),
            rs.getBoolean("ativo")
        );
    }
}