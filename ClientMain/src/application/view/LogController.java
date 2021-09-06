package application.view;

import java.io.IOException;
import java.net.URL;
//import java.net.URISyntaxException;
//import java.net.URL;
import java.sql.SQLException;
//import java.util.ResourceBundle;
import java.util.ResourceBundle;

import application.db.DBConnect;
import javafx.event.EventHandler;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
//import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.KeyEvent;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
//import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LogController implements Initializable {
	WaitController c2;

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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			login_id.setOnKeyPressed((EventHandler<? super KeyEvent>) new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent t) {
					if (t.getCode() == KeyCode.TAB) {
						login_id.setText(login_id.getText().trim());
						login_pw.requestFocus();
					}
				}
			});
			login_pw.setOnKeyPressed((EventHandler<? super KeyEvent>) new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent t) {
					if (t.getCode() == KeyCode.ENTER) {
						login_pw.setText(login_pw.getText().trim());
						try {
							login();
						} catch (ClassNotFoundException | SQLException | IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
			user_email.setOnKeyPressed((EventHandler<? super KeyEvent>) new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode() == KeyCode.TAB) {
						user_email.setText(user_email.getText().trim());
						user_id.requestFocus();
					}
				}
			});
			user_id.setOnKeyPressed((EventHandler<? super KeyEvent>) new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode() == KeyCode.TAB) {
						user_id.setText(user_id.getText().trim());
						user_pw.requestFocus();
					}
				}
			});
			user_pw.setOnKeyPressed((EventHandler<? super KeyEvent>) new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					if (event.getCode() == KeyCode.ENTER) {
						user_pw.setText(user_pw.getText().trim());
						try {
							signup();
						} catch (ClassNotFoundException | SQLException | IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
		} catch (Exception e) {
			System.out.println("initialize Exception");
			e.printStackTrace();
		}
	}

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
			tmp.setContentText("회원가입이 정상적으로 완료되었습니다.");
			tmp.setHeaderText("회원가입 완료");
			tmp.setTitle("회원가입");
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
			FXMLLoader f = new FXMLLoader(getClass().getResource("wait.fxml"));
			Parent parent = (Parent) f.load();
			
			c2 = new WaitController();
			c2 = f.getController();
			c2.DataInit(i);
			
			Stage backStage = new Stage();
			backStage.setScene(new Scene(parent));
			backStage.setTitle("Summer Vacation Project");
			backStage.setUserData(i); // i = 유저의 id
			backStage.setOnCloseRequest(event -> c2.closeWaitingRoom());
			backStage.show();
			
			Stage tmp = (Stage) login_id.getScene().getWindow();
			tmp.close();
		}
		else {
			System.out.println("로그인 실패");
		}
	}
	
	@FXML
	public void goSignup() throws IOException {
		FXMLLoader f = new FXMLLoader(getClass().getResource("signup.fxml"));
		Parent p = (Parent) f.load();
		Stage backStage = new Stage();
		backStage.setScene(new Scene(p));
		backStage.setTitle("Summer Vacation Project");
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
		backStage.setTitle("Summer Vacation Project");
		backStage.show();
		
		Stage tmp = (Stage) user_id.getScene().getWindow();
		tmp.close();
	}

}
