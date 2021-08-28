package application.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

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
	Controller3 c3 = new Controller3();
	private DBConnect dc = new DBConnect();
	
	Label titleLabel;
	Label roomMasterLabel;
	Label memberCountLabel;
	OutputStream os;
	InputStream is;
	Socket socketRoominfo;
	
	BorderPane pane;
	BorderPane pane2;

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

	ObservableList<BorderPane> savedList = FXCollections.observableArrayList();
	int checkSearch = 0;
	int roomCode = 0;
	
	
	// chattingscene method variable
	FXMLLoader f;
	Parent r;
	Stage stage2;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		try {
			openWaitingRoom();
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
			        	System.out.println("뭐냐이건");
			        }
			    }
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// ----------------- 방만들기 버튼 -----------------------
	@FXML
	private void testFunc(ActionEvent event) {
		if (title_text.getText().equals("") || nick_text.getText().equals("") || members_text.getText().equals("") || 
				(members_text.getText().charAt(0) < '2' || members_text.getText().charAt(0) > '9')) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText("방만들기를 할 수 없습니다.");
			alert.setContentText("방 정보를 모두 입력해주세요!");

			alert.showAndWait();
			return ;
		}
		
		dc.connect();
		roomCode = dc.InsertRoominfo(title_text.getText(), nick_text.getText(),
				Integer.parseInt(members_text.getText()));
		dc.close();

		chattingScene(title_text.getText(), nick_text.getText(), members_text.getText());
		SendRoominfo(members_text.getText() + "#" + roomCode + "#" + nick_text.getText());
		RefreshRoomList();

		title_text.setText("");
		members_text.setText("");
		nick_text.setText("");
	}
	
	// 닉네임 교체 버튼
	@FXML
	private void changebtnOnAction(ActionEvent event) {
		dc.connect();
		dc.testDelete();
		System.out.println("Delete success");
		dc.close();
		RefreshRoomList();
	}
	
	// 새로고침 버튼
	@FXML
	private void refreshbtnOnAction(ActionEvent event) {
		RefreshRoomList();
	}
	
	// ----------------- 대기실 관련 메소드 --------------------------

	public void openWaitingRoom() {
		try {
			socketRoominfo = new Socket("127.0.0.1", 8888);
			System.out.println("[ 대기실 socket 연결 성공 ]");
		} catch (Exception e) {
			System.out.println("[ 대기실 socket 연결 실패 ]");
			e.printStackTrace();
			closeWaitingRoom();
		}
//		String s = (String)(roomList.getScene().getWindow()).getUserData();
//		nick_text.setText(s);
	}
	
	public void RefreshRoomList() {
		roomList.getItems().clear();

		dc.connect();
		String roominfo = dc.SelectRoomInfo();
		if (roominfo.equals("")) return;
		dc.close();
		
		String[] roomArray = roominfo.split("\n"); // 방 제목, 인원수, 비번, 코드
		
		for (int i = 0; i < roomArray.length; i++) {
			String[] roomArrayinfo = roomArray[i].split(", ");

			Platform.runLater(() -> {
				titleLabel = new Label(roomArrayinfo[1] + " : " + roomArrayinfo[0]);
				roomMasterLabel = new Label(roomArrayinfo[2]);
				memberCountLabel = new Label(roomArrayinfo[3] + "/" + roomArrayinfo[4]);

				Button btn = new Button("입장");
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
					if (nick_text.getText().equals("")) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("입장 할 수 없습니다.");
						alert.setContentText("닉네임을 입력해주세요!");

						alert.showAndWait();
						return ;
					}
					dc.connect();
					int enterReturnVal = dc.EnterRoom(Integer.parseInt(btn.getId()));
					if ( enterReturnVal == 1 ){
						chattingScene(roomArrayinfo[1], nick_text.getText(), roomArrayinfo[4]);
						SendRoominfo("entry#" + btn.getId() + "#" + nick_text.getText());
						RefreshRoomList();
					} else if (enterReturnVal == 2){
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("방에 입장할 수 없습니다.");
						alert.setContentText("인원수가 꽉 찼습니다!");
						alert.showAndWait();
						return ;
					} else {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Warning");
						alert.setHeaderText("방에 입장할 수 없습니다.");
						alert.setContentText("방이 존재하지 않습니다!");
						alert.showAndWait();
						return ;
					}
					dc.close();
				});
			});
		}
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
	
	public void chattingScene(String title, String nick, String maxNum) {
		try {
			f = new FXMLLoader(getClass().getResource("main2.fxml"));
			r = (Parent) f.load();
			
			c3 = f.getController();
			c3.DataInit(title, nick, maxNum);;

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
}
// main1 컨트롤러 달기, 리스트뷰 이름 정하기, 버튼에 함수 달기
