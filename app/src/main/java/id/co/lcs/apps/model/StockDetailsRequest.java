package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by TED on 15-Jul-20
 */
public class StockDetailsRequest implements Serializable {
    @SerializedName("WhsCode")
    private String wh;
    @SerializedName("ItemCode")
    private String itemCode;
    @SerializedName("BarCode")
    private String barcode;

    public String getWh() {
        return wh;
    }

    public void setWh(String wh) {
        this.wh = wh;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
