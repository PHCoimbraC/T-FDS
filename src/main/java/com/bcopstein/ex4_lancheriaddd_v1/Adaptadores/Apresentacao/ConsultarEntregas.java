package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao;

import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.ListarEntregas;
import com.bcopstein.ex4_lancheriaddd_v1.Configuracao.AuthHelper;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
public class ConsultarEntregas {
    private final ListarEntregas listarPedidosUC;
    private final AuthHelper authHelper;

    public ConsultarEntregas(ListarEntregas listarPedidosUC, AuthHelper authHelper) {
        this.listarPedidosUC = listarPedidosUC;
        this.authHelper = authHelper;
    }

    /**
     * UC6 - Listar os pedidos entregues entre duas datas (REQUER AUTENTICAÇÃO)
     * Clientes veem apenas seus pedidos, Masters veem todos
     */
    @GetMapping("/pedidos/entregues")
    public ResponseEntity<List<Pedido>> listarPedidosEntregues(
        @RequestParam("inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
        @RequestParam("fim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
        HttpServletRequest request
    ) {
        // Verificar autenticação
        Usuario usuario = authHelper.getUsuarioAutenticado(request);
        
        List<Pedido> pedidos = listarPedidosUC.run(inicio, fim);
        
        // Se for cliente, filtrar apenas seus pedidos
        if (usuario.isCliente()) {
            pedidos = pedidos.stream()
                .filter(p -> p.getCliente().getEmail().equals(usuario.getEmail()))
                .collect(Collectors.toList());
        }
        // Se for master, retorna todos os pedidos
        
        return ResponseEntity.ok(pedidos);
    }
}