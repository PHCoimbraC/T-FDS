package com.bcopstein.ex4_lancheriaddd_v1.Aplicacao;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.PedidoRequestDto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemPedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.PedidoService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PedidoUC {
    private final PedidoService pedidoService;

    public PedidoUC(PedidoService pedidosService){
        this.pedidoService = pedidosService;
    }

    public Pedido run(PedidoRequestDto dto){
        Cliente cli = new Cliente("","",  "", dto.getEnderecoEntrega(), dto.getEmailCliente());
        return pedidoService.submeterPedido(cli, dto.getItens(), LocalDateTime.now());
    }

    public Pedido getPedidoById(Long id) {
    return pedidoService.buscarPedidoPorId(id);
    }
    
    public Pedido cancelarPedido(Long id) {
    return pedidoService.cancelarPedido(id);
    }

}