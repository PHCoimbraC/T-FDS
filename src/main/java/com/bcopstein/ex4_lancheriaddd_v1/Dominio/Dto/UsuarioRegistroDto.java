package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto;

// registro de novo usuario
public class UsuarioRegistroDto {
    private String nome;
    private String cpf;
    private String celular;
    private String endereco;
    private String email;
    private String senha;

    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getCelular() { return celular; }
    public String getEndereco() { return endereco; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }

    public void setNome(String nome) { this.nome = nome; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setCelular(String celular) { this.celular = celular; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
}

