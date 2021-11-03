package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Acien on 9/11/17.
 */

public class WMDetailStock implements Serializable {
	@SerializedName("InStock")
	String availableQty;
	@SerializedName("UomName")
	String uom;
	@SerializedName("WhsCode")
	String whs;
	@SerializedName("CommitedQty")
	String commitedQty;

	String batch;
	String allocatedQty;

	public String getAvailableQty() {
		return availableQty;
	}

	public void setAvailableQty(String availableQty) {
		this.availableQty = availableQty;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public String getAllocatedQty() {
		return allocatedQty;
	}

	public void setAllocatedQty(String allocatedQty) {
		this.allocatedQty = allocatedQty;
	}

	public String getWhs() {
		return whs;
	}

	public void setWhs(String whs) {
		this.whs = whs;
	}
	public String getCommitedQty() {
		return commitedQty;
	}

	public void setCommitedQty(String commitedQty) {
		this.commitedQty = commitedQty;
	}
}

