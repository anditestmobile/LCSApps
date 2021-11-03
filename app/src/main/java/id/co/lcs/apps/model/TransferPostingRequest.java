package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TED on 15-Jul-20
 */

//{
//        "FromWhsCode": "02",
//        "ToWhsCode": "04",
//        "Date": "20201216",
//        "ITRItem": [
//        {
//        "ItemCode": "6110-AM-18718",
//        "Quantity": 2
//        }
//        ]
//        }
public class TransferPostingRequest implements Serializable {
    @SerializedName("FromWhsCode")
    private String fromWH;
    @SerializedName("ToWhsCode")
    private String toWH;
    @SerializedName("Date")
    private String date;
    @SerializedName("ITRItem")
    private List<TPItem> tpItems;

    public String getFromWH() {
        return fromWH;
    }

    public void setFromWH(String fromWH) {
        this.fromWH = fromWH;
    }

    public String getToWH() {
        return toWH;
    }

    public void setToWH(String toWH) {
        this.toWH = toWH;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<TPItem> getTpItems() {
        return tpItems;
    }

    public void setTpItems(List<TPItem> tpItems) {
        this.tpItems = tpItems;
    }
}
