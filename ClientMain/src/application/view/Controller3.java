package application.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import application.db.DBConnect;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
	@FXML
	private ListView<BorderPane> member_list;
	@FXML
	private Label currentNum_Label;
	@FXML
	private Label maxNum_Label;
	@FXML
	private Label title_Label;
	
	BorderPane p = new BorderPane();
	BorderPane u = new BorderPane();
	BorderPane d = new BorderPane();
	Label text;
	Label name;
	
	Socket socket;
	OutputStream os;
	InputStream is;
	String[] s;
	String nick;
	private int roomCode;
	
	private DBConnect dc = new DBConnect();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			openChattingRoom();
			
			chat_text.setOnKeyPressed((EventHandler<? super KeyEvent>) new EventHandler<KeyEvent>() {
			    @Override
			    public void handle(KeyEvent t) {
			    	p = new BorderPane();
			        if (t.getCode() == KeyCode.ENTER) {
			        	Label findText = new Label(chat_text.getText().trim() + "      ");
			        	SendMessage(nick + "#" + findText.getText());
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
			System.out.println("initialize exception");
			e.printStackTrace();
			closeChattingRoom();
		}
	}
	
	public void DataInit(String code, String title, String nick, String maxNum) {
		this.roomCode = Integer.parseInt(code);
		this.title_Label.setText(title);
		this.nick = nick;
		this.maxNum_Label.setText(maxNum);
	}

	// 실사용 하지 않는 컨트롤러
	Thread thread;
	public void openChattingRoom() {
		System.out.println("openchattingroom start");
		thread = new Thread() {
			public void run() {
				try {
//					socket = new Socket("211.202.61.16", 9999);
					socket = new Socket("127.0.0.1", 9999);
					
					ReceiveMessage();
				} catch (Exception e) {
					System.out.println("openChattingRoom exception");
					e.printStackTrace();
					closeChattingRoom();
				}
			}
		};
		thread.start();
	}
	
	String[] m = null;
	public void ReceiveMessage() {
		try {
			is = socket.getInputStream();
			byte[] buffer = new byte[512];
			while (true) {
				int length = is.read(buffer);
				if (length == -1) throw new IOException();
				String message = new String(buffer, 0, length, "UTF-8");
				System.out.println("message : " + message);
				m = message.split("#");
				if (m[0].equals("in")) {
					// message : in#currentNum#nick#입장#memArray
					Platform.runLater(() -> {
						currentNum_Label.setText(m[1]);
						printMessage(m[2] + m[3], "");
						System.out.println("m[4] : " + m[4]);
						if ( m[4].charAt(0) == '[' ) {
							m[4] = m[4].substring(1, m[4].length() - 1);
							String[] memArray = m[4].split(", ");
							for (int i = memArray.length - 1; i >= 0; i--) {
								BorderPane bp = new BorderPane();
								Label mem = new Label(memArray[i]);
								mem.setUserData(memArray[i]);
								bp.setLeft(mem);
								member_list.getItems().add(bp);
//								System.out.println(member_list.getItems().get(i).getLeft().getUserData());
							}
						} else {
							BorderPane bp = new BorderPane();
							Label mem = new Label(m[4]);
							mem.setUserData(m[4]);
							bp.setLeft(mem);
							member_list.getItems().add(bp);
						}
					});
				} else if (m[0].equals("out")) {
					Platform.runLater(() -> {
						currentNum_Label.setText(m[1]);
						printMessage(m[2] + m[3], "");
						for (int i = 0; i < member_list.getItems().size(); i++ ) {
							if ( m[2].equals(member_list.getItems().get(i).getLeft().getUserData()) ){
								System.out.println("remove : " + m[2]);
								member_list.getItems().remove(i);
								break;
							}
						}
//						member_list.getItems().
					});
				} else if (m[0].equals("closeChattingSocket")) { 
					Stage tmp = (Stage) exitBtnComponent.getScene().getWindow();
					dc.connect();
					if (Integer.parseInt(currentNum_Label.getText()) > 1) {
						dc.ExitRoom(roomCode);
					} else {
						dc.DeleteRoom(roomCode);
					}
					dc.close();
					try {
						if (socket != null && !socket.isClosed()) {
							System.out.println(socket.getPort() + " socket is closed.");
							socket.close();
						}
					} catch (Exception e) {
						System.out.println("closeChattingRoom exception");
						e.printStackTrace();
					}
					tmp.close();
					is.close();
					return ;
				} else {
					Platform.runLater(() -> {
						printMessage(m[0], m[1]);
					});
				}
			}
		} catch (Exception e) {
			System.out.println("ReceiveMessage exception");
			e.printStackTrace();
		}

	}
	
	// 메세지 보내는 부분
	public void SendMessage(String message) {
		Thread thread = new Thread() {
			public void run() {
				try {
					os = socket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					os.write(buffer);
					os.flush();
				} catch (Exception e) {
					System.out.println("SendMessage exception");
					e.printStackTrace();
					closeChattingRoom();
				}
			}
		};
		thread.start();
	}

	// 채팅방에서 나올 때
	public void closeChattingRoom() {
		try {
			os = socket.getOutputStream();
			String outMessage = "closeChattingSocket#" + nick;
			byte[] buffer = outMessage.getBytes("UTF-8");
			int length = buffer.length;
			os.write(buffer, 0, length);
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printMessage(String name, String text) {
		p = new BorderPane();
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
		closeChattingRoom();
	}
	
	@FXML
	public void change_btn() {
		// 닉네임 변경 버튼
		System.out.println("잉");
	}
}
