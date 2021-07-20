package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class Main extends Application {
	ClientThread cth;
	Socket socket;
	OutputStream os;
	InputStream is;
	
	TextArea textArea_ChattingRoom;

	// 입장 버튼 눌렀을 때
	public void openChattingRoom() {
		Thread thread = new Thread() {
			public void run() {
				try {
					// 서버 PC address
					socket = new Socket("192.151.24.58", 5000);
					Thread clientTh = new Thread(cth);
					clientTh.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
	}

	// 채팅방에서 나올 때
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

					// byte 단위로 전송하는 것 OutputStream.write vs String 단위로 전송하는 것 ObjectOutputStream.writeObject
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

	@Override
	public void start(Stage primaryStage) {

	}

	public static void main(String[] args) {
		launch(args);
	}
}
