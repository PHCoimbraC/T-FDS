package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades;

import java.time.LocalDateTime;

public class Usuario {
    public enum TipoUsuario {
        MASTER,
        CLIENTE
    }

    private Long id;
    private String nome;
    private String cpf;
    private String celular;
    private String endereco;
    private String email;
    private String senha;
    private TipoUsuario tipo;
    private LocalDateTime dataCadastro;
    private boolean ativo;

    public Usuario(Long id, String nome, String cpf, String celular, String endereco, 
                   String email, String senha, TipoUsuario tipo, LocalDateTime dataCadastro, boolean ativo) {
        validarCamposObrigatorios(email, senha);
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.celular = celular;
        this.endereco = endereco;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
        this.dataCadastro = dataCadastro;
        this.ativo = ativo;
    }

    private void validarCamposObrigatorios(String email, String senha) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido");
        }
    }

    public boolean isMaster() {
        return this.tipo == TipoUsuario.MASTER;
    }

    public boolean isCliente() {
        return this.tipo == TipoUsuario.CLIENTE;
    }

    // Getters
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getCelular() { return celular; }
    public String getEndereco() { return endereco; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public TipoUsuario getTipo() { return tipo; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public boolean isAtivo() { return ativo; }

    // Setters
    public void setNome(String nome) { this.nome = nome; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setCelular(String celular) { this.celular = celular; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public void setSenha(String senha) { this.senha = senha; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", tipo=" + tipo +
                ", ativo=" + ativo +
                '}';
    }
}