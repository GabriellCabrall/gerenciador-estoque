package dao;

import db.DatabaseConnection;
import models.Produto;
import models.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {
	public void cadastrarProduto(Produto produto) throws SQLException {
		String sql = "CALL sp_cadastrar_produto(?, ?, ?, ?, ?, ?)";
		try (Connection conn = DatabaseConnection.getConnection();
				CallableStatement stmt = conn.prepareCall(sql)) {
			stmt.setString(1, produto.getNome());
			stmt.setString(2, produto.getDescricao());
			stmt.setInt(3, produto.getQuantidadeEmEstoque());
			stmt.setDouble(4, produto.getPrecoCompra());
			stmt.setDouble(5, produto.getPrecoVenda());
			stmt.setInt(6, produto.getCategoria().getId());
			stmt.execute();
		}
	}
	
	public List<Produto> consultarTodosProdutos() throws SQLException {
		String sql = "SELECT * FROM produtos";
		List<Produto> produtos = new ArrayList<>();
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				Produto produto = new Produto(
						rs.getString("nome_produto"),
						rs.getString("descricao"),
						rs.getInt("quantidade_em_estoque"),
						rs.getDouble("preco_compra"),
						rs.getDouble("preco_venda"),
						new Categoria(rs.getInt("id_categoria"))
				);
				produto.setId(rs.getInt("id_produto"));
				produtos.add(produto);
			}
		}
		return produtos;
	}
	
	public List<Produto> consultarProdutosPorNome(String nome) throws SQLException {
		String sql = "CALL sp_consultar_produtos_por_nome(?)";
		List<Produto> produtos = new ArrayList<>();
		try(Connection conn = DatabaseConnection.getConnection();
				CallableStatement stmt = conn.prepareCall(sql)) {
			stmt.setString(1, nome);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Produto produto = new Produto(
						rs.getString("nome_produto"),
						rs.getString("descricao"),
						rs.getInt("quantidade_em_estoque"),
						rs.getDouble("preco_compra"),
						rs.getDouble("preco_venda"),
						new Categoria(rs.getInt("id_categoria"))
				);
				produto.setId(rs.getInt("id_produto"));
				produtos.add(produto);
			}
		}
		return produtos;
	}
	
	public void atualizarProduto(Produto produto) throws SQLException {
		String sql = "UPDATE produtos SET nome_produto = ?, descricao = ?, quantidade_em_estoque = ?, preco_compra = ?, preco_venda = ?, id_categoria = ? WHERE id_produto = ?";
		try(Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, produto.getNome());
			stmt.setString(2, produto.getDescricao());
			stmt.setInt(3, produto.getQuantidadeEmEstoque());
			stmt.setDouble(4, produto.getPrecoCompra());
			stmt.setDouble(5, produto.getPrecoVenda());
			stmt.setInt(6, produto.getCategoria().getId());
			stmt.setInt(7, produto.getId());
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Entrada de produto realizada com sucesso!");
			} else {
				System.out.println("Produto não encontrado");
			}
		}
	}
	
	public void excluirProduto(int idProduto) throws SQLException {
		String sql = "DELETE FROM produtos WHERE id_produto = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, idProduto);
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Produto excluido com sucesso");
			} else {
				System.out.println("Produto não encontrado");
			}
		}
	}
}
