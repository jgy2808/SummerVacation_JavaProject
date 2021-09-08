package application.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import application.db.DBConnect;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ChatController implements Initializable{
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
	
	private BorderPane p;
	private BorderPane subp;
	private BorderPane u;
	private BorderPane d;
	private Label text;
	private Label name;

	private Socket socket;
	private OutputStream os;
	private InputStream is;
	private String nick;
	private int roomCode;
	
	private DBConnect dc = new DBConnect();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			openChattingRoom();
			
			chat_text.setOnKeyPressed((EventHandler<? super KeyEvent>) new EventHandler<KeyEvent>() {
			    @Override
			    public void handle(KeyEvent t) {
			        if (t.getCode() == KeyCode.ENTER) {
			        	String mestext = chat_text.getText().trim() + "      ";
			        	SendMessage(nick + "#" + mestext);
			        	printMessage("", mestext);
			        	
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
		thread = new Thread() {
			public void run() {
				try {
//					socket = new Socket("211.202.61.16", 9999);
//					socket = new Socket("211.207.42.58", 9999);
					socket = new Socket("127.0.0.1", 9999);
					os = socket.getOutputStream();
					is = socket.getInputStream();
					System.out.println("[ 채팅방 socket 연결 성공 ] : " + socket.getPort());
					ReceiveMessage(socket);
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
	public void ReceiveMessage(Socket socket) {
		try {
			byte[] buffer = new byte[512];
			int length;
			while ( (length = is.read(buffer)) != -1 ) {
				String message = new String(buffer, 0, length, "UTF-8");
				System.out.println(nick + " : chatting message : " + message);
				m = message.split("#");
				if (m[0].equals("in")) {
					// message : in#currentNum#nick#입장#memArray
					Platform.runLater(() -> {
						currentNum_Label.setText(m[1]);
						
						p = new BorderPane();
						Label entryMessage = new Label(m[2] + m[3]);
						entryMessage.setFont(new Font("gulim", 15));
						p.setCenter(entryMessage);
			        	chat_list.getItems().add(p);
			        	
						BorderPane bp;
						Label mem = null;
						//member_list 추가
						if ( m[4].charAt(0) == '[' ) {
							m[4] = m[4].substring(1, m[4].length() - 1);
							String[] memArray = m[4].split(", ");
							for (int i = memArray.length - 1; i >= 0; i--) {
								bp = new BorderPane();
								mem = new Label(memArray[i]);
					        	mem.setFont(new Font("gulim", 15));
								mem.setUserData(memArray[i]);
								bp.setLeft(mem);
								member_list.getItems().add(bp);
							}
						} else {
							bp = new BorderPane();
							mem = new Label(m[4]);
				        	mem.setFont(new Font("gulim", 15));
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
								System.out.println(nick + " : remove : " + m[2]);
								member_list.getItems().remove(i);
								break;
							}
						}
					});
				} else if (m[0].equals("closeChattingSocket")) {
					Stage tmp = (Stage) chat_list.getScene().getWindow();
					Platform.runLater(() -> {
						tmp.close();
					});
					
					dc.connect();
					if (Integer.parseInt(currentNum_Label.getText()) > 1) {
						dc.ExitRoom(roomCode);
					} else {
						dc.DeleteRoom(roomCode);
					}
					dc.close();

					try {
						if (socket != null && !socket.isClosed()) {
							System.out.println(nick + " socket is closed.");
							socket.close();
						}
					} catch (Exception e) {
						System.out.println(nick + " : closeChattingRoom exception");
						e.printStackTrace();
					}
					break;
				} else {
					Platform.runLater(() -> {
						printMessage(m[0], m[1]);
					});
				}
			}
			is.close();
		} catch (Exception e) {
			System.out.println(nick + " : ReceiveMessage exception");
			e.printStackTrace();
		}
	}
	
	// 메세지 보내는 부분
	public void SendMessage(String message) {
		Thread thread = new Thread() {
			public void run() {
				try {
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
			String outMessage = "closeChattingSocket#" + nick;
			byte[] buffer = outMessage.getBytes("UTF-8");
			os.write(buffer);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printMessage(String name, String text) {
		// 줄바꿈 추가 코드
		StringBuffer mes = new StringBuffer();
		mes.append(text);
		int maxwidthlen = 30;
		System.out.println(" 0. " + mes.toString());
		for (int i = 1; i <= text.length() / maxwidthlen; i++) {
			mes.insert(i * maxwidthlen - 1, "\n");
			System.out.println(" " + Integer.toString(i) + ". \n" + mes);
		}
		
		// 시간 추가 코드
		LocalDateTime ldt = LocalDateTime.now();
		String hour;
		if (ldt.getHour() / 12 > 0) {
			hour = "오후" + Integer.toString(ldt.getHour() % 12);
		} else {
			hour = "오전" + Integer.toString(ldt.getHour());
		}
		String minute;
		if ((minute = Integer.toString(ldt.getMinute())).length() == 1) {
			minute = "0" + minute;
		}
		String time = hour + " : " + minute;
		System.out.println("hour : " + hour + " min : " + minute + " time : " + time);
		Label currentTime = new Label(time);
		currentTime.setFont(new Font("gulim", 10));
		currentTime.setAlignment(Pos.CENTER);
		currentTime.setStyle("-fx-background-color : red; -fx-text-fill : white; -fx-padding : 5px;");
		
		// 채팅 리스트에 붙이기 코드
		if (name.equals("")) {
	    	p = new BorderPane();
	    	subp = new BorderPane();
	    	subp.setStyle("-fx-background-color : grey;");

			Label findText = new Label(mes.toString());
			findText.setFont(new Font("gulim", 20));
			subp.setRight(findText);
			currentTime.setText(currentTime.getText() + "        ");
			subp.setLeft(currentTime);
        	
        	p.setRight(subp);
        	chat_text.setText("");
        	chat_list.getItems().add(p);
		} else {
			p = new BorderPane();
			subp = new BorderPane();
			u = new BorderPane();
			d = new BorderPane();
			this.name = new Label(name);
			this.text = new Label(mes.toString());

			u.setLeft(this.name);
			d.setLeft(this.text);
			currentTime.setText("        " + currentTime.getText());
			subp.setLeft(currentTime);
			d.setCenter(subp);
			p.setTop(u);
			p.setBottom(d);
			chat_list.getItems().add(p);
		}
	}
}
