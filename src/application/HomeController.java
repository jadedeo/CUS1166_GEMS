package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventObject;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventTarget;

public class HomeController implements Initializable {

	@FXML
	private AnchorPane homePane;
	@FXML
	private Button btnDept1;

	@FXML
	private Button btnDept2;

	@FXML
	private Button btnDept3;

	@FXML
	private Button btnDept4;

	@FXML
	private Button btnDept5;

	@FXML
	private Button btnMasterList;

	@FXML
	private Button btnGenerateReport;



	Stage stage = new Stage();
	Scene scene;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}

	//this action event opens a page for the department button that has been selected
	@FXML
	public void openDepartmentWindow(ActionEvent event) throws IOException {

		String dept = ((Button) event.getSource()).getText();

		FXMLLoader loader = new FXMLLoader(getClass().getResource("Department.fxml"));
		Parent testParent = (Parent) loader.load();

		DepartmentController deptController = loader.getController();
		deptController.whichDept(dept);
		deptController.populateTable(dept);

		Scene testParentScene = new Scene(testParent);
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		window.setScene(testParentScene);
		window.show();

	}

	//this action event takes the user to the resource masterlist page
	@FXML
	void openMasterList(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MasterSWHW.fxml"));
		Parent testParent = (Parent) loader.load();

		MasterSWHWController masterSWHWController = loader.getController();
		masterSWHWController.populateTablesMaster();

		Scene testParentScene = new Scene(testParent);
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		window.setScene(testParentScene);
		window.show();

	}

	//this action event takes the user to the report generation page
	@FXML
	void generateReport(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("GenerateReports.fxml"));
		Parent testParent = (Parent) loader.load();

		Scene testParentScene = new Scene(testParent);
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		window.setScene(testParentScene);
		window.show();
	}

}
