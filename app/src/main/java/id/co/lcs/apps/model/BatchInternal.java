package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by TED on 15-Jul-20
 */

public class BatchInternal implements Serializable {
    @SerializedName("ItemCode")
    private String itemCode;
    @SerializedName("WhsCode")
    private String whsCode;
    @SerializedName("BatchNo")
    private String batch;
    @SerializedName("Quantity")
    private String qty;
    @SerializedName("AdminissionDate")
    private String admDate;

    private transient String batchQty;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getWhsCode() {
        return whsCode;
    }

    public void setWhsCode(String whsCode) {
        this.whsCode = whsCode;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getAdmDate() {
        return admDate;
    }

    public void setAdmDate(String admDate) {
        this.admDate = admDate;
    }

    public String getBatchQty() {
        return batchQty;
    }

    public void setBatchQty(String batchQty) {
        this.batchQty = batchQty;
    }
}
