package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Controller3 implements Initializable{

	@FXML
	private Button exitBtnComponent;
	@FXML
	private TextArea chat_text;
	@FXML
	private ListView<BorderPane> chat_list;

	BorderPane p = new BorderPane();
	BorderPane u = new BorderPane();
	BorderPane d = new BorderPane();
	Label text;
	Label name;
	
	OutputStream os;
	Socket socket;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		try {
			chat_text.setOnKeyPressed((EventHandler<? super KeyEvent>) new EventHandler<KeyEvent>() {
			    @Override
			    public void handle(KeyEvent t) {
			    	p = new BorderPane();
			        if (t.getCode() == KeyCode.ENTER) {
			        	Label findText = new Label(chat_text.getText().trim() + "      ");
			        	//SendMessage(findText.getText());
			        	p.setRight(findText);
			        	chat_text.setText("");
			        	chat_list.getItems().add(p);
			        } else if (t.getCode() == KeyCode.ESCAPE) {
			        	System.out.println("ESC");
			        } else if (t.getCode() == KeyCode.F1) {
			        	printMessage("홍길동", "\t\t상대방의 메시지입니다.");
			        }
			    }
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	// 실사용 하지 않는 컨트롤러
	
	public void SendMessage(String message) {
		Thread thread = new Thread() {
			public void run() {
				try {
					os = socket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					os.write(buffer);
					os.flush();
				} catch (Exception e) {
					e.printStackTrace();
					closeChattingRoom();
				}
			}
		};
		thread.start();
	}

//	// 입장 버튼 눌렀을 때
//	public void openChattingRoom() {
//		Thread thread = new Thread() {
//			public void run() {
//				try {
//					socket = new Socket("211.202.61.16", 9999);
//					ReceiveMessage();
//				} catch(Exception e) {
//					closeChattingRoom();
//					e.printStackTrace();
//				}
//			}
//		};
//	}

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

	
	public void printMessage(String name, String text) {
    	u = new BorderPane();
    	d = new BorderPane();
		this.name = new Label(name);
		this.text = new Label(text);
		u.setLeft(this.name);
		d.setLeft(this.text);
		p.setTop(u);
		p.setBottom(d);
		chat_list.getItems().add(p);
	}
	
	@FXML
	public void exit_btn() throws IOException {
//		FXMLLoader f = new FXMLLoader(getClass().getResource("main1.fxml"));
//		Parent p = (Parent) f.load();
//		Stage backStage = new Stage();
//		backStage.setScene(new Scene(p));
//		backStage.setTitle("YSB");
//		backStage.show();
		
		Stage tmp = (Stage) exitBtnComponent.getScene().getWindow();
		tmp.close();
	}
	
	@FXML
	public void change_btn() {
		// 닉네임 변경 버튼
		System.out.println("잉");
	}
	
	Socket getSocket() {
		return socket;
	}
	
}
