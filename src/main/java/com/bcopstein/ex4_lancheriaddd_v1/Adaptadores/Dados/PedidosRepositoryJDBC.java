package com.bcopstein.ex4_lancheriaddd_v1.Adaptadores.Dados;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados.PedidosRepository;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;

@Repository
public class PedidosRepositoryJDBC implements PedidosRepository {

    @Override
public Optional<Pedido> findByCodigo(long codigo) {
    // Apenas para testes
    Pedido pedido = new Pedido(
        codigo,
        null, // cliente (pode ser null por enquanto)
        java.time.LocalDateTime.now(),
        new java.util.ArrayList<>(), // lista de itens
        Pedido.Status.APROVADO, // status inicial
        100.0, // valorItens
        0.0,   // desconto
        10.0,  // imposto
        110.0  // valorTotal
    );
    return Optional.of(pedido);
}

}
