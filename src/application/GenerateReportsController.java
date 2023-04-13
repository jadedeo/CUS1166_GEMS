package application;

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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class GenerateReportsController implements Initializable {

	@FXML
	private AnchorPane generateReportsPane;

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
	private Button btnCompleteReport;
	@FXML
	private Button btnDepartmentReport;
	@FXML
	private Button btnHardwareReport;
	
    @FXML
    private ChoiceBox<String> hwChoiceBox;
	@FXML
	private ChoiceBox<String> deptChoiceBox;

	private ObservableList<String> departmentNameObsList;
	private ObservableList<String> hardwareBrandTypeModelObsList;

	String selectedDepartmentID;
	String selectedDepartmentName;

	ArrayList<String> departmentIDs = new ArrayList<String>();
	ArrayList<String> departmentNames = new ArrayList<String>();
	
	String selectedHWID;
	
	ArrayList<String> hwBrandTypeModels = new ArrayList<String>();
	ArrayList<String> hwIDs = new ArrayList<String>();

	private Connection conn = GEMSConnect.connect();
	
	
	//when the page is opened, create the department &  hardware drop-down boxes with items in the db
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		departmentNameObsList = FXCollections.observableArrayList();
		hardwareBrandTypeModelObsList = FXCollections.observableArrayList();
		
		try {
			//department drop-down
			ResultSet rs1 = conn.createStatement().executeQuery("SELECT * FROM DEPARTMENT;");
			while (rs1.next()) {
				departmentNameObsList.add(rs1.getString(2));
				departmentIDs.add(rs1.getString(1));
				departmentNames.add(rs1.getString(2));
				deptChoiceBox.setItems(departmentNameObsList);
			}
			//hardware drop-down
			ResultSet rs2 = conn.createStatement().executeQuery("SELECT * FROM HARDWARE;");
			while (rs2.next()) {
				hardwareBrandTypeModelObsList.add(rs2.getString(3) + " " + rs2.getString(2) + " | Model: " + rs2.getString(4));
				hwIDs.add(rs2.getString(1));
				hwBrandTypeModels.add(rs2.getString(3) + " " + rs2.getString(2) + " | Model: " + rs2.getString(4));
				hwChoiceBox.setItems(hardwareBrandTypeModelObsList);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//generating a report for a selected piece of hardware, pulling a list of employees having it
    @FXML
    void hardwareReportClicked(ActionEvent event) {
    	
    	System.out.println("CLICKED");
    	String chosenHW = hwChoiceBox.getValue();
    	
    	//if a selectionn is not made before the button is clicked, display a prompt to specify hardware
    	if(chosenHW == null) {
			infoBox("Please select a piece of hardware.", null, "Invalid Selection");
		}
    	//if a valid selection is made, create report
    	else {
    		for(int i = 0; i < hwBrandTypeModels.size(); i++) {
	    		if(hwBrandTypeModels.get(i).equals(chosenHW)) {
	    			selectedHWID = hwIDs.get(i);
	    			break;
	    		}
	    	}
	    	
    		//name output file
	    	String generationTime = new SimpleDateFormat("MM-dd-yyyy | HH-mm").format(new Date());
			String filePath = "GEMS Report | " + chosenHW + " | " + generationTime + ".txt";
			File outputFile = new File(filePath);
			
			try {
				//write headers & info to file
				FileWriter outFile = new FileWriter(outputFile);
				outFile.write("GEMS Report\n" + chosenHW + "\n\nGenerated: " + generationTime + "\n\n\n");
		
				//create list of department info to be consulted for each employee
				ArrayList<String> allDepartmentNames = new ArrayList<String>();
				ArrayList<String> allDepartmentIDs = new ArrayList<String>();
				
				Statement st2 = conn.createStatement();
				ResultSet rs2 = st2.executeQuery("SELECT * FROM DEPARTMENT;");
				while(rs2.next()) {
					String deptID = rs2.getString(1);
					String deptName = rs2.getString(2);
					
					allDepartmentIDs.add(deptID);
					allDepartmentNames.add(deptName);
				}
				
				//get all the employees having the selected hardware
				Statement st1 = conn.createStatement();
				ResultSet rs1 = st1.executeQuery("SELECT DISTINCT EMPLOYEE.EmpID, EMPLOYEE.EmpName, EMPLOYEE.EmpTitle, EMPLOYEE.DeptID from EMPLOYEE INNER JOIN EMPLOYEEHW ON EmpID_EHW = EMPLOYEE.EmpID WHERE EMPLOYEEHW.HardID = '" + selectedHWID + "';");
				int empCount = 0;
				while (rs1.next()) {
					empCount++;
					String empID = rs1.getString(1);
					String empName = rs1.getString(2);
					String empTitle = rs1.getString(3);
					String empDeptID = rs1.getString(4);
					String empDeptName = "";
					
					for(int i = 0; i < allDepartmentIDs.size(); i++) {
						if(allDepartmentIDs.get(i).equals(empDeptID)) {
							empDeptName = allDepartmentNames.get(i);
						}
					}
					outFile.write("\n" + empCount + ".   " + empID + "   " + empName + "\t\t" + empTitle + ", " + empDeptName + "\n");
				}
				outFile.close();
				openReport(outputFile);
			}
			catch (SQLException | IOException e) {
				e.printStackTrace();
			}
    	}
    }

    //generate a report for a specific department, including employees & their allocated software & hardware
	@FXML
	void departmentReportClicked(ActionEvent event) {

		selectedDepartmentName = deptChoiceBox.getValue();
		
		//if a department selection has not been made, prompt the user to do so
		if(selectedDepartmentName == null) {
			infoBox("Please select a department.", null, "Invalid Selection");
		}
		//if a valid selection has been made, generate a report
		else {
			for (int i = 0; i < departmentNames.size(); i++) {
				if (departmentNames.get(i).equals(selectedDepartmentName)) {
					selectedDepartmentID = departmentIDs.get(i);
				}
			}
	
			//create & name output file
			String generationTime = new SimpleDateFormat("MM-dd-yyyy | HH-mm").format(new Date());
			String filePath = "GEMS Report | "+ selectedDepartmentName + " | " + generationTime + ".txt";
			File outputFile = new File(filePath);
			try {
				FileWriter outFile = new FileWriter(outputFile);
				outFile.write("GEMS Report\n" + selectedDepartmentName + " Department\n\nGenerated: " + generationTime + "\n\n\n");
		
				//get & write information of all employees in this department
				Statement st1 = conn.createStatement();
				ResultSet rs1 = st1.executeQuery("SELECT * FROM EMPLOYEE WHERE EMPLOYEE.DeptID = '" + selectedDepartmentID + "';");
				int empDeptCount = 0;
				while (rs1.next()) {
					empDeptCount++;
					String empID = rs1.getString(1);
					String empName = rs1.getString(2);
					String empTitle = rs1.getString(3);
	
					outFile.write("\n" + empDeptCount + ".   " + empID + "   " + empName + ", " + empTitle + "\n");
	
					//get & write all the software information for this employee
					outFile.write("\n\t    SOFTWARE:\n");
					Statement st2 = conn.createStatement();
					ResultSet rs2 = st2.executeQuery(
							"SELECT SOFTWARE.SoftID, SOFTWARE.SoftName, SOFTWARE.LicenseKey, SOFTWARE.Expiration, SOFTWARE.VersionNo, SOFTWARE.SoftDescription FROM EMPLOYEE INNER JOIN EMPLOYEESW ON EmpID_ESW = EMPLOYEE.EmpID INNER JOIN SOFTWARE ON EMPLOYEESW.SoftID = SOFTWARE.SoftID WHERE EMPLOYEE.EmpID = '"
									+ empID + "';");
					int swCount = 0;
					while (rs2.next()) {
						swCount++;
						String softID = rs2.getString(1);
						String softName = rs2.getString(2);
						String softLicenseKey = rs2.getString(3);
						String softExpiration = rs2.getString(4);
						String softVersionNo = rs2.getString(5);
						String softDescription = rs2.getString(6);
	
						outFile.write("\t    " + swCount + ".\t" + softID + "\t" + softName + "\n\t\t\tLicense Key:\t\t"
								+ softLicenseKey + "\n\t\t\tExpiration:\t\t" + softExpiration + "\n\t\t\tVersion No.:\t\t"
								+ softVersionNo + "\n\t\t\tDescription:\t\t" + softDescription + "\n\n");
	
					}
	
					//get & write all the hardware information for this employee
					outFile.write("\n\t    HARDWARE:\n");
					Statement st4 = conn.createStatement();
					ResultSet rs4 = st4.executeQuery(
							"SELECT HARDWARE.HardID, HARDWARE.HWDescription, HARDWARE.Brand, HARDWARE.Model, HARDWARE.SerialNumber, HARDWARE.DatePurchased FROM EMPLOYEE INNER JOIN EMPLOYEEHW ON EmpID_EHW = EMPLOYEE.EmpID INNER JOIN HARDWARE ON EMPLOYEEHW.HardID = HARDWARE.HardID WHERE EMPLOYEE.EmpID = '"
									+ empID + "';");
					int hwCount = 0;
					while (rs4.next()) {
						hwCount++;
						String hardID = rs4.getString(1);
						String hardType = rs4.getString(2);
						String hardBrand = rs4.getString(3);
						String hardModel = rs4.getString(4);
						String hardSerialNo = rs4.getString(5);
						String hardDatePurchased = rs4.getString(6);
	
						outFile.write("\t    " + hwCount + ".\t" + hardID + "\t" + hardBrand + " " + hardType
								+ "\n\t\t\tModel:\t\t\t" + hardModel + "\n\t\t\tSerial No.:\t\t" + hardSerialNo
								+ "\n\t\t\tDate Purchased:\t\t" + hardDatePurchased + "\n");
					}
				}
	
				outFile.close();
				openReport(outputFile);
			} catch (SQLException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	//generate a complete report of all department &  employee information in the system
	@FXML
	void completeReportClicked(ActionEvent event) {

		//create & name output file
		String generationTime = new SimpleDateFormat("MM-dd-yyyy | HH-mm").format(new Date());
		String filePath = "GEMS Report | " + generationTime + ".txt";
		File outputFile = new File(filePath);
		
		try {
			FileWriter outFile = new FileWriter(outputFile);
			outFile.write("GEMS Report\nComplete System\n\nGenerated: " + generationTime + "\n\n\n");

			//get & write information for each department
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM DEPARTMENT;");
			while (rs.next()) {
				String deptID = rs.getString(1);
				String deptName = rs.getString(2);

				outFile.write(
						"\n----- " + deptName + " (" + deptID + ") -------------------------------------------\n\n");

				//get & write information for each employee in the department
				Statement st2 = conn.createStatement();
				ResultSet rs2 = st2.executeQuery("SELECT * FROM EMPLOYEE WHERE EMPLOYEE.DeptID = '" + deptID + "';");
				int empDeptCount = 0;
				while (rs2.next()) {
					empDeptCount++;
					String empID = rs2.getString(1);
					String empName = rs2.getString(2);
					String empTitle = rs2.getString(3);

					outFile.write("\n" + empDeptCount + ".   " + empID + "   " + empName + ", " + empTitle + "\n");

					//get & write information for employee's software
					outFile.write("\n\t    SOFTWARE:\n");
					Statement st3 = conn.createStatement();
					ResultSet rs3 = st3.executeQuery(
							"SELECT SOFTWARE.SoftID, SOFTWARE.SoftName, SOFTWARE.LicenseKey, SOFTWARE.Expiration, SOFTWARE.VersionNo, SOFTWARE.SoftDescription FROM EMPLOYEE INNER JOIN EMPLOYEESW ON EmpID_ESW = EMPLOYEE.EmpID INNER JOIN SOFTWARE ON EMPLOYEESW.SoftID = SOFTWARE.SoftID WHERE EMPLOYEE.EmpID = '"
									+ empID + "';");
					int swCount = 0;
					while (rs3.next()) {
						swCount++;
						String softID = rs3.getString(1);
						String softName = rs3.getString(2);
						String softLicenseKey = rs3.getString(3);
						String softExpiration = rs3.getString(4);
						String softVersionNo = rs3.getString(5);
						String softDescription = rs3.getString(6);

						outFile.write("\t    " + swCount + ".\t" + softID + "\t" + softName + "\n\t\t\tLicense Key:\t\t"
								+ softLicenseKey + "\n\t\t\tExpiration:\t\t" + softExpiration
								+ "\n\t\t\tVersion No.:\t\t" + softVersionNo + "\n\t\t\tDescription:\t\t"
								+ softDescription + "\n\n");

					}

					//get & write information for employee's hardware
					outFile.write("\n\t    HARDWARE:\n");
					Statement st4 = conn.createStatement();
					ResultSet rs4 = st4.executeQuery(
							"SELECT HARDWARE.HardID, HARDWARE.HWDescription, HARDWARE.Brand, HARDWARE.Model, HARDWARE.SerialNumber, HARDWARE.DatePurchased FROM EMPLOYEE INNER JOIN EMPLOYEEHW ON EmpID_EHW = EMPLOYEE.EmpID INNER JOIN HARDWARE ON EMPLOYEEHW.HardID = HARDWARE.HardID WHERE EMPLOYEE.EmpID = '"
									+ empID + "';");
					int hwCount = 0;
					while (rs4.next()) {
						hwCount++;
						String hardID = rs4.getString(1);
						String hardType = rs4.getString(2);
						String hardBrand = rs4.getString(3);
						String hardModel = rs4.getString(4);
						String hardSerialNo = rs4.getString(5);
						String hardDatePurchased = rs4.getString(6);

						outFile.write("\t    " + hwCount + ".\t" + hardID + "\t" + hardBrand + " " + hardType
								+ "\n\t\t\tModel:\t\t\t" + hardModel + "\n\t\t\tSerial No.:\t\t" + hardSerialNo
								+ "\n\t\t\tDate Purchased:\t\t" + hardDatePurchased + "\n");
					}
				}
			}
			outFile.close();
			openReport(outputFile);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	
	//automatically open report once generated (called at the end of each report's generation action method)
	void openReport(File fileName) {
		try {
			if (!Desktop.isDesktopSupported()) {
				infoBox("Unable to open file.", null, "Error");
				return;
			}
			Desktop desktop = Desktop.getDesktop();
			if (fileName.exists()) {
				desktop.open(fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	//take user to page of selected department
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

	//method to generate info box pop-ups
	public static void infoBox(String infoMessage, String headerText, String title){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText(infoMessage);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }
}