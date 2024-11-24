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

ALTER TABLE produtos
MODIFY quantidade_em_estoque INT NOT NULL DEFAULT 10;

CREATE TABLE movimentacao_estoque (
    id_movimentacao INT AUTO_INCREMENT PRIMARY KEY,
    id_produto INT,
    quantidade INT NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    data_movimentacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_produto) REFERENCES produtos(id_produto)
);

CREATE TABLE alertas_estoque (
    id_alerta INT AUTO_INCREMENT PRIMARY KEY,
    id_produto INT,
    nome_produto VARCHAR(100),
    mensagem_alerta TEXT,
    data_alerta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_produto) REFERENCES produtos(id_produto)
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

DELIMITER $$

CREATE TRIGGER VerificarEstoqueBaixo
AFTER UPDATE ON produtos
FOR EACH ROW
BEGIN
    DECLARE quantidade_minima INT DEFAULT 10; -- Define a quantidade mínima para alerta
    DECLARE nome_produto VARCHAR(100);

    -- Consulta o nome do produto
    SELECT nome_produto INTO nome_produto FROM produtos WHERE id_produto = NEW.id_produto;

    IF NEW.quantidade_em_estoque < quantidade_minima THEN
        IF nome_produto IS NULL THEN
            SET nome_produto = 'Desconhecido';
        END IF;
        SET @msg = CONCAT('Alerta: Quantidade em estoque do produto ', nome_produto, ' está abaixo do mínimo especificado.');
        INSERT INTO alertas_estoque (id_produto, nome_produto, mensagem_alerta) VALUES (NEW.id_produto, nome_produto, @msg);
    END IF;
END$$

DELIMITER ;

Select * from produtos;
select * from categorias;
select * from alertas_estoque;
