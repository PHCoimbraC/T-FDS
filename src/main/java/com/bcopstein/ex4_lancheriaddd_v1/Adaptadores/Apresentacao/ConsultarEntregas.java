package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.ListarEntregas;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;

@RestController
public class ConsultarEntregas {
    private final ListarEntregas listarPedidosUC;

    public ConsultarEntregas(ListarEntregas listarPedidosUC) {
        this.listarPedidosUC = listarPedidosUC;
    }

    @GetMapping("/pedidos/entregues")
    public List<Pedido> listarPedidosEntregues(
        @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
        @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        return listarPedidosUC.run(inicio, fim);
    }
}
