package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Acien on 9/11/17.
 */

public class TRStock implements Serializable {
	@SerializedName("ItemCode")
	String idMaterial;
	@SerializedName("ItemName")
	String desc;
	@SerializedName("InStock")
	String availableQty;
	@SerializedName("UomName")
	String uom;
	@SerializedName("WhsCode")
	String whs;

	public String getIdMaterial() {
		return idMaterial;
	}

	public void setIdMaterial(String idMaterial) {
		this.idMaterial = idMaterial;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

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

	public String getWhs() {
		return whs;
	}

	public void setWhs(String whs) {
		this.whs = whs;
	}
}
