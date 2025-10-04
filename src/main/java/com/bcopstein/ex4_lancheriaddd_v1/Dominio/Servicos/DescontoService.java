package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Cliente;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemPedido;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
public class DescontoService {

    public double calcularDesconto(Cliente cliente, List<ItemPedido> itens, LocalDate hoje){
        boolean flag = temDesconto(cliente, hoje);
        if (!flag) return 0.0;
        double subtotal = itens.stream().mapToDouble(i -> i.getItem().getPreco() * i.getQuantidade()).sum();
        return subtotal * 0.07;
    }

    protected boolean temDesconto(Cliente cliente, LocalDate hoje){

        // fzr

        return false;
    }
}
