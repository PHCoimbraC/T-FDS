package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto;

import java.util.List;

public class PedidoRequestDto {
    private String emailCliente;
    private String enderecoEntrega;
    private String nomeCliente;
    private String cpfCliente;
    private String celularCliente;
    private List<ItemPedidoDto> itens;

    // Getters
    public String getEmailCliente() { return emailCliente; }
    public String getEnderecoEntrega() { return enderecoEntrega; }
    public String getNomeCliente() { return nomeCliente; }
    public String getCpfCliente() { return cpfCliente; }
    public String getCelularCliente() { return celularCliente; }
    public List<ItemPedidoDto> getItens() { return itens; }

    // Setters
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }
    public void setEnderecoEntrega(String enderecoEntrega) { this.enderecoEntrega = enderecoEntrega; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }
    public void setCpfCliente(String cpfCliente) { this.cpfCliente = cpfCliente; }
    public void setCelularCliente(String celularCliente) { this.celularCliente = celularCliente; }
    public void setItens(List<ItemPedidoDto> itens) { this.itens = itens; }
}