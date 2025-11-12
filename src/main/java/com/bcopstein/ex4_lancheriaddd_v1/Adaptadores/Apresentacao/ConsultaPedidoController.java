package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao;

import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.VerStatus;
import com.bcopstein.ex4_lancheriaddd_v1.Configuracao.AuthHelper;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.PedidoResponseDto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin("*")
public class ConsultaPedidoController {
    private final VerStatus verStatus;
    private final AuthHelper authHelper;
    private final PedidosRepository pedidosRepository;

    public ConsultaPedidoController(VerStatus verStatus, 
                                    AuthHelper authHelper,
                                    PedidosRepository pedidosRepository) {
        this.verStatus = verStatus;
        this.authHelper = authHelper;
        this.pedidosRepository = pedidosRepository;
    }

    /**
     * UC3 - Solicitar status de pedido (REQUER AUTENTICAÇÃO)
     */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDto> consultarPedido(@PathVariable long id,
                                                              HttpServletRequest request) {
        try {
            // Verificar autenticação
            Usuario usuario = authHelper.getUsuarioAutenticado(request);
            
            // Buscar o pedido
            Optional<Pedido> pedidoOpt = pedidosRepository.findByCodigo(id);
            
            if (pedidoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Pedido pedido = pedidoOpt.get();
            
            // Verificar se o pedido pertence ao usuário autenticado (ou se é master)
            if (!pedido.getCliente().getEmail().equals(usuario.getEmail()) 
                && !usuario.isMaster()) {
                return ResponseEntity.status(403).build(); // Forbidden
            }
            
            PedidoResponseDto dto = verStatus.run(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}