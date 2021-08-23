package application;

import java.io.IOException;

import application.view.Controller2;
import application.view.Controller3;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	Controller2 c2 = new Controller2();
	
	public void start(Stage primaryStage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("view/main1.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("YSB");
		primaryStage.setOnCloseRequest(event -> c2.closeWaitingRoom());
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
}