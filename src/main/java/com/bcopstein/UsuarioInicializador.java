package com.bcopstein;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.UsuarioService;


@Component
public class UsuarioInicializador implements CommandLineRunner {
    private final UsuarioService usuarioService;

    public UsuarioInicializador(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) throws Exception {
        usuarioService.criarUsuarioMasterSeNaoExistir();
    }
}