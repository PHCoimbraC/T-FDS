package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto;

public class FaltaEstoqueDto {

        private String produto;
        private Long produtoId;

        public FaltaEstoqueDto(String produto, Long produtoId) {
            this.produto = produto;
            this.produtoId = produtoId;
        }

    public String getProduto() {
            return produto;
    }
    public Long getProdutoId() {
            return produtoId;
    }
}
