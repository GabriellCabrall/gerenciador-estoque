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
}
