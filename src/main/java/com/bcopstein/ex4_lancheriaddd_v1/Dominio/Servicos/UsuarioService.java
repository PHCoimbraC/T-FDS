package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.UsuarioRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Usuario;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Usuario registrarCliente(String nome, String cpf, String celular,
                                    String endereco, String email, String senha) {
        // Validações
        if (usuarioRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        if (cpf != null && !cpf.isEmpty() && usuarioRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

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

    public Optional<Usuario> autenticar(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuario.isAtivo()) {
            throw new IllegalStateException("Usuário inativo");
        }

        // senha codificada
        if (passwordEncoder.matches(senha, usuario.getSenha())) {
            return Optional.of(usuario);
        }

        return Optional.empty();
    }

    public void desativar(Long usuarioId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setAtivo(false);
        usuarioRepository.atualizar(usuario);
    }

    public void criarUsuarioMasterSeNaoExistir() {
        if (!usuarioRepository.existsByEmail("master@gmail.com")) {
            Usuario master = new Usuario(
                    null,
                    "Master",
                    null,
                    null,
                    null,
                    "master@gmail.com",
                    criptografarSenha("123"),
                    Usuario.TipoUsuario.MASTER,
                    LocalDateTime.now(),
                    true
            );
            usuarioRepository.salvar(master);
            System.out.println("Usuário master criado: master@gmail.com");
        }
    }

    // criptografar
    private String criptografarSenha(String senha) {
        return passwordEncoder.encode(senha);
    }
}
