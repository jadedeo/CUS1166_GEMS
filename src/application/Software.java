package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Software {
	
	private final SimpleStringProperty softID;
	private final SimpleStringProperty softName;
	private final SimpleStringProperty softLicenseKey;
	private final SimpleStringProperty softExpiration;
	private final SimpleStringProperty softVersionNo;
	private final SimpleStringProperty softDescription;
	
	public Software(String sID, String sName, String sLicenseKey, String sExpiration, String sVersionNo, String sDescription){
		this.softID = new SimpleStringProperty(sID);
		this.softName = new SimpleStringProperty(sName);
		this.softLicenseKey = new SimpleStringProperty(sLicenseKey);
		this.softExpiration = new SimpleStringProperty(sExpiration);
		this.softVersionNo = new SimpleStringProperty(sVersionNo);
		this.softDescription = new SimpleStringProperty(sDescription);
	}
	
	public String getSoftID() {
		return softID.get();
	}
	public void setSoftID(String sID) {
		softID.set(sID);
	}
	
	
	public String getSoftName() {
		return softName.get();
	}
	public void setSoftName(String sName) {
		softName.set(sName);
	}
	
	
	public String getSoftLicenseKey() {
		return softLicenseKey.get();
	}
	public void setSoftLicenseKey(String sLicenseKey) {
		softLicenseKey.set(sLicenseKey);
	}
	
	
	public String getSoftExpiration() {
		return softExpiration.get();
	}
	public void setSoftExpiration(String sExpiration) {
		softExpiration.set(sExpiration);
	}

	
	public String getSoftVersionNo() {
		return softVersionNo.get();
	}
	public void setSoftVersionNo(String sVersionNo) {
		softVersionNo.set(sVersionNo);
	}
	
	
	public String getSoftDescription() {
		return softDescription.get();
	}
	public void setSoftDescription(String sDescription) {
		softDescription.set(sDescription);
	}
	
	
	public StringProperty softIDProperty() {
		return softID;
	}
	public StringProperty softNameProperty() {
		return softName;
	}
	public StringProperty softLicenseKeyProperty() {
		return softLicenseKey;
	}
	public StringProperty softExpirationProperty() {
		return softExpiration;
	}
	public StringProperty softVersionNoProperty() {
		return softVersionNo;
	}
	public StringProperty softDescriptionProperty() {
		return softDescription;
	}
}
