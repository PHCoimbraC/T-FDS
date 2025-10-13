package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import org.springframework.stereotype.Service;

@Service
public class ImpostoService {

    public double calcularImpostos(double baseCalculo){
        return baseCalculo * 0.10;
    }
}