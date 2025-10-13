package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;

@Service
public class CozinhaService {
    private Queue<Pedido> filaEntrada;
    private Pedido emPreparacao;
    private Queue<Pedido> filaSaida;

    private ScheduledExecutorService scheduler;

    public CozinhaService() {
        filaEntrada = new LinkedBlockingQueue<Pedido>();
        emPreparacao = null;
        filaSaida = new LinkedBlockingQueue<Pedido>();
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    private synchronized void colocaEmPreparacao(Pedido pedido){
        pedido.setStatus(Pedido.Status.PREPARACAO);
        emPreparacao = pedido;
        System.out.println("Pedido em preparacao: "+pedido);
        scheduler.schedule(() -> pedidoPronto(), 5, TimeUnit.SECONDS);
    }

    public synchronized void chegadaDePedido(Pedido p) {
        p.setStatus(Pedido.Status.AGUARDANDO);
        filaEntrada.add(p);
        System.out.println("Pedido na fila de entrada: "+p);
        if (emPreparacao == null) {
            colocaEmPreparacao(filaEntrada.poll());
        }
    }

    public synchronized void pedidoPronto() {
        emPreparacao.setStatus(Pedido.Status.PRONTO);
        filaSaida.add(emPreparacao);
        System.out.println("Pedido na fila de saida: "+emPreparacao);
        emPreparacao = null;
        if (!filaEntrada.isEmpty()){
            Pedido prox = filaEntrada.poll();
            scheduler.schedule(() -> colocaEmPreparacao(prox), 1, TimeUnit.SECONDS);
        }
    }
}
