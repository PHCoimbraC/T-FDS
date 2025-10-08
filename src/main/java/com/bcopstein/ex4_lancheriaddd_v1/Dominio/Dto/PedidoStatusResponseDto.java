package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto;

public class PedidoStatusResponseDto {
    private long id;
    private String emailCliente;
    private String status;
    private double total;

    public PedidoStatusResponseDto(long id, String emailCliente, String status, double total) {
        this.id = id;
        this.emailCliente = emailCliente;
        this.status = status;
        this.total = total;
    }

    public long getId() { return id; }
    public String getEmailCliente() { return emailCliente; }
    public String getStatus() { return status; }
    public double getTotal() { return total; }
}
