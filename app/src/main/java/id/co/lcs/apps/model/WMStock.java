package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Acien on 9/11/17.
 */

public class WMStock implements Serializable {
	@SerializedName("ItemCode")
	String idMaterial;
	@SerializedName("ItemName")
	String desc;
	@SerializedName("Price")
	String price;
	@SerializedName("Stock")
	List<WMDetailStock> detailStocks;

	public List<WMDetailStock> getDetailStocks() {
		return detailStocks;
	}

	public void setDetailStocks(List<WMDetailStock> detailStocks) {
		this.detailStocks = detailStocks;
	}

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

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
}
