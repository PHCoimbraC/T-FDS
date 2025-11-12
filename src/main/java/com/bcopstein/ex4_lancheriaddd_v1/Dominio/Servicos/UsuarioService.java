package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.UsuarioRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Usuario;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registra um novo usuário cliente
     */
    public Usuario registrarCliente(String nome, String cpf, String celular, 
                                     String endereco, String email, String senha) {
        // Validações
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        
        if (cpf != null && !cpf.isEmpty() && usuarioRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        // Criptografar senha (em produção, usar BCrypt ou similar)
        String senhaCriptografada = criptografarSenha(senha);

        Usuario novoUsuario = new Usuario(
            null,
            nome,
            cpf,
            celular,
            endereco,
            email,
            senhaCriptografada,
            Usuario.TipoUsuario.CLIENTE,
            LocalDateTime.now(),
            true
        );

        return usuarioRepository.salvar(novoUsuario);
    }

    /**
     * Autentica um usuário
     */
    public Optional<Usuario> autenticar(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        
        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = usuarioOpt.get();
        
        if (!usuario.isAtivo()) {
            throw new IllegalStateException("Usuário inativo");
        }

        String senhaCriptografada = criptografarSenha(senha);
        if (usuario.getSenha().equals(senhaCriptografada)) {
            return Optional.of(usuario);
        }

        return Optional.empty();
    }

    /**
     * Busca um usuário por email
     */
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Busca um usuário por ID
     */
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Atualiza os dados de um usuário
     */
    public void atualizar(Usuario usuario) {
        usuarioRepository.atualizar(usuario);
    }

    /**
     * Altera a senha de um usuário
     */
    public void alterarSenha(Long usuarioId, String senhaAntiga, String senhaNova) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        String senhaAntigaCriptografada = criptografarSenha(senhaAntiga);
        
        if (!usuario.getSenha().equals(senhaAntigaCriptografada)) {
            throw new IllegalArgumentException("Senha antiga incorreta");
        }

        usuario.setSenha(criptografarSenha(senhaNova));
        usuarioRepository.atualizar(usuario);
    }

    /**
     * Desativa um usuário
     */
    public void desativar(Long usuarioId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setAtivo(false);
        usuarioRepository.atualizar(usuario);
    }

    /**
     * Cria o usuário master no sistema (chamado na inicialização)
     */
    public void criarUsuarioMasterSeNaoExistir() {
        String emailMaster = "master@pizzaria.com";
        
        if (!usuarioRepository.existsByEmail(emailMaster)) {
            Usuario master = new Usuario(
                null,
                "Administrador",
                null,
                null,
                null,
                emailMaster,
                criptografarSenha("master123"),
                Usuario.TipoUsuario.MASTER,
                LocalDateTime.now(),
                true
            );
            usuarioRepository.salvar(master);
            System.out.println("Usuário master criado: " + emailMaster);
        }
    }

    
    private String criptografarSenha(String senha) {
        return senha;
    }
}