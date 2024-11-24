package reports;

import dao.ProdutoDAO;
import models.Produto;
import java.sql.SQLException;
import java.util.List;

public class Relatorio {

    private ProdutoDAO produtoDAO;

    public Relatorio() {
        this.produtoDAO = new ProdutoDAO();
    }

    // Relatório de produtos cadastrados
    public List<Produto> gerarRelatorioProdutosCadastrados() throws SQLException {
        return produtoDAO.relatorioProdutosCadastrados();
    }

    // Relatório de movimentação de estoque
    public List<String> gerarRelatorioMovimentacaoEstoque() throws SQLException {
        return produtoDAO.relatorioMovimentacaoEstoque();
    }

    // Relatório de produtos com baixo estoque
    public List<Produto> gerarRelatorioProdutosBaixoEstoque(int quantidadeMinima) throws SQLException {
        return produtoDAO.relatorioProdutosBaixoEstoque(quantidadeMinima);
    }

    // Relatório de vendas e lucro
    public List<String> gerarRelatorioVendasELucro() throws SQLException {
        return produtoDAO.relatorioVendasELucro();
    }
}
