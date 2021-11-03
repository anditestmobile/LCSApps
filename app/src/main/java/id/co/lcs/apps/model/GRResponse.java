package id.co.lcs.apps.model;

import java.io.Serializable;
import java.util.ArrayList;

public class GRResponse implements Serializable {
    private int StatusCode;
    private String StatusMessage;
    private ArrayList<GoodReceipt> ResponseData;

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

    public ArrayList<GoodReceipt> getResponseData() {
        return ResponseData;
    }

    public void setResponseData(ArrayList<GoodReceipt> responseData) {
        ResponseData = responseData;
    }
}
