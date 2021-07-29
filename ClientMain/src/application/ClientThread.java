package application;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javafx.application.Platform;


public class ClientThread extends Thread {
	Controller3 controller3;
	InputStream is;
	byte[] buffer;
	int length;
	String message;

	public ClientThread(Controller3 controller3) {
		this.controller3 = controller3;
	}

	// 서버로부터 메세지 받아서 처리하고 화면에 출력하기
	public void run() {
		while (true) {
			try {
				buffer = new byte[512];
				length = is.read(buffer);
				if (length == -1)
					throw new IOException();
				message = new String(buffer, 0, length, "UTF-8");
				System.out.println(message);
				// controller 
				Platform.runLater(() -> {
					controller3.printMessage("nickname", "Text");
				});
			} catch (IOException e) {
				e.printStackTrace();
				Platform.exit();
			}
		}
	}

}
