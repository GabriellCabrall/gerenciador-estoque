CREATE DATABASE CadastroProdutos;

CREATE TABLE categorias (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nome_categoria VARCHAR(100) NOT NULL,
    descricao_categoria TEXT
);

CREATE TABLE produtos (
    id_produto INT AUTO_INCREMENT PRIMARY KEY,
    nome_produto VARCHAR(100) NOT NULL,
    descricao TEXT,
    quantidade_em_estoque INT NOT NULL,
    preco_compra DECIMAL(10, 2) NOT NULL,
    preco_venda DECIMAL(10, 2) NOT NULL,
    id_categoria INT,
    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
);

DELIMITER $$

CREATE PROCEDURE sp_cadastrar_categoria (
    IN nome_categoria VARCHAR(100),
    IN descricao_categoria TEXT
)
BEGIN
    INSERT INTO categorias (nome_categoria, descricao_categoria)
    VALUES (nome_categoria, descricao_categoria);
END$$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_cadastrar_produto (
    IN nome_produto VARCHAR(100),
    IN descricao TEXT,
    IN quantidade_em_estoque INT,
    IN preco_compra DECIMAL(10, 2),
    IN preco_venda DECIMAL(10, 2),
    IN id_categoria INT
)
BEGIN
    INSERT INTO produtos (nome_produto, descricao, quantidade_em_estoque, preco_compra, preco_venda, id_categoria)
    VALUES (nome_produto, descricao, quantidade_em_estoque, preco_compra, preco_venda, id_categoria);
END$$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_consultar_produtos_por_nome (
    IN nome_produto VARCHAR(100)
)
BEGIN
    SELECT * FROM produtos 
    WHERE nome_produto LIKE CONCAT('%', nome_produto, '%');
END$$

DELIMITER ;

CALL sp_cadastrar_categoria('Eletrônicos', 'Produtos eletrônicos diversos');
SELECT * from categorias;
CALL sp_cadastrar_produto('Celular', 'Smartphone de última geração', 50, 1000.00, 1500.00, 1);
SELECT * FROM produtos;
CALL sp_consultar_produtos_por_nome('Celular');


