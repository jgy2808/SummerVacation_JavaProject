package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	public void start(Stage primaryStage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("main1.fxml"));
		
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("YSB");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}