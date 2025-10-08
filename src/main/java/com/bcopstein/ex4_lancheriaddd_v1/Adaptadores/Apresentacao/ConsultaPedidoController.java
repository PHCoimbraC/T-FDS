package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao;

import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.ConsultarStatusPedidoUC;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.PedidoStatusResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
public class ConsultaPedidoController {
    private final ConsultarStatusPedidoUC consultarStatusPedidoUC;

    public ConsultaPedidoController(ConsultarStatusPedidoUC consultarStatusPedidoUC) {
        this.consultarStatusPedidoUC = consultarStatusPedidoUC;
    }

    @GetMapping("/{id}")
    @CrossOrigin("*")
    public ResponseEntity<PedidoStatusResponseDto> consultarPedido(@PathVariable long id) {
        try {
            PedidoStatusResponseDto dto = consultarStatusPedidoUC.run(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
