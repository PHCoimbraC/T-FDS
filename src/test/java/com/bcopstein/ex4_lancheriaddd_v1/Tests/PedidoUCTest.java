package com.bcopstein.ex4_lancheriaddd_v1.Tests;

import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.PedidoUC;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.ItemPedidoDto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.PedidoRequestDto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.PedidoService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


public class PedidoUCTest {

    @Test
    void ChamarPedidoServiceComCliente() {
        PedidoService pedidoService = mock(PedidoService.class);
        PedidoUC uc = new PedidoUC(pedidoService);

        PedidoRequestDto dto = new PedidoRequestDto();
        dto.setEmailCliente("cliente@ex.com");
        dto.setEnderecoEntrega("Rua Y, 123");

        ItemPedidoDto item = new ItemPedidoDto();
        item.setProdutoId(1L);
        item.setQuantidade(2);
        dto.setItens(List.of(item));

        Pedido pedidoRetornado = new Pedido(
                10L,
                new Cliente("", "", "", dto.getEnderecoEntrega(), dto.getEmailCliente()),
                null,
                List.of(),
                Pedido.Status.APROVADO,
                0, 0, 0, 0, null
        );

        when(pedidoService.submeterPedido(any(Cliente.class), eq(dto.getItens())))
                .thenReturn(pedidoRetornado);

        Pedido resultado = uc.run(dto);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        verify(pedidoService, times(1)).submeterPedido(any(Cliente.class), eq(dto.getItens()));
    }
}