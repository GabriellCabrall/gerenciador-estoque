package dao;

import db.DatabaseConnection;
import models.Categoria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {
	public void cadastrarCategoria(Categoria categoria) throws SQLException {
		String sql = "CALL sp_cadastrar_categoria(?, ?)";
		try (Connection conn = DatabaseConnection.getConnection();
				CallableStatement stmt = conn.prepareCall(sql)) {
			stmt.setString(1, categoria.getNome());
			stmt.setString(2, categoria.getDescricao());
			stmt.execute();
		}
	}
	
	public List<Categoria> consultarCategorias(String filtro) throws SQLException {
		String sql = "SELECT * FROM categorias WHERE nome_categoria LIKE ?";
		List<Categoria> categorias = new ArrayList<>();
		try(Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, "%" + filtro + "%");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Categoria categoria = new Categoria(
						rs.getString("nome_categoria"),
						rs.getString("descricao_categoria")
				);
				categoria.setId(rs.getInt("id_categoria"));
				categorias.add(categoria);
			}
		}
		return categorias;
	}
	
	public void excluirCategoria(int idCategoria) throws SQLException {
	    Connection conn = null;
	    try {
	        conn = DatabaseConnection.getConnection();
	        conn.setAutoCommit(false);

	        // Excluir alertas de estoque associados aos produtos da categoria
	        String sqlExcluirAlertas = "DELETE ae FROM alertas_estoque ae JOIN produtos p ON ae.id_produto = p.id_produto WHERE p.id_categoria = ?";
	        try (PreparedStatement stmtExcluirAlertas = conn.prepareStatement(sqlExcluirAlertas)) {
	            stmtExcluirAlertas.setInt(1, idCategoria);
	            stmtExcluirAlertas.executeUpdate();
	        }

	        // Excluir movimentações de estoque associadas aos produtos da categoria
	        String sqlExcluirMovimentacoes = "DELETE me FROM movimentacao_estoque me JOIN produtos p ON me.id_produto = p.id_produto WHERE p.id_categoria = ?";
	        try (PreparedStatement stmtExcluirMovimentacoes = conn.prepareStatement(sqlExcluirMovimentacoes)) {
	            stmtExcluirMovimentacoes.setInt(1, idCategoria);
	            stmtExcluirMovimentacoes.executeUpdate();
	        }

	        // Excluir produtos associados à categoria
	        String sqlExcluirProdutos = "DELETE FROM produtos WHERE id_categoria = ?";
	        try (PreparedStatement stmtExcluirProdutos = conn.prepareStatement(sqlExcluirProdutos)) {
	            stmtExcluirProdutos.setInt(1, idCategoria);
	            stmtExcluirProdutos.executeUpdate();
	        }

	        // Excluir a categoria
	        String sqlExcluirCategoria = "DELETE FROM categorias WHERE id_categoria = ?";
	        try (PreparedStatement stmtExcluirCategoria = conn.prepareStatement(sqlExcluirCategoria)) {
	            stmtExcluirCategoria.setInt(1, idCategoria);
	            int rowsAffected = stmtExcluirCategoria.executeUpdate();
	            if (rowsAffected > 0) {
	                System.out.println("Categoria excluída com sucesso");
	            } else {
	                System.out.println("Categoria não encontrada");
	            }
	        }

	        conn.commit();
	    } catch (SQLException e) {
	        if (conn != null) {
	            conn.rollback();
	        }
	        throw e;
	    } finally {
	        if (conn != null) {
	            conn.setAutoCommit(true);
	            conn.close();
	        }
	    }
	}

	
	public void editarCategoria (Categoria categoria) throws SQLException {
		String sql = "UPDATE categorias SET nome_categoria = ?, descricao_categoria = ? WHERE id_categoria = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, categoria.getNome());
			stmt.setString(2, categoria.getDescricao());
			stmt.setInt(3, categoria.getId());
			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Categoria editada com sucesso");
			} else {
				System.out.println("Categoria não encontrada");
			}
		}
	}
}
