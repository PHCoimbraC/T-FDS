package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import org.springframework.stereotype.Service;

@Service
public class ImpostoService {
    // 10% sobre o valor dos itens (após desconto)
    public double calcularImpostos(double baseCalculo){
        return baseCalculo * 0.10;
    }
}