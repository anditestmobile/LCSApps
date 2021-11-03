package id.co.lcs.apps.model;

/**
 * Created by TED on 15-Jul-20
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GoodReceiptSupplierDetail implements Serializable {
    @Expose
    @SerializedName("ItemCode")
    private String itemNumber;
    @Expose
    @SerializedName("ItemName")
    private String item;
    @Expose
    @SerializedName("ItemType")
    private String itemType;
    @Expose
    @SerializedName("Qty")
    private String qty;
    @Expose
    @SerializedName("ObjectType")
    private String objectType;
    @Expose
    @SerializedName("DocEntry")
    private String docEntry;
    @Expose
    @SerializedName("LineNum")
    private String lineNum;
    @Expose
    @SerializedName("UomName")
    private String uom;

    private transient double rejectedQty;
    private transient double shortageQty;
    private transient String grQty;
    private transient List<Batch> batch;
    private transient List<SerialNumber> serialNo;
    private transient boolean isChecked;

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

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
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

    public double getRejectedQty() {
        return rejectedQty;
    }

    public void setRejectedQty(double rejectedQty) {
        this.rejectedQty = rejectedQty;
    }

    public double getShortageQty() {
        return shortageQty;
    }

    public void setShortageQty(double shortageQty) {
        this.shortageQty = shortageQty;
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

    public String getGrQty() {
        return grQty;
    }

    public void setGrQty(String grQty) {
        this.grQty = grQty;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
