package application;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import javafx.application.Platform;


public class ClientThread extends Thread {
	Main mc;
	InputStream is;
	byte[] buffer;
	int length;
	String message;

	public ClientThread(Main mc) {
		this.mc = mc;
	}

	// 서버로부터 메세지 받아서 처리하고 화면에 출력하기
	public void run() {
		while (true) {
			try {
				mc.socket = new Socket("211.202.61.16", 9999);
				is = mc.socket.getInputStream();
				buffer = new byte[512];
				length = is.read(buffer);
				if (length == -1)
					throw new IOException();
				message = new String(buffer, 0, length, "UTF-8");
				System.out.println(message);
				// controller 
//				Platform.runLater(() -> {
//					// 채팅방 채팅 목록에 뿌려주기
//					mc.textArea_ChattingRoom.appendText(message);
//				});
			} catch (IOException e) {
				e.printStackTrace();
				Platform.exit();
			}
		}
	}

}
