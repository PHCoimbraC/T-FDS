package com.bcopstein.ex4_lancheriaddd_v1.Aplicacao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;

@Component
public class ListarPedidosEntreguesEntreDatasUC {
    private final PedidosRepository pedidosRepository;

    public ListarPedidosEntreguesEntreDatasUC(PedidosRepository pedidosRepository) {
        this.pedidosRepository = pedidosRepository;
    }

    public List<Pedido> run(LocalDateTime inicio, LocalDateTime fim) {
        return pedidosRepository.findByStatusAndPeriodo("ENTREGUE", inicio, fim);
    }
}
