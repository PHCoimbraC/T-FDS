package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Dados;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.ProdutosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemPedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Produto;

@Repository
public class PedidosRepositoryJDBC implements PedidosRepository {

    private final JdbcTemplate jdbc;
    private final ProdutosRepository produtosRepository;

    public PedidosRepositoryJDBC(JdbcTemplate jdbc, ProdutosRepository produtosRepository) {
        this.jdbc = jdbc;
        this.produtosRepository = produtosRepository;
    }

    @Override
    public Optional<Pedido> findByCodigo(long codigo) {
        String sql = "SELECT * FROM pedidos WHERE id = ?";
        List<Pedido> lst = jdbc.query(sql,
                ps -> ps.setLong(1, codigo),
                (rs, rowNum) -> mapPedido(rs));
        if (lst.isEmpty()) return Optional.empty();
        Pedido p = lst.get(0);
        p.getItens().addAll(carregarItens(p.getId()));
        return Optional.of(p);
    }

    @Override
    public List<Pedido> findByClienteEmailAndTempo(String email, LocalDateTime inicio, LocalDateTime fim) {
        String sql = """
            SELECT * FROM pedidos 
            WHERE email_cliente = ? 
              AND criado_em >= ? 
              AND criado_em <= ?
            """;
        return jdbc.query(sql, ps -> {
            ps.setString(1, email);
            ps.setObject(2, inicio);
            ps.setObject(3, fim);
        }, (rs, rowNum) -> mapPedido(rs));
    }

    @Override
    public void salvar(Pedido pedido, LocalDateTime criadoEm) {
        String sqlPed = """
            INSERT INTO pedidos 
            (id, email_cliente, endereco_entrega, status, data_pagamento, 
             valor_itens, desconto, impostos, valor_total, criado_em)
            VALUES (?,?,?,?,?,?,?,?,?,?)
            """;

        jdbc.update(sqlPed, ps -> {
            ps.setLong(1, pedido.getId());
            ps.setString(2, pedido.getCliente().getEmail());
            ps.setString(3, pedido.getCliente().getEndereco());
            ps.setString(4, pedido.getStatus().name());
            ps.setObject(5, pedido.getDataHoraPagamento());
            ps.setDouble(6, pedido.getValor());
            ps.setDouble(7, pedido.getDesconto());
            ps.setDouble(8, pedido.getImpostos());
            ps.setDouble(9, pedido.getValorCobrado());
            ps.setObject(10, criadoEm);
        });

        String sqlItem = "INSERT INTO pedido_itens (pedido_id, produto_id, quantidade) VALUES (?,?,?)";

        for (ItemPedido it : pedido.getItens()) {
            jdbc.update(sqlItem, ps -> {
                ps.setLong(1, pedido.getId());
                ps.setLong(2, it.getItem().getId());
                ps.setInt(3, it.getQuantidade());
            });
        }
    }

    @Override
    public void atualizarPagamento(long id, LocalDateTime dataPagamento) {
        String sql = "UPDATE pedidos SET data_pagamento = ?, status = ? WHERE id = ?";
        jdbc.update(sql, ps -> {
            ps.setObject(1, dataPagamento);
            ps.setString(2, Pedido.Status.PAGO.name());
            ps.setLong(3, id);
        });
    }

    @Override
    public void atualizarStatus(long id, Pedido.Status status) {
        String sql = "UPDATE pedidos SET status = ? WHERE id = ?";
        jdbc.update(sql, ps -> {
            ps.setString(1, status.name());
            ps.setLong(2, id);
        });
    }

    private Pedido mapPedido(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String email = rs.getString("email_cliente");
        String endereco = rs.getString("endereco_entrega");
        String status = rs.getString("status");
        LocalDateTime dataPag = rs.getObject("data_pagamento", LocalDateTime.class);
        double valor = rs.getDouble("valor_itens");
        double desconto = rs.getDouble("desconto");
        double impostos = rs.getDouble("impostos");
        double total = rs.getDouble("valor_total");

        Cliente cli = new Cliente(null, null, null, endereco, email);

        return new Pedido(
            id, cli, dataPag,
            new java.util.ArrayList<>(),
            Pedido.Status.valueOf(status),
            valor, impostos, desconto, total, null
        );
    }

    private List<ItemPedido> carregarItens(long pedidoId) {
        String sql = "SELECT produto_id, quantidade FROM pedido_itens WHERE pedido_id = ?";
        return jdbc.query(sql, ps -> ps.setLong(1, pedidoId), (rs, rowNum) -> {
            long prodId = rs.getLong("produto_id");
            int qt = rs.getInt("quantidade");
            Produto p = produtosRepository.recuperaProdutoPorid(prodId);
            return new ItemPedido(p, qt);
        });
    }

    @Override
    public List<Pedido> findByStatusAndTempo(String status, LocalDateTime inicio, LocalDateTime fim) {
        String sql = """
            SELECT * FROM pedidos 
            WHERE status = ? 
              AND data_pagamento BETWEEN ? AND ?
            ORDER BY data_pagamento
            """;
        return jdbc.query(sql, ps -> {
            ps.setString(1, status);
            ps.setObject(2, inicio);
            ps.setObject(3, fim);
        }, (rs, rowNum) -> {
            Pedido pedido = mapPedido(rs);
            pedido.getItens().addAll(carregarItens(pedido.getId()));
            return pedido;
        });
    }
}
