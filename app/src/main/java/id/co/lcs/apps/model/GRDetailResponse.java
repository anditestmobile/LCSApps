package id.co.lcs.apps.model;

import java.io.Serializable;
import java.util.ArrayList;

import id.co.lcs.apps.activity.GoodReceiptActivity;

public class GRDetailResponse implements Serializable {
    private int StatusCode;
    private String StatusMessage;
    private ArrayList<GoodReceiptDetail> ResponseData;

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

    public ArrayList<GoodReceiptDetail> getResponseData() {
        return ResponseData;
    }

    public void setResponseData(ArrayList<GoodReceiptDetail> responseData) {
        ResponseData = responseData;
    }
}
