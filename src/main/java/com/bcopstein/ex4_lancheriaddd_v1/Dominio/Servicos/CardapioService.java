package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.CardapioRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.ConfiguracaoRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.CabecalhoCardapio;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cardapio;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Produto;

@Service
public class CardapioService {

    private CardapioRepository cardapioRepository;
    private ConfiguracaoRepository configuracaoRepository;

    @Autowired
    public CardapioService(CardapioRepository cardapioRepository,
                          ConfiguracaoRepository configuracaoRepository){
        this.cardapioRepository = cardapioRepository;
        this.configuracaoRepository = configuracaoRepository;
    }

    public Cardapio recuperaCardapio(long id){
        return cardapioRepository.recuperaPorId(id);
    }

    public List<CabecalhoCardapio> recuperaListaDeCardapios(){
        return cardapioRepository.cardapiosDisponiveis();
    }

    public List<Produto> recuperaSugestoesDoChef(){
        return cardapioRepository.indicacoesDoChef();
    }


    public long getCardapioAtivoId() {
        return configuracaoRepository.getValor("cardapio_ativo_id")
            .map(Long::parseLong)
            .orElse(1L);
    }

    public Cardapio getCardapioAtivo() {
        long idAtivo = getCardapioAtivoId();
        return recuperaCardapio(idAtivo);
    }

    public void setCardapioAtivo(long cardapioId) {
        Cardapio cardapio = recuperaCardapio(cardapioId);
        if (cardapio == null) {
            throw new IllegalArgumentException("Cardápio não encontrado: " + cardapioId);
        }
        configuracaoRepository.setValor("cardapio_ativo_id", String.valueOf(cardapioId));
        System.out.println("Cardápio ativo alterado para: " + cardapioId + " - " + cardapio.getTitulo());
    }
}