package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.VerStatus;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.PedidoResponseDto;

@RestController
@RequestMapping("/pedidos")
public class ConsultaPedidoController {
    private final VerStatus VerStatus;

    public ConsultaPedidoController(VerStatus VerStatus) {
        this.VerStatus = VerStatus;
    }

    @GetMapping("/{id}")
    @CrossOrigin("*")
    public ResponseEntity<PedidoResponseDto> consultarPedido(@PathVariable long id) {
        try {
            PedidoResponseDto dto = VerStatus.run(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
