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
            Categoria categoria = new Categoria("Alimentos melhores", "Produtos de alimentos melhores diversos");
            categoriaDAO.cadastrarCategoria(categoria);
            System.out.println("Categoria criada com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Testando criação de produto
        try {
            //definir um ID de categoria existente no banco de dados
            int idCategoria = 5; // Supondo que a categoria tenha o ID 1 no banco de dados
            Categoria categoria = new Categoria(idCategoria);

            Produto produto = new Produto(
                "Sucrilhos",
                "Sucrilhos Kellogs",
                10,
                10.00,
                15.00,
                categoria
            );

            produtoDAO.cadastrarProduto(produto);
            System.out.println("Produto criado com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // testando alteração de produto
        try {
        	int idCategoria = 5;
        	Categoria categoria = new Categoria(idCategoria);
        	
        	Produto produtoAtualizado = new Produto(
        			"sucrilhos",
        			"Sucrilhos kellogs melhor",
        			15,
        			12.00,
        			18.00,
        			categoria
        	);
        	produtoAtualizado.setId(4);
        	
        	produtoDAO.atualizarProduto(produtoAtualizado);
        } catch (SQLException e){
        	e.printStackTrace();
        }
        
        // Testando exclusão de produtos
        try {
        	int idProduto = 4;
        	produtoDAO.excluirProduto(idProduto);
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        
        // testando exclusão de categoria
        try {
        	int idCategoria = 5;
        	categoriaDAO.excluirCategoria(idCategoria);
        } catch (SQLException e) {
        	e.printStackTrace();
        }
        
        // testando edição de categoria
        try {
        	Categoria categoriaAtualizada = new Categoria("Comidinhas", "Comidinhas boas!");
        	categoriaAtualizada.setId(4);
        	
        	categoriaDAO.editarCategoria(categoriaAtualizada);
        } catch (SQLException e) {
        	e.printStackTrace();
        }
    }
}
