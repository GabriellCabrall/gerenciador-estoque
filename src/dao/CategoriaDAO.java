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
	
	public void excluirCategoria(int idCAtegoria) throws SQLException {
		String sqlProdutos = "DELETE FROM produtos WHERE id_categoria = ?";
		String sqlCategoria = "DELETE FROM categorias WHERE id_categoria = ?";
		try (Connection conn = DatabaseConnection.getConnection()) {
			// excluindo produtos
			try (PreparedStatement stmtProdutos = conn.prepareStatement(sqlProdutos)) {
				stmtProdutos.setInt(1, idCAtegoria);
				stmtProdutos.executeUpdate();
			}
			
			// excluindo a categoria
			try (PreparedStatement stmtCategoria = conn.prepareStatement(sqlCategoria)) {
				stmtCategoria.setInt(1, idCAtegoria);
				int rowsAffected = stmtCategoria.executeUpdate();
				if (rowsAffected > 0) {
					System.out.println("Categoria excluida com sucesso");
				} else {
					System.out.println("Categoria não encontrada");
				}
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
