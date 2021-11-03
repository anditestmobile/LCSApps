package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TED on 15-Jul-20
 */
public class StockTakeRequest implements Serializable {
    @SerializedName("Name")
    private String name;
    @SerializedName("CountingDate")
    private String countingDate;
    @SerializedName("BinLocation")
    private String binLocation;
    @SerializedName("WhsCode")
    private String whsCode;
    @SerializedName("ICDraftItem")
    private List<StockTake> stockTake;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountingDate() {
        return countingDate;
    }

    public void setCountingDate(String countingDate) {
        this.countingDate = countingDate;
    }

    public String getBinLocation() {
        return binLocation;
    }

    public void setBinLocation(String binLocation) {
        this.binLocation = binLocation;
    }

    public String getWhsCode() {
        return whsCode;
    }

    public void setWhsCode(String whsCode) {
        this.whsCode = whsCode;
    }

    public List<StockTake> getStockTake() {
        return stockTake;
    }

    public void setStockTake(List<StockTake> stockTake) {
        this.stockTake = stockTake;
    }
}
