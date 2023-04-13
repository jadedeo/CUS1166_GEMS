package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Department {
	
	private final SimpleStringProperty deptID;
	private final SimpleStringProperty deptName;
	
	public Department(String dID, String dName){
		this.deptID = new SimpleStringProperty(dID);
		this.deptName = new SimpleStringProperty(dName);
	}
	
	public String getDeptID() {
		return deptID.get();
	}
	public void setSoftID(String dID) {
		deptID.set(dID);
	}
	
	
	public String getDeptName() {
		return deptName.get();
	}
	public void setDeptName(String dName) {
		deptName.set(dName);
	}

	
	public StringProperty deptIDProperty() {
		return deptID;
	}
	public StringProperty deptNameProperty() {
		return deptName;
	}
}
