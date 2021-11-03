package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TED on 15-Jul-20
 */
public class GoodReceipt implements Serializable {
    @SerializedName("DocNum")
    private String trNo;
    @SerializedName("FromWhsCode")
    private String fromWh;
    @SerializedName("ToWhsCode")
    private String toWh;
    @SerializedName("DocDate")
    private String docDate;
    @SerializedName("DocDueDate")
    private String docDueDate;
    @SerializedName("ITDetails")
    private List<GoodReceiptDetail> grDetail;

    private transient int totalCheckBox;

    public String getFromWh() {
        return fromWh;
    }

    public void setFromWh(String fromWh) {
        this.fromWh = fromWh;
    }

    public String getToWh() {
        return toWh;
    }

    public void setToWh(String toWh) {
        this.toWh = toWh;
    }

    public String getDocDate() {
        return docDate;
    }

    public void setDocDate(String docDate) {
        this.docDate = docDate;
    }

    public String getDocDueDate() {
        return docDueDate;
    }

    public void setDocDueDate(String docDueDate) {
        this.docDueDate = docDueDate;
    }

    public List<GoodReceiptDetail> getGrDetail() {
        return grDetail;
    }

    public void setGrDetail(List<GoodReceiptDetail> grDetail) {
        this.grDetail = grDetail;
    }

    public String getTrNo() {
        return trNo;
    }

    public void setTrNo(String trNo) {
        this.trNo = trNo;
    }

    public int getTotalCheckBox() {
        return totalCheckBox;
    }

    public void setTotalCheckBox(int totalCheckBox) {
        this.totalCheckBox = totalCheckBox;
    }
}
