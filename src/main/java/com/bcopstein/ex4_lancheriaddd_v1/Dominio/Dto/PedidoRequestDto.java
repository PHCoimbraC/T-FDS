package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto;

import java.util.List;

public class PedidoRequestDto {
    private String emailCliente;
    private String enderecoEntrega;
    private List<ItemPedidoDto> itens;

    public String getEmailCliente() { return emailCliente; }
    public String getEnderecoEntrega() { return enderecoEntrega; }
    public List<ItemPedidoDto> getItens() { return itens; }
}