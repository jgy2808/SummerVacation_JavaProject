package application.view;

import java.io.IOException;
//import java.net.URISyntaxException;
//import java.net.URL;
import java.sql.SQLException;
//import java.util.ResourceBundle;

import application.db.DBConnect;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
//import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.KeyEvent;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
//import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LogController{

	@FXML
	TextArea user_email;
	@FXML
	TextArea user_id;
	@FXML
	TextArea user_pw;
	@FXML
	TextArea login_id;
	@FXML
	TextArea login_pw;
	
	@FXML
	public void signup() throws ClassNotFoundException, SQLException, IOException {
		String e = user_email.getText();
		String i = user_id.getText();
		String p = user_pw.getText();
		System.out.println(e);
		DBConnect d1 = new DBConnect();
		d1.connect();
		int r = d1.signup(e,i,p);
		d1.close();
		if(r==1) {
			goLogin();
			Alert tmp = new Alert(AlertType.CONFIRMATION);
			tmp.setContentText("ȸ�������� ���������� �Ϸ�Ǿ����ϴ�.");
			tmp.setHeaderText("ȸ������ �Ϸ�");
			tmp.setTitle("ȸ������");
			tmp.show();
		}
	}
	
	@FXML
	public void login() throws ClassNotFoundException, SQLException, IOException {
		String i = login_id.getText();
		String p = login_pw.getText();
		System.out.println(i);
		DBConnect d2 = new DBConnect();
		d2.connect();
		int r = d2.login(i,p);
		d2.close();
		
		if(r == 1) {
			FXMLLoader f = new FXMLLoader(getClass().getResource("main1.fxml"));
			Parent parent = (Parent) f.load();
			
			Controller2 c2 = new Controller2();
			c2 = f.getController();
			c2.DataInit(i);
			
			Stage backStage = new Stage();
			backStage.setScene(new Scene(parent));
			backStage.setTitle("YSB");
			backStage.setUserData(i); // i = ������ id
			backStage.show();
			
			Stage tmp = (Stage) login_id.getScene().getWindow();
			tmp.close();
		}
		else {
			System.out.println("�α��� ����");
		}
	}
	
	@FXML
	public void goSignup() throws IOException {
		FXMLLoader f = new FXMLLoader(getClass().getResource("signup.fxml"));
		Parent p = (Parent) f.load();
		Stage backStage = new Stage();
		backStage.setScene(new Scene(p));
		backStage.setTitle("YSB");
		backStage.show();
		
		Stage tmp = (Stage) login_id.getScene().getWindow();
		tmp.close();
	}
	
	@FXML
	public void goLogin() throws IOException {
		FXMLLoader f = new FXMLLoader(getClass().getResource("login.fxml"));
		Parent p = (Parent) f.load();
		Stage backStage = new Stage();
		backStage.setScene(new Scene(p));
		backStage.setTitle("YSB");
		backStage.show();
		
		Stage tmp = (Stage) user_id.getScene().getWindow();
		tmp.close();
	}
	
	
	
}