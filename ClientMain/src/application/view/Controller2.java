package application.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Vector;

import application.Main;
import application.db.DBConnect;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Controller2 implements Initializable{
	Label titleLabel;
	Label roomMasterLabel;
	Label memberCountLabel;
	OutputStream os;
	InputStream is;
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
	@FXML
	public TextArea nick_text;
	@FXML
	private Button createbtn;
	@FXML
	private Button changebtn;
	@FXML
	private Button refreshbtn;
	
	Main scene = new Main();
	Stage stage;
	
	DBConnect dc = new DBConnect();

	ObservableList<BorderPane> savedList = FXCollections.observableArrayList();
	int checkSearch = 0;
	int roomCode = 0;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		try {
			// ������ �� ����Ʈ ���� ������ ��û�ؾ��� -> �Լ��� ������ѳ��� ������ 
//			openWaitingRoom();
			RefreshRoomList();
			
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

	// ----------------- �游��� ��ư -----------------------
	@FXML
	private void testFunc(ActionEvent event) {
		// ���ǿ� room list����ִ°� initialize�� refresh ��ư
		
		if (title_text.getText().equals("") || nick_text.getText().equals("") || members_text.getText().equals("")) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText("�游��⸦ �� �� �����ϴ�.");
			alert.setContentText("�� ������ ��� �Է����ּ���!");

			alert.showAndWait();
			return ;
		}
//		
		dc.connect();
		roomCode = dc.getLastCode();
		if (roomCode > 0) {
			roomCode += 1;
		}
		dc.InsertRoominfo(roomCode, title_text.getText(), nick_text.getText(), Integer.parseInt(members_text.getText()));
		dc.close();
//		
//		System.out.println("testFunc end");
		
		SendRoominfo(members_text.getText() + "#" + roomCode);
		scene.chattingScene(Integer.toString(roomCode));
//		
//		pane = new BorderPane();
//		pane2 = new BorderPane();
//		String t = title_text.getText();
//		Button btn = new Button("����");
//		btn.setId(Integer.toString(roomCode++));
//		titleLabel = new Label(t + " : " + btn.getId());
//		try {
//			roomMasterLabel = new Label(nick_text.getText());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		memberCountLabel = new Label("1/" + members_text.getText());
//		
//		pane.setLeft(titleLabel);
//		pane.setCenter(roomMasterLabel);
//		pane2.setLeft(memberCountLabel);
//		pane2.setRight(btn);
//		pane.setRight(pane2);
//		
//		roomList.getItems().add(pane);
////		roomList.getItems().add(title_text.getText() + " : " + members_text.getText());
//		
//		
//		
//		// ���� ��ư
//		btn.setOnAction(arg0 -> {
//			SendRoominfo("entry#" + btn.getId());
////			stage = (Stage) createbtn.getScene().getWindow();
//			scene.chattingScene(btn.getId());
//		});
	}
	
	// �г��� ��ü ��ư
	@FXML
	private void changebtnOnAction(ActionEvent event) {
//		dc.connect();
//		dc.testDelete();
//		System.out.println("Delete success");
//		dc.close();
	}
	
	// ���ΰ�ħ ��ư
	@FXML
	private void refreshbtnOnAction(ActionEvent event) {
		RefreshRoomList();
	}
	
	// ----------------- ���� ���� �޼ҵ� --------------------------

	public void openWaitingRoom() {
		try {
			socketRoominfo = new Socket("127.0.0.1", 8888);
			System.out.println("[ ���� socket ���� ���� ]");
		} catch (Exception e) {
			System.out.println("[ ���� socket ���� ���� ]");
			e.printStackTrace();
			closeWaitingRoom();
		}
	}
	
	public void RefreshRoomList() {
		roomList.getItems().clear();

		dc.connect();
		String roominfo = dc.testSelect();
		if (roominfo.equals("")) return;
		
		String[] roomArray = roominfo.split("\n"); // �� ����, �ο���, ���, �ڵ�
		
		for (int i = 0; i < roomArray.length; i++) {
			String[] roomArrayinfo = roomArray[i].split(", ");

			Platform.runLater(() -> {
				titleLabel = new Label(roomArrayinfo[1] + " : " + roomArrayinfo[0]);
				roomMasterLabel = new Label(roomArrayinfo[2]);
				memberCountLabel = new Label(roomArrayinfo[3] + "/" + roomArrayinfo[4]);

				Button btn = new Button("����");
				btn.setId(roomArrayinfo[0]);

				pane = new BorderPane();
				pane2 = new BorderPane();

				pane.setLeft(titleLabel);
				pane.setCenter(roomMasterLabel);
				pane2.setLeft(memberCountLabel);
				pane2.setRight(btn);
				pane.setRight(pane2);

				roomList.getItems().add(pane);
				btn.setOnAction(event2 -> {
					String[] memArray = memberCountLabel.getText().split("/");
					if (Integer.parseInt(memArray[0]) < Integer.parseInt(memArray[1])){
						SendRoominfo("entry#" + btn.getId());
						dc.EnterRoom(Integer.parseInt(btn.getId()));
						// ä�þ� �����ֱ� ������ ���� ������� �Ǿ�����
						scene.chattingScene(btn.getId());
					} else {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("�濡 ������ �� �����ϴ�.");
						alert.setContentText("�ο����� �� á���ϴ�!");
						alert.showAndWait();
						return ;
					}
				});
			});
		}
		dc.close();
	}

	
	public void SendRoominfo(String roominfo) {
		try {
			os = socketRoominfo.getOutputStream();
			byte[] buffer = roominfo.getBytes("UTF-8");
			os.write(buffer);
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
			closeWaitingRoom();
		}
	}
	
	public void closeWaitingRoom() {
		try {
			if (socketRoominfo != null && !socketRoominfo.isClosed()) {
				socketRoominfo.close();
				System.out.println("socketRoominfo end");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void search_cancel() {
		roomList.setItems(savedList);
		checkSearch = 0;
	}
//	
//	private void setRoomCode() {
//		dc.connect();
//		if (dc.getLastCode() == 0) return;
//		roomCode = dc.getLastCode() + 1;
//		dc.close();
//	}
}
// main1 ��Ʈ�ѷ� �ޱ�, ����Ʈ�� �̸� ���ϱ�, ��ư�� �Լ� �ޱ�
