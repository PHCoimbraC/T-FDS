package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto;

// DTO para registro de novo usuário
public class UsuarioRegistroDto {
    private String nome;
    private String cpf;
    private String celular;
    private String endereco;
    private String email;
    private String senha;

    // Getters
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getCelular() { return celular; }
    public String getEndereco() { return endereco; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }

    // Setters
    public void setNome(String nome) { this.nome = nome; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public void setCelular(String celular) { this.celular = celular; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
}

// DTO para login
class LoginDto {
    private String email;
    private String senha;

    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    
    public void setEmail(String email) { this.email = email; }
    public void setSenha(String senha) { this.senha = senha; }
}

// DTO para resposta de autenticação
class AutenticacaoResponseDto {
    private Long id;
    private String nome;
    private String email;
    private String tipo;
    private String token; // Para futuro uso com JWT

    public AutenticacaoResponseDto(Long id, String nome, String email, String tipo, String token) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
        this.token = token;
    }

    // Getters
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTipo() { return tipo; }
    public String getToken() { return token; }
}

// DTO para alteração de senha
class AlterarSenhaDto {
    private String senhaAntiga;
    private String senhaNova;

    public String getSenhaAntiga() { return senhaAntiga; }
    public String getSenhaNova() { return senhaNova; }
    
    public void setSenhaAntiga(String senhaAntiga) { this.senhaAntiga = senhaAntiga; }
    public void setSenhaNova(String senhaNova) { this.senhaNova = senhaNova; }
}