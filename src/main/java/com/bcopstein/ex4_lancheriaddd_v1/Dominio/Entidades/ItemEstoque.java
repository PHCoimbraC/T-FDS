package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "itensEstoque")
public class ItemEstoque {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private Ingrediente ingrediente;
    
    @Column(nullable = false)
    private int quantidade;

    public ItemEstoque() {
    }

    public ItemEstoque(Ingrediente ingrediente, int quantidade) {
        this.ingrediente = ingrediente;
        this.quantidade = quantidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ingrediente getIngrediente() {
        return ingrediente;
    }

    public void setIngrediente(Ingrediente ingrediente) {
        this.ingrediente = ingrediente;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void diminuirQuantidade(int qtd) {
        if (this.quantidade < qtd) {
            throw new IllegalStateException("Estoque insuficiente de " + ingrediente.getDescricao());
        }
        this.quantidade -= qtd;
    }

    public void aumentarQuantidade(int qtd) {
        this.quantidade += qtd;
    }
}