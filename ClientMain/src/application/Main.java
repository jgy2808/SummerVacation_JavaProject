package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import application.view.Controller2;
import application.view.Controller3;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	Controller2 c2 = new Controller2();
	Controller3 c3 = new Controller3();
	FXMLLoader f;
	Parent r;
	Stage stage2;
	
	public void start(Stage primaryStage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("view/main1.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("YSB");
		primaryStage.setOnCloseRequest(event -> c2.closeWaitingRoom());
		primaryStage.show();
	}
	
	public void chattingScene(String title) {
		try {
			f = new FXMLLoader(getClass().getResource("view/main2.fxml"));
			
			r = (Parent) f.load();
			stage2 = new Stage();
			stage2.setScene(new Scene(r));
			stage2.setTitle(title);
			stage2.setOnCloseRequest(event -> c3.closeChattingRoom());
			stage2.show();				// 새로운 창을 여는 코드	
			
//			Stage tmp = (Stage) btn.getScene().getWindow();
//			tmp.close();		// 해당 두줄은 방입장이 기존 대기실방 닫는 코드
		}
		catch(IOException ex) {
			System.out.println(ex);
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
	
}