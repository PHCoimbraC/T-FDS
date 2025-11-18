package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto;

public class PedidoResponseDto {
    private long id;
    private String emailCliente;
    private String status;
    private double total;
    private String message;

    public PedidoResponseDto(long id, String emailCliente, String status, double total, String message) {
        this.id = id;
        this.emailCliente = emailCliente;
        this.status = status;
        this.total = total;
        this.message = message;
    }

    public long getId() { return id; }
    public String getEmailCliente() { return emailCliente; }
    public String getStatus() { return status; }
    public double getTotal() { return total; }
    public String getMessage() { return message; }
}
