package main;

import dao.CategoriaDAO;
import dao.ProdutoDAO;
import db.DatabaseConnection;
import models.Categoria;
import models.Produto;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Verificando conexão com o banco de dados
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                System.out.println("Conexão com o banco de dados bem sucedida!");
            } else {
                System.out.println("Falha na conexão com o banco de dados!");
                return; // Sai do programa se não houver conexão
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return; // Sai do programa se ocorrer uma exceção
        }

        CategoriaDAO categoriaDAO = new CategoriaDAO();
        ProdutoDAO produtoDAO = new ProdutoDAO();

        // Testando criação de categoria
        try {
            Categoria categoria = new Categoria("Eletrônicos", "Produtos eletrônicos diversos");
            categoriaDAO.cadastrarCategoria(categoria);
            System.out.println("Categoria criada com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Testando criação de produto
        try {
            // Primeiro, precisamos definir um ID de categoria existente no banco de dados
            int idCategoria = 1; // Supondo que a categoria tenha o ID 1 no banco de dados
            Categoria categoria = new Categoria(idCategoria);

            Produto produto = new Produto(
                "Smartphone",
                "Smartphone de última geração",
                10,
                1200.00,
                1500.00,
                categoria
            );

            produtoDAO.cadastrarProduto(produto);
            System.out.println("Produto criado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
