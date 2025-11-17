package com.bcopstein.ex4_lancheriaddd_v1.Tests;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos.ImpostoService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ImpostoServiceTest {

    @Test
    void deveCalcularDezPorCentoDaBase() {
        ImpostoService service = new ImpostoService();

        assertEquals(0.0, service.calcularImpostos(0.0), 0.0001);
        assertEquals(10.0, service.calcularImpostos(100.0), 0.0001);
        assertEquals(25.05, service.calcularImpostos(250.5), 0.0001);
        assertEquals(1000000.0, service.calcularImpostos(10000000.0), 0.0001);
    }
}