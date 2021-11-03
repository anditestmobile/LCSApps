package id.co.lcs.apps.model;

/**
 * Created by TED on 15-Jul-20
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;


public class GoodReceiptDetail implements Serializable {
    @SerializedName("DocNum")
    private String docNum;
    @SerializedName("ItemCode")
    private String itemNumber;
    @SerializedName("ItemName")
    private String itemName;
    @SerializedName("BarCode")
    private String barcode;
    @SerializedName("ObjectType")
    private String objectType;
    @SerializedName("DocEntry")
    private String docEntry;
    @SerializedName("LineNum")
    private String lineNum;
    @SerializedName("Qty")
    private String qty;
    @SerializedName("ItemType")
    private String itemType;
    @SerializedName("UomName")
    private String uom;

    private transient ArrayList<BatchInternal> batchInternals;
    private transient ArrayList<SerialNumberInternal> serialNumberInternals;
    private transient String grQty;
    private transient boolean isChecked;
    private transient boolean isExpand;

    public String getDocNum() {
        return docNum;
    }

    public void setDocNum(String docNum) {
        this.docNum = docNum;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
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

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getGrQty() {
        return grQty;
    }

    public void setGrQty(String grQty) {
        this.grQty = grQty;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public ArrayList<BatchInternal> getBatchInternals() {
        return batchInternals;
    }

    public void setBatchInternals(ArrayList<BatchInternal> batchInternals) {
        this.batchInternals = batchInternals;
    }

    public ArrayList<SerialNumberInternal> getSerialNumberInternals() {
        return serialNumberInternals;
    }

    public void setSerialNumberInternals(ArrayList<SerialNumberInternal> serialNumberInternals) {
        this.serialNumberInternals = serialNumberInternals;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
