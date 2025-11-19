package com.bcopstein.ex4_lancheriaddd_v1.Aplicacao;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.PedidoResponseDto;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerStatus {
    private final PedidosRepository pedidosRepository;

    public VerStatus(PedidosRepository pedidosRepository) {
        this.pedidosRepository = pedidosRepository;
    }

    public PedidoResponseDto run(long idPedido) {
        Optional<Pedido> pedidoOpt = pedidosRepository.findByCodigo(idPedido);

        if (pedidoOpt.isEmpty()) {
            throw new RuntimeException("Pedido não encontrado" + idPedido);
        }

        Pedido pedido = pedidoOpt.get();
        return new PedidoResponseDto(
            pedido.getId(),
            pedido.getCliente().getEmail(),
            pedido.getStatus().toString(),
            pedido.getValorCobrado(),
    "Pedido encontrado",
                null
        );
    }
}
