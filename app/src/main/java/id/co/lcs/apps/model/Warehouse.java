package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Warehouse implements Serializable {
	@SerializedName("WhsCode")
	private String whID;
	@SerializedName("WhsName")
	private String whName;
	private Boolean whSelected;

	public Warehouse(String whID, String whName, Boolean whSelected) {
		this.whID = whID;
		this.whName = whName;
		this.whSelected = whSelected;
	}

	public String getWhID() {
		return whID;
	}

	public void setWhID(String whID) {
		this.whID = whID;
	}

	public String getWhName() {
		return whName;
	}

	public void setWhName(String whName) {
		this.whName = whName;
	}

	public Boolean getWhSelected() {
		return whSelected;
	}

	public void setWhSelected(Boolean whSelected) {
		this.whSelected = whSelected;
	}
}
	