package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import dao.CategoriaDAO;
import models.Categoria;
import java.io.IOException;

public class CategoriasController {

    @FXML
    private ListView<Categoria> categoriaListView;

    private CategoriaDAO categoriaDAO = new CategoriaDAO();

    @FXML
    public void initialize() {
        carregarCategorias();
    }

    private void carregarCategorias() {
        try {
            List<Categoria> categorias = categoriaDAO.consultarCategorias("");
            categoriaListView.getItems().setAll(categorias);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro ao carregar categorias", e.getMessage());
        }
    }

    @FXML
    private void handleAdicionarCategoria() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Adicionar Categoria");
        dialog.setHeaderText("Insira o nome e a descrição da nova categoria");

        // Configurando os botões do diálogo
        ButtonType adicionarButtonType = new ButtonType("Adicionar", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(adicionarButtonType, ButtonType.CANCEL);

        // Criando campos de texto para nome e descrição
        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome da Categoria");
        TextField descricaoField = new TextField();
        descricaoField.setPromptText("Descrição da Categoria");

        // Colocando os campos em um layout
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(new VBox(8, nomeField, descricaoField));

        // Convertendo o resultado para nome e descrição quando o botão Adicionar for clicado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == adicionarButtonType) {
                return new Pair<>(nomeField.getText(), descricaoField.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(nomeDescricao -> {
            String nome = nomeDescricao.getKey();
            String descricao = nomeDescricao.getValue();
            try {
                Categoria novaCategoria = new Categoria(nome, descricao);
                categoriaDAO.cadastrarCategoria(novaCategoria);
                carregarCategorias();
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Erro ao adicionar categoria", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEditarCategoria() {
        Categoria selecionada = categoriaListView.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Editar Categoria");
            dialog.setHeaderText("Edite o nome e a descrição da categoria");

            // Configurando os botões do diálogo
            ButtonType editarButtonType = new ButtonType("Salvar",ButtonType.OK.getButtonData());
            dialog.getDialogPane().getButtonTypes().addAll(editarButtonType, ButtonType.CANCEL);

            // Criando campos de texto para nome e descrição
            TextField nomeField = new TextField(selecionada.getNome());
            nomeField.setPromptText("Nome da Categoria");
            TextField descricaoField = new TextField(selecionada.getDescricao());
            descricaoField.setPromptText("Descrição da Categoria");

            // Colocando os campos em um layout
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.setContent(new VBox(8, nomeField, descricaoField));

            // Convertendo o resultado para nome e descrição quando o botão Salvar for clicado
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == editarButtonType) {
                    return new Pair<>(nomeField.getText(), descricaoField.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(nomeDescricao -> {
                String nome = nomeDescricao.getKey();
                String descricao = nomeDescricao.getValue();
                try {
                    selecionada.setNome(nome);
                    selecionada.setDescricao(descricao);
                    categoriaDAO.editarCategoria(selecionada);
                    carregarCategorias();
                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro ao editar categoria", e.getMessage());
                }
            });
        } else {
            mostrarAlerta("Nenhuma Categoria Selecionada", "Por favor, selecione uma categoria para editar.");
        }
    }

    @FXML
    private void handleExcluirCategoria() {
        Categoria selecionada = categoriaListView.getSelectionModel().getSelectedItem();
        if (selecionada != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Exclusão");
            alert.setHeaderText(null);
            alert.setContentText("Tem certeza que deseja excluir a categoria selecionada?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    categoriaDAO.excluirCategoria(selecionada.getId());
                    carregarCategorias();
                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro ao excluir categoria", e.getMessage());
                }
            }
        } else {
            mostrarAlerta("Nenhuma Categoria Selecionada", "Por favor, selecione uma categoria para excluir.");
        }
    }
    
    @FXML
    private void handleVoltar() {
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainMenu.fxml"));
    		Scene scene = new Scene(loader.load());
    		Stage stage = (Stage) categoriaListView.getScene().getWindow();
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
}
