package id.co.lcs.apps.model;

/**
 * Created by TED on 15-Jul-20
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GRPOPostingDetail implements Serializable {
    @SerializedName("ItemCode")
    private String itemNumber;
    @SerializedName("ItemName")
    private String item;
    @SerializedName("Qty")
    private double qty;
    @SerializedName("ObjectType")
    private String objectType;
    @SerializedName("DocEntry")
    private String docEntry;
    @SerializedName("LineNum")
    private String lineNum;
    @SerializedName("BatchNum")
    private List<Batch> batch;
    @SerializedName("SerialNum")
    private List<SerialNumber> serialNo;


    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getDocEntry() {
        return docEntry;
    }

    public void setDocEntry(String docEntry) {
        this.docEntry = docEntry;
    }

    public String getLineNum() {
        return lineNum;
    }

    public void setLineNum(String lineNum) {
        this.lineNum = lineNum;
    }

    public List<Batch> getBatch() {
        return batch;
    }

    public void setBatch(List<Batch> batch) {
        this.batch = batch;
    }

    public List<SerialNumber> getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(List<SerialNumber> serialNo) {
        this.serialNo = serialNo;
    }
}
