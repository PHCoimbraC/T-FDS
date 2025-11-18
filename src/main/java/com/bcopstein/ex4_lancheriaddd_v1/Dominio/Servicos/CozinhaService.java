package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;

@Service
public class CozinhaService {

    private final PedidosRepository pedidosRepository;
    private Queue<Pedido> filaEntrada;
    private Pedido emPreparacao;
    private Queue<Pedido> filaSaida;

    private ScheduledExecutorService scheduler;       // preparo dos pedidos
    private ScheduledExecutorService entregaScheduler; // entrega dos pedidos

    public CozinhaService(PedidosRepository pedidosRepository) {
        this.pedidosRepository = pedidosRepository;

        filaEntrada = new LinkedBlockingQueue<>();
        emPreparacao = null;
        filaSaida = new LinkedBlockingQueue<>();
        scheduler = Executors.newSingleThreadScheduledExecutor();
        entregaScheduler = Executors.newSingleThreadScheduledExecutor();
        entregaSimulada();
    }

    // Coloca um pedido em preparo
    private synchronized void colocaEmPreparacao(Pedido pedido){
        if (pedido == null) return;
        pedido.setStatus(Pedido.Status.PREPARACAO);
        emPreparacao = pedido;
        System.out.println("Pedido em preparação: " + pedido.getId());
        scheduler.schedule(this::pedidoPronto, 5, TimeUnit.SECONDS); // preparo simulado
    }

    // Chegada de pedido pago
    public synchronized void chegadaDePedido(Pedido p) {
        if (p == null) return;
        p.setStatus(Pedido.Status.AGUARDANDO);
        filaEntrada.add(p);
        System.out.println("Pedido na fila de entrada: " + p.getId());
        if (emPreparacao == null && !filaEntrada.isEmpty()) {
            colocaEmPreparacao(filaEntrada.poll());
        }
    }

    // Pedido finalizado na cozinha
    public synchronized void pedidoPronto() {
        if (emPreparacao == null) return;
        emPreparacao.setStatus(Pedido.Status.PRONTO);
        filaSaida.add(emPreparacao);
        System.out.println("Pedido pronto e na fila de saída: " + emPreparacao.getId());
        emPreparacao = null;
        if (!filaEntrada.isEmpty()){
            Pedido prox = filaEntrada.poll();
            scheduler.schedule(() -> colocaEmPreparacao(prox), 1, TimeUnit.SECONDS);
        }
    }

    private synchronized void entregaSimulada() {
    Pedido p = filaSaida.poll();
    if (p != null) {
        System.out.println("Entregador saiu para pedido: " + p.getId());
        p.setStatus(Pedido.Status.TRANSPORTE);
        pedidosRepository.atualizarStatus(p.getId(), Pedido.Status.TRANSPORTE);

        entregaScheduler.schedule(() -> finalizarEntrega(p), 3, TimeUnit.SECONDS);
    } else {
        entregaScheduler.schedule(() -> entregaSimulada(), 2, TimeUnit.SECONDS);
    }
    }

    private synchronized void finalizarEntrega(Pedido p) {
    p.setStatus(Pedido.Status.ENTREGUE);
    pedidosRepository.atualizarStatus(p.getId(), Pedido.Status.ENTREGUE);
    System.out.println("Pedido entregue: " + p.getId());

    entregaScheduler.schedule(() -> entregaSimulada(), 2, TimeUnit.SECONDS);
    }

}
