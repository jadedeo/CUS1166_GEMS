package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EmployeeController implements Initializable {

    @FXML
    private AnchorPane accountingPane;
    
    @FXML
    private Text empPageHeader;
    
    @FXML
    private Text empTitleAndDepartment;
    
    @FXML
    private TableView<Software> tableSoftware;
    
    @FXML
    private TableColumn<Software, String> columnSWID;
    @FXML
    private TableColumn<Software, String> columnName;
    @FXML
    private TableColumn<Software, String> columnLicenseKey;
    @FXML
    private TableColumn<Software, String> columnExpiration;
    @FXML
    private TableColumn<Software, String> columnVersionNo;
    @FXML
    private TableColumn<Software, String> columnDescription;
    
    
    @FXML
    private TableView<Hardware> tableHardware;
    
    @FXML
    private TableColumn<Hardware, String> columnHWID;
    @FXML
    private TableColumn<Hardware, String> columnType;
    @FXML
    private TableColumn<Hardware, String> columnBrand;
    @FXML
    private TableColumn<Hardware, String> columnModel;
    @FXML
    private TableColumn<Hardware, String> columnSerialNo;
    @FXML
    private TableColumn<Hardware, String> columnDatePurchased;

    @FXML
    private Button btnAccounting;
    @FXML
    private Button btnFundAccounting;
    @FXML
    private Button btnMartketing;
    @FXML
    private Button btnLegal;
    @FXML
    private Button btnInformationTechnology;
    @FXML
    private Button btnGenerateReport;
    @FXML
    private Button btnMasterList;
    
    @FXML
    private Button btnEditEmployee;
    @FXML
    private Button btnReturnToDept;
    
    @FXML
    private Pane removePane;
    @FXML
    private Button btnYesRemove;
    @FXML
    private Button btnNoRemove;
    
    private String typeToRemove = "";
    private String selectedID = "";
    private String selectedSWName = "";
    private String selectedHWType = "";
    private String selectedHWBrand = "";
    private String selectedHWBrandType = "";
    
    private ObservableList <Software> employeeSoftware;
    private ObservableList <Hardware> employeeHardware;
    
    private String currentEmployeeID;
    private String currentEmployeeName;
    private String currentEmployeeTitle;
    private String currentEmployeeDepartment;
    private ArrayList<String> empSoftwareAL = new ArrayList<String>();
    private ArrayList<String> empHardwareAL = new ArrayList<String>();
    
    private Connection conn = GEMSConnect.connect();
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		removePane.setVisible(false);
		empSoftwareAL.clear();
		empHardwareAL.clear();
	}
    
	//recieve information about the selected employee, passed in from the previous department page
    public void whichEmp(String fullChosenEmpID, String chosenEmpName, String chosenEmpTitle, String chosenEmpDepartment) {
    	empPageHeader.setText(chosenEmpName);
    	String chosenEmpTitleAndDepartment = chosenEmpTitle + ", " + chosenEmpDepartment;
    	empTitleAndDepartment.setText(chosenEmpTitleAndDepartment);
    	
    	currentEmployeeID = fullChosenEmpID;
    	currentEmployeeName = chosenEmpName;
    	currentEmployeeTitle = chosenEmpTitle;
    	currentEmployeeDepartment = chosenEmpDepartment;
	}
    

    //if user chooses to add to employee information, open the editing screen & pass the current SW/HW forward
    @FXML
    void editEmployeeClicked(ActionEvent event) throws IOException{ 
    	
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("SWHW.fxml"));	
    	Parent testParent = (Parent) loader.load();
    	
		SWHWController swhwController = loader.getController();
		swhwController.editWhichEmp(currentEmployeeID, currentEmployeeName, currentEmployeeTitle, currentEmployeeDepartment);
		
		swhwController.getEmpSoftwareAL(empSoftwareAL);
		swhwController.getEmpHardwareAL(empHardwareAL);
		swhwController.populateTablesFull(currentEmployeeID);
    	
		Scene testParentScene = new Scene(testParent);
		Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
		window.setScene(testParentScene);
		window.show();
    }
    
    //when this page opens, populate the SW/HW tables for the selected employee
    public void populateTables(String chosenEmpID) {
    	
    	try {
    		
    		employeeSoftware = FXCollections.observableArrayList();
    		employeeHardware = FXCollections.observableArrayList();
			
    		ResultSet rs1 = conn.createStatement().executeQuery("SELECT SOFTWARE.SoftID, SOFTWARE.SoftName, SOFTWARE.LicenseKey, SOFTWARE.Expiration, SOFTWARE.VersionNo, SOFTWARE.SoftDescription FROM EMPLOYEE INNER JOIN EMPLOYEESW ON EmpID_ESW = EMPLOYEE.EmpID INNER JOIN SOFTWARE ON EMPLOYEESW.SoftID = SOFTWARE.SoftID WHERE EMPLOYEE.EmpID = '" + chosenEmpID + "';");
			ResultSet rs2 = conn.createStatement().executeQuery("SELECT HARDWARE.HardID, HARDWARE.HWDescription, HARDWARE.Brand, HARDWARE.Model, HARDWARE.SerialNumber, HARDWARE.DatePurchased FROM EMPLOYEE INNER JOIN EMPLOYEEHW ON EmpID_EHW = EMPLOYEE.EmpID INNER JOIN HARDWARE ON EMPLOYEEHW.HardID = HARDWARE.HardID WHERE EMPLOYEE.EmpID = '" + chosenEmpID + "';");
    		
			empSoftwareAL.clear();
			empHardwareAL.clear();
			
			while(rs1.next()) {
				employeeSoftware.add(new Software(rs1.getString(1), rs1.getString(2), rs1.getString(3), rs1.getString(4), rs1.getString(5), rs1.getString(6)));
				tableSoftware.setItems(employeeSoftware);
				empSoftwareAL.add(rs1.getString(2));
			
			}
			while(rs2.next()) {
				employeeHardware.add(new Hardware(rs2.getString(1), rs2.getString(2), rs2.getString(3), rs2.getString(4), rs2.getString(5), rs2.getString(6)));
				tableHardware.setItems(employeeHardware);
				empHardwareAL.add(rs2.getString(3) + " " + rs2.getString(2));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			System.out.println("ERROR: " + e);
		}
    	
		columnSWID.setCellValueFactory(new PropertyValueFactory<>("softID"));
		columnName.setCellValueFactory(new PropertyValueFactory<>("softName"));
		columnLicenseKey.setCellValueFactory(new PropertyValueFactory<>("softLicenseKey"));
		columnExpiration.setCellValueFactory(new PropertyValueFactory<>("softExpiration"));
		columnVersionNo.setCellValueFactory(new PropertyValueFactory<>("softVersionNo"));
		columnDescription.setCellValueFactory(new PropertyValueFactory<>("softDescription"));
  
		
		columnHWID.setCellValueFactory(new PropertyValueFactory<>("hardID"));
		columnType.setCellValueFactory(new PropertyValueFactory<>("hardType"));
		columnBrand.setCellValueFactory(new PropertyValueFactory<>("hardBrand"));
		columnModel.setCellValueFactory(new PropertyValueFactory<>("hardModel"));
		columnSerialNo.setCellValueFactory(new PropertyValueFactory<>("hardSerialNo"));
		columnDatePurchased.setCellValueFactory(new PropertyValueFactory<>("hardDatePurchased"));
    }
    
    //if an item in the SW table is selected, get its data
    @FXML
    void getSelectedSW(MouseEvent event) {

    	try {
    		//display the pane containing the option to remove selected item
	    	removePane.setVisible(true);
	    	//save "S" to the variable that determines the type of the selected item
	    	typeToRemove = "S";
	    	Software selectedSoftware = tableSoftware.getSelectionModel().getSelectedItem();
	    	selectedID = selectedSoftware.getSoftID();
	    	selectedSWName = selectedSoftware.getSoftName();
    	}
    	catch(Exception e) {
    		//from not clicking on a row
    		//ignore
    	}
    }
    
    //if an item in the HW table is selected, get its data
    @FXML
    void getSelectedHW(MouseEvent event) {
    	
    	try {
    		//display the pane containing the option to remove selected item
	    	removePane.setVisible(true);
	    	//save "H" to the variable that determines the type of the selected item
	    	typeToRemove = "H";
	    	Hardware selectedHardware = tableHardware.getSelectionModel().getSelectedItem();
	    	selectedID = selectedHardware.getHardID();
	    	selectedHWType = selectedHardware.getHardType();
	    	selectedHWBrand = selectedHardware.getHardBrand();
	    	selectedHWBrandType = selectedHWBrand + " " + selectedHWType;
    	}
    	catch(Exception e) {
    		//from not clicking on a row
    		//ignore
    	}
    }
   
    //if user chooses not to remove selected item , make no changes
    @FXML
    void noRemoveClicked(ActionEvent event) {
    	//hide remove pane again
    	removePane.setVisible(false);
    	selectedID = "";

    }
    
    //if user chooses to remove selected item
    @FXML
    void yesRemoveClicked(ActionEvent event) {
    	
    	//follow this procedure if the item is from the software table
    	if(typeToRemove.equals("S")) {    		
    		try {
    			//delete this software item from the employee's assignments
    			PreparedStatement deleteStatement = conn.prepareStatement("DELETE FROM EMPLOYEESW WHERE EmpID_ESW = '" + currentEmployeeID + "' AND SoftID = '" + selectedID + "';");
    			deleteStatement.executeUpdate();

    			//update the arraylist of the employee's software
    			for (int i = 0; i < empSoftwareAL.size(); i++) {
    				if (empSoftwareAL.get(i).equals(selectedSWName)) {
    					empSoftwareAL.remove(i);
    				}
    			}
    		}
    		
    		catch (SQLException e) {
    			e.printStackTrace();
    		}
    		
    		selectedSWName = "";
    		selectedID = "";
    		
    	}
    	//follow this procedure if the item is from the hardware table
    	else if(typeToRemove.equals("H")) {
    		try {
    			//delete only one assigment of this hardware item to the employee
    			PreparedStatement deleteStatement = conn.prepareStatement(
    					"DELETE FROM EMPLOYEEHW WHERE EmpID_EHW = '" + currentEmployeeID + "' AND hardID = '" + selectedID + "' LIMIT 1;");
    			deleteStatement.executeUpdate();
    			
    			//update the arraylist of the employee's hardware
    			for (int i = 0; i < empHardwareAL.size(); i++) {
    				if (empHardwareAL.get(i).equals(selectedHWBrandType)) {
    					empHardwareAL.remove(i);
    					break;
    				}
    			}
    		}
    		catch (SQLException e) {
    			e.printStackTrace();
    		}
    		
    		selectedID = "";
    		selectedHWBrand = "";
    		selectedHWType = "";
    		selectedHWBrandType = "";
    	}
    	
    	//repopulate tables after performing removal
    	populateTables(currentEmployeeID);
    	removePane.setVisible(false);
    }

    //open the window for selected department
    @FXML
    void openDepartmentWindow(ActionEvent event) throws IOException{
    	String dept = ((Button)event.getSource()).getText();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Department.fxml"));
		Parent testParent = (Parent) loader.load();
		
		DepartmentController deptController = loader.getController();
		deptController.whichDept(dept);
		deptController.populateTable(dept);
		
		Scene testParentScene = new Scene(testParent);
		Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
		window.setScene(testParentScene);
		window.show();
    }
    
    //open the report generation page
	@FXML
	void generateReport(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("GenerateReports.fxml"));
		Parent testParent = (Parent) loader.load();

		Scene testParentScene = new Scene(testParent);
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		window.setScene(testParentScene);
		window.show();
	}
    
	//open the resource management page
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
    
    //return to the page for the department to which this employee belongs
    @FXML
    void returnToDeptClicked(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Department.fxml"));
		Parent testParent = (Parent) loader.load();

		DepartmentController deptController = loader.getController();
		deptController.whichDept(currentEmployeeDepartment);
		deptController.populateTable(currentEmployeeDepartment);

		Scene testParentScene = new Scene(testParent);
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		window.setScene(testParentScene);
		window.show();
    }

}