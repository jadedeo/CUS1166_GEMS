package application;

import javafx.beans.property.SimpleStringProperty;

public class Employee {

	private SimpleStringProperty empID;
	private SimpleStringProperty empName;
	private SimpleStringProperty empTitle;
	
	Employee (String eID, String eName, String eTitle){
		this.empID = new SimpleStringProperty(eID);
		this.empName = new SimpleStringProperty(eName);
		this.empTitle = new SimpleStringProperty(eTitle);
		
	}
	
	public String getEmpID() {
		return empID.get();
	}
	public void setEmpID(String eID) {
		this.empID.set(eID);
	}
	
	
	public String getEmpName() {
		return empName.get();
	}
	public void setEmpName(String eName) {
		this.empName.set(eName);
	}
	
	
	public String getEmpTitle() {
		return empTitle.get();
	}
	public void setEmpTitle(String eTitle) {
		this.empTitle.set(eTitle);
	}
	
}
