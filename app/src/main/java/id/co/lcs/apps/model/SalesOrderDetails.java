package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

public class SalesOrderDetails {
    @SerializedName("ItemCode")
    private String itemCode;
    @SerializedName("ItemName")
    private String itemName;
    @SerializedName("Qty")
    private String qty;
    @SerializedName("Warehouse")
    private String warehouse;
    @SerializedName("Price")
    private String price;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
