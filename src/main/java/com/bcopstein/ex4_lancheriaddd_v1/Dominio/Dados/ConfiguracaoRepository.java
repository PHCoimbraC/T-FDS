package com.bcopstein.ex4_lancheriaddd_v1.Dominio.Dados;

   import java.util.Optional;

   public interface ConfiguracaoRepository {
       Optional<String> getValor(String chave);
       void setValor(String chave, String valor);
   }