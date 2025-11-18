package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.PedidoResponseDto;
import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.ProdutosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.ItemPedidoDto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemPedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Produto;

@Service
public class PedidoService {
    private long x = 0;

    private final ProdutosRepository produtosRepository;
    private final ImpostoService impostosService;
    private final DescontoService descontosService;
    private final EstoqueService estoqueService;
    private final CozinhaService cozinhaService;
    private final PedidosRepository pedidosRepository;

    public PedidoService(
            ProdutosRepository produtosRepository,
            ImpostoService impostosService,
            DescontoService descontosService,
            EstoqueService estoqueService,
            CozinhaService cozinhaService,
            PedidosRepository pedidosRepository) {
        this.produtosRepository = produtosRepository;
        this.impostosService = impostosService;
        this.descontosService = descontosService;
        this.estoqueService = estoqueService;
        this.cozinhaService = cozinhaService;
        this.pedidosRepository = pedidosRepository;
    }

    public Pedido submeterPedido(Cliente cliente, List<ItemPedidoDto> listaItens) {
    List<ItemPedido> itens = new ArrayList<>();
    for (ItemPedidoDto i : listaItens) {
        Produto p = produtosRepository.recuperaProdutoPorid(i.getProdutoId());
        if (p == null) {
            System.out.println("Produto não encontrado: " + i.getProdutoId());
            return new Pedido(0, cliente, null, List.of(), Pedido.Status.NEGADO, 0, 0, 0, 0);
        }
        itens.add(new ItemPedido(p, i.getQuantidade()));
    }

    // Verifica disponibilidade no estoque
    boolean estoqueOk = estoqueService.verificarDisponibilidade(itens);
    if (!estoqueOk) {
        return new Pedido(0, cliente, null, itens, Pedido.Status.NEGADO, 0, 0, 0, 0);
    }

    // Dar salvar estoque
    estoqueService.SalvarEstoque(itens);

    double subtotal = itens.stream().mapToDouble(ip -> ip.getItem().getPreco() * ip.getQuantidade()).sum();
    double desconto = descontosService.calcularDesconto(cliente, itens);
    double impostos = impostosService.calcularImpostos(subtotal - desconto);
    double total = subtotal - desconto + impostos;

    x = x + 1;
    Pedido pedido = new Pedido(x, cliente, null, itens, Pedido.Status.APROVADO, subtotal, impostos, desconto, total);

    pedidosRepository.salvar(pedido, LocalDateTime.now());

    return pedido;
}

    public Pedido cancelarPedido(Long id) {
        Optional<Pedido> pedidoOptional = pedidosRepository.findByCodigo(id);
        if (pedidoOptional.isEmpty()) {
            return null;
        }
        Pedido pedido = pedidoOptional.get();

        if (pedido.getStatus() != Pedido.Status.APROVADO) {
            return pedido;
        }

        pedido.setStatus(Pedido.Status.CANCELADO);
        pedidosRepository.atualizarStatus(id, Pedido.Status.CANCELADO);

        return pedido;
    }

    public Pedido pagarPedido(Long id) {
        Optional<Pedido> pedidoOptional = pedidosRepository.findByCodigo(id);
        if (pedidoOptional.isEmpty()) {
            return null;
        }
        Pedido pedido = pedidoOptional.get();

        if (pedido.getStatus() != Pedido.Status.APROVADO) {
            return pedido;
        }
        pedido.setStatus(Pedido.Status.PAGO);
        pedido.setDataHoraPagamento( LocalDateTime.now());
        pedidosRepository.atualizarPagamento(id, LocalDateTime.now());

        return pedido;
    }
}
