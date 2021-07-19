module ClientMain {
	exports application;
	requires javafx.controls;
	
	opens application to javafx.graphics, javafx.fxml;
}
