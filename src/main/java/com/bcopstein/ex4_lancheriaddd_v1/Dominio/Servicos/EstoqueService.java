package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.FaltaEstoqueDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.EstoqueRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.ProdutosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Ingrediente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemEstoque;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemPedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Receita;

@Service
public class EstoqueService {
    private final EstoqueRepository estoqueRepository;
    private final ProdutosRepository produtosRepository;

    public EstoqueService(EstoqueRepository estoqueRepository,
                          ProdutosRepository produtosRepository) {
        this.estoqueRepository = estoqueRepository;
        this.produtosRepository = produtosRepository;
    }


    @Transactional(readOnly = true)
    public List<FaltaEstoqueDto> verificarDisponibilidade(List<ItemPedido> itens) {
        List<FaltaEstoqueDto> faltas = new ArrayList<>();

        for (ItemPedido itemPedido : itens) {
            Receita receita = itemPedido.getItem().getReceita();

            for (Ingrediente ingrediente : receita.getIngredientes()) {
                Optional<ItemEstoque> itemEstoqueOpt = estoqueRepository.findByIngredienteId(ingrediente.getId());

                if (itemEstoqueOpt.isEmpty()) {
                    faltas.add(new FaltaEstoqueDto(ingrediente.getDescricao(), ingrediente.getId()));
                    produtosRepository.marqueProdutoIndisponivel(ingrediente.getId());
                    continue;
                }

                ItemEstoque itemEstoque = itemEstoqueOpt.get();
                int quantidadeNecessaria = itemPedido.getQuantidade();

                if (itemEstoque.getQuantidade() <= quantidadeNecessaria) {
                    if (!faltas.contains(new FaltaEstoqueDto(itemPedido.getItem().getDescricao(), itemPedido.getItem().getId()))) {
                        faltas.add(new FaltaEstoqueDto(itemPedido.getItem().getDescricao(), itemPedido.getItem().getId()));
                        produtosRepository.marqueProdutoIndisponivel(ingrediente.getId());
                    }
                }
            }
        }

        return faltas;
    }


    @Transactional
    public void SalvarEstoque(List<ItemPedido> itens) {
        for (ItemPedido itemPedido : itens) {
            Receita receita = itemPedido.getItem().getReceita();

            for (Ingrediente ingrediente : receita.getIngredientes()) {
                ItemEstoque itemEstoque = estoqueRepository.findByIngredienteId(ingrediente.getId())
                        .orElseThrow(() -> new IllegalStateException("Ingrediente não encontrado: " + ingrediente.getDescricao()));

                int quantidadeNecessaria = itemPedido.getQuantidade();
                itemEstoque.diminuirQuantidade(quantidadeNecessaria);

                estoqueRepository.save(itemEstoque);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<ItemEstoque> listarEstoque() {
        return estoqueRepository.findAll();
    }


    @Transactional
    public ItemEstoque reporEstoque(Long ingredienteId, Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        ItemEstoque itemEstoque = estoqueRepository.findByIngredienteId(ingredienteId)
                .orElseThrow(() -> new IllegalArgumentException("Ingrediente não encontrado no estoque: " + ingredienteId));

        itemEstoque.aumentarQuantidade(quantidade);
        estoqueRepository.save(itemEstoque);

        produtosRepository.marcarProdutosDisponiveisPorIngrediente(ingredienteId);

        return itemEstoque;
    }
}