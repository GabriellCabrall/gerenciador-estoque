package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import reports.Relatorio;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import dao.ProdutoDAO;
import dao.CategoriaDAO;
import models.Produto;
import models.Categoria;
import utils.Quintuple;
import utils.Sextuple;

public class ProdutosController {

    @FXML
    private ListView<Produto> produtoListView;
    
    @FXML
    private TextField filtroNomeField;
    
    @FXML
    private ComboBox<Categoria> filtroCategoriaComboBox;
    
    @FXML
    private TextField filtroQuantidadeField; 

    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private CategoriaDAO categoriaDAO = new CategoriaDAO();
    private Relatorio relatorio = new Relatorio();

    @FXML
    public void initialize() {
        carregarProdutos();
        carregarCategorias();
    }
    
    private void carregarCategorias() {
    	try {
    		List<Categoria> categorias = categoriaDAO.consultarCategorias("");
    		filtroCategoriaComboBox.getItems().setAll(categorias); 
    	} catch (SQLException e) { 
    		e.printStackTrace();
    	}
    }

    private void carregarProdutos() {
        try {
            List<Produto> produtos = produtoDAO.relatorioProdutosCadastrados();
            produtoListView.getItems().setAll(produtos);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao carregar produtos", e.getMessage());
        }
    }
    
    @FXML 
    private void handleBuscarProdutos() {
    	String nome = filtroNomeField.getText().trim();
    	Categoria categoria = filtroCategoriaComboBox.getValue();
    	String quantidadeStr = filtroQuantidadeField.getText().trim();
    	Integer quantidade = null;
    	if (!quantidadeStr.isEmpty()) {
    		try {
    			quantidade = Integer.parseInt(quantidadeStr);
    		} catch (NumberFormatException e) {
    			mostrarAlerta("Erro de Formato", "Certifique-se de que a quantidade em estoque é um número válido.");
    			return;
    		}
    	}
    	
    	try {
    		List<Produto> produtosFiltrados = produtoDAO.buscarProdutos(nome, categoria, quantidade);
    		produtoListView.getItems().setAll(produtosFiltrados);
    	} catch (SQLException e) {
    		e.printStackTrace();
    		mostrarAlerta("Erro ao buscar produtos", e.getMessage());
    	}
    }

    @FXML
    private void handleAdicionarProduto() {
        Dialog<Sextuple<String, String, Integer, Double, Double, Categoria>> dialog = new Dialog<>();
        dialog.setTitle("Adicionar Produto");
        dialog.setHeaderText("Insira os dados do novo produto");

        // Configurando os botões do diálogo
        ButtonType adicionarButtonType = new ButtonType("Adicionar", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(adicionarButtonType, ButtonType.CANCEL);

        // Criando campos de texto para os dados do produto
        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome do Produto");
        TextField descricaoField = new TextField();
        descricaoField.setPromptText("Descrição do Produto");
        TextField quantidadeField = new TextField();
        quantidadeField.setPromptText("Quantidade em Estoque");
        TextField precoCompraField = new TextField();
        precoCompraField.setPromptText("Preço de Compra");
        TextField precoVendaField = new TextField();
        precoVendaField.setPromptText("Preço de Venda");

        // Criando ComboBox para seleção de categoria
        ComboBox<Categoria> categoriaComboBox = new ComboBox<>();
        try {
            List<Categoria> categorias = categoriaDAO.consultarCategorias("");
            categoriaComboBox.getItems().setAll(categorias);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        categoriaComboBox.setPromptText("Selecione a Categoria");

        // Colocando os campos em um layout
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(new VBox(8, nomeField, descricaoField, quantidadeField, precoCompraField, precoVendaField, categoriaComboBox));

        // Convertendo o resultado para os dados do produto quando o botão Adicionar for clicado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == adicionarButtonType) {
                try {
                    String nome = nomeField.getText();
                    String descricao = descricaoField.getText();
                    int quantidade = Integer.parseInt(quantidadeField.getText());
                    double precoCompra = Double.parseDouble(precoCompraField.getText());
                    double precoVenda = Double.parseDouble(precoVendaField.getText());
                    Categoria categoria = categoriaComboBox.getValue();

                    return new Sextuple<>(nome, descricao, quantidade, precoCompra, precoVenda, categoria);
                } catch (NumberFormatException e) {
                    mostrarAlerta("Erro de Formato", "Certifique-se de que todos os campos estão preenchidos corretamente.");
                    return null;
                }
            }
            return null;
        });

        Optional<Sextuple<String, String, Integer, Double, Double, Categoria>> result = dialog.showAndWait();
        result.ifPresent(dadosProduto -> {
            String nome = dadosProduto.getFirst();
            String descricao = dadosProduto.getSecond();
            int quantidade = dadosProduto.getThird();
            double precoCompra = dadosProduto.getFourth();
            double precoVenda = dadosProduto.getFifth();
            Categoria categoria = dadosProduto.getSixth();
            try {
                Produto novoProduto = new Produto(nome, descricao, quantidade, precoCompra, precoVenda, categoria); 
                produtoDAO.cadastrarProduto(novoProduto);
                carregarProdutos();
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Erro ao adicionar produto", e.getMessage());
            }
        });
    }


    @FXML
    private void handleEditarProduto() {
        Produto selecionado = produtoListView.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            Dialog<Quintuple<String, String, Double, Double, Categoria>> dialog = new Dialog<>();
            dialog.setTitle("Editar Produto");
            dialog.setHeaderText("Edite os dados do produto");

            // Configurando os botões do diálogo
            ButtonType editarButtonType = new ButtonType("Salvar", ButtonType.OK.getButtonData());
            dialog.getDialogPane().getButtonTypes().addAll(editarButtonType, ButtonType.CANCEL);

            // Criando campos de texto para os dados do produto
            TextField nomeField = new TextField(selecionado.getNome());
            nomeField.setPromptText("Nome do Produto");
            TextField descricaoField = new TextField(selecionado.getDescricao());
            descricaoField.setPromptText("Descrição do Produto");
            TextField precoCompraField = new TextField(String.valueOf(selecionado.getPrecoCompra()));
            precoCompraField.setPromptText("Preço de Compra");
            TextField precoVendaField = new TextField(String.valueOf(selecionado.getPrecoVenda()));
            precoVendaField.setPromptText("Preço de Venda");

            // Criando campos de texto para exibir a quantidade em estoque atual
            TextField quantidadeField = new TextField(String.valueOf(selecionado.getQuantidadeEmEstoque()));
            quantidadeField.setEditable(false); // Campo não editável


            // Criando ComboBox para seleção de nova categoria
            ComboBox<Categoria> categoriaComboBox = new ComboBox<>();
            try {
                List<Categoria> categorias = categoriaDAO.consultarCategorias("");
                categoriaComboBox.getItems().setAll(categorias);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Criando botões para entrada e saída de produtos
            Button entradaButton = new Button("Adicionar Entrada (+1)");
            Button saidaButton = new Button("Registrar Saída (-1)");

            // Definindo ações para os botões
            entradaButton.setOnAction(event -> {
                try {
                    produtoDAO.registrarEntrada(selecionado.getId(), 1);
                    selecionado.setQuantidadeEmEstoque(selecionado.getQuantidadeEmEstoque() + 1);
                    quantidadeField.setText(String.valueOf(selecionado.getQuantidadeEmEstoque()));
                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro ao registrar entrada", e.getMessage());
                }
            });

            saidaButton.setOnAction(event -> {
                try {
                    produtoDAO.registrarSaida(selecionado.getId(), 1);
                    selecionado.setQuantidadeEmEstoque(selecionado.getQuantidadeEmEstoque() - 1);
                    quantidadeField.setText(String.valueOf(selecionado.getQuantidadeEmEstoque()));
                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro ao registrar saída", e.getMessage());
                }
            });

            // Colocando os campos em um layout
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.setContent(new VBox(8, 
                nomeField, 
                descricaoField, 
                precoCompraField, 
                precoVendaField, 
                new Label("Quantidade em Estoque:"), 
                quantidadeField, 
                new Label("Selecionar Nova Categoria:"), 
                categoriaComboBox,
                new Label("Operações de Estoque:"),
                entradaButton,
                saidaButton
            ));

            // Convertendo o resultado para os dados do produto quando o botão Salvar for clicado
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == editarButtonType) {
                    try {
                        String nome = nomeField.getText();
                        String descricao = descricaoField.getText();
                        double precoCompra = Double.parseDouble(precoCompraField.getText());
                        double precoVenda = Double.parseDouble(precoVendaField.getText());
                        Categoria novaCategoria = categoriaComboBox.getValue();

                        return new Quintuple<>(nome, descricao, precoCompra, precoVenda, novaCategoria);
                    } catch (NumberFormatException e) {
                        mostrarAlerta("Erro de Formato", "Certifique-se de que todos os campos estão preenchidos corretamente.");
                        return null;
                    }
                }
                return null;
            });

            Optional<Quintuple<String, String, Double, Double, Categoria>> result = dialog.showAndWait();
            result.ifPresent(dadosProduto -> {
                String nome = dadosProduto.getFirst();
                String descricao = dadosProduto.getSecond();
                double precoCompra = dadosProduto.getThird();
                double precoVenda = dadosProduto.getFourth();
                Categoria novaCategoria = dadosProduto.getFifth();
                try {
                    selecionado.setNome(nome);
                    selecionado.setDescricao(descricao);
                    selecionado.setPrecoCompra(precoCompra);
                    selecionado.setPrecoVenda(precoVenda);
                    selecionado.setCategoria(novaCategoria);
                    produtoDAO.atualizarProduto(selecionado);
                    carregarProdutos();
                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro ao editar produto", e.getMessage());
                }
            });
        } else {
            mostrarAlerta("Nenhum Produto Selecionado", "Por favor, selecione um produto para editar.");
        }
    }



    @FXML
    private void handleExcluirProduto() {
        Produto selecionado = produtoListView.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Exclusão");
            alert.setHeaderText(null);
            alert.setContentText("Tem certeza que deseja excluir o produto selecionado?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    produtoDAO.excluirProduto(selecionado.getId());
                    carregarProdutos();
                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro ao excluir produto", e.getMessage());
                }
            }
        } else {
            mostrarAlerta("Nenhum Produto Selecionado", "Por favor, selecione um produto para excluir.");
        }
    }

    @FXML
    private void handleVoltar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainMenu.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) produtoListView.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    private void mostrarAlertaSucesso(String titulo, String mensagem) {
    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle(titulo);
    	alert.setHeaderText(null);
    	alert.setContentText(mensagem);
    	alert.showAndWait();
    }
    
    @FXML
    private void handleGerarRelatorioCompleto() {
        try {
            List<Produto> produtosCadastrados = relatorio.gerarRelatorioProdutosCadastrados();
            List<String> movimentacaoEstoque = relatorio.gerarRelatorioMovimentacaoEstoque();
            List<Produto> produtosBaixoEstoque = relatorio.gerarRelatorioProdutosBaixoEstoque(10); // Considerando 10 como quantidade mínima
            List<String> vendasELucro = relatorio.gerarRelatorioVendasELucro();
            String filePath = "C:/Users/PC/Documents/relatorios/relatorio_completo.csv";
            exportarRelatorioCompletoParaCSV(produtosCadastrados, movimentacaoEstoque, produtosBaixoEstoque, vendasELucro, filePath);
            mostrarAlertaSucesso("Relatório Completo Gerado", "O relatório completo foi gerado com sucesso em:" + filePath);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao gerar relatório completo", e.getMessage());
        }
    }

    private void exportarRelatorioCompletoParaCSV(List<Produto> produtosCadastrados, List<String> movimentacaoEstoque,
                                                   List<Produto> produtosBaixoEstoque, List<String> vendasELucro, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Relatório de produtos cadastrados
            writer.append("Relatorio de Produtos Cadastrados\n");
            writer.append("ID,Nome,Descricaoo,Quantidade,Preco de Compra,Preco de Venda,Categoria\n");
            for (Produto produto : produtosCadastrados) {
                writer.append(String.valueOf(produto.getId()));
                writer.append(",");
                writer.append(produto.getNome());
                writer.append(",");
                writer.append(produto.getDescricao());
                writer.append(",");
                writer.append(String.valueOf(produto.getQuantidadeEmEstoque()));
                writer.append(",");
                writer.append(String.valueOf(produto.getPrecoCompra()));
                writer.append(",");
                writer.append(String.valueOf(produto.getPrecoVenda()));
                writer.append(",");
                writer.append(produto.getCategoria() != null ? produto.getCategoria().getNome() : "Sem Categoria");
                writer.append("\n");
            }

            // Relatório de movimentação de estoque
            writer.append("\nRelatorio de Movimentacao de Estoque\n");
            writer.append("Movimentacao\n");
            for (String movimentacao : movimentacaoEstoque) {
                writer.append(movimentacao);
                writer.append("\n");
            }

            // Relatório de produtos com baixo estoque
            writer.append("\nRelatorio de Produtos com Baixo Estoque\n");
            writer.append("ID,Nome,Descricao,Quantidade,Preco de Compra,Preco de Venda,Categoria\n");
            for (Produto produto : produtosBaixoEstoque) {
                writer.append(String.valueOf(produto.getId()));
                writer.append(",");
                writer.append(produto.getNome());
                writer.append(",");
                writer.append(produto.getDescricao());
                writer.append(",");
                writer.append(String.valueOf(produto.getQuantidadeEmEstoque()));
                writer.append(",");
                writer.append(String.valueOf(produto.getPrecoCompra()));
                writer.append(",");
                writer.append(String.valueOf(produto.getPrecoVenda()));
                writer.append(",");
                writer.append(produto.getCategoria() != null ? produto.getCategoria().getNome() : "Sem Categoria");
                writer.append("\n");
            }

            // Relatório de vendas e lucro
            writer.append("\nRelatorio de Vendas e Lucro\n");
            writer.append("Vendas e Lucro\n");
            for (String venda : vendasELucro) {
                writer.append(venda);
                writer.append("\n");
            }
        }
    }
}