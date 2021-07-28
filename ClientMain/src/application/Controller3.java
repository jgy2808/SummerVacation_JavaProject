package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

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
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Controller3 implements Initializable{

	@FXML
	private Button exitBtnComponent;
	@FXML
	private TextArea chat_text;
	@FXML
	private ListView<BorderPane> chat_list;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			chat_text.setOnKeyPressed((EventHandler<? super KeyEvent>) new EventHandler<KeyEvent>() {
			    @Override
			    public void handle(KeyEvent t) {
		        	BorderPane p = new BorderPane();
			        if (t.getCode() == KeyCode.ENTER) {
			        	Label findText = new Label(chat_text.getText().trim() + "      ");
			        	p.setRight(findText);
			        	chat_text.setText("");
			        	chat_list.getItems().add(p);
			        } else if (t.getCode() == KeyCode.ESCAPE) {
			        	System.out.println("ESC");
			        } else if (t.getCode() == KeyCode.F1) {
			        	BorderPane u = new BorderPane();
			        	BorderPane d = new BorderPane();
			        	Label examText = new Label("\t\t상대방의 메시지입니다.");
			        	Label examName = new Label("홍길동");
			        	u.setLeft(examName);
			        	d.setLeft(examText);
			        	p.setTop(u);
			        	p.setBottom(d);
			        	chat_list.getItems().add(p);
			        }
			    }
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	// 실사용 하지 않는 컨트롤러
	
	@FXML
	public void exit_btn() throws IOException {
		FXMLLoader f = new FXMLLoader(getClass().getResource("main1.fxml"));
		Parent p = (Parent) f.load();
		Stage backStage = new Stage();
		backStage.setScene(new Scene(p));
		backStage.setTitle("YSB");
		backStage.show();
		
		Stage tmp = (Stage) exitBtnComponent.getScene().getWindow();
		tmp.close();
	}
	
	@FXML
	public void change_btn() {
		// 닉네임 변경 버튼
		System.out.println("잉");
	}
	
	
}
