package id.co.lcs.apps.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SalesOrderRequest {
    @SerializedName("CustomerCode")
    private String customerCode;
    @SerializedName("Name")
    private String name;
    @SerializedName("Date")
    private String date;
    @SerializedName("DeliveryDate")
    private String delDate;
    @SerializedName("UserId")
    private String userId;
    @SerializedName("companyname")
    private String compName;
    @SerializedName("Phone")
    private String phone;
    @SerializedName("SODraftItem")
    private List<SalesOrderDetails> soItem;

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDelDate() {
        return delDate;
    }

    public void setDelDate(String delDate) {
        this.delDate = delDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<SalesOrderDetails> getSoItem() {
        return soItem;
    }

    public void setSoItem(List<SalesOrderDetails> soItem) {
        this.soItem = soItem;
    }
}
