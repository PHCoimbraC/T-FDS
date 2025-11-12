package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados;

import java.util.Optional;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Usuario;

public interface UsuarioRepository {
    /**
     * Busca um usuário por email
     */
    Optional<Usuario> findByEmail(String email);
    
    /**
     * Busca um usuário por ID
     */
    Optional<Usuario> findById(Long id);
    
    /**
     * Busca um usuário por CPF
     */
    Optional<Usuario> findByCpf(String cpf);
    
    /**
     * Salva um novo usuário
     */
    Usuario salvar(Usuario usuario);
    
    /**
     * Atualiza os dados de um usuário existente
     */
    void atualizar(Usuario usuario);
    
    /**
     * Verifica se existe um usuário com o email informado
     */
    boolean existsByEmail(String email);
    
    /**
     * Verifica se existe um usuário com o CPF informado
     */
    boolean existsByCpf(String cpf);
}