package application;

import javafx.event.ActionEvent;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginController implements Initializable{

	@FXML
	private AnchorPane loginPane;
	
	@FXML
	private Text gemsTitle;

	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private Button btnLogin;

	Connection con = null;
	PreparedStatement pst= null;
	ResultSet rs = null;

	public LoginController() {
		con = GEMSConnect.connect();
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	@FXML
	public void login(ActionEvent event) {

		//retrieve information entered to the username/password fields
		String username = usernameField.getText().toString();
		String password = passwordField.getText().toString();
		
		String sql = "SELECT * FROM AUTHENTICATION WHERE Username = ? and Password = ?";
		
		try {
			pst = con.prepareStatement(sql);
			pst.setString(1, username);
			pst.setString(2, password);
			rs = pst.executeQuery();
			//if entered values are not found in the db's authentication table, show error message
			if(!rs.next()) {
				infoBox("Please enter correct Username and Password", null, "Failed");
				usernameField.setText("");
				passwordField.setText("");
			}
			//if login was successful, proceed to homepage
			else {
				AnchorPane pane = FXMLLoader.load(getClass().getResource("Home.fxml"));
				loginPane.getChildren().setAll(pane);
			}	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//Info Box popup to be used for alerting user of incorrect password/username entry
	public static void infoBox(String infoMessage, String headerText, String title){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText(infoMessage);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }
	
}
