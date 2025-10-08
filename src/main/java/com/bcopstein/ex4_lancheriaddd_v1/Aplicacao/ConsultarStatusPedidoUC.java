package com.bcopstein.ex4_lancheriaddd_v1.Aplicacao;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.PedidoStatusResponseDto;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConsultarStatusPedidoUC {
    private final PedidosRepository pedidosRepository;

    public ConsultarStatusPedidoUC(PedidosRepository pedidosRepository) {
        this.pedidosRepository = pedidosRepository;
    }

    public PedidoStatusResponseDto run(long idPedido) {
        Optional<Pedido> pedidoOpt = pedidosRepository.findByCodigo(idPedido);

        if (pedidoOpt.isEmpty()) {
            throw new RuntimeException("Pedido não encontrado com ID: " + idPedido);
        }

        Pedido pedido = pedidoOpt.get();
        return new PedidoStatusResponseDto(
            pedido.getId(),
            pedido.getCliente().getEmail(),
            pedido.getStatus().toString(),
            pedido.getValorCobrado()
        );
    }
}
