package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.ConfiguracaoRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemPedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;

@Service
public class DescontoService {
    private static final String CHAVE_DESCONTO_ATIVO = "desconto_ativo_codigo";
    private static final String COD_CLIENTE_FREQUENTE = "ClienteFrequente";
    private static final String COD_CLIENTE_GASTADOR = "ClienteGastador";

    private final PedidosRepository pedidosRepository;
    private final ConfiguracaoRepository configuracaoRepository;

    public DescontoService(PedidosRepository pedidosRepository,
                           ConfiguracaoRepository configuracaoRepository) {
        this.pedidosRepository = pedidosRepository;
        this.configuracaoRepository = configuracaoRepository;
    }

    public String getDescontoAtivoCodigo() {
        // Valor padrão: ClienteFrequente
        return configuracaoRepository.getValor(CHAVE_DESCONTO_ATIVO)
                .orElse(COD_CLIENTE_FREQUENTE);
    }

    public void definirDescontoAtivo(String codigo) {
        if (!COD_CLIENTE_FREQUENTE.equals(codigo) &&
            !COD_CLIENTE_GASTADOR.equals(codigo)) {
            throw new IllegalArgumentException(
                "Tipo de desconto inválido: " + codigo +
                ". Use 'ClienteFrequente' ou 'ClienteGastador'."
            );
        }
        configuracaoRepository.setValor(CHAVE_DESCONTO_ATIVO, codigo);
        System.out.println("Desconto ativo alterado para: " + codigo);
    }

    public double calcularDesconto(Cliente cliente, List<ItemPedido> itens) {
        if (cliente == null || itens == null || itens.isEmpty()) {
            return 0.0;
        }

        double subtotal = itens.stream()
                .mapToDouble(i -> i.getItem().getPreco() * i.getQuantidade())
                .sum();

        String codigoAtivo = getDescontoAtivoCodigo();

        if (COD_CLIENTE_FREQUENTE.equals(codigoAtivo)) {
            if (isClienteFrequente(cliente)) {
                // 7% de desconto nos itens
                return subtotal * 0.07;
            }
        } else if (COD_CLIENTE_GASTADOR.equals(codigoAtivo)) {
            if (isClienteGastador(cliente)) {
                // 15% de desconto no valor a pagar
                return subtotal * 0.15;
            }
        }

        return 0.0;
    }

    public boolean isClienteFrequente(Cliente cliente) {
        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = fim.minusDays(20);

        List<Pedido> pedidos = pedidosRepository
                .findByClienteEmailAndTempo(cliente.getEmail(), inicio, fim);

        return pedidos.size() > 3;
    }

    public boolean isClienteGastador(Cliente cliente) {
        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = fim.minusDays(30);

        List<Pedido> pedidos = pedidosRepository
                .findByClienteEmailAndTempo(cliente.getEmail(), inicio, fim);

        double totalGasto = pedidos.stream()
                .mapToDouble(Pedido::getValorCobrado)
                .sum();

        return totalGasto > 50000.0;
    }
}