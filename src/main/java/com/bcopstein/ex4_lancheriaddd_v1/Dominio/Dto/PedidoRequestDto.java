package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto;

import java.util.List;

public class PedidoRequestDto {
    private String emailCliente;
    private String enderecoEntrega;
    private String nomeCliente;
    private String cpfCliente;
    private String celularCliente;
    private List<ItemPedidoDto> itens;

    public String getEmailCliente() { return emailCliente; }
    public String getEnderecoEntrega() { return enderecoEntrega; }
    public String getNomeCliente() { return nomeCliente; }
    public String getCpfCliente() { return cpfCliente; }
    public String getCelularCliente() { return celularCliente; }
    public List<ItemPedidoDto> getItens() { return itens; }
}
