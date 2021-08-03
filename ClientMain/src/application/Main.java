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
	
	Socket socketroominfo;
	
	public void openWaitingroom() {
		Thread thread = new Thread() {
			public void run() {
				try {
					socketroominfo = new Socket("127.0.0.1", 8888);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	public void closeWindow() {
		System.out.println("called closeWindow()");
		try {
			if (socketroominfo != null && !socketroominfo.isClosed()) {
				socketroominfo.close();
				System.out.println("socketroominfo is closed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start(Stage primaryStage) throws IOException {
		
		//openWaitingroom();
		
		Parent root = FXMLLoader.load(getClass().getResource("main1.fxml"));
		
		primaryStage.setOnCloseRequest(event -> closeWindow());
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("YSB");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public Socket getSocket() {
		return socketroominfo;
	}
}