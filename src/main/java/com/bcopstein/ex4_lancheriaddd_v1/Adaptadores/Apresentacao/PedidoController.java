package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao;

import java.util.HashMap;
import java.util.Map;

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
    private final PedidoUC PedidoUC;
    private final PedidoService PedidoService;
    private final CozinhaService cozinhaService;
    private final AuthHelper authHelper;

    // ← CONSTRUTOR ATUALIZADO
    public PedidoController(PedidoUC PedidoUC, 
                           PedidoService PedidoService, 
                           CozinhaService cozinhaService,
                           AuthHelper authHelper) {
        this.PedidoUC = PedidoUC;
        this.PedidoService = PedidoService;
        this.cozinhaService = cozinhaService;
        this.authHelper = authHelper;
    }

    @PostMapping("/fazerPedido")
        public ResponseEntity<?> fazerPedido(@RequestBody PedidoRequestDto pedidoDTO,
                                            HttpServletRequest request){
            // Pegar usuário autenticado da sessão
            Usuario usuario = authHelper.getUsuarioAutenticado(request);
            
            // Usar o email do usuário autenticado (não do JSON)
            pedidoDTO.setEmailCliente(usuario.getEmail());
            
            Pedido pedido = PedidoUC.run(pedidoDTO);
            
            // Se aprovado, retorna 200 OK
            if (pedido.getStatus() == Pedido.Status.APROVADO){
                return ResponseEntity.ok(pedido);
            }
            
            // Se negado por estoque, retorna 400 com mensagem clara
            if (pedido.getStatus() == Pedido.Status.NEGADO) {
                Map<String, Object> erro = new HashMap<>();
                erro.put("status", "NEGADO");
                erro.put("mensagem", "Pedido negado: estoque insuficiente para um ou mais produtos");
                erro.put("pedido", pedido);
                return ResponseEntity.badRequest().body(erro);
            }
            
            // Outros casos de erro
            return ResponseEntity.badRequest().body(pedido);
        }

    // ← MÉTODO ATUALIZADO
    @DeleteMapping("/{id}/cancelar")
    public ResponseEntity<Pedido> cancelarPedido(@PathVariable Long id,
                                                  HttpServletRequest request) {
        // Pegar usuário autenticado
        Usuario usuario = authHelper.getUsuarioAutenticado(request);
        
        Pedido pedidoCancelado = PedidoService.cancelarPedido(id);
        if (pedidoCancelado == null) {
            return ResponseEntity.notFound().build();
        }

        // Verificar se o pedido pertence ao usuário (ou se é master)
        if (!pedidoCancelado.getCliente().getEmail().equals(usuario.getEmail()) 
            && !usuario.isMaster()) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        if (pedidoCancelado.getStatus() != Pedido.Status.NOVO) {
            return ResponseEntity.badRequest().body(pedidoCancelado);
        }

        return ResponseEntity.ok(pedidoCancelado);
    }

    // ← MÉTODO ATUALIZADO
    @PostMapping("/{id}/pagar")
    public ResponseEntity<String> pagar(@PathVariable Long id,
                                        HttpServletRequest request) {
        // Pegar usuário autenticado
        Usuario usuario = authHelper.getUsuarioAutenticado(request);
        
        var pedido = PedidoService.pagarPedido(id);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }

        // Verificar se o pedido pertence ao usuário (ou se é master)
        if (!pedido.getCliente().getEmail().equals(usuario.getEmail()) 
            && !usuario.isMaster()) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        if (pedido.getStatus() != Pedido.Status.PAGO) {
            return ResponseEntity.badRequest().body("Pedido não aprovado");
        }
        cozinhaService.chegadaDePedido(pedido);
        return ResponseEntity.ok("pago");
    }
}