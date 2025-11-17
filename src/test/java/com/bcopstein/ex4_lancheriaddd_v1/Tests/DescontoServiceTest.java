package com.bcopstein.ex4_lancheriaddd_v1.Tests;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.ConfiguracaoRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemPedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Produto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Receita;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.DescontoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
public class DescontoServiceTest {

    private PedidosRepository pedidosRepository;
    private ConfiguracaoRepository configuracaoRepository;
    private DescontoService descontoService;

    @BeforeEach
    void setup() {
        pedidosRepository = Mockito.mock(PedidosRepository.class);
        configuracaoRepository = Mockito.mock(ConfiguracaoRepository.class);
        descontoService = new DescontoService(pedidosRepository, configuracaoRepository);
    }

    @Test
    void clienteComMaisDeTresPedidosEhFrequente() {
        Cliente cli = new Cliente("1", "Teste", "999", "Rua X", "cli@ex.com");

        List<Pedido> pedidos = List.of(
                pedido(cli, 1000),
                pedido(cli, 2000),
                pedido(cli, 3000),
                pedido(cli, 4000)
        );

        when(pedidosRepository.findByClienteEmailAndTempo(eq(cli.getEmail()), any(), any()))
                .thenReturn(pedidos);

        assertTrue(descontoService.isClienteFrequente(cli));
    }

    @Test
    void clienteComTresPedidosNaoEhFrequente() {
        Cliente cli = new Cliente("1", "Teste", "999", "Rua X", "cli@ex.com");

        List<Pedido> pedidos = List.of(
                pedido(cli, 1000),
                pedido(cli, 2000),
                pedido(cli, 3000)
        );

        when(pedidosRepository.findByClienteEmailAndTempo(eq(cli.getEmail()), any(), any()))
                .thenReturn(pedidos);

        assertFalse(descontoService.isClienteFrequente(cli));
    }

    @Test
    void clienteGastadorComMaisDeQuinhentosReais() {
        Cliente cli = new Cliente("1", "Teste", "999", "Rua X", "cli@ex.com");

        List<Pedido> pedidos = List.of(
                pedido(cli, 30000),
                pedido(cli, 25001) // total = 55001 (> 50000)
        );

        when(pedidosRepository.findByClienteEmailAndTempo(eq(cli.getEmail()), any(), any()))
                .thenReturn(pedidos);

        assertTrue(descontoService.isClienteGastador(cli));
    }

    @Test
    void clienteComExatosQuinhentosReaisNaoEhGastador() {
        Cliente cli = new Cliente("1", "Teste", "999", "Rua X", "cli@ex.com");

        List<Pedido> pedidos = List.of(
                pedido(cli, 20000),
                pedido(cli, 30000) // total = 50000
        );

        when(pedidosRepository.findByClienteEmailAndTempo(eq(cli.getEmail()), any(), any()))
                .thenReturn(pedidos);

        assertFalse(descontoService.isClienteGastador(cli));
    }

    @Test
    void semConfiguracaoRetornaClienteFrequente() {
        when(configuracaoRepository.getValor(anyString())).thenReturn(Optional.empty());
        assertEquals("ClienteFrequente", descontoService.getDescontoAtivoCodigo());
    }

    @Test
    void definirDescontoAtivoValidoGravaNaBase() {
        descontoService.definirDescontoAtivo("ClienteGastador");
        verify(configuracaoRepository).setValor("desconto_ativo_codigo", "ClienteGastador");
    }

    @Test
    void definirDescontoInvalidoLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> descontoService.definirDescontoAtivo("OutroTipo"));
    }

    @Test
    void calcularDescontoClienteFrequenteSetePorCento() {
        Cliente cli = new Cliente("1", "Teste", "999", "Rua X", "cli@ex.com");
        List<ItemPedido> itens = List.of(itemPedido(1L, 10000, 1)); // subtotal = 10000

        when(configuracaoRepository.getValor("desconto_ativo_codigo"))
                .thenReturn(Optional.of("ClienteFrequente"));
        when(pedidosRepository.findByClienteEmailAndTempo(eq(cli.getEmail()), any(), any()))
                .thenReturn(List.of(pedido(cli, 1000), pedido(cli, 1000), pedido(cli, 1000), pedido(cli, 1000)));

        double desconto = descontoService.calcularDesconto(cli, itens);

        assertEquals(700.0, desconto, 0.01); // 7% de 10000
    }

    @Test
    void calcularDescontoClienteGastadorQuinzePorCento() {
        Cliente cli = new Cliente("1", "Teste", "999", "Rua X", "cli@ex.com");
        List<ItemPedido> itens = List.of(itemPedido(1L, 20000, 1)); // subtotal = 20000

        when(configuracaoRepository.getValor("desconto_ativo_codigo"))
                .thenReturn(Optional.of("ClienteGastador"));
        when(pedidosRepository.findByClienteEmailAndTempo(eq(cli.getEmail()), any(), any()))
                .thenReturn(List.of(pedido(cli, 30000), pedido(cli, 30001)));

        double desconto = descontoService.calcularDesconto(cli, itens);

        assertEquals(3000.0, desconto, 0.01); // 15% de 20000
    }

    // ----------------- helpers -----------------

    private Pedido pedido(Cliente cli, double valorCobrado) {
        return new Pedido(
                1L,
                cli,
                LocalDateTime.now(),
                List.of(),
                Pedido.Status.ENTREGUE,
                valorCobrado,
                0.0,
                0.0,
                valorCobrado
        );
    }

    private ItemPedido itemPedido(long idProduto, int preco, int qtd) {
        Receita r = new Receita(1L, "Teste", List.of());
        Produto p = new Produto(idProduto, "Prod", r, preco);
        return new ItemPedido(p, qtd);
    }
}