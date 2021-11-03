package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by TED on 15-Jul-20
 */
public class StockTake {
    @SerializedName("ItemCode")
    private String itemCode;
    @SerializedName("Qty")
    private int qty;

    private transient String itemName;

    public StockTake() {
    }

    public StockTake(String itemCode, int qty, String itemName) {
        this.itemCode = itemCode;
        this.qty = qty;
        this.itemName = itemName;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
