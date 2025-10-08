package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.ItemPedidoDto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.ProdutosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemPedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Produto;

@Service
public class PedidoService {
    private long  x = 0;
    private final ProdutosRepository produtosRepository;
    private final ImpostoService impostosService;
    private final DescontoService descontosService;
    private final EstoqueService estoqueService;

    // Armazenamento em memória dos pedidos
    private final Map<Long, Pedido> pedidos = new HashMap<>();

    public PedidoService(ProdutosRepository produtosRepository, ImpostoService impostosService,
                         DescontoService descontosService, EstoqueService estoqueService) {
        this.produtosRepository = produtosRepository;
        this.impostosService = impostosService;
        this.descontosService = descontosService;
        this.estoqueService = estoqueService;
    }

    public Pedido submeterPedido(Cliente cliente, List<ItemPedidoDto> listaItens, LocalDateTime agora){
        List<ItemPedido> itens = new ArrayList<>();
        for (ItemPedidoDto i: listaItens){
            Produto p = produtosRepository.recuperaProdutoPorid(i.getProdutoId());
            if (p == null) {
                return new Pedido(0, cliente, null, List.of(), Pedido.Status.NOVO, 0, 0, 0, 0);
            }
            itens.add(new ItemPedido(p, i.getQuantidade()));
        }

        boolean estoqueOk = estoqueService.verificarDisponibilidade(itens);
        if (!estoqueOk){
            return new Pedido(0, cliente, null, itens, Pedido.Status.NOVO, 0, 0, 0, 0);
        }

        double subtotal = itens.stream().mapToDouble(ip -> ip.getItem().getPreco() * ip.getQuantidade()).sum();
        double desconto = descontosService.calcularDesconto(cliente, itens, agora.toLocalDate());
        double impostos = impostosService.calcularImpostos(subtotal - desconto);
        double total = subtotal - desconto + impostos;

        // Gerar ID único
        x += 1;
        Pedido pedido = new Pedido(x, cliente, null, itens, Pedido.Status.APROVADO, subtotal, impostos, desconto, total);

        // Armazenar pedido
        pedidos.put(x, pedido);

        return pedido;
    }

    public Pedido buscarPedidoPorId(Long id) {
        return pedidos.get(id);
    }
    
    public Pedido cancelarPedido(Long id) {
    Pedido pedido = pedidos.get(id);
    if (pedido == null) {
        return null;
    }

    if (pedido.getStatus() != Pedido.Status.APROVADO) {
        return pedido; 
    }

    pedido.setStatus(Pedido.Status.CANCELADO);
    return pedido;
}

}
