package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by TED on 15-Jul-20
 */

public class GoodReceiptSupplier implements Serializable {
    @SerializedName("CardCode")
    private String cardCode;
    @SerializedName("CardName")
    private String cardName;
    @SerializedName("NumAtCard")
    private String numAtCard;
    @SerializedName("PONumber")
    private String poNumber;
    @SerializedName("DocDate")
    private String docDate;
    @SerializedName("DocDueDate")
    private String docDueDate;
    @SerializedName("PODetails")
    private List<GoodReceiptSupplierDetail> grSupplierDetail;

    private transient int totalCheckBox;

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getNumAtCard() {
        return numAtCard;
    }

    public void setNumAtCard(String numAtCard) {
        this.numAtCard = numAtCard;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
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

    public List<GoodReceiptSupplierDetail> getGrSupplierDetail() {
        return grSupplierDetail;
    }

    public void setGrSupplierDetail(List<GoodReceiptSupplierDetail> grSupplierDetail) {
        this.grSupplierDetail = grSupplierDetail;
    }

    public int getTotalCheckBox() {
        return totalCheckBox;
    }

    public void setTotalCheckBox(int totalCheckBox) {
        this.totalCheckBox = totalCheckBox;
    }
}
