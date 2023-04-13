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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DepartmentController implements Initializable{

    @FXML
    private AnchorPane departmentPane;
    
    @FXML
    private Text deptPageHeader;

    @FXML
    private TableView<Employee> tableEmployee;
    
    @FXML
    private TableColumn<Employee, String> columnEmpId;
    @FXML
    private TableColumn<Employee, String> columnEmpName;
    @FXML
    private TableColumn<Employee, String> columnEmpTitle;

    @FXML
    private Button btnLoad;

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
    private Button btnGenerateReport;
    @FXML
    private Button btnMasterList;

    @FXML
    private Button btnViewEmp;
    
    @FXML
    private TextField empNameField;
    @FXML
    private TextField empTitleField;
    @FXML
    private TextField empIDField;
    
    @FXML
    private Button btnAddEmp;
    @FXML
    private Button btnRemoveEmp;

    @FXML
    private ChoiceBox<String> employeeChoiceBox;

    private String thisDepartmentName;
    private String thisDepartmentID;
    
    private ObservableList <Employee> deptEmployees;
    private ObservableList<String> employeeIDsObsList;
    
    ArrayList<String> employeeIDs = new ArrayList<String>();
    
    private Connection conn = GEMSConnect.connect();
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	
	}
    
	//retrieve the information for the selected department passed in from the homepage
    public void whichDept(String dept) {
    	
		deptPageHeader.setText(dept);
		thisDepartmentName = dept;
		
		ArrayList<String> departmentIDs = new ArrayList<String>();
		ArrayList<String> departmentNames = new ArrayList<String>();
		try {
			
			ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM DEPARTMENT;");
			while(rs.next()) {
				departmentIDs.add(rs.getString(1));
				departmentNames.add(rs.getString(2));
			}
			
			for(int i = 0; i < departmentNames.size(); i++) {
				if(departmentNames.get(i).equals(thisDepartmentName)) {
					thisDepartmentID = departmentIDs.get(i);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}	
	}
    
    //populate the table of employees for this specific department & the drop-down list of employees for the department
    public void populateTable(String dept) {
    	
    	employeeIDsObsList = FXCollections.observableArrayList();
    	employeeIDsObsList.clear();
    	employeeIDs.clear();
    	employeeChoiceBox.getItems().clear();
    	
    	try {
    		deptEmployees = FXCollections.observableArrayList();
			ResultSet rs = conn.createStatement().executeQuery("SELECT EMPLOYEE.EmpID, EMPLOYEE.EmpName, EMPLOYEE.EmpTitle FROM EMPLOYEE INNER JOIN DEPARTMENT ON EMPLOYEE.DeptID = DEPARTMENT.DeptID WHERE DEPARTMENT.DeptName = '" + dept + "';");
			
			while(rs.next()) {
				deptEmployees.add(new Employee(rs.getString(1), rs.getString(2), rs.getString(3)));
				tableEmployee.setItems(deptEmployees);
				employeeIDs.add(rs.getString(1));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
		columnEmpId.setCellValueFactory(new PropertyValueFactory<>("empID"));
		columnEmpName.setCellValueFactory(new PropertyValueFactory<>("empName"));
		columnEmpTitle.setCellValueFactory(new PropertyValueFactory<>("empTitle"));
		
		
		//construct drop-down
		for(int i = 0; i < employeeIDs.size(); i++) {
			employeeIDsObsList.add(employeeIDs.get(i));
			employeeChoiceBox.getItems().add(employeeIDs.get(i));;
		}
    }

    //take user to another department's page
	@FXML
	public void openDepartmentWindow(ActionEvent event) throws IOException{
		
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
	
	//get the employee selected from the drop-down & bring up their employee page
    @FXML
    void viewEmpClicked(ActionEvent event) throws IOException{
    	
    	try {
    		String empDepartmentName = deptPageHeader.getText();
	    	String fullChosenEmpID = employeeChoiceBox.getValue();
	    	
			ResultSet rs = conn.createStatement().executeQuery("SELECT EMPLOYEE.EmpName, EMPLOYEE.EmpTitle FROM EMPLOYEE INNER JOIN DEPARTMENT ON EMPLOYEE.DeptID = DEPARTMENT.DeptID WHERE EMPLOYEE.EmpID = '" + fullChosenEmpID + "' AND DEPARTMENT.DeptName = '" + empDepartmentName + "';");
			String chosenEmpName = "";
			String chosenEmpTitle = "";
			
			while (rs.next()) {
				chosenEmpName = rs.getString("EmpName");
				chosenEmpTitle = rs.getString("EmpTitle");
			}
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Employee.fxml"));
			Parent testParent = (Parent) loader.load();
			
			EmployeeController empController = loader.getController();
			empController.whichEmp(fullChosenEmpID, chosenEmpName, chosenEmpTitle, empDepartmentName);
			empController.populateTables(fullChosenEmpID);
	    	
			Scene testParentScene = new Scene(testParent);
			Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
			window.setScene(testParentScene);
			window.show();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    //if the user clicks "remove" employee
    @FXML
    void removeEmpClicked(ActionEvent event) {
    	
    	//make sure a selection has been made first
    	if(empIDField.getText().isEmpty()) {
			infoBox("Fill at least the ID field to remove an employee.", null, "Error");
    	}
    	//if a selection has been made
    	else {
	    	String deleteEmpID = empIDField.getText().toUpperCase();
	    	
	    	try {
				ResultSet rs = conn.createStatement().executeQuery("SELECT EmpName FROM EMPLOYEE WHERE EMPLOYEE.EmpID = '" + deleteEmpID + "' AND EMPLOYEE.DeptID = '" + thisDepartmentID + "';");
				String chosenEmpName = "";
				
				while (rs.next()) {
					chosenEmpName = rs.getString("EmpName");
				}
				
				//if user enter the ID of an employee not in this department, display an error
				if(chosenEmpName == ""){
					infoBox("Invalid employee selection.\nPlease enter an ID from the list above.", null, "Error");
					empIDField.setText("");
				}
	    	
				//delete records in EMPLOYEESW & EMPLOYEEHW for this employee before deleting the employee themselvess
				PreparedStatement deleteStatementEHW = conn.prepareStatement("DELETE FROM EMPLOYEEHW WHERE EmpID_EHW = '" + deleteEmpID + "';");
				deleteStatementEHW.executeUpdate();
				PreparedStatement deleteStatementESW = conn.prepareStatement("DELETE FROM EMPLOYEESW WHERE EmpID_ESW = '" + deleteEmpID + "';");
				deleteStatementESW.executeUpdate();
				PreparedStatement deleteStatementE = conn.prepareStatement("DELETE FROM EMPLOYEE WHERE EmpID = '" + deleteEmpID + "';");
				deleteStatementE.executeUpdate();
				
				empIDField.setText("");
				empNameField.setText("");
				empTitleField.setText("");
				populateTable(thisDepartmentName);
	    	}
	    	catch (SQLException e) {
				e.printStackTrace();
			}
    	}
    }
    
    //if the user clicks "add" employee
    @FXML
    void addEmpClicked(ActionEvent event) {
    	
    	//first check that all fields have been filled
    	if(empIDField.getText().isEmpty()||empNameField.getText().isEmpty()||empTitleField.getText().isEmpty() ) {
			infoBox("Fill all fields to add a new employee.", null, "Error");
    	}
    	//if all have been filled
    	else {
	    	String newEmpID = empIDField.getText().toUpperCase();
	    	String newEmpName = empNameField.getText();
	    	String newEmpTitle = empTitleField.getText();
	    	
	    	
	    	ArrayList<String> existingPK = new ArrayList<String>();
	    	
			try {
				//check whether an employee with the entered ID exists already in the system
				ResultSet rs = conn.createStatement().executeQuery("SELECT EmpID FROM EMPLOYEE;");
				while(rs.next()) {
					existingPK.add(rs.getString(1));
				}
				boolean exists = false;
				for(int i = 0; i <  existingPK.size(); i++) {
					if(existingPK.get(i).equals(newEmpID)) {
						exists = true;
					}
				}
				//if the ID exists, display an error
				if(exists == true) {
					infoBox("ID must be unique.", null, "Error");
					empIDField.setText("");
				}
				
				//if the ID doesn't exist, employee can be added
				else if(exists == false){
					Statement insertStatement = conn.createStatement();
					insertStatement.executeUpdate("INSERT INTO EMPLOYEE VALUES ('" + newEmpID + "','" + newEmpName + "','" + newEmpTitle + "','" + thisDepartmentID + "');");
					populateTable(thisDepartmentName);
					
					empIDField.setText("");
					empNameField.setText("");
					empTitleField.setText("");
					
					//take user to new employee's page (the tables will be empty, but the user can choose to add resources)
					FXMLLoader loader = new FXMLLoader(getClass().getResource("Employee.fxml"));
					Parent testParent = (Parent) loader.load();
					
					EmployeeController empController = loader.getController();
					empController.whichEmp(newEmpID, newEmpName, newEmpTitle, thisDepartmentName);
					empController.populateTables(newEmpID);
			    	
					Scene testParentScene = new Scene(testParent);
					Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
					window.setScene(testParentScene);
					window.show();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }
    
    //take user to report generation page
	@FXML
	void generateReport(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("GenerateReports.fxml"));
		Parent testParent = (Parent) loader.load();

		Scene testParentScene = new Scene(testParent);
		Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		window.setScene(testParentScene);
		window.show();
	}
    
	//take user to masterlist page
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
    
    //method used to create info box pop-ups
	public static void infoBox(String infoMessage, String headerText, String title){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText(infoMessage);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }
	
}
