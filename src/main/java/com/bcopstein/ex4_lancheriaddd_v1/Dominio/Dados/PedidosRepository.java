package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;

public interface PedidosRepository {
    Optional<Pedido> findByCodigo(long codigo);
    List<Pedido> findByClienteEmailAndPeriodo(String email, LocalDateTime inicio, LocalDateTime fim);
    void salvar(Pedido pedido, LocalDateTime criadoEm);
    void atualizarPagamento(long id, LocalDateTime dataPagamento);
}
