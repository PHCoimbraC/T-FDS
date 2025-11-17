package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.EstoqueRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Ingrediente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemEstoque;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemPedido;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Receita;

@Service
public class EstoqueService {
    
    private final EstoqueRepository estoqueRepository;

    public EstoqueService(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    /**
     * Verifica se há estoque disponível para todos os itens do pedido
     */
    @Transactional(readOnly = true)
    public boolean verificarDisponibilidade(List<ItemPedido> itens) {
        for (ItemPedido itemPedido : itens) {
            Receita receita = itemPedido.getItem().getReceita();
            
            // Verifica cada ingrediente da receita
            for (Ingrediente ingrediente : receita.getIngredientes()) {
                Optional<ItemEstoque> itemEstoqueOpt = estoqueRepository.findByIngredienteId(ingrediente.getId());
                
                if (itemEstoqueOpt.isEmpty()) {
                    System.out.println("Ingrediente não encontrado no estoque: " + ingrediente.getDescricao());
                    return false;
                }
                
                ItemEstoque itemEstoque = itemEstoqueOpt.get();
                int quantidadeNecessaria = itemPedido.getQuantidade(); // 1 ingrediente por unidade
                
                if (itemEstoque.getQuantidade() < quantidadeNecessaria) {
                    System.out.println("Estoque insuficiente de " + ingrediente.getDescricao() + 
                                     ". Disponível: " + itemEstoque.getQuantidade() + 
                                     ", Necessário: " + quantidadeNecessaria);
                    return false;
                }
            }
        }
        
        System.out.println("Estoque disponível para todos os itens do pedido");
        return true;
    }

    /**
     * Dá baixa no estoque quando um pedido é aprovado
     */
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
                System.out.println("Baixa no estoque: " + ingrediente.getDescricao() + 
                                 " - Quantidade restante: " + itemEstoque.getQuantidade());
            }
        }
    }

    /**
     * Lista todo o estoque (para MASTER)
     */
    @Transactional(readOnly = true)
    public List<ItemEstoque> listarEstoque() {
        return estoqueRepository.findAll();
    }

    /**
     * Repõe estoque de um ingrediente específico (para MASTER)
     */
    @Transactional
    public ItemEstoque reporEstoque(Long ingredienteId, Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        ItemEstoque itemEstoque = estoqueRepository.findByIngredienteId(ingredienteId)
            .orElseThrow(() -> new IllegalArgumentException("Ingrediente não encontrado no estoque: " + ingredienteId));

        itemEstoque.aumentarQuantidade(quantidade);
        estoqueRepository.save(itemEstoque);

        System.out.println("Estoque reposto: " + itemEstoque.getIngrediente().getDescricao() + 
                         " - Nova quantidade: " + itemEstoque.getQuantidade());

        return itemEstoque;
    }
}