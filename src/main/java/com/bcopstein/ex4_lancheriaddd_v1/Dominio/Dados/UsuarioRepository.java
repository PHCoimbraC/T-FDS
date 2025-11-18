package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados;

import java.util.Optional;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Usuario;

public interface UsuarioRepository {

    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByCpf(String cpf);
    Usuario salvar(Usuario usuario);
    void atualizar(Usuario usuario);
    boolean existsByEmail(String email);
    boolean existsByCpf(String cpf);
}