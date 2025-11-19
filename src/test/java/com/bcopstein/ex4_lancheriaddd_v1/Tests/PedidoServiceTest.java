package com.bcopstein.ex4_lancheriaddd_v1.Tests;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.ProdutosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.FaltaEstoqueDto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.ItemPedidoDto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Produto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Receita;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class PedidoServiceTest {

    private ProdutosRepository produtosRepository;
    private ImpostoService impostoService;
    private DescontoService descontoService;
    private EstoqueService estoqueService;
    private CozinhaService cozinhaService;
    private PedidosRepository pedidosRepository;

    private PedidoService pedidoService;

    @BeforeEach
    void setup() {
        produtosRepository = mock(ProdutosRepository.class);
        impostoService = new ImpostoService();
        descontoService = mock(DescontoService.class);
        estoqueService = mock(EstoqueService.class);
        cozinhaService = mock(CozinhaService.class);
        pedidosRepository = mock(PedidosRepository.class);

        // sem desconto
        when(descontoService.calcularDesconto(any(), any())).thenReturn(0.0);

        pedidoService = new PedidoService(
                produtosRepository,
                impostoService,
                descontoService,
                estoqueService,
                cozinhaService,
                pedidosRepository
        );
    }

    @Test
    void pedidoAprovado() {
        Cliente cli = new Cliente("1", "C", "9", "R", "cli@ex.com");

        ItemPedidoDto itemDto = new ItemPedidoDto();
        itemDto.setProdutoId(1L);
        itemDto.setQuantidade(1);

        Receita r = new Receita(1L, "R", List.of());
        Produto p = new Produto(1L, "Pizza", r, 5000); // 50,00

        when(produtosRepository.recuperaProdutoPorid(1L)).thenReturn(p);
        when(estoqueService.verificarDisponibilidade(any())).thenReturn(List.of());

        Pedido pedido = pedidoService.submeterPedido(cli, List.of(itemDto));

        assertEquals(Pedido.Status.APROVADO, pedido.getStatus());
        assertEquals(5000.0, pedido.getValor(), 0.01);      // subtotal
        assertEquals(0.0, pedido.getDesconto(), 0.01);      // sem desconto
        assertEquals(500.0, pedido.getImpostos(), 0.01);    // 10% de 5000
        assertEquals(5500.0, pedido.getValorCobrado(), 0.01);

        verify(pedidosRepository, times(1)).salvar(any(), any());
    }

    @Test
    void produtoInexistenteRetornaPedidoComIdZero() {
        Cliente cli = new Cliente("1", "C", "9", "R", "cli@ex.com");

        ItemPedidoDto itemDto = new ItemPedidoDto();
        itemDto.setProdutoId(1L);
        itemDto.setQuantidade(1);

        when(produtosRepository.recuperaProdutoPorid(1L)).thenReturn(null);

        Pedido pedido = pedidoService.submeterPedido(cli, List.of(itemDto));

        assertEquals(0L, pedido.getId());
        assertTrue(pedido.getItens().isEmpty());
        verify(pedidosRepository, never()).salvar(any(), any());
    }

    @Test
    void estoqueIndisponivel() {
        Cliente cli = new Cliente("1", "C", "9", "R", "cli@ex.com");

        ItemPedidoDto itemDto = new ItemPedidoDto();
        itemDto.setProdutoId(1L);
        itemDto.setQuantidade(1);

        Receita r = new Receita(1L, "R", List.of());
        Produto p = new Produto(1L, "Pizza", r, 5000);

        when(produtosRepository.recuperaProdutoPorid(1L)).thenReturn(p);
        when(estoqueService.verificarDisponibilidade(any()))
                .thenReturn(List.of(new FaltaEstoqueDto("Falta", 1L)));

        Pedido pedido = pedidoService.submeterPedido(cli, List.of(itemDto));

        assertEquals(Pedido.Status.NEGADO, pedido.getStatus());
        assertFalse(pedido.getItens().isEmpty());
        verify(pedidosRepository, never()).salvar(any(), any());
    }

    @Test
    void cancelarPedidoInexistente() {
        when(pedidosRepository.findByCodigo(1L)).thenReturn(Optional.empty());
        assertNull(pedidoService.cancelarPedido(1L));
    }

    @Test
    void cancelarPedidoNaoAprovadoSemMudarStatus() {
        Cliente cli = new Cliente("1", "C", "9", "R", "cli@ex.com");
        Pedido ped = new Pedido(1L, cli, null, List.of(), Pedido.Status.PAGO, 0, 0, 0, 0, null);

        when(pedidosRepository.findByCodigo(1L)).thenReturn(Optional.of(ped));

        Pedido resultado = pedidoService.cancelarPedido(1L);

        assertEquals(Pedido.Status.PAGO, resultado.getStatus());
        verify(pedidosRepository, never()).atualizarStatus(anyLong(), any());
    }

    @Test
    void cancelarPedidoAprovado() {
        Cliente cli = new Cliente("1", "C", "9", "R", "cli@ex.com");
        Pedido ped = new Pedido(1L, cli, null, List.of(), Pedido.Status.APROVADO, 0, 0, 0, 0, null);

        when(pedidosRepository.findByCodigo(1L)).thenReturn(Optional.of(ped));

        Pedido resultado = pedidoService.cancelarPedido(1L);

        assertEquals(Pedido.Status.CANCELADO, resultado.getStatus());
        verify(pedidosRepository, times(1))
                .atualizarStatus(1L, Pedido.Status.CANCELADO);
    }

    @Test
    void pagarPedidoInexistenteRetornaNull() {
        when(pedidosRepository.findByCodigo(1L)).thenReturn(Optional.empty());
        assertNull(pedidoService.pagarPedido(1L));
    }

    @Test
    void pagarPedidoNaoAprovadoNaoMudaStatus() {
        Cliente cli = new Cliente("1", "C", "9", "R", "cli@ex.com");
        Pedido ped = new Pedido(1L, cli, null, List.of(), Pedido.Status.NOVO, 0, 0, 0, 0, null);

        when(pedidosRepository.findByCodigo(1L)).thenReturn(Optional.of(ped));

        Pedido resultado = pedidoService.pagarPedido(1L);

        assertEquals(Pedido.Status.NOVO, resultado.getStatus());
        verify(pedidosRepository, never()).atualizarPagamento(anyLong(), any());
    }

    @Test
    void pagarPedidoAprovadoMudaParaPago() {
        Cliente cli = new Cliente("1", "C", "9", "R", "cli@ex.com");
        Pedido ped = new Pedido(1L, cli, null, List.of(), Pedido.Status.APROVADO, 0, 0, 0, 0, null);

        when(pedidosRepository.findByCodigo(1L)).thenReturn(Optional.of(ped));

        Pedido resultado = pedidoService.pagarPedido(1L);

        assertEquals(Pedido.Status.PAGO, resultado.getStatus());
        verify(pedidosRepository, times(1))
                .atualizarPagamento(eq(1L), any());
    }
}
