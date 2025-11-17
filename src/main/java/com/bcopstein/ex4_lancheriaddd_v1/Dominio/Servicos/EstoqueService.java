package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.util.List;
import java.util.Optional;

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
    public boolean verificarDisponibilidade(List<ItemPedido> itens) {
        boolean tudoOk = true;

        for (ItemPedido itemPedido : itens) {
            Receita receita = itemPedido.getItem().getReceita();

            for (Ingrediente ingrediente : receita.getIngredientes()) {
                Optional<ItemEstoque> itemEstoqueOpt = estoqueRepository.findByIngredienteId(ingrediente.getId());

                if (itemEstoqueOpt.isEmpty()) {
                    System.out.println("Ingrediente não encontradoF " + ingrediente.getDescricao());
                    produtosRepository.marcarProdutosIndisponiveisPorIngrediente(ingrediente.getId());
                    tudoOk = false;
                    continue;
                }

                ItemEstoque itemEstoque = itemEstoqueOpt.get();
                int quantidadeNecessaria = itemPedido.getQuantidade();

                if (itemEstoque.getQuantidade() < quantidadeNecessaria) {
                    System.out.println("Estoque insuficiente de " + ingrediente.getDescricao() +
                            ". Disponível: " + itemEstoque.getQuantidade() +
                            ", Necessário: " + quantidadeNecessaria);
                    produtosRepository.marcarProdutosIndisponiveisPorIngrediente(ingrediente.getId());
                    tudoOk = false;
                }
            }
        }

        if (tudoOk) {
            System.out.println("Estoque disponível");
        } else {
            System.out.println("Falta de estoque");
        }

        return tudoOk;
    }

    @Transactional
    public void darBaixaEstoque(List<ItemPedido> itens) {
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