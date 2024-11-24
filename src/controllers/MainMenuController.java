package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class MainMenuController {
	@FXML
	private Button categoriasButton;
	
	@FXML
	private Button produtosButton;
	
	@FXML
	private Button sairButton;
	
	@FXML
	private void handleCategorias() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Categorias.fxml"));
			Scene scene = new Scene(loader.load());
			Stage stage = (Stage) categoriasButton.getScene().getWindow();
			stage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleProdutos() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Produtos.fxml"));
			Scene scene = new Scene(loader.load());
			Stage stage = (Stage) produtosButton.getScene().getWindow();
			stage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void handleSair() {
		Stage stage = (Stage) sairButton.getScene().getWindow();
		stage.close();
	}
}
