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
import com.bcopstein.ex4_lancheriaddd_v1.Configuracao.AuthHelper;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.PedidoRequestDto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Usuario;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.CozinhaService;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.PedidoService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin("*")
public class PedidoController {
    private final PedidoUC pedidoUC;
    private final PedidoService pedidoService;
    private final CozinhaService cozinhaService;
    private final AuthHelper authHelper;

    public PedidoController(PedidoUC pedidoUC,
                            PedidoService pedidoService,
                            CozinhaService cozinhaService,
                            AuthHelper authHelper) {
        this.pedidoUC = pedidoUC;
        this.pedidoService = pedidoService;
        this.cozinhaService = cozinhaService;
        this.authHelper = authHelper;
    }

    @PostMapping("/fazerPedido")
    public ResponseEntity<Pedido> fazerPedido(@RequestBody PedidoRequestDto pedidoDTO,
                                              HttpServletRequest request) {
        Usuario usuario = authHelper.getUsuarioAutenticado(request);

        // Sempre usar o email do usuário autenticado
        pedidoDTO.setEmailCliente(usuario.getEmail());

        Pedido aprovadoOuNegado = pedidoUC.run(pedidoDTO);
        if (aprovadoOuNegado.getStatus() == Pedido.Status.APROVADO) {
            return ResponseEntity.ok(aprovadoOuNegado);
        }
        return ResponseEntity.badRequest().body(aprovadoOuNegado);
    }

    @DeleteMapping("/{id}/cancelar")
    public ResponseEntity<Pedido> cancelarPedido(@PathVariable Long id,
                                                 HttpServletRequest request) {
        Usuario usuario = authHelper.getUsuarioAutenticado(request);

        Pedido pedidoCancelado = pedidoService.cancelarPedido(id);
        if (pedidoCancelado == null) {
            return ResponseEntity.notFound().build();
        }

        // Somente o dono do pedido ou o MASTER podem cancelar
        if (!pedidoCancelado.getCliente().getEmail().equals(usuario.getEmail())
                && !usuario.isMaster()) {
            return ResponseEntity.status(403).build();
        }

        if (pedidoCancelado.getStatus() != Pedido.Status.NOVO) {
            return ResponseEntity.badRequest().body(pedidoCancelado);
        }

        return ResponseEntity.ok(pedidoCancelado);
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<String> pagar(@PathVariable Long id,
                                        HttpServletRequest request) {
        Usuario usuario = authHelper.getUsuarioAutenticado(request);

        Pedido pedido = pedidoService.pagarPedido(id);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }

        // Somente o dono do pedido ou o MASTER podem pagar
        if (!pedido.getCliente().getEmail().equals(usuario.getEmail())
                && !usuario.isMaster()) {
            return ResponseEntity.status(403).build();
        }

        if (pedido.getStatus() != Pedido.Status.PAGO) {
            return ResponseEntity.badRequest().body("Pedido não aprovado");
        }

        cozinhaService.chegadaDePedido(pedido);
        return ResponseEntity.ok("pago");
    }
}