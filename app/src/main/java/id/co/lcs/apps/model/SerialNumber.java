package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by TED on 15-Jul-20
 */
public class SerialNumber implements Serializable {
    @SerializedName("SerialNo")
    private String serialNo;
    @SerializedName("Qty")
    private String qty;

    public SerialNumber(String serialNo, String qty) {
        this.serialNo = serialNo;
        this.qty = qty;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }
}
