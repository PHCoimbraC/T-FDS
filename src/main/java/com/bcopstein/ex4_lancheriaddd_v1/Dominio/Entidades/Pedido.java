package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.FaltaEstoqueDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    public enum Status {
        NOVO,
        APROVADO,
        NEGADO,
        PAGO,
        AGUARDANDO,
        PREPARACAO,
        PRONTO,
        TRANSPORTE,
        ENTREGUE,
        CANCELADO
    }
    private long id;
    private Cliente cliente;
    private LocalDateTime dataHoraPagamento;
    private List<ItemPedido> itens;
    private Status status;
    private double valor;
    private double impostos;
    private double desconto;
    private double valorCobrado;
    private List<FaltaEstoqueDto> faltas;

    public Pedido(long id, Cliente cliente, LocalDateTime dataHoraPagamento, List<ItemPedido> itens,
            Pedido.Status status, double valor, double impostos, double desconto, double valorCobrado, List<FaltaEstoqueDto> faltas) {
        this.id = id;
        this.cliente = cliente;
        this.dataHoraPagamento = dataHoraPagamento;
        this.itens = itens;
        this.status = status;
        this.valor = valor;
        this.impostos = impostos;
        this.desconto = desconto;
        this.valorCobrado = valorCobrado;
        this.faltas = faltas;
    }

    public long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDateTime getDataHoraPagamento() {
        return dataHoraPagamento;
    }

    public void setFaltas(List<FaltaEstoqueDto> faltas) {this.faltas = faltas;}

    public void setDataHoraPagamento(LocalDateTime dataHoraPagamento) {this.dataHoraPagamento = dataHoraPagamento;}

    public List<ItemPedido> getItens() {
        return itens;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status){
        this.status = status;
    }

    public double getValor() {
        return valor;
    }

    public double getImpostos() {
        return impostos;
    }

    public double getDesconto() {
        return desconto;
    }

    public double getValorCobrado() {
        return valorCobrado;
    }

    public List<FaltaEstoqueDto> getFaltas() {
        return faltas;
    }
}
