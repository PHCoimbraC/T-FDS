package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemPedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DescontoService {
    private final PedidosRepository pedidosRepository;

    public DescontoService(PedidosRepository pedidosRepository) {
        this.pedidosRepository = pedidosRepository;
    }

    public double calcularDesconto(Cliente cliente, List<ItemPedido> itens){
        if (!temDesconto(cliente)) return 0.0;
        double subtotal = itens.stream().mapToDouble(i -> i.getItem().getPreco() * i.getQuantidade()).sum();
        return subtotal * 0.07;
    }

    protected boolean temDesconto(Cliente cliente){
        String email = cliente.getEmail();
        if (email == null || email.isBlank()) return false;
        LocalDateTime inicio = LocalDateTime.now().toLocalDate().minusDays(20).atStartOfDay();
        LocalDateTime fim = LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay();
        List<Pedido> pedidos = pedidosRepository.findByClienteEmailAndPeriodo(email, inicio, fim);
        return pedidos != null && pedidos.size() > 3;
    }
}
