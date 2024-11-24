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
	
	public List<Produto> buscarProdutos(String nome, Categoria categoria, Integer quantidade) throws SQLException {
	    String sql = "SELECT * FROM produtos WHERE 1=1";
	    List<Object> parametros = new ArrayList<>();

	    if (nome != null && !nome.isEmpty()) {
	        sql += " AND nome_produto LIKE ?";
	        parametros.add("%" + nome + "%");
	    }

	    if (categoria != null) {
	        sql += " AND id_categoria = ?";
	        parametros.add(categoria.getId());
	    }

	    if (quantidade != null) {
	        sql += " AND quantidade_em_estoque = ?";
	        parametros.add(quantidade);
	    }

	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        for (int i = 0; i < parametros.size(); i++) {
	            stmt.setObject(i + 1, parametros.get(i));
	        }

	        try (ResultSet rs = stmt.executeQuery()) {
	            List<Produto> produtos = new ArrayList<>();
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
	            return produtos;
	        }
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
		String sql = "UPDATE produtos SET nome_produto = ?, descricao = ?, preco_compra = ?, preco_venda = ?, id_categoria = ? WHERE id_produto = ?";
		try(Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, produto.getNome());
			stmt.setString(2, produto.getDescricao());
			stmt.setDouble(3, produto.getPrecoCompra());
			stmt.setDouble(4, produto.getPrecoVenda());
			stmt.setInt(5, produto.getCategoria().getId());
			stmt.setInt(6, produto.getId());
			stmt.executeUpdate();
		}
	}
	
	public void excluirProduto(int idProduto) throws SQLException {
		// Excluir alertas associados ao produto
		String sqlExcluirAlertas = "DELETE FROM alertas_estoque WHERE id_produto = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmtExcluirAlertas = conn.prepareStatement(sqlExcluirAlertas)) {
			stmtExcluirAlertas.setInt(1, idProduto);
			stmtExcluirAlertas.executeUpdate();
		}
		
		// Excluir movimentações de estoque associadas ao produto 
		String sqlExcluirMovimentacoes = "DELETE FROM movimentacao_estoque WHERE id_produto = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmtExcluirMovimentacoes = conn.prepareStatement(sqlExcluirMovimentacoes)) {
			stmtExcluirMovimentacoes.setInt(1, idProduto);
			stmtExcluirMovimentacoes.executeUpdate();
		}
		// Excluir o produto 
		String sqlExcluirProduto = "DELETE FROM produtos WHERE id_produto = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmtExcluirProduto = conn.prepareStatement(sqlExcluirProduto)) {
			stmtExcluirProduto.setInt(1, idProduto);
			stmtExcluirProduto.executeUpdate();
		}
	}
	
	public void registrarEntrada(int idProduto, int quantidade) throws SQLException {
		String sql = "UPDATE produtos SET quantidade_em_estoque = quantidade_em_estoque + ? WHERE id_produto = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, quantidade);
			stmt.setInt(2,idProduto);
			stmt.executeUpdate();
		}
		registrarMovimentacaoEstoque(idProduto, quantidade, "Entrada");
	}
	
	public void registrarSaida(int idProduto, int quantidade) throws SQLException {
		String sql = "UPDATE produtos SET quantidade_em_estoque = quantidade_em_estoque - ? WHERE id_produto = ?";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, quantidade);
			stmt.setInt(2, idProduto);
			stmt.executeUpdate();
		}
		registrarMovimentacaoEstoque(idProduto, quantidade, "Saída");
	}
	
	private void registrarMovimentacaoEstoque(int idProduto, int quantidade, String tipo) throws SQLException {
		String sql = "INSERT INTO movimentacao_estoque (id_produto, quantidade, tipo) VALUES (?, ?, ?)";
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, idProduto);
			stmt.setInt(2, quantidade);
			stmt.setString(3, tipo);
			stmt.executeUpdate();
		}
	}
	
	// Métodos para geração do relatório
	public List<Produto> relatorioProdutosCadastrados() throws SQLException {
		String sql = "SELECT * FROM produtos";
		List<Produto> produtos = new ArrayList<>();
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) { 
				Produto produto = new Produto(rs.getString("nome_produto"), rs.getString("descricao"), rs.getInt("quantidade_em_estoque"),
						rs.getDouble("preco_compra"), rs.getDouble("preco_venda"), new Categoria(rs.getInt("id_categoria")));
				produto.setId(rs.getInt("id_produto"));
				produtos.add(produto);
			}
		}
		return produtos;
	}
	
	public List<String> relatorioMovimentacaoEstoque() throws SQLException {
		String sql = "SELECT * FROM movimentacao_estoque";
		List<String> movimentacoes = new ArrayList<>();
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) { 
			while (rs.next()) {
				movimentacoes.add("Produto ID: " + rs.getInt("id_produto") + " | Quantidade: " + rs.getInt("quantidade") + " | Tipo: " + rs.getString("tipo"));
			} 
		} 
		return movimentacoes;
	}
	
	public List<Produto> relatorioProdutosBaixoEstoque(int quantidadeMinima) throws SQLException {
		String sql = "SELECT * FROM produtos WHERE quantidade_em_estoque < ?";
		List<Produto> produtos = new ArrayList<>();
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, quantidadeMinima);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Produto produto = new Produto(rs.getString("nome_produto"), rs.getString("descricao"), rs.getInt("quantidade_em_estoque"),
							rs.getDouble("preco_compra"), rs.getDouble("preco_venda"), new Categoria(rs.getInt("id_categoria")));
					produto.setId(rs.getInt("id_produto"));
					produtos.add(produto);
				}
			}
		} return produtos;
	}
	
	public List<String> relatorioVendasELucro() throws SQLException {
		String sql = "SELECT * FROM produtos";
		List<String> relatorio = new ArrayList<>();
		try (Connection conn = DatabaseConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				double lucro = (rs.getDouble("preco_venda") - rs.getDouble("preco_compra")) * rs.getInt("quantidade_em_estoque");
				relatorio.add("Produto ID: " + rs.getInt("id_produto") + " | Nome: " + rs.getString("nome_produto") + " | Lucro: " + lucro);
			}
		}
		return relatorio;
	}
}
