package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MasterSWHWController implements Initializable {

	@FXML
	private AnchorPane SWHWMasterPane;

	@FXML
	private Text SWHWMasterPageHeader;

	@FXML
	private TableView<Software> tableSoftwareMaster;

	@FXML
	private TableColumn<Software, String> columnSWIDMaster;
	@FXML
	private TableColumn<Software, String> columnNameMaster;
	@FXML
	private TableColumn<Software, String> columnLicenseKeyMaster;
	@FXML
	private TableColumn<Software, String> columnExpirationMaster;
	@FXML
	private TableColumn<Software, String> columnVersionNoMaster;
	@FXML
	private TableColumn<Software, String> columnDescriptionMaster;

	@FXML
	private TableView<Hardware> tableHardwareMaster;

	@FXML
	private TableColumn<Hardware, String> columnHWIDMaster;
	@FXML
	private TableColumn<Hardware, String> columnTypeMaster;
	@FXML
	private TableColumn<Hardware, String> columnBrandMaster;
	@FXML
	private TableColumn<Hardware, String> columnModelMaster;
	@FXML
	private TableColumn<Hardware, String> columnSerialNoMaster;
	@FXML
	private TableColumn<Hardware, String> columnDatePurchasedMaster;

	@FXML
	private Button btnAccounting;

	@FXML
	private Button btnFundAccounting;

	@FXML
	private Button btnMarketing;

	@FXML
	private Button btnLegal;

	@FXML
	private Button btnInformationTechnology;

    @FXML
    private Button btnGenerateReport;
	
    @FXML
    private Button btnMasterList;

	private ObservableList<Software> masterSoftware;
	private ObservableList<Hardware> masterHardware;

	@FXML
	private TextField swIDField;
	@FXML
	private TextField swNameField;
	@FXML
	private TextField swLicenseKeyField;
	@FXML
	private TextField swExpirationField;
	@FXML
	private TextField swVersionNoField;
	@FXML
	private TextField swDescriptionField;
	
	
    @FXML
    private TextField hwIDField;
    @FXML
    private TextField hwTypeField;
    @FXML
    private TextField hwBrandField;
    @FXML
    private TextField hwModelField;
    @FXML
    private TextField hwSerialNoField;
    @FXML
    private TextField hwDatePurchasedField;
    
	@FXML
	private Button btnAddSW;
	@FXML
	private Button btnRemoveSW;
	
    @FXML
    private Button btnAddHW;
    @FXML
    private Button btnRemoveHW;
    
	private Connection conn = GEMSConnect.connect();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}

	//add software
	@FXML
	void addSWClicked(ActionEvent event) {
		
		//verify that all fields have been filled
    	if(swIDField.getText().isEmpty()||swNameField.getText().isEmpty()||swLicenseKeyField.getText().isEmpty()||swExpirationField.getText().isEmpty()||swVersionNoField.getText().isEmpty()||swDescriptionField.getText().isEmpty()) {
			infoBox("Fill all fields to add a new software application.", null, "Error");
    	}
    	
    	//if all fields have been filled
    	else {
			String newID = swIDField.getText().toUpperCase();
			String newName = swNameField.getText();
			String newLicenseKey = swLicenseKeyField.getText();
			String newExpiration = swExpirationField.getText();
			String newVersionNo = swVersionNoField.getText();
			String newDescription = swDescriptionField.getText();
			ArrayList<String> existingPK = new ArrayList<String>();
			
			try {
				//get list of existing software IDs
				ResultSet rs = conn.createStatement().executeQuery("SELECT SoftID FROM SOFTWARE;");
				while(rs.next()) {
					existingPK.add(rs.getString(1));
				}
				
				boolean exists = false;
				//if entered ID exists already, display error
				for(int i = 0; i <  existingPK.size(); i++) {
					if(existingPK.get(i).equals(newID)) {
						exists = true;
					}
				}
				
				if(exists == true) {
					infoBox("ID must be unique.", null, "Error");
					swIDField.setText("");
				}
				
				//if entered ID does not exist yet, perform addition
				else if(exists == false){
					Statement insertStatement = conn.createStatement();
					insertStatement.executeUpdate("INSERT INTO SOFTWARE VALUES ('" + newID + "','" + newName + "','"
							+ newLicenseKey + "','" + newExpiration + "','" + newVersionNo + "','" + newDescription + "');");
					populateTablesMaster();
					swIDField.setText("");
					swNameField.setText("");
					swLicenseKeyField.setText("");
					swExpirationField.setText("");
					swVersionNoField.setText("");
					swDescriptionField.setText("");
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
    	}
	}

    //remove software selected
	@FXML
	void removeSWClicked(ActionEvent event) {
		
		//first ensure that the ID field is filled
		if(swIDField.getText().isEmpty()) {
			infoBox("Fill at least the ID field to remove a software application.", null, "Error");
		}
		//if field has been filled, perform removal
		else {
			String removeID = swIDField.getText().toUpperCase();
	
			try {
				//first remove any assignments to employees
				//then remove from software table
				Statement removeStatement = conn.createStatement();
				Statement removeStatement2 = conn.createStatement();
				removeStatement.executeUpdate("DELETE FROM EMPLOYEESW WHERE SoftID = '" + removeID + "';");
				removeStatement2.executeUpdate("DELETE FROM SOFTWARE WHERE SoftID = '" + removeID + "';");
				
				populateTablesMaster();
				swIDField.setText("");
				swNameField.setText("");
				swLicenseKeyField.setText("");
				swExpirationField.setText("");
				swVersionNoField.setText("");
				swDescriptionField.setText("");
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//add hardware
    @FXML
    void addHWClicked(ActionEvent event) {
    	
    	//verify that all fields have been filled
    	if(hwIDField.getText().isEmpty()||hwTypeField.getText().isEmpty()||hwBrandField.getText().isEmpty()||hwModelField.getText().isEmpty()||hwSerialNoField.getText().isEmpty()||hwDatePurchasedField.getText().isEmpty()) {
			infoBox("Fill all fields to add a new software application.", null, "Error");
    	}
    	
    	//if all fields have been filled
    	else {
			String newID = hwIDField.getText().toUpperCase();
			String newType = hwTypeField.getText();
			String newBrand = hwBrandField.getText();
			String newModel = hwModelField.getText();
			String newSerialNo = hwSerialNoField.getText();
			String newDatePurchased = hwDatePurchasedField.getText();
			ArrayList<String> existingPK = new ArrayList<String>();
	
			try {
	
				//get list of existing hardware IDs
				ResultSet rs = conn.createStatement().executeQuery("SELECT HardID FROM HARDWARE;");
				while(rs.next()) {
					existingPK.add(rs.getString(1));
				}
				
				boolean exists = false;
				//if entered ID exists already, display error
				for(int i = 0; i <  existingPK.size(); i++) {
					if(existingPK.get(i).equals(newID)) {
						exists = true;
					}
				}
				
				if(exists == true) {
					infoBox("ID must be unique.", null, "Error");
					hwIDField.setText("");
				}
				
				//if entered ID does not exist yet, perform addition
				else if(exists == false){
					Statement insertStatement = conn.createStatement();
					insertStatement.executeUpdate("INSERT INTO HARDWARE VALUES ('" + newID + "','" + newType + "','"
							+ newBrand + "','" + newModel + "','" + newSerialNo + "','" + newDatePurchased + "');");
					populateTablesMaster();
					hwIDField.setText("");
					hwTypeField.setText("");
					hwBrandField.setText("");
					hwModelField.setText("");
					hwSerialNoField.setText("");
					hwDatePurchasedField.setText("");
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }
	
    //remove hardware selected
    @FXML
    void removeHWClicked(ActionEvent event) {
    	
    	//first ensure that the ID field is filled
		if(hwIDField.getText().isEmpty()) {
			infoBox("Fill at least the ID field to remove a piece of hardware.", null, "Error");
		}
		//if field has been filled, perform removal
		else {
			String removeID = hwIDField.getText().toUpperCase();
	
			try {
				//first remove any assignments to employees
				//then remove from hardware table
				Statement removeStatement = conn.createStatement();
				Statement removeStatement2 = conn.createStatement();
				removeStatement.executeUpdate("DELETE FROM EMPLOYEEHW WHERE HardID = '" + removeID + "';");
				removeStatement2.executeUpdate("DELETE FROM HARDWARE WHERE HardID = '" + removeID + "';");
				
				populateTablesMaster();
				hwIDField.setText("");
				hwTypeField.setText("");
				hwBrandField.setText("");
				hwModelField.setText("");
				hwSerialNoField.setText("");
				hwDatePurchasedField.setText("");
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
    }

    //populate tables with all software and hardware in the system
	public void populateTablesMaster() {
		try {

			masterSoftware = FXCollections.observableArrayList();
			masterHardware = FXCollections.observableArrayList();

			ResultSet rs1 = conn.createStatement().executeQuery("SELECT * FROM SOFTWARE;");
			ResultSet rs2 = conn.createStatement().executeQuery("SELECT * FROM HARDWARE;");


			while (rs1.next()) {
				masterSoftware.add(new Software(rs1.getString(1), rs1.getString(2), rs1.getString(3), rs1.getString(4),
						rs1.getString(5), rs1.getString(6)));
				tableSoftwareMaster.setItems(masterSoftware);

			}

			while (rs2.next()) {
				masterHardware.add(new Hardware(rs2.getString(1), rs2.getString(2), rs2.getString(3), rs2.getString(4),
						rs2.getString(5), rs2.getString(6)));
				tableHardwareMaster.setItems(masterHardware);
			}
		}

		catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e);
		}

		columnSWIDMaster.setCellValueFactory(new PropertyValueFactory<>("softID"));
		columnNameMaster.setCellValueFactory(new PropertyValueFactory<>("softName"));
		columnLicenseKeyMaster.setCellValueFactory(new PropertyValueFactory<>("softLicenseKey"));
		columnExpirationMaster.setCellValueFactory(new PropertyValueFactory<>("softExpiration"));
		columnVersionNoMaster.setCellValueFactory(new PropertyValueFactory<>("softVersionNo"));
		columnDescriptionMaster.setCellValueFactory(new PropertyValueFactory<>("softDescription"));

		columnHWIDMaster.setCellValueFactory(new PropertyValueFactory<>("hardID"));
		columnTypeMaster.setCellValueFactory(new PropertyValueFactory<>("hardType"));
		columnBrandMaster.setCellValueFactory(new PropertyValueFactory<>("hardBrand"));
		columnModelMaster.setCellValueFactory(new PropertyValueFactory<>("hardModel"));
		columnSerialNoMaster.setCellValueFactory(new PropertyValueFactory<>("hardSerialNo"));
		columnDatePurchasedMaster.setCellValueFactory(new PropertyValueFactory<>("hardDatePurchased"));
	}

	//take user to page for the selected department
	@FXML
	void openDepartmentWindow(ActionEvent event) throws IOException {
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
	
	//method for creation of info box pop-ups
	public static void infoBox(String infoMessage, String headerText, String title){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText(infoMessage);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }

	//take user to report generating page
	@FXML
	void generateReport(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("GenerateReports.fxml"));
		Parent testParent = (Parent) loader.load();

		Scene testParentScene = new Scene(testParent);
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		window.setScene(testParentScene);
		window.show();
	}
	
	//take user to resource masterlist page
    @FXML
    void openMasterList(ActionEvent event) throws IOException{
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("MasterSWHW.fxml"));
		Parent testParent = (Parent) loader.load();
		
		MasterSWHWController masterSWHWController = loader.getController();
		masterSWHWController.populateTablesMaster();
    	
    	Scene testParentScene = new Scene(testParent);
		Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
		window.setScene(testParentScene);
		window.show();
    	
    }
}