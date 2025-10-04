package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao;

import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.PedidoUC;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.PedidoRequestDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoUC PedidoUC;

    public PedidoController(PedidoUC PedidoUC) {
        this.PedidoUC = PedidoUC;
    }

    @PostMapping("/fazerPedido")
    @CrossOrigin("*")
    public ResponseEntity<Pedido> fazerPedido(@RequestBody PedidoRequestDto pedidoDTO){
        Pedido aprovadoOuNegado = PedidoUC.run(pedidoDTO);
        if (aprovadoOuNegado.getStatus() == Pedido.Status.APROVADO){
            return ResponseEntity.ok(aprovadoOuNegado);
        }
        return ResponseEntity.badRequest().body(aprovadoOuNegado);
    }
}