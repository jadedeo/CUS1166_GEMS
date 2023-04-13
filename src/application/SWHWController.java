package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SWHWController implements Initializable {

	@FXML
	private AnchorPane SWHWPane;

	@FXML
	private Pane tablePanes;

	@FXML
	private Text SWHWPageHeader;

	@FXML
	private TableView<Software> tableSoftwareAll;

	@FXML
	private TableColumn<Software, String> columnSWIDAll;
	@FXML
	private TableColumn<Software, String> columnNameAll;
	@FXML
	private TableColumn<Software, String> columnLicenseKeyAll;
	@FXML
	private TableColumn<Software, String> columnExpirationAll;
	@FXML
	private TableColumn<Software, String> columnVersionNoAll;
	@FXML
	private TableColumn<Software, String> columnDescriptionAll;

	@FXML
	private TableView<Hardware> tableHardwareAll;

	@FXML
	private TableColumn<Hardware, String> columnHWIDAll;
	@FXML
	private TableColumn<Hardware, String> columnTypeAll;
	@FXML
	private TableColumn<Hardware, String> columnBrandAll;
	@FXML
	private TableColumn<Hardware, String> columnModelAll;
	@FXML
	private TableColumn<Hardware, String> columnSerialNoAll;
	@FXML
	private TableColumn<Hardware, String> columnDatePurchasedAll;

	@FXML
	private TextArea screenSW;
	@FXML
	private TextArea screenHW;

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
	private Button btnMasterList;

	@FXML
	private Button btnGenerateReport;

	@FXML
	private Button btnReturnToEmp;

	@FXML
	private Pane errorSWPane;
	@FXML
	private Button btnKeepSW;
	@FXML
	private Button btnRemoveSW;

	@FXML
	private Pane errorHWPane;
	@FXML
	private Button btnKeepHW;
	@FXML
	private Button btnRemoveHW;
	@FXML
	private Button btnAddToSelected;

	@FXML
	private Pane forExistingHW;
	@FXML
	private Button btnKeepExistingHW;
	@FXML
	private Button btnRemoveExistingHW;
	@FXML
	private Button btnAddToExisting;

	@FXML
	private Button btnSubmitSW;
	@FXML
	private Button btnSubmitHW;
	
    @FXML
    private Button btnClearSWSelections;
    @FXML
    private Button btnClearHWSelections;

	private String empID;
	private String empName;
	private String empTitle;
	private String empDepartmentName;

	private ObservableList<Software> softwareEmpDoesntHave;
	private ObservableList<Hardware> allHardware;
	

	private Connection conn = GEMSConnect.connect();

	private ArrayList<String> empSoftwareAL = new ArrayList<String>();
	private ArrayList<String> empHardwareAL = new ArrayList<String>();
	

	private ArrayList<String> softwareNameSelected = new ArrayList<String>();
	private ArrayList<String> softwareIDSelected = new ArrayList<String>();

	private ArrayList<String> hardwareBrandTypeSelected = new ArrayList<String>();
	private ArrayList<String> hardwareIDSelected = new ArrayList<String>();

	String selectedSWNameVar;
	String selectedSWIDVar;

	String selectedHWIDVar;
	String selectedHWBrandVar;
	String selectedHWTypeVar;
	String selectedHWBrandTypeVar;

	//when page opens, hide all "error" panes
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		errorSWPane.setVisible(false);
		errorHWPane.setVisible(false);
		forExistingHW.setVisible(false);
	}

	//receive informationn for employee, passed in from previous page
	public void editWhichEmp(String chosenEmpID, String currentEmpName, String currentEmpTitle,
			String currentEmpDepartmentName) {
		empID = chosenEmpID;
		empName = currentEmpName;
		empTitle = currentEmpTitle;
		empDepartmentName = currentEmpDepartmentName;
	}

	//get a list of employee's software
	public void getEmpSoftwareAL(ArrayList<String> empSWAL) {
		empSoftwareAL = empSWAL;
	}

	//get a list of employee's hardware
	public void getEmpHardwareAL(ArrayList<String> empHWAL) {
		empHardwareAL = empHWAL;
	}

	//clear all software selections
    @FXML
    void clearHWSelectionsClicked(ActionEvent event) {
    	screenHW.setText("");
    	hardwareBrandTypeSelected.clear();
    	hardwareIDSelected.clear();
    }
    
  //clear all hardware selections
    @FXML
    void clearSWSelectionsClicked(ActionEvent event) {
    	screenSW.setText("");
    	softwareNameSelected.clear();
    	softwareIDSelected.clear();
    }

	//if an item in the hardware table is clicked, perform this process
	@FXML
	private void displaySelectedHW(MouseEvent event) {
		//hide all "error panes" when a selection is made
		errorSWPane.setVisible(false);
		errorHWPane.setVisible(false);
		forExistingHW.setVisible(false);

		Hardware selectedHardware = tableHardwareAll.getSelectionModel().getSelectedItem();
		
		//if row is empty, output existing selections to text area
		if (selectedHardware == null) {
			screenSW.setText(printHardwareSelected());
		}

		//if row is not empty
		else {
			String hardwareID = selectedHardware.getHardID();
			String hardwareType = selectedHardware.getHardType();
			String hardwareBrand = selectedHardware.getHardBrand();
			selectedHWIDVar = hardwareID;
			selectedHWTypeVar = hardwareType;
			selectedHWBrandVar = hardwareBrand;
			selectedHWBrandTypeVar = selectedHWBrandVar + " " + selectedHWTypeVar;

			//if employee does not have item
			if (!empHardwareAL.contains(selectedHWBrandTypeVar)) {
				//if item has also not yet been selected, add it to text area of selections
				if (!hardwareBrandTypeSelected.contains(selectedHWBrandTypeVar)) {
					hardwareBrandTypeSelected.add(selectedHWBrandTypeVar);
					hardwareIDSelected.add(selectedHWIDVar);
				}
				//if item has already been selected, display pane with options to either add another or only keep existing selections
				else {
					errorHWPane.setVisible(true);
				}
			}
			//if employee already has item, display pane with options to either add another or not
			else {
				forExistingHW.setVisible(true);
			}
			
			screenHW.setText(printHardwareSelected());
		}
	}

	//if an item in the software table is clicked, perform this process
	@FXML
	private void displaySelectedSW(MouseEvent event) throws IOException {
		//hide all "error panes" when a selection is made
		errorSWPane.setVisible(false);
		errorHWPane.setVisible(false);
		forExistingHW.setVisible(false);

		//if row is empty, output existing info to text area
		Software selectedSoftware = tableSoftwareAll.getSelectionModel().getSelectedItem();
		if (selectedSoftware == null) {
			screenSW.setText(printSoftwareSelected());
		}
		
		//if row is not empty
		else {
			String softwareID = selectedSoftware.getSoftID();
			String softwareName = selectedSoftware.getSoftName();
			selectedSWIDVar = softwareID;
			selectedSWNameVar = softwareName;

			//add selection to text area if it isn't already present
			if (!softwareNameSelected.contains(softwareName)) {
				softwareNameSelected.add(softwareName);
				softwareIDSelected.add(softwareID);
			}
			//if item has previously been selected, display pane with options to remove selection or keep it
			else {
				errorSWPane.setVisible(true);
			}
		}
		
		screenSW.setText(printSoftwareSelected());
	}


	//format list of selected software & output to text area for displaying selections
	public String printSoftwareSelected() {
		String listAsString = "";
		for (int i = 0; i < softwareNameSelected.size(); i++) {
			listAsString = listAsString + " " + softwareNameSelected.get(i) + ", ";
		}
		return listAsString;
	}
	
	//format list of selected hardware & output to text area for displaying selections
	public String printHardwareSelected() {
		String listAsString = "";
		for (int i = 0; i < hardwareBrandTypeSelected.size(); i++) {
			listAsString = listAsString + " " + hardwareBrandTypeSelected.get(i) + ", ";
		}
		return listAsString;
	}

	//populate tables for employee:
	//software - only items the employee doesn't already have
	//hardware - all possible item
	public void populateTablesFull(String chosenEmpID) {

		try {
			allHardware = FXCollections.observableArrayList();
			softwareEmpDoesntHave =  FXCollections.observableArrayList();

			ResultSet rs1 = conn.createStatement().executeQuery("SELECT * FROM SOFTWARE;");
			ResultSet rs2 = conn.createStatement().executeQuery("SELECT * FROM HARDWARE;");

			while (rs1.next()) {
				if (!empSoftwareAL.contains(rs1.getString(2))) {
					softwareEmpDoesntHave.add(new Software(rs1.getString(1), rs1.getString(2), rs1.getString(3), rs1.getString(4),
							rs1.getString(5), rs1.getString(6)));
				}
				
				tableSoftwareAll.setItems(softwareEmpDoesntHave);
			}

			while (rs2.next()) {
				allHardware.add(new Hardware(rs2.getString(1), rs2.getString(2), rs2.getString(3), rs2.getString(4),
						rs2.getString(5), rs2.getString(6)));
				tableHardwareAll.setItems(allHardware);
			}
		}

		catch (SQLException e) {
			e.printStackTrace();
		}

		columnSWIDAll.setCellValueFactory(new PropertyValueFactory<>("softID"));
		columnNameAll.setCellValueFactory(new PropertyValueFactory<>("softName"));
		columnLicenseKeyAll.setCellValueFactory(new PropertyValueFactory<>("softLicenseKey"));
		columnExpirationAll.setCellValueFactory(new PropertyValueFactory<>("softExpiration"));
		columnVersionNoAll.setCellValueFactory(new PropertyValueFactory<>("softVersionNo"));
		columnDescriptionAll.setCellValueFactory(new PropertyValueFactory<>("softDescription"));

		columnHWIDAll.setCellValueFactory(new PropertyValueFactory<>("hardID"));
		columnTypeAll.setCellValueFactory(new PropertyValueFactory<>("hardType"));
		columnBrandAll.setCellValueFactory(new PropertyValueFactory<>("hardBrand"));
		columnModelAll.setCellValueFactory(new PropertyValueFactory<>("hardModel"));
		columnSerialNoAll.setCellValueFactory(new PropertyValueFactory<>("hardSerialNo"));
		columnDatePurchasedAll.setCellValueFactory(new PropertyValueFactory<>("hardDatePurchased"));
	}

	//methods to set & get indentifying information for the selected item (software or hardware)
	void setSelectedSWName(String name) {
		selectedSWNameVar = name;
	}
	String getSelectedSWName() {
		return selectedSWNameVar;
	}

	void setSelectedHWBrandType(String brandType) {
		selectedHWBrandTypeVar = brandType;
	}
	String getSelectedHWBrandType() {
		return selectedHWBrandTypeVar;
	}

	//if software has already been selected & user wishes to keep it selected, make no changes
	@FXML
	void keepSWClicked(ActionEvent event) {
		errorSWPane.setVisible(false);
	}

	//if software has already beenn selected & user wishes to remove it, remove from selections
	@FXML
	void removeSWClicked(ActionEvent event) {
		for (int i = 0; i < softwareNameSelected.size(); i++) {
			if (softwareNameSelected.get(i) == getSelectedSWName()) {
				softwareNameSelected.remove(i);
				softwareIDSelected.remove(i);
			}
		}
		errorSWPane.setVisible(false);
		screenSW.setText(printSoftwareSelected());
	}


	//if employee currently has selected hardware & user does not wish to add another, make no changes
	@FXML
	void keepHWClicked(ActionEvent event) {
		errorHWPane.setVisible(false);
	}

	//if hardware duplicate is clicked & user doesn't want to keep all, remove one of the duplicates
	@FXML
	void removeHWClicked(ActionEvent event) {

		for (int i = 0; i < hardwareBrandTypeSelected.size(); i++) {
			if (hardwareBrandTypeSelected.get(i).equals(getSelectedHWBrandType())) {
				hardwareBrandTypeSelected.remove(i);
				hardwareIDSelected.remove(i);
				break;
			}
		}
		
		errorHWPane.setVisible(false);
		screenHW.setText(printHardwareSelected());
	}

	//if user does wish to add another hardware duplicate, record selection
	@FXML
	void addToSelectedClicked(ActionEvent event) {
		hardwareBrandTypeSelected.add(selectedHWBrandTypeVar);
		hardwareIDSelected.add(selectedHWIDVar);

		errorHWPane.setVisible(false);
		screenHW.setText(printHardwareSelected());
	}

	//if user does not want to add another hardware duplicate, don't make any changes
	@FXML
	void keepExistingHWClicked(ActionEvent event) {
		forExistingHW.setVisible(false);
	}

	//for the case in which hardware selected is already part of employee's information & another should be added regardless
	@FXML
	void addToExistingClicked(ActionEvent event) {
		hardwareBrandTypeSelected.add(selectedHWBrandTypeVar);
		hardwareIDSelected.add(selectedHWIDVar);

		forExistingHW.setVisible(false);
		screenHW.setText(printHardwareSelected());
	}


	//get all of the selected software (those present in the text area) and add them to the employee
	@FXML
	void submitSWClicked(ActionEvent event) {
		errorSWPane.setVisible(false);
		errorHWPane.setVisible(false);
		forExistingHW.setVisible(false);

		for (int i = 0; i < softwareIDSelected.size(); i++) {
			String swElementID = softwareIDSelected.get(i);

			try {
				Statement insertStatement = conn.createStatement();
				insertStatement
						.executeUpdate("INSERT INTO EMPLOYEESW VALUES ('" + empID + "' , '" + swElementID + "');");
				empSoftwareAL.add(selectedSWNameVar);
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		infoBox("Additions made successfully.", null, null);
		screenSW.setText("");
		softwareNameSelected.clear();
		softwareIDSelected.clear();
	}

	//get all of the selected hardware (those present in the text area) and add them to the employee
	@FXML
	void submitHWClicked(ActionEvent event) {
		errorSWPane.setVisible(false);
		errorHWPane.setVisible(false);
		forExistingHW.setVisible(false);

		for (int i = 0; i < hardwareIDSelected.size(); i++) {
			String hwElementID = hardwareIDSelected.get(i);

			try {
				Statement insertStatement = conn.createStatement();
				insertStatement
						.executeUpdate("INSERT INTO EMPLOYEEHW VALUES ('" + empID + "' , '" + hwElementID + "');");
				empHardwareAL.add(selectedHWBrandTypeVar);
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		infoBox("Additions made successfully.", null, null);

		screenHW.setText("");
		hardwareBrandTypeSelected.clear();
		hardwareIDSelected.clear();
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
	
	//go back one screen to return to this employee's page
	@FXML
	void returnToEmpClicked(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Employee.fxml"));
		Parent testParent = (Parent) loader.load();

		EmployeeController empController = loader.getController();
		empController.whichEmp(empID, empName, empTitle, empDepartmentName);
		empController.populateTables(empID);

		Scene testParentScene = new Scene(testParent);
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		window.setScene(testParentScene);
		window.show();
	}

	//method for creation of info box pop-ups
	public static void infoBox(String infoMessage, String headerText, String title) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setContentText(infoMessage);
		alert.setTitle(title);
		alert.setHeaderText(headerText);
		alert.showAndWait();
	}

}
