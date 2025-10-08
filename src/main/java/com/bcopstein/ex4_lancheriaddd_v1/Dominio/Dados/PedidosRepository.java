package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados;

import java.util.Optional;
import com.bcopstein.ex4_lancheriaddd_v1.Dominio.Entidades.Pedido;

public interface PedidosRepository {
    Optional<Pedido> findByCodigo(long codigo);
}
