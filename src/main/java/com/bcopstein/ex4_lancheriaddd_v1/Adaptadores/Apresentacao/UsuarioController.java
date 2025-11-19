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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bcopstein.ex4_lancheriaddd_v1.Configuracao.AuthHelper;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dto.UsuarioRegistroDto;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Usuario;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.DescontoService;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin("*")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final AuthHelper authHelper;
    private final DescontoService descontoService;

    public UsuarioController(UsuarioService usuarioService,
                             AuthHelper authHelper,
                             DescontoService descontoService) {
        this.usuarioService = usuarioService;
        this.authHelper = authHelper;
        this.descontoService = descontoService;
    }

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
            response.put("mensagem", "Usuário registrado");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao registrar usuário");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

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
            authHelper.login(request, usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("id", usuario.getId());
            response.put("nome", usuario.getNome());
            response.put("email", usuario.getEmail());
            response.put("tipo", usuario.getTipo().name());
            response.put("endereco", usuario.getEndereco());
            response.put("mensagem", "Login realizado");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao realizar login");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        try {
            usuarioService.desativar(id);

            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Usuário deletado");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao deletar o usuário");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        authHelper.logout(request);
        Map<String, String> response = new HashMap<>();
        response.put("mensagem", "Logout realizado");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/descontos/ativo/{codigo}")
    public ResponseEntity<?> definirDescontoAtivo(@PathVariable String codigo,
                                                  HttpServletRequest request) {
        try {
            Usuario usuario = authHelper.getUsuarioAutenticado(request);
            if (!usuario.isMaster()) {
                Map<String, String> error = new HashMap<>();
                error.put("erro", "Apenas usuários MASTER podem definir o desconto");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            descontoService.definirDescontoAtivo(codigo);

            Map<String, Object> response = new HashMap<>();
            response.put("mensagem", "Desconto atualizado");
            response.put("descontoAtivo", descontoService.getDescontoAtivoCodigo());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao definir desconto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/descontos/ativo")
    public ResponseEntity<?> consultarDescontoAtivo() {
        Map<String, Object> response = new HashMap<>();
        response.put("descontoAtivo", descontoService.getDescontoAtivoCodigo());
        return ResponseEntity.ok(response);
    }
}