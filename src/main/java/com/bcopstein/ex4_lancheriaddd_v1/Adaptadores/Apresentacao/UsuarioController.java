package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Apresentacao;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bcopstein.ex4_lancheriaddd_v1.Configuracao.AuthHelper;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.UsuarioRegistroDto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Usuario;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin("*")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final AuthHelper authHelper;

    public UsuarioController(UsuarioService usuarioService, AuthHelper authHelper) {
        this.usuarioService = usuarioService;
        this.authHelper = authHelper;
    }

    /**
     * Endpoint para registro de novo usuário cliente
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody UsuarioRegistroDto dto) {
        try {
            Usuario usuario = usuarioService.registrarCliente(
                dto.getNome(),
                dto.getCpf(),
                dto.getCelular(),
                dto.getEndereco(),
                dto.getEmail(),
                dto.getSenha()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("id", usuario.getId());
            response.put("nome", usuario.getNome());
            response.put("email", usuario.getEmail());
            response.put("mensagem", "Usuário registrado com sucesso");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao registrar usuário");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint para login/autenticação
     * Usa /auth/login para evitar conflito com /usuarios/{id}
     */
    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpServletRequest request) {
    try {
        String email = loginData.get("email");
        String senha = loginData.get("senha");

        Optional<Usuario> usuarioOpt = usuarioService.autenticar(email, senha);

        if (usuarioOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Email ou senha inválidos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        Usuario usuario = usuarioOpt.get();

        // ✅ CRIAR SESSÃO
        authHelper.login(request, usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("id", usuario.getId());
        response.put("nome", usuario.getNome());
        response.put("email", usuario.getEmail());
        response.put("tipo", usuario.getTipo().name());
        response.put("endereco", usuario.getEndereco());
        response.put("mensagem", "Login realizado com sucesso");

        return ResponseEntity.ok(response);
    } catch (IllegalStateException e) {
        Map<String, String> error = new HashMap<>();
        error.put("erro", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    } catch (Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("erro", "Erro ao realizar login");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

    /**
     * Endpoint para buscar dados do usuário por email
     * Colocado antes de /{id} para evitar conflitos
     */
    @GetMapping("/por-email/{email}")
    public ResponseEntity<?> buscarPorEmail(@PathVariable String email) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(email);

            if (usuarioOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "Usuário não encontrado");
                return ResponseEntity.notFound().build();
            }

            Usuario usuario = usuarioOpt.get();

            Map<String, Object> response = new HashMap<>();
            response.put("id", usuario.getId());
            response.put("nome", usuario.getNome());
            response.put("email", usuario.getEmail());
            response.put("cpf", usuario.getCpf());
            response.put("celular", usuario.getCelular());
            response.put("endereco", usuario.getEndereco());
            response.put("tipo", usuario.getTipo().name());
            response.put("ativo", usuario.isAtivo());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao buscar usuário");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint para buscar dados do usuário por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);

            if (usuarioOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "Usuário não encontrado");
                return ResponseEntity.notFound().build();
            }

            Usuario usuario = usuarioOpt.get();

            Map<String, Object> response = new HashMap<>();
            response.put("id", usuario.getId());
            response.put("nome", usuario.getNome());
            response.put("email", usuario.getEmail());
            response.put("cpf", usuario.getCpf());
            response.put("celular", usuario.getCelular());
            response.put("endereco", usuario.getEndereco());
            response.put("tipo", usuario.getTipo().name());
            response.put("ativo", usuario.isAtivo());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao buscar usuário");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint para alteração de senha
     */
    @PutMapping("/{id}/senha")
    public ResponseEntity<?> alterarSenha(
            @PathVariable Long id,
            @RequestBody Map<String, String> senhas) {
        try {
            String senhaAntiga = senhas.get("senhaAntiga");
            String senhaNova = senhas.get("senhaNova");

            usuarioService.alterarSenha(id, senhaAntiga, senhaNova);

            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Senha alterada com sucesso");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao alterar senha");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint para desativar usuário
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        try {
            usuarioService.desativar(id);

            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Usuário desativado com sucesso");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao desativar usuário");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        authHelper.logout(request);
        Map<String, String> response = new HashMap<>();
        response.put("mensagem", "Logout realizado com sucesso");
        return ResponseEntity.ok(response);
}
}