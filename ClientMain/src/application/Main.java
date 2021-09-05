package application;

import java.io.IOException;

import application.view.WaitController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader fl = new FXMLLoader(getClass().getResource("view/login.fxml"));
		Parent root = (Parent) fl.load();
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("view/loginStyle.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("YSB");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
}