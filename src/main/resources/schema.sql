create table if not exists clientes(
  cpf varchar(15) not null primary key,
  nome varchar(100) not null,
  celular varchar(20) not null,
  endereco varchar(255) not null,
  email varchar(255) not null
);

create table if not exists ingredientes (
  id bigint primary key,
  descricao varchar(255) not null
);

CREATE TABLE IF NOT EXISTS ITENS_ESTOQUE (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ingrediente_id BIGINT NOT NULL,
    quantidade INT NOT NULL,
    FOREIGN KEY (ingrediente_id) REFERENCES ingredientes(id)
);

-- Tabela Receita
create table if not exists receitas (
  id bigint primary key,
  titulo varchar(255) not null
);

-- Tabela de relacionamento entre Receita e Ingrediente
create table if not exists receita_ingrediente (
  receita_id bigint not null,
  ingrediente_id bigint not null,
  primary key (receita_id, ingrediente_id),
  foreign key (receita_id) references receitas(id),
  foreign key (ingrediente_id) references ingredientes(id)
);

-- Tabela de Produtos
create table if not exists produtos (
  id bigint primary key,
  descricao varchar(255) not null,
  preco bigint
);

-- Tabela de relacionamento entre Produto e Receita
create table if not exists produto_receita (
  produto_id bigint not null,
  receita_id bigint not null,
  primary key (produto_id,receita_id),
  foreign key (produto_id) references produtos(id),
  foreign key (receita_id) references receitas(id)
);

-- Tabela de Cardapios
create table if not exists cardapios (
  id bigint primary key,
  titulo varchar(255) not null
);

-- Tabela de relacionamento entre Cardapio e Produto
create table if not exists cardapio_produto (
  cardapio_id bigint not null,
  produto_id bigint not null,
  primary key (cardapio_id,produto_id),
  foreign key (cardapio_id) references cardapios(id),
  foreign key (produto_id) references produtos(id)
);

create table if not exists pedidos (
  id bigint primary key,
  email_cliente varchar(255) not null,
  endereco_entrega varchar(255) not null,
  status varchar(30) not null,
  data_pagamento timestamp null,
  valor_itens double,
  desconto double,
  impostos double,
  valor_total double,
  criado_em timestamp not null
);

create table if not exists pedido_itens (
  pedido_id bigint not null,
  produto_id bigint not null,
  quantidade int not null,
  primary key (pedido_id, produto_id),
  foreign key (pedido_id) references pedidos(id),
  foreign key (produto_id) references produtos(id)
);

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14),
    celular VARCHAR(20),
    endereco VARCHAR(500),
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    data_cadastro TIMESTAMP NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    
    CONSTRAINT uk_usuarios_email UNIQUE (email),
    CONSTRAINT uk_usuarios_cpf UNIQUE (cpf)
);

-- Tabela de configurações do sistema
CREATE TABLE IF NOT EXISTS configuracoes (
    chave VARCHAR(100) PRIMARY KEY,
    valor VARCHAR(255) NOT NULL
);

-- Definir cardápio inicial como ativo (ID 1)
MERGE INTO configuracoes (chave, valor) 
KEY(chave) 
VALUES ('cardapio_ativo_id', '1');

CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_cpf ON usuarios(cpf);
CREATE INDEX idx_usuarios_tipo ON usuarios(tipo);


