package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
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
			// 서버에 방 리스트 정보 수신을 요청해야함 -> 함수를 실행시켜놓고 있으면 
			//RefreshRoomList();
			
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
	
	FXMLLoader f;
	Parent r;
	Stage stage2;
	
	BorderPane pane;
	BorderPane pane2;
	@FXML
	
	// ----------------- 방만들기 버튼 -----------------------
	private void testFunc(ActionEvent event) {
		pane = new BorderPane();
		pane2 = new BorderPane();
		String t = title_text.getText();
		btn = new Button("입장");
		titleLabel = new Label(t);
		memberLabel = new Label("members_text");
		passwdLabel = new Label("passwd_text");
		
		// pane 을 저장
		// pane 요소들의 text만 따서 파일에 저장
		// text를 불러와서 요소들 초기화해주고 pane에 저장
		// pane 개수만큼 반복해서 roomlist에 추가
		//String roominfo = new String(titleLabel.getText() + "," + memberLabel.getText() + "," + passwdLabel.getText() + "\n");
		//SendRoominfo(roominfo);
		
		pane.setLeft(titleLabel);
		pane.setCenter(memberLabel);
		pane2.setLeft(passwdLabel);
		pane2.setRight(btn);
		pane.setRight(pane2);
		
//		roomList.getItems().add(title_text.getText() + " : " + members_text.getText());
		roomList.getItems().add(pane);
		
		
		
		f = new FXMLLoader(getClass().getResource("main2.fxml"));
		try {
			r = (Parent) f.load();
			stage2 = new Stage();
			stage2.setScene(new Scene(r));
			stage2.setTitle("YSB2");
			stage2.show();
		}
		catch(IOException ex) {
			System.out.println(ex);
		}
		
		// 입장 버튼
		btn.setOnAction(arg0 -> {
			f = new FXMLLoader(getClass().getResource("main2.fxml"));
			try {
				r = (Parent) f.load();
				stage2 = new Stage();
				stage2.setScene(new Scene(r));
				stage2.setTitle("YSB2");
				stage2.show();				// 새로운 창을 여는 코드	
				
//				Stage tmp = (Stage) btn.getScene().getWindow();
//				tmp.close();		// 해당 두줄은 방입장이 기존 대기실방 닫는 코드
			}
			catch(IOException ex) {
				System.out.println(ex);
			}
		});
	}
	
	
	// ----------------- 대기실 관련 메소드 --------------------------

	public void openWaitingRoom() {
		Thread thread = new Thread() {
			public void run() {
				try {
					if (socketRoominfo == null && !socketRoominfo.isConnected()) {
						socketRoominfo = new Socket("127.0.0.1", 8888);
					}
				} catch (Exception e) {
					e.printStackTrace();
					closeWaitingRoom();
				}
			}
		};
		thread.start();
	}
	
	public void RefreshRoomList() {
		Thread thread = new Thread() {
			public void run() {
				try {
					OutputStream os = socketRoominfo.getOutputStream();
					String sign = "sign";
					byte[] refreshSign = sign.getBytes("UTF-8");
					os.write(refreshSign);
					os.flush();
					
					InputStream is = socketRoominfo.getInputStream();
					byte[] buffer = new byte[512];
					int length = is.read(buffer);
					if (length == -1)
						throw new IOException();
					String roominfo = new String(buffer, 0, length, "UTF-8");
					String[] roomArray = roominfo.split("\n"); // 방 제목, 인원수, 비번, 코드

					btn = new Button("입장");
					for (int i = 0; i < roomArray.length; i++) {
						String[] roomArrayinfo = roomArray[i].split(",");
						titleLabel = new Label(roomArrayinfo[0]);
						memberLabel = new Label(roomArrayinfo[1]);
						passwdLabel = new Label(roomArrayinfo[2]);
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
					closeWaitingRoom();
				}
			}
		};
		thread.start();
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
					closeWaitingRoom();
				}
			}
		};
		thread.start();
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
}
// main1 컨트롤러 달기, 리스트뷰 이름 정하기, 버튼에 함수 달기
