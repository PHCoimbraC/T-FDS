package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;   
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao.Presenters.CabecalhoCardapioPresenter;
import com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao.Presenters.CardapioPresenter;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.RecuperaListaCardapiosUC;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.RecuperarCardapioUC;
import com.bcopstein.ex4_lancheriaddd_v1.Aplicacao.Responses.CardapioResponse;
import com.bcopstein.ex4_lancheriaddd_v1.Configuracao.AuthHelper;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Produto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Usuario;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.CardapioService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/cardapio")
@CrossOrigin("*")
public class CardapioController {
    private RecuperarCardapioUC recuperaCardapioUC;
    private RecuperaListaCardapiosUC recuperaListaCardapioUC;
    private CardapioService cardapioService;
    private AuthHelper authHelper;

    public CardapioController(RecuperarCardapioUC recuperaCardapioUC,
                              RecuperaListaCardapiosUC recuperaListaCardapioUC,
                              CardapioService cardapioService,
                              AuthHelper authHelper) {
        this.recuperaCardapioUC = recuperaCardapioUC;
        this.recuperaListaCardapioUC = recuperaListaCardapioUC;
        this.cardapioService = cardapioService;
        this.authHelper = authHelper;
    }

    @GetMapping("/atual")
    public CardapioPresenter recuperaCardapioAtual(){
        long idAtivo = cardapioService.getCardapioAtivoId();
        CardapioResponse cardapioResponse = recuperaCardapioUC.run(idAtivo);
        
        Set<Long> conjIdSugestoes = new HashSet<>(cardapioResponse.getSugestoesDoChef().stream()
                .map(produto->produto.getId())
                .toList());
        CardapioPresenter cardapioPresenter = new CardapioPresenter(cardapioResponse.getCardapio().getTitulo());
        for(Produto produto:cardapioResponse.getCardapio().getProdutos()){
            boolean sugestao = conjIdSugestoes.contains(produto.getId());
            cardapioPresenter.insereItem(produto.getId(), produto.getDescricao(), produto.getPreco(), sugestao);
        }
        return cardapioPresenter;
    }

    @GetMapping("/{id}")
    public CardapioPresenter recuperaCardapio(@PathVariable(value="id")long id){
        CardapioResponse cardapioResponse = recuperaCardapioUC.run(id);
        Set<Long> conjIdSugestoes = new HashSet<>(cardapioResponse.getSugestoesDoChef().stream()
            .map(produto->produto.getId())
            .toList());
        CardapioPresenter cardapioPresenter = new CardapioPresenter(cardapioResponse.getCardapio().getTitulo());
        for(Produto produto:cardapioResponse.getCardapio().getProdutos()){
            boolean sugestao = conjIdSugestoes.contains(produto.getId());
            cardapioPresenter.insereItem(produto.getId(), produto.getDescricao(), produto.getPreco(), sugestao);
        }
        return cardapioPresenter;
    }

    @GetMapping("/lista")
    public List<CabecalhoCardapioPresenter> recuperaListaCardapios(){
         List<CabecalhoCardapioPresenter> lstCardapios = 
            recuperaListaCardapioUC.run().cabecalhos().stream()
            .map(cabCar -> new CabecalhoCardapioPresenter(cabCar.id(),cabCar.titulo()))
            .toList();
         return lstCardapios;
    }

    /**
     * UC: Master define o cardápio ativo
     * Apenas usuários MASTER podem acessar
     */
    @PostMapping("/{id}/ativar")
    public ResponseEntity<?> ativarCardapio(@PathVariable long id, HttpServletRequest request) {
        try {
            Usuario usuario = authHelper.getUsuarioAutenticado(request);
            
            if (!usuario.isMaster()) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "Apenas usuários MASTER podem ativar cardápios");
                return ResponseEntity.status(403).body(error);
            }

            cardapioService.setCardapioAtivo(id);

            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Cardápio ativado com sucesso");
            response.put("cardapioId", id);
            response.put("cardapioAtivo", cardapioService.getCardapioAtivo().getTitulo());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao ativar cardápio");
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Consultar qual cardápio está ativo atualmente
     */
    @GetMapping("/ativo/info")
    public ResponseEntity<?> infoCardapioAtivo() {
        try {
            long idAtivo = cardapioService.getCardapioAtivoId();
            var cardapioAtivo = cardapioService.getCardapioAtivo();

            Map<String, Object> response = new HashMap<>();
            response.put("cardapioId", idAtivo);
            response.put("titulo", cardapioAtivo.getTitulo());
            response.put("quantidadeProdutos", cardapioAtivo.getProdutos().size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao consultar cardápio ativo");
            return ResponseEntity.status(500).body(error);
        }
    }
}
