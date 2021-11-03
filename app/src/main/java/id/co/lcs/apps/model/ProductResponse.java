package id.co.lcs.apps.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProductResponse implements Serializable {
    private int StatusCode;
    private String StatusMessage;
    private ArrayList<Product> ResponseData;
    private String SalesPeriod;

    public int getStatusCode() {
        return StatusCode;
    }

    public void setStatusCode(int statusCode) {
        StatusCode = statusCode;
    }

    public String getStatusMessage() {
        return StatusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        StatusMessage = statusMessage;
    }

    public ArrayList<Product> getResponseData() {
        return ResponseData;
    }

    public void setResponseData(ArrayList<Product> responseData) {
        ResponseData = responseData;
    }

    public String getSalesPeriod() {
        return SalesPeriod;
    }

    public void setSalesPeriod(String salesPeriod) {
        SalesPeriod = salesPeriod;
    }
}
