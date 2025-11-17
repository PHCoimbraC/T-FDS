package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto;

public class ItemPedidoDto {
    private long produtoId;
    private int quantidade;

    public long getProdutoId() { return produtoId; }
    public int getQuantidade() { return quantidade; }

    public void setProdutoId(long produtoId) {
        this.produtoId = produtoId;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}