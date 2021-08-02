package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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


public class Controller2 implements Initializable{
	
	Button btn;
	Label titleLabel;
	Label memberLabel;
	Label passwdLabel;
	
	Socket socket;
	Socket socketRoominfo;

	@FXML
	private ListView<BorderPane> roomList;
	@FXML
	private TextArea title_text;
	@FXML
	private TextArea members_text;
	@FXML
	private TextArea passwd_text;
	@FXML
	private TextArea search_text;
	

	ObservableList<BorderPane> savedList = FXCollections.observableArrayList();
	int checkSearch = 0;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		try {
			// ���Ͽ��� �� ����Ʈ �����ͼ� roomlist�� �߰��ؼ� �����ֱ� -> ���Ͽ� ������ �������� ó��
			//openWaitingRoom();
			
			search_text.setOnKeyPressed((EventHandler<? super KeyEvent>) new EventHandler<KeyEvent>() {

			    @Override
			    public void handle(KeyEvent t) {
			        if (t.getCode() == KeyCode.ENTER) {
			        	if(checkSearch == 0) {
			        		savedList = roomList.getItems();
			        		checkSearch = 1;
			        	}
			        	ObservableList<BorderPane> entries = FXCollections.observableArrayList();
			        	String findText = search_text.getText().trim();
			        	search_text.setText("");
			        	for(BorderPane p : savedList) {
			        		int start = p.getChildren().get(0).toString().indexOf("'");
//			        		int end = p.getChildren().get(0).toString().indexOf("'",start+1);
//			        		System.out.println(findText + " :: " + p.getChildren().get(0).toString().indexOf(findText,start));
			        		if(p.getChildren().get(0).toString().indexOf(findText,start) != -1) {
			        			entries.add(p);
			        			System.out.println("success!");
			        		}
			        	}
			        	roomList.setItems(entries);
			        } else if (t.getCode() == KeyCode.ESCAPE) {
			        	System.out.println("�����̰�");
			        }
			    }
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	FXMLLoader f;
	Parent r;
	Stage stage2;
	
	BorderPane pane;
	BorderPane pane2;
	@FXML
	
	// ----------------- �游��� ��ư -----------------------
	private void testFunc(ActionEvent event) {
		pane = new BorderPane();
		pane2 = new BorderPane();
		String t = title_text.getText();
		btn = new Button("����");
		titleLabel = new Label(t);
		memberLabel = new Label("�ִ� �ο� ���̵�� members_text");
		passwdLabel = new Label("��� ���̵�� passwd_text");
		
		// pane �� ����
		// pane ��ҵ��� text�� ���� ���Ͽ� ����
		// text�� �ҷ��ͼ� ��ҵ� �ʱ�ȭ���ְ� pane�� ����
		// pane ������ŭ �ݺ��ؼ� roomlist�� �߰�
		//String roominfo = new String(titleLabel.getText() + "," + memberLabel.getText() + "," + passwdLabel.getText() + "\n");
		//SendRoominfo(roominfo);
		
		pane.setLeft(titleLabel);
		pane.setCenter(memberLabel);
		pane2.setLeft(passwdLabel);
		pane2.setRight(btn);
		pane.setRight(pane2);
		
//		roomList.getItems().add(title_text.getText() + " : " + members_text.getText());
		roomList.getItems().add(pane);
		
		//ReceiveRoominfo();
		
		openChattingRoom();
		
		f = new FXMLLoader(getClass().getResource("main2.fxml"));
		
		try {
			r = (Parent) f.load();
			stage2 = new Stage();
			stage2.setScene(new Scene(r));
			stage2.setTitle("YSB2");
			stage2.show();				// ���ο� â�� ���� �ڵ�	
//			
//			Stage tmp = (Stage) btn.getScene().getWindow();
//			tmp.close();		// �ش� ������ �������� ���� ���ǹ� �ݴ� �ڵ�
			
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
		
		
		btn.setOnAction(arg0 -> {
			openChattingRoom();
			
			f = new FXMLLoader(getClass().getResource("main2.fxml"));
			try {
				r = (Parent) f.load();
				stage2 = new Stage();
				stage2.setScene(new Scene(r));
				stage2.setTitle("YSB2");
				stage2.show();				// ���ο� â�� ���� �ڵ�	
				
//				Stage tmp = (Stage) btn.getScene().getWindow();
//				tmp.close();		// �ش� ������ �������� ���� ���ǹ� �ݴ� �ڵ�
				
			}
			catch(IOException ex) {
				System.out.println(ex);
			}
		});
	}
	
	
	// ----------------- ���� ���� �޼ҵ� --------------------------
	// ���� �����ڸ��� ������ ��� ���� �� �� ���� �޾Ƽ� roomlist�� �ѷ��ֱ�
	public void openWaitingRoom() {
		Thread thread = new Thread() {
			public void run() {
				try {
					socketRoominfo = new Socket("127.0.0.1", 9999);
					ReceiveRoominfo();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	public void ReceiveRoominfo() {
		while(true) {
			try {
				InputStream is = socketRoominfo.getInputStream();
				byte[] buffer = new byte[512];
				int length = is.read(buffer);
				if (length == -1 ) throw new IOException();
				String roominfo = new String(buffer, 0, length, "UTF_8");
				String[] roomArray = roominfo.split("\n");
				
				btn = new Button("����");
				for (int i = 0; i < roomArray.length; i++) {
					String[] roominfoArray = roominfo.split(",");
					titleLabel = new Label(roominfoArray[0]);
					memberLabel = new Label(roominfoArray[1]);
					passwdLabel = new Label(roominfoArray[2]);
					Platform.runLater(() -> {
						pane.setLeft(titleLabel);
						pane.setCenter(memberLabel);
						pane2.setLeft(passwdLabel);
						pane2.setRight(btn);
						pane.setRight(pane2);
					});
					roomList.getItems().add(pane);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void SendRoominfo(String roominfo) {
		Thread thread = new Thread() {
			public void run() {
				try {
					OutputStream os = socketRoominfo.getOutputStream();
					byte[] buffer = roominfo.getBytes("UTF-8");
					os.write(buffer);
					os.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	

	// ---------------------- ä�� ���� �޼��� -------------------------
	Controller3 c3 = new Controller3();
	public void openChattingRoom() {
		Thread thread = new Thread() {
			public void run() {
				try {
//					socket = new Socket("211.202.61.16", 9999);
					socket = new Socket("127.0.0.1", 9999);
					
					ReceiveMessage();
				} catch(Exception e) {
					c3.closeChattingRoom();
					e.printStackTrace();
					Platform.exit();
				}
			}
		};
		thread.start();
	}
	

	String[] m = null;
	public void ReceiveMessage() {
		while(true) {
			try {
				InputStream is = socket.getInputStream();
				byte[] buffer = new byte[512];
				int length = is.read(buffer);
				if (length == -1 ) throw new IOException();
				String message = new String(buffer, 0, length, "UTF-8");
				System.out.println(message);
				m = message.split("#");
				Platform.runLater(()->{
					c3.printMessage(m[0], m[1]);
				});
			} catch(Exception e) {
				System.exit(0);
				e.printStackTrace();
				Platform.exit();
			}
		}
	}

	@FXML
	public void search_cancel() {
		roomList.setItems(savedList);
		checkSearch = 0;
	}


	
}
// main1 ��Ʈ�ѷ� �ޱ�, ����Ʈ�� �̸� ���ϱ�, ��ư�� �Լ� �ޱ�