package id.co.lcs.apps.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuotationRequest {
    @SerializedName("Quotation")
    private String quotation;
    @SerializedName("Date")
    private String date;

    public String getQuotation() {
        return quotation;
    }

    public void setQuotation(String quotation) {
        this.quotation = quotation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
