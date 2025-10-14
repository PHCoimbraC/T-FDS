package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.PedidoUC;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.PedidoRequestDto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.CozinhaService;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.PedidoService;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidoUC PedidoUC;
    private final PedidoService PedidoService;
    private final CozinhaService cozinhaService;

    public PedidoController(PedidoUC PedidoUC, PedidoService PedidoService, CozinhaService cozinhaService) {
        this.PedidoUC = PedidoUC;
        this.PedidoService = PedidoService;
        this.cozinhaService = cozinhaService;
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

    @DeleteMapping("/{id}/cancelar")
    @CrossOrigin("*")
    public ResponseEntity<Pedido> cancelarPedido(@PathVariable Long id) {
    Pedido pedidoCancelado = PedidoService.cancelarPedido(id);
        if (pedidoCancelado == null) {
            return ResponseEntity.notFound().build();
        }

        if (pedidoCancelado.getStatus() != Pedido.Status.NOVO) {
            return ResponseEntity.badRequest().body(pedidoCancelado);
        }

        return ResponseEntity.ok(pedidoCancelado);
    }

    @PostMapping("/{id}/pagar")
    @CrossOrigin("*")
    public ResponseEntity<String> pagar(@PathVariable Long id) {
        var pedido = PedidoService.pagarPedido(id);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }
        if (pedido.getStatus() != Pedido.Status.PAGO) {
            return ResponseEntity.badRequest().body("Pedido não aprovado");
        }
        cozinhaService.chegadaDePedido(pedido);
        return ResponseEntity.ok("pago");
    }
}