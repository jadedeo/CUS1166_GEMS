package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Hardware {
	
	private final SimpleStringProperty hardID;
	private final SimpleStringProperty hardType;
	private final SimpleStringProperty hardBrand;
	private final SimpleStringProperty hardModel;
	private final SimpleStringProperty hardSerialNo;
	private final SimpleStringProperty hardDatePurchased;
	
	Hardware(String hID, String hType, String hBrand, String hModel, String hSerialNo, String hDatePurchased){
		this.hardID = new SimpleStringProperty(hID);
		this.hardType = new SimpleStringProperty(hType);
		this.hardBrand = new SimpleStringProperty(hBrand);
		this.hardModel = new SimpleStringProperty(hModel);
		this.hardSerialNo = new SimpleStringProperty(hSerialNo);
		this.hardDatePurchased = new SimpleStringProperty(hDatePurchased);
		
	}
	
	public String getHardID() {
		return hardID.get();
	}
	public void setHardID(String hID) {
		hardID.set(hID);
	}
	
	public String getHardType() {
		return hardType.get();
	}
	public void setHardType(String hType) {
		hardType.set(hType);
	}
	
	public String getHardBrand() {
		return hardBrand.get();
	}
	public void setHardBrand(String hBrand) {
		hardBrand.set(hBrand);
	}
	
	public String getHardModel() {
		return hardModel.get();
	}
	public void setHardModel(String hModel) {
		hardModel.set(hModel);
	}
	
	public String getHardSerialNo() {
		return hardSerialNo.get();
	}
	public void setHardSerialNo(String hSerialNo) {
		hardSerialNo.set(hSerialNo);
	}
	
	public String getHardDatePurchased() {
		return hardDatePurchased.get();
	}
	public void setHardDatePurchased(String hDatePurchased) {
		hardDatePurchased.set(hDatePurchased);
	}

	
	
	public StringProperty hardIDProperty() {
		return hardID;
	}
	public StringProperty hardTypeProperty() {
		return hardType;
	}
	public StringProperty hardBrandProperty() {
		return hardBrand;
	}
	public StringProperty hardModelProperty() {
		return hardModel;
	}
	public StringProperty hardSerialNoProperty() {
		return hardSerialNo;
	}
	public StringProperty hardDatePurchasedProperty() {
		return hardDatePurchased;
	}
	
}
