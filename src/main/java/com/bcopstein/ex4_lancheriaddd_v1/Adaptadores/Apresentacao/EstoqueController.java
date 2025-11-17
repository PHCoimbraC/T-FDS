package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bcopstein.ex4_lancheriaddd_v1.Configuracao.AuthHelper;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemEstoque;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Usuario;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.EstoqueService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/estoque")
@CrossOrigin("*")
public class EstoqueController {
    
    private final EstoqueService estoqueService;
    private final AuthHelper authHelper;

    public EstoqueController(EstoqueService estoqueService, AuthHelper authHelper) {
        this.estoqueService = estoqueService;
        this.authHelper = authHelper;
    }

    /**
     * Listar todo o estoque (MASTER apenas)
     */
    @GetMapping
    public ResponseEntity<?> listarEstoque(HttpServletRequest request) {
        try {
            Usuario usuario = authHelper.getUsuarioAutenticado(request);
            
            if (!usuario.isMaster()) {
                return ResponseEntity.status(403).body(Map.of("erro", "Apenas MASTER pode consultar estoque"));
            }

            List<ItemEstoque> estoque = estoqueService.listarEstoque();
            
            List<Map<String, Object>> response = estoque.stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", item.getId());
                    map.put("ingredienteId", item.getIngrediente().getId());
                    map.put("ingrediente", item.getIngrediente().getDescricao());
                    map.put("quantidade", item.getQuantidade());
                    return map;
                })
                .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("erro", "Erro ao listar estoque"));
        }
    }

    /**
     * Repor estoque (MASTER apenas)
     */
    @PostMapping("/{ingredienteId}/repor")
    public ResponseEntity<?> reporEstoque(@PathVariable Long ingredienteId,
                                          @RequestBody Map<String, Integer> body,
                                          HttpServletRequest request) {
        try {
            Usuario usuario = authHelper.getUsuarioAutenticado(request);
            
            if (!usuario.isMaster()) {
                return ResponseEntity.status(403).body(Map.of("ERRO", "Apenas MASTER pode repor estoque"));
            }

            Integer quantidade = body.get("quantidade");
            if (quantidade == null || quantidade <= 0) {
                return ResponseEntity.badRequest().body(Map.of("ERRO", "Quantidade inválida"));
            }

            ItemEstoque item = estoqueService.reporEstoque(ingredienteId, quantidade);

            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Estoque reposto com sucesso");
            response.put("ingrediente", item.getIngrediente().getDescricao());
            response.put("quantidadeAdicionada", quantidade);
            response.put("quantidadeAtual", item.getQuantidade());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("ERRO", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("ERRO", "ERRO ao repor estoque"));
        }
    }
}