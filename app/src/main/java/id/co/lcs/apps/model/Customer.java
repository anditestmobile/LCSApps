package id.co.lcs.apps.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by TED on 15-Jul-20
 */
public class Customer implements Serializable {
    @SerializedName("Name")
    private String name;
    @SerializedName("companyname")
    private String companyName;
    @SerializedName("CustomerCode")
    private String customerCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
}
