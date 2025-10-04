package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Servicos;

import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.ItemPedido;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstoqueService {

    public boolean verificarDisponibilidade(List<ItemPedido> itens){
        return true;
    }
}
