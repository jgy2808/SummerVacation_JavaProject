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
	ClientThread cth;
	Socket socket;
	OutputStream os;
	InputStream is;
	
	// ���� ��ư ������ ��
	public void openChattingRoom() {
		Thread clientTh = new Thread(cth);
		clientTh.start();
	}

	// ä�ù濡�� ���� ��
	public void closeChattingRoom() {
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(String message) {
		Thread thread = new Thread() {
			public void run() {
				try {
					os = socket.getOutputStream();

					// byte ������ �����ϴ� �� OutputStream.write vs String ������ �����ϴ� �� ObjectOutputStream.writeObject
					byte[] buffer = message.getBytes("UTF-8");
					os.write(buffer);
					os.flush();
				} catch (Exception e) {
					e.printStackTrace();
					closeChattingRoom();
				}
			}
		};
	}

	
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
